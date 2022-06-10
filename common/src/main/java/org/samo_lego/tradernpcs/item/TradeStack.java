package org.samo_lego.tradernpcs.item;

import net.minecraft.world.item.ItemStack;

public record TradeStack(ItemStack first, ItemStack second) {
    public TradeStack(ItemStack payment) {
        this(payment, ItemStack.EMPTY);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof TradeStack trd) {
            return trd.first().equals(this.first) && trd.second().equals(this.second);
        }
        return false;
    }
}
