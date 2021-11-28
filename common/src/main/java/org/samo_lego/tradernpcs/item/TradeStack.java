package org.samo_lego.tradernpcs.item;

import net.minecraft.world.item.ItemStack;

public class TradeStack {
    private final ItemStack first;
    private final ItemStack second;

    public TradeStack(ItemStack payment) {
        this(payment, ItemStack.EMPTY);
    }

    public TradeStack(ItemStack payment1, ItemStack payment2) {
        this.first = payment1;
        this.second = payment2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof TradeStack trd) {
            return trd.getFirst().equals(this.first) && trd.getSecond().equals(this.second);
        }
        return false;
    }

    public ItemStack getFirst() {
        return this.first;
    }

    public ItemStack getSecond() {
        return this.second;
    }
}
