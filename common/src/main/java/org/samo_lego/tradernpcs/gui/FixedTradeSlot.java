package org.samo_lego.tradernpcs.gui;

import eu.pb4.sgui.virtual.merchant.VirtualMerchant;
import eu.pb4.sgui.virtual.merchant.VirtualTradeOutputSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.samo_lego.tradernpcs.profession.TraderNPCProfession;

public class FixedTradeSlot extends VirtualTradeOutputSlot {
    private final TraderNPCProfession profession;
    private final MerchantContainer merchantInventory;

    public FixedTradeSlot(ServerPlayer player, TraderNPCProfession profession, VirtualMerchant merchant, MerchantContainer merchantInventory) {
        super(player, merchant, merchantInventory, 2, 0, 0);
        this.profession = profession;
        this.merchantInventory = merchantInventory;
    }

    @Override
    public void onTake(Player player, ItemStack itemStack) {
        MerchantOffer tradeOffer = this.merchantInventory.getActiveOffer();
        this.profession.onTrade(tradeOffer);
        super.onTake(player, itemStack);
    }
}
