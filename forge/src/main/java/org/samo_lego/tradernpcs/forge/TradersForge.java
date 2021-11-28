package org.samo_lego.tradernpcs.forge;

import dev.architectury.platform.forge.EventBuses;
import org.samo_lego.tradernpcs.Traders;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Traders.MOD_ID)
public class TradersForge {
    public TradersForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Traders.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Traders.init();
    }
}
