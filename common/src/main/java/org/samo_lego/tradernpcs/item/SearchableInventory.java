package org.samo_lego.tradernpcs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class SearchableInventory extends HashMap<Item, SearchableInventory.ItemList> {


    public SearchableInventory(int capacity) {
        super(capacity);
    }

    /**
     * Puts payment stack into inventory or increases existing stack's count. Empty safe.
     * @param payment payment stack to add.
     */
    public void addStack(ItemStack payment) {
        if (payment.isEmpty())
            return;

        ItemStack paymentStack = this.getSimilarStack(payment);

        if (paymentStack.isEmpty()) {
            // Not yet in inventory
            ItemList itemStacks = new ItemList();
            boolean add = itemStacks.add(payment.copy());

            // Don't add if empty
            if (add)
                this.put(payment.getItem(), itemStacks);
        } else {
            int count = paymentStack.getCount();

            if (count < payment.getMaxStackSize()) {
                // Just increase stack size
                paymentStack.grow(payment.getCount());
            } else {
                // New stack is needed to not overflow
                this.get(payment.getItem()).add(payment.copy());
            }
        }
    }

    /**
     * Decreases similar stack count by size of provided stack.
     * @param stack the stack to look similar stacks for and decrease their count.
     */
    public void decreaseStack(ItemStack stack) {
        this.decreaseStack(stack, stack.getCount());
    }

    /**
     * Decreases similar stack count by given amount.
     * @param stack the stack to look similar stacks for and decrease their count.
     * @param count the amount to decrease.
     */
    public void decreaseStack(ItemStack stack, int count) {
        ItemStack paymentStack = this.getSimilarStack(stack);

        if (!paymentStack.isEmpty()) {
            if (paymentStack.getCount() <= count) {
                // Remove stack
                this.get(paymentStack.getItem()).remove(paymentStack);

                // Recursive for next stack
                this.decreaseStack(stack, count - paymentStack.getCount());
            } else {
                paymentStack.shrink(count);
            }
        }
    }


    /**
     * Gets the common count of itemstacks "similar" to the given stack.
     * Similar as equal NBT, but ignoring count.
     * @param result the stack to get count for.
     * @return common stack count.
     */
    public int getCommonStackSize(ItemStack result) {
        LinkedList<ItemStack> potentialStock = this.getOrDefault(result.getItem(), null);

        // No items
        if (potentialStock == null)
            return 0;

        int size = 0;

        // Check potential stock, we must take item count into account
        // and make sure that NBT is equal
        for (ItemStack stockStack : potentialStock) {
            assert !stockStack.isEmpty();
            if (areSimilar(stockStack, result)) {
                // Item is the same, increase stock
                size += stockStack.getCount();
            }
        }
        return size;
    }

    /**
     * Gets an itemstack that has same NBT as the given stack, ignoring count.
     * @param result the stack to get similar stack for.
     * @return the saved itemstack if found, otherwise empty.
     */
    public ItemStack getSimilarStack(ItemStack result) {
        LinkedList<ItemStack> potentialStock = this.getOrDefault(result.getItem(), null);

        // No item
        if (potentialStock == null)
            return ItemStack.EMPTY;

        // Check potential stock, we must take item count into account
        // and make sure that NBT is equal
        for (ItemStack stockStack : potentialStock) {
            assert !stockStack.isEmpty();
            if (areSimilar(stockStack, result)) {
                // Item is the same, increase stock
                return stockStack;
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Checks whether stacks have same NBT ignoring count.
     * @param stack1 first stack.
     * @param stack2 second stack.
     * @return true if stacks are similar (differ in count only).
     */
    public static boolean areSimilar(ItemStack stack1, ItemStack stack2) {
        ItemStack copy = stack1.copy();
        copy.setCount(stack2.getCount());

        return ItemStack.matches(copy, stack2);
    }

    /**
     * Clears current items and adds new ones from given list.
     * @param items items to set.
     */
    public void setFromArray(Collection<ItemStack> items) {
        this.clear();

        for (ItemStack stack : items) {
            this.addStack(stack);
        }
    }

    static class ItemList extends LinkedList<ItemStack> {
        @Override
        public boolean add(ItemStack stack) {
            if (stack.isEmpty())
                return false;
            return super.add(stack);
        }
    }
}



