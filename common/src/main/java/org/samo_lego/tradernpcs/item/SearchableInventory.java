package org.samo_lego.tradernpcs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public final class SearchableInventory extends HashMap<Item, SearchableInventory.ItemList> {

    public SearchableInventory(int capacity) {
        super(capacity);
    }

    /**
     * Puts stack into inventory or increases existing stack's count. Empty safe.
     * @param stack stack stack to add.
     */
    public void addStack(ItemStack stack) {
        if (stack.isEmpty())
            return;

        ItemStack existingStack = this.getSmallestSimilarStack(stack);

        ItemStack copy = stack.copy();
        if (existingStack.isEmpty()) {
            // Not yet in inventory
            ItemList itemStacks = new ItemList();
            boolean add = itemStacks.add(copy);

            // Don't add if empty
            if (add)
                this.put(stack.getItem(), itemStacks);
        } else {
            int count = stack.getCount();
            int newCount = existingStack.getCount() + count;
            int maxStackSize = stack.getMaxStackSize();

            if (newCount < maxStackSize) {
                // Just increase stack size to limit
                existingStack.grow(count);
            } else {
                // New stack is needed to not overflow
                existingStack.setCount(maxStackSize);
                copy.setCount(newCount - maxStackSize);
                this.get(stack.getItem()).add(copy);
            }
        }
    }

    /**
     * Decreases similar stack count by size of provided stack.
     * @param stack the stack to look similar stacks for and decrease their count.
     * @param similarStack pointer to similar stack.
     */
    public void decreaseStack(ItemStack stack, ItemStack similarStack) {
        this.decreaseStack(stack, stack.getCount(), similarStack);
    }

    /**
     * Decreases similar stack count by given amount.
     * @param stack the stack to look similar stacks for and decrease their count.
     * @param count the amount to decrease.
     */
    public void decreaseStack(ItemStack stack, int count) {
        ItemStack similarStack = this.getSmallestSimilarStack(stack);
        this.decreaseStack(stack, count, similarStack);
    }

    /**
     * Decreases similar stack count by given amount.
     * @param stack the stack to look similar stacks for and decrease their count.
     * @param count the amount to decrease.
     * @param similarStack pointer to similar stack.
     */
    public void decreaseStack(ItemStack stack, int count, ItemStack similarStack) {
        if (!similarStack.isEmpty()) {
            if (similarStack.getCount() <= count) {
                // Remove stack
                this.get(similarStack.getItem()).remove(similarStack);

                // Recursive for next stack
                this.decreaseStack(stack, count - similarStack.getCount());
            } else {
                similarStack.shrink(count);
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
     * Gets an itemstack with the lowest count that has same NBT as the given stack, ignoring count.
     * @param result the stack to get similar stack for.
     * @return the saved itemstack if found, otherwise empty.
     */
    public ItemStack getSmallestSimilarStack(ItemStack result) {
        LinkedList<ItemStack> potentialStock = this.getOrDefault(result.getItem(), null);

        // No item
        if (potentialStock == null)
            return ItemStack.EMPTY;

        // Check potential stock, we must take item count into account
        // and make sure that NBT is equal
        ItemStack foundStack = ItemStack.EMPTY;
        for (ItemStack stockStack : potentialStock) {
            assert !stockStack.isEmpty();
            if (areSimilar(stockStack, result) && (stockStack.getCount() < foundStack.getCount() || foundStack == ItemStack.EMPTY)) {
                // Item is the same
                foundStack = stockStack;
            }
        }

        return foundStack;
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

    /**
     * Returns array of all items in this inventory.
     * @return array of all items in this inventory.
     */
    public ArrayList<ItemStack> toArray() {
        ArrayList<ItemStack> items = new ArrayList<>();
        // Set items
        for (LinkedList<ItemStack> itemList : this.values()) {
            items.addAll(itemList);
        }

        return items;
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
