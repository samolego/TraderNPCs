package org.samo_lego.tradernpcs.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.samo_lego.tradernpcs.profession.SurvivalTraderProfession;

public class TradeMenuGUI extends SimpleGui {

    /**
     * Selection - whether player wants to edit the trades, set stock, or view the trades.
     * @param profession the profession to edit.
     * @param player the player to construct the gui for.
     */
    public TradeMenuGUI(SurvivalTraderProfession profession, ServerPlayer player) {
        super(MenuType.HOPPER, player, false);

        // Edit trades
        ItemStack edit = new ItemStack(Items.EMERALD);
        edit.setHoverName(Component.literal("Edit trades"));
        GuiElement editTradesBtn = new GuiElement(edit, (index, type1, action) -> {
            this.close();
            new TradeEditGUI(profession, player, 0).open();
        });
        this.setSlot(0, editTradesBtn);

        // Set stock
        ItemStack stock = new ItemStack(Items.CHEST);
        stock.setHoverName(Component.literal("Set stock & collect earnings"));
        GuiElement setStockBtn = new GuiElement(stock, (index, type1, action) -> {
            this.close();
            new SurvivalStockGUI(profession, player).open();
        });
        this.setSlot(2, setStockBtn);

        // View trades
        ItemStack view = new ItemStack(Items.ENDER_EYE);
        view.setHoverName(Component.literal("View trades"));
        GuiElement viewTradesBtn = new GuiElement(view, (index, type1, action) -> {
            this.close();
            new TradeGUI(profession, player).open();
        });
        this.setSlot(4, viewTradesBtn);
    }
}
