package org.samo_lego.tradernpcs.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.samo_lego.tradernpcs.profession.SurvivalTraderProfession;

import java.util.ArrayList;

public class SurvivalStockGUI extends SimpleGui implements Container {
    private final ArrayList<ItemStack> items;
    private final SurvivalTraderProfession profession;

    /**
     * Constructs a new simple container gui for the supplied player.
     * @param profession the profession to use for the gui.
     * @param player the player to server this gui to.
     */
    public SurvivalStockGUI(SurvivalTraderProfession profession, ServerPlayer player) {
        super(MenuType.GENERIC_9x6, player, false);
        this.items = profession.getInventoryItems();
        this.profession = profession;

        // Redirect slots
        for (int i = 0; i < this.getSize(); i++) {
            this.setSlotRedirect(i, new Slot(this, i, 0, 0));
        }
    }

    @Override
    public int getContainerSize() {
        return 54;  // as the container is 9x6
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
        if (i < this.items.size()) {
            this.profession.setDirty();
            return this.items.remove(i);
        }
        return ItemStack.EMPTY;

    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        this.profession.setDirty();
        if (i < this.items.size()) {
            if (itemStack.isEmpty())
                this.items.remove(i);
            else
                this.items.set(i, itemStack);
        } else {
            this.items.add(itemStack);
        }
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
