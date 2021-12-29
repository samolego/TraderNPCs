package org.samo_lego.tradernpcs;

import org.samo_lego.taterzens.api.TaterzensAPI;
import org.samo_lego.tradernpcs.command.TraderCommand;
import org.samo_lego.tradernpcs.profession.SurvivalTraderProfession;
import org.samo_lego.tradernpcs.profession.TraderNPCProfession;

public class Traders {
    public static final String MOD_ID = "tradernpcs";

    public static void init() {
        TaterzensAPI.registerProfession(TraderNPCProfession.ID, new TraderNPCProfession());
        TaterzensAPI.registerProfession(SurvivalTraderProfession.ID, new SurvivalTraderProfession());
    }

    public static void registerCommands() {
        TraderCommand.register();
    }
}
