package org.samo_lego.tradernpcs.gui.slot;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.samo_lego.tradernpcs.gui.TradeEditGUI;

public class TradeSlot extends Slot {
    public TradeSlot(TradeEditGUI container, int ix) {
        super(container, ix, 0, 0);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        int maxPages = ((TradeEditGUI) this.container).getMaxPages() + 1;
        return maxPages * 10 >= ((TradeEditGUI) this.container).getTrades().size();
    }
}
