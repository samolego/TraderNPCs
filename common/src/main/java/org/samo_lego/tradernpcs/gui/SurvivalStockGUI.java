package org.samo_lego.tradernpcs.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.samo_lego.tradernpcs.profession.SurvivalTraderProfession;

import java.util.ArrayList;

public class SurvivalStockGUI extends SimpleGui implements Container {
    private final ArrayList<ItemStack> items;

    /**
     * Constructs a new simple container gui for the supplied player.
     * @param profession the profession to use for the gui.
     * @param player the player to server this gui to.
     */
    public SurvivalStockGUI(SurvivalTraderProfession profession, ServerPlayer player) {
        super(MenuType.GENERIC_9x6, player, true);
        this.items = profession.getInventoryItems();
    }

    @Override
    public int getContainerSize() {
        return 54;
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        if (i < this.items.size())
            return this.items.get(i);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return this.removeItemNoUpdate(i);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        if (i < this.items.size())
            return this.items.remove(i);
        return ItemStack.EMPTY;

    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        if (i < this.items.size())
            this.items.set(i, itemStack);
        this.items.add(itemStack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
}
