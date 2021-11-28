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
import org.samo_lego.taterzens.api.professions.TaterzenProfession;
import org.samo_lego.taterzens.npc.TaterzenNPC;
import org.samo_lego.tradernpcs.gui.TradeEditGUI;
import org.samo_lego.tradernpcs.gui.TradeGUI;

public class TraderNPCProfession implements TaterzenProfession {
    public static final ResourceLocation ID = new ResourceLocation("tradernpcs", "trader");
    private TaterzenNPC npc;
    public final MerchantOffers trades = new MerchantOffers();

    @Override
    public InteractionResult interactAt(Player player, Vec3 pos, InteractionHand hand) {
        new TradeGUI(this, (ServerPlayer) player).open();
        return InteractionResult.PASS;
    }

    @Override
    public void readNbt(CompoundTag tag) {
    }

    @Override
    public void saveNbt(CompoundTag tag) {
    }

    @Override
    public TaterzenProfession create(TaterzenNPC taterzen) {
        TraderNPCProfession profession = new TraderNPCProfession();
        profession.npc = taterzen;
        return profession;
    }

    public void openEditGui(ServerPlayer player) {
        TradeEditGUI tradeEditGUI = new TradeEditGUI(this, player);
        tradeEditGUI.open();
    }

    public void addTrade(ItemStack tradeStack1, ItemStack sellStack) {
        this.addTrade(tradeStack1, ItemStack.EMPTY, sellStack);
    }


    public void addTrade(ItemStack tradeStack1, ItemStack tradeStack2, ItemStack sellStack) {
        this.trades.add(new MerchantOffer(tradeStack1, tradeStack2, sellStack, 0, Integer.MAX_VALUE, 0));
    }

    public MerchantOffers getTrades() {
        return this.trades;
    }

    public TaterzenNPC getNpc() {
        return this.npc;
    }
}
