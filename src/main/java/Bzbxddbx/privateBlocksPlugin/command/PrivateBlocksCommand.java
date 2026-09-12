package Bzbxddbx.privateBlocksPlugin.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PrivateBlocksCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        sender.sendMessage(Component.text("====== [ Private Blocks ] ======", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Доступные блоки для защиты территории:", NamedTextColor.YELLOW));

        // Список блоков
        sender.sendMessage(Component.text("- ", NamedTextColor.GRAY)
                .append(Component.text("Золотой блок: ", NamedTextColor.YELLOW))
                .append(Component.text("радиус 7х7x7", NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("- ", NamedTextColor.GRAY)
                .append(Component.text("Алмазный блок: ", NamedTextColor.AQUA))
                .append(Component.text("радиус 9х9x9", NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("- ", NamedTextColor.GRAY)
                .append(Component.text("Изумрудный блок: ", NamedTextColor.GREEN))
                .append(Component.text("радиус 15x15x15", NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("- ", NamedTextColor.GRAY)
                .append(Component.text("Незеритовый блок: ", NamedTextColor.DARK_GRAY))
                .append(Component.text("радиус 21x21x21", NamedTextColor.GREEN)));

        sender.sendMessage(Component.text("================================", NamedTextColor.GOLD));

        return true;
    }
}
