package org.samo_lego.tradernpcs.profession;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.phys.Vec3;
import org.samo_lego.taterzens.api.professions.AbstractProfession;
import org.samo_lego.taterzens.npc.TaterzenNPC;
import org.samo_lego.tradernpcs.gui.TradeEditGUI;
import org.samo_lego.tradernpcs.gui.TradeGUI;

import static org.samo_lego.tradernpcs.Traders.MOD_ID;

public class TraderNPCProfession extends AbstractProfession {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "trader");
    protected MerchantOffers trades = new MerchantOffers();

    public TraderNPCProfession(TaterzenNPC npc) {
        super(npc);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 pos, InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            new TradeGUI(this, (ServerPlayer) player).open();
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void readNbt(CompoundTag tag) {
        if (tag.contains("Trades", 10)) {
            this.trades = new MerchantOffers(tag.getCompound("Trades"));
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        MerchantOffers merchantOffers = this.getTrades();
        if (!merchantOffers.isEmpty()) {
            tag.put("Trades", merchantOffers.createTag());
        }
    }

    public void openEditGui(ServerPlayer player) {
        TradeEditGUI tradeEditGUI = new TradeEditGUI(this, player);
        tradeEditGUI.open();
    }

    public void addTrade(ItemStack tradeStack1, ItemStack sellStack) {
        this.addTrade(tradeStack1, ItemStack.EMPTY, sellStack);
    }


    public void addTrade(ItemStack tradeStack1, ItemStack tradeStack2, ItemStack sellStack) {
        if (!tradeStack1.isEmpty() || !tradeStack2.isEmpty() || !sellStack.isEmpty())
            this.trades.add(new MerchantOffer(tradeStack1, tradeStack2, sellStack, Integer.MAX_VALUE, 0, 0));
    }

    public MerchantOffers getTrades() {
        return this.trades;
    }

    public TaterzenNPC getNpc() {
        return this.npc;
    }

    /**
     * Calls when item is being traded.
     * @param tradeOffer the offer being traded.
     */
    public void onTrade(MerchantOffer tradeOffer) {
    }

    /**
     * Whether this player is allowed to trade with this npc.
     * @param player the player.
     * @param offer the offer being traded.
     * @return true if allowed, false if not.
     */
    public boolean mayTrade(Player player, MerchantOffer offer) {
        return true;
    }

    /**
     * Sets that the trades have been changed.
     */
    public void setDirty() {
    }
}
