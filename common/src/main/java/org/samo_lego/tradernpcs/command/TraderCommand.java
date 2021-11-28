package org.samo_lego.tradernpcs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.samo_lego.taterzens.api.professions.TaterzenProfession;
import org.samo_lego.taterzens.commands.NpcCommand;
import org.samo_lego.tradernpcs.profession.TraderNPCProfession;

import static net.minecraft.commands.Commands.literal;
import static org.samo_lego.taterzens.commands.ProfessionCommand.PROFESSION_COMMAND_NODE;

public class TraderCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        LiteralCommandNode<CommandSourceStack> node = literal("trader")
                .then(
                        literal("edit")
                                .executes(TraderCommand::openGui)
                )
                .build();

        PROFESSION_COMMAND_NODE.addChild(node);
    }

    private static int openGui(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        return NpcCommand.selectedTaterzenExecutor(player, npc -> {
            TaterzenProfession profession = npc.getProfession(TraderNPCProfession.ID);
            if (profession instanceof TraderNPCProfession trader) {
                trader.openEditGui(player);
            } else {
                // Doesn't have profession assigned
            }
        });
    }
}
