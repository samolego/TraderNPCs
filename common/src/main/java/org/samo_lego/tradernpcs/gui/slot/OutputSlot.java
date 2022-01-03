package org.samo_lego.tradernpcs.gui.slot;

import eu.pb4.sgui.virtual.merchant.VirtualMerchant;
import eu.pb4.sgui.virtual.merchant.VirtualTradeOutputSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.samo_lego.tradernpcs.gui.TradeGUI;
import org.samo_lego.tradernpcs.profession.TraderNPCProfession;

public class OutputSlot extends VirtualTradeOutputSlot {
    private final TraderNPCProfession profession;
    private final MerchantContainer merchantInventory;
    private final TradeGUI tradeGUI;

    public OutputSlot(ServerPlayer player, TraderNPCProfession profession, VirtualMerchant merchant, MerchantContainer merchantInventory, TradeGUI tradeGUI) {
        super(player, merchant, merchantInventory, 2, 0, 0);
        this.profession = profession;
        this.merchantInventory = merchantInventory;
        this.tradeGUI = tradeGUI;
    }

    @Override
    public boolean mayPickup(Player player) {
        MerchantOffer activeOffer = this.merchantInventory.getActiveOffer();
        return this.profession.mayTrade(player, activeOffer) && super.mayPickup(player);
    }

    @Override
    public void onTake(Player player, ItemStack itemStack) {
        MerchantOffer tradeOffer = this.merchantInventory.getActiveOffer();
        this.profession.onTrade(tradeOffer);

        super.onTake(player, itemStack);
        this.tradeGUI.sendUpdate();  //todo needed?
    }
}
