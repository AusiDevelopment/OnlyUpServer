package fun.noah.server.onlyup.commands;

import fun.noah.server.onlyup.OnlyUpServer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public class InfoCommand extends Command {



    public InfoCommand() {
        super("info");

        addSyntax((sender, ctx) -> {
           if (!(sender instanceof Player player)) return;
            int coins = player.getTag(Tag.Integer("coins")) != null ? player.getTag(Tag.Integer("coins")) : 0;
           player.sendMessage(Component.text(OnlyUpServer.getLined()));
           player.sendMessage(Component.text("§8"));
           player.sendMessage(Component.text(OnlyUpServer.getPrefix() + "§7Informationen über §f§l" + player.getUsername()));
           player.sendMessage(Component.text(OnlyUpServer.getPrefix() + "§7Dein Rang§8: §f§l" + OnlyUpServer.rankManager.getRank(player)));
           player.sendMessage(Component.text(OnlyUpServer.getPrefix() + "§7Deine Coins§8: §f§l" + coins));
        });
    }
}
