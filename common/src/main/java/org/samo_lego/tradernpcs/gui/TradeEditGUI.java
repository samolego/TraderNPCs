package org.samo_lego.tradernpcs.gui;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.taterzens.fabric.gui.ListItemsGUI;
import org.samo_lego.tradernpcs.gui.slot.TradeSlot;
import org.samo_lego.tradernpcs.mixin.MerchantOfferAccessor;
import org.samo_lego.tradernpcs.profession.TraderNPCProfession;

public class TradeEditGUI extends ListItemsGUI {

    private final TraderNPCProfession profession;
    private final MerchantOffers trades;
    private final int maxAdditionalPages;

    /**
     * Constructs a new layered container gui for the supplied player.
     *
     * @param player                      the player to server this gui to
     */
    public TradeEditGUI(TraderNPCProfession profession, ServerPlayer player, int maxPages) {
        super(player, profession.getNpc().getName(), "merchant.trades");
        this.profession = profession;
        this.trades = profession.getTrades();

        // Redirect slots
        int i = 9;
        do {
            // - 9 as first row is occupied but we want to have index 0 at first element
            this.setSlotRedirect(i, new Slot(this, i - 9, 0, 0));
            this.setSlotRedirect(i + 1, new TradeSlot(this, i - 8));
            this.setSlotRedirect(i + 3, new TradeSlot(this, i - 6));

            // Second trade
            this.setSlotRedirect(i + 5, new TradeSlot(this, i - 4));
            this.setSlotRedirect(i + 6, new TradeSlot(this, i - 3));
            this.setSlotRedirect(i + 8, new TradeSlot(this, i - 1));
            i += 9;
        } while (i < this.getSize());

        final ItemStack pane = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        pane.setTag(customData.copy());
        pane.setHoverName(TextComponent.EMPTY);

        final ItemStack tradeFor = new ItemStack(Items.SPECTRAL_ARROW);
        tradeFor.setTag(customData.copy());
        tradeFor.setHoverName(new TextComponent("->"));

        // GUI skeleton
        for (i = 9; i + 8 < this.getSize(); i += 9) {
            this.setSlot(i + 2, tradeFor);
            this.setSlot(i + 4, pane);
            this.setSlot(i + 7, tradeFor);
        }

        this.maxAdditionalPages = maxPages;
    }

    public TradeEditGUI(TraderNPCProfession profession, ServerPlayer player) {
        this(profession, player, -1);
    }

    public ItemStack editTrade(int slotIndex, ItemStack replacementStack) {
        MerchantOffer offer = this.getOffer(slotIndex);

        if (offer != null) {
            ItemStack takenStack;
            if (slotIndex % 9 == 0 || slotIndex % 9 == 5) {
                takenStack = offer.getBaseCostA();
                ((MerchantOfferAccessor) offer).setBaseCostA(replacementStack);
            } else if (slotIndex % 9 == 1 || slotIndex % 9 == 6) {
                takenStack = offer.getCostB();
                ((MerchantOfferAccessor) offer).setCostB(replacementStack);
            } else {
                takenStack = offer.getResult();
                ((MerchantOfferAccessor) offer).setResult(replacementStack);
            }

            // If all stacks are now empty, this whole trade is empty
            if (offer.getBaseCostA().isEmpty() && offer.getCostB().isEmpty() && offer.getResult().isEmpty()) {
                this.trades.remove(offer);
            }
            return takenStack;
        }
        return ItemStack.EMPTY;
    }

    public MerchantOffers getTrades() {
        return this.trades;
    }

    @Nullable
    private MerchantOffer getOffer(int tradeIndex) {
        int ix = getTradeIndex(tradeIndex);

        if (ix < this.trades.size())
            return this.trades.get(ix);

        return null;
    }

    /**
     * Gets the index of trade from clicked slot index
     * @param slotIndex slot index
     * @return index of trade to get
     */
    private int getTradeIndex(int slotIndex) {
        int row = slotIndex / 9;
        int col = slotIndex % 9;
        int i = col < 4 ? 0 : 1;

        return row * 2 + i;
    }

    @Override
    public void clearContent() {
        this.profession.getTrades().clear();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        i = this.getSlot2MessageIndex(i);
        MerchantOffer offer = this.getOffer(i);

        ItemStack stack = ItemStack.EMPTY;
        if (offer != null) {
            if (i % 9 == 0 || i % 9 == 5) {
                stack = offer.getBaseCostA();
            } else if (i % 9 == 1 || i % 9 == 6) {
                stack = offer.getCostB();
            } else {
                stack = offer.getResult();
            }

        }
        return stack;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return this.removeItemNoUpdate(i);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        i = this.getSlot2MessageIndex(i);
        if (this.getTradeIndex(i) < this.trades.size()) {
            return this.editTrade(i, ItemStack.EMPTY);
        }
        this.setChanged();
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        i = this.getSlot2MessageIndex(i);
        if (this.getTradeIndex(i) < this.trades.size()) {
            this.editTrade(i, itemStack);
        } else {
            this.profession.addTrade(itemStack, ItemStack.EMPTY);
        }
        this.setChanged();
    }

    @Override
    public int getMaxPages() {
        if (this.trades == null || this.maxAdditionalPages != -1) {
            return maxAdditionalPages;
        }
        // 10 being the amount of possible trades per page
        return this.trades.size() / 10;
    }

    @Override
    public void setChanged() {
        this.profession.setDirty();
    }
}
