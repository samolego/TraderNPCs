package org.samo_lego.tradernpcs.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.samo_lego.tradernpcs.Traders;

@Mod(Traders.MOD_ID)
public class TradersForge {
    public TradersForge() {
        MinecraftForge.EVENT_BUS.register(this);
        Traders.init();
    }

    @SubscribeEvent()
    public void registerCommands(RegisterCommandsEvent event) {
        Traders.registerCommands();
    }
}
