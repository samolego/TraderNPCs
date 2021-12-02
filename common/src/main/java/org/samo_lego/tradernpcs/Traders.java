package org.samo_lego.tradernpcs;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.samo_lego.taterzens.api.TaterzensAPI;
import org.samo_lego.tradernpcs.command.TraderCommand;
import org.samo_lego.tradernpcs.profession.TraderNPCProfession;

public class Traders {
    public static final String MOD_ID = "tradernpcs";

    public static void init() {
        TaterzensAPI.registerProfession(TraderNPCProfession.ID, new TraderNPCProfession());
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
        TraderCommand.register(dispatcher);
    }
}
