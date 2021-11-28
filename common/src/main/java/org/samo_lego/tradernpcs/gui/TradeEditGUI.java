package org.samo_lego.tradernpcs.gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.taterzens.gui.ListItemsGUI;
import org.samo_lego.taterzens.gui.RedirectedSlot;
import org.samo_lego.tradernpcs.profession.TraderNPCProfession;

public class TradeEditGUI extends ListItemsGUI {

    private final TraderNPCProfession profession;
    private final MerchantOffers trades;

    /**
     * Constructs a new layered container gui for the supplied player.
     *
     * @param player                      the player to server this gui to
     */
    public TradeEditGUI(TraderNPCProfession profession, ServerPlayer player) {
        super(player, profession.getNpc().getName(), "");
        this.profession = profession;
        this.trades = profession.getTrades();

        // Redirect slots
        int i = 9;
        do {
            // - 9 as first row is occupied but we want to have index 0 at first element
            this.setSlotRedirect(i, new RedirectedSlot(this, i - 9));
            i += 3;
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

        profession.getTrades().forEach(offer -> {
            //todo
        });
    }

    public void editTrade(int tradeIndex, ItemStack replacementStack) {
        MerchantOffer offer = this.getOffer(tradeIndex);

        if (offer != null) {
            int ix = this.getTradeIndex(tradeIndex);

            CompoundTag offerTag = this.trades.get(ix).createTag();
            System.out.println("Editing " + offerTag);
            String stackTag = "";
            switch (ix) {
                case 0 -> stackTag = "buy";
                case 1 -> stackTag = "sell";
                case 3 -> stackTag = "buyB";
            }
            offerTag.put(stackTag, replacementStack.save(new CompoundTag()));

            this.trades.set(ix, new MerchantOffer(offerTag));
        }
    }

    @Nullable
    private MerchantOffer getOffer(int tradeIndex) {
        int ix = getTradeIndex(tradeIndex);

        if (ix < this.trades.size())
            return this.trades.get(ix);

        return null;
    }

    private int getTradeIndex(int tradeIndex) {
        int row = tradeIndex / 9;
        int col = tradeIndex % 9;
        int i = col < 4 ? 0 : 1;

        return row + i;
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
        MerchantOffer offer = getOffer(i);

        ItemStack stack = ItemStack.EMPTY;
        if (offer != null) {
            int col = i % 9;
            int stackIx = col % 5;

            switch (stackIx) {
                case 0 -> stack = offer.getBaseCostA();
                case 1 -> stack = offer.getCostB();
                case 3 -> stack = offer.getResult();
            }
        }
        return stack;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        i = this.getSlot2MessageIndex(i);
        return this.removeItemNoUpdate(i);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        i = this.getSlot2MessageIndex(i);
        if (i < this.trades.size()) {
            this.editTrade(i, ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        i = this.getSlot2MessageIndex(i);
        if (i < this.trades.size())
            this.editTrade(i, itemStack);
        else
            this.profession.addTrade(itemStack, ItemStack.EMPTY);
    }

    @Override
    public int getMaxPages() {
        return 0;
    }
}
