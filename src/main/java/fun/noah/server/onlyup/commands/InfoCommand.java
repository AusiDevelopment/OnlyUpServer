package fun.noah.server.onlyup.commands;

import fun.noah.server.onlyup.OnlyUpServer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InfoCommand extends Command {



    public InfoCommand() {
        super("info");

        addSyntax((sender, ctx) -> {
           if (!(sender instanceof Player player)) return;
           player.sendMessage(Component.text(OnlyUpServer.getLined()));
           player.sendMessage(Component.text("§8"));
           player.sendMessage(Component.text(OnlyUpServer.getPrefix() + "§7Informationen über §f§l" + player.getUsername()));
           player.sendMessage(Component.text(OnlyUpServer.getPrefix() + "§aNerv nicht"));
        });
    }
}
