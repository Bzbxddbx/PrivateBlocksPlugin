package Bzbxddbx.privateBlocksPlugin.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class PrivateBlocksCommand {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private PrivateBlocksCommand() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("pbhelp")
                .executes(context -> {
                    context.getSource().getSender().sendMessage(
                            MINI_MESSAGE.deserialize("<gold>====== [ Private Blocks ] ======</gold>"));
                    context.getSource().getSender().sendMessage(
                            MINI_MESSAGE.deserialize("<yellow>Доступные блоки для защиты территории:</yellow>"));

                    context.getSource().getSender().sendMessage(
                            MINI_MESSAGE.deserialize("<gray>- </gray><gold>Золотой блок: </gold><white>радиус 7х7x7</white>"));
                    context.getSource().getSender().sendMessage(
                            MINI_MESSAGE.deserialize("<gray>- </gray><aqua>Алмазный блок: </aqua><white>радиус 9х9x9</white>"));
                    context.getSource().getSender().sendMessage(
                            MINI_MESSAGE.deserialize("<gray>- </gray><green>Изумрудный блок: </green><white>радиус 15x15x15</white>"));
                    context.getSource().getSender().sendMessage(
                            MINI_MESSAGE.deserialize("<gray>- </gray><dark_gray>Незеритовый блок: </dark_gray><white>радиус 21x21x21</white>"));

                    context.getSource().getSender().sendMessage(
                            MINI_MESSAGE.deserialize("<gold>================================</gold>"));
                    return 1;
                });
    }
}