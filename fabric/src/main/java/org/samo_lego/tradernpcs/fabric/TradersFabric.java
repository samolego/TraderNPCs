package org.samo_lego.tradernpcs.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.samo_lego.tradernpcs.Traders;

public class TradersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Traders.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> Traders.registerCommands());
    }
}
