package org.samo_lego.tradernpcs.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import org.samo_lego.taterzens.api.professions.TaterzenProfession;
import org.samo_lego.taterzens.commands.NpcCommand;
import org.samo_lego.tradernpcs.profession.TraderNPCProfession;

import static net.minecraft.commands.Commands.literal;
import static org.samo_lego.taterzens.commands.ProfessionCommand.PROFESSION_COMMAND_NODE;
import static org.samo_lego.taterzens.util.TextUtil.errorText;

public class TraderCommand {

    public static void register() {

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
                String id = TraderNPCProfession.ID.toString();
                // Doesn't have profession assigned
                player.sendSystemMessage(
                        errorText("taterzens.profession.lacking", id)
                                .withStyle(style -> style
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(id)))
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/npc edit professions add tradernpcs:trader"))
                                ));
            }
        });
    }
}
