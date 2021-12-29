package org.samo_lego.tradernpcs.gui;

import eu.pb4.sgui.api.gui.MerchantGui;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffer;
import org.samo_lego.tradernpcs.profession.TraderNPCProfession;


public class TradeGUI extends MerchantGui {
    private final TraderNPCProfession profession;

    /**
     * Constructs a new simple container gui for the supplied player.
     *
     * @param player                      the player to server this gui to
     */
    public TradeGUI(TraderNPCProfession profession, ServerPlayer player) {
        super(player, false);
        this.setTitle(profession.getNpc().getDisplayName());
        this.profession = profession;

        profession.getTrades().forEach(this::addTrade);
    }

    @Override
    public boolean onTrade(MerchantOffer offer) {
        return this.profession.onTrade(offer);
    }
}
