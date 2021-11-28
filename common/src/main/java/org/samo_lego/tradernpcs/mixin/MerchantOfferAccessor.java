package org.samo_lego.tradernpcs.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantOffer.class)
public interface MerchantOfferAccessor {
    @Mutable
    @Accessor("baseCostA")
    void setBaseCostA(ItemStack baseCostA);

    @Mutable
    @Accessor("costB")
    void setCostB(ItemStack costB);

    @Mutable
    @Accessor("result")
    void setResult(ItemStack result);
}
