package org.samo_lego.tradernpcs.gui;

import eu.pb4.sgui.api.gui.MerchantGui;
import net.minecraft.server.level.ServerPlayer;
import org.samo_lego.tradernpcs.gui.slot.OutputSlot;
import org.samo_lego.tradernpcs.profession.TraderNPCProfession;


public class TradeGUI extends MerchantGui {

    /**
     * Constructs a new simple container gui for the supplied player.
     *
     * @param player                      the player to server this gui to
     */
    public TradeGUI(TraderNPCProfession profession, ServerPlayer player) {
        super(player, false);
        this.setTitle(profession.getNpc().getDisplayName());

        this.setSlotRedirect(2, new OutputSlot(player, profession, merchant, this.merchantInventory, this));

        profession.getTrades().forEach(this::addTrade);
    }
}
