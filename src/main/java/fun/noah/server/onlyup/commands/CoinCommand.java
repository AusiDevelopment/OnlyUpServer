package fun.noah.server.onlyup.commands;

import fun.noah.server.onlyup.OnlyUpServer;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public class CoinCommand extends Command {



    public CoinCommand() {
        super("coins");

        var action = new ArgumentLiteral("set");
        var add = new ArgumentLiteral("add");
        var target = new ArgumentString("player");
        var amount = new ArgumentInteger("amount");

        addSyntax((sender, ctx) -> {
            if (!(sender instanceof Player p)) return;
            int coins = p.getTag(Tag.Integer("coins")) != null ? p.getTag(Tag.Integer("coins")) : 0;
            p.sendMessage(OnlyUpServer.getPrefix() + "§eDeine Coins§8: §a" + coins);
        });

        addSyntax((sender, ctx) -> {
            if (!(sender instanceof Player player)) return;
            Player targetPlayer = getTarget(ctx.get(target));
            if (!OnlyUpServer.rankManager.hasPermission(player, "MOD_OR_HIGHER")) {
                player.sendMessage(Component.text(OnlyUpServer.getPrefix() + "§cKeine Berechtigung."));
                return;
            }
            if (targetPlayer == null) {
                sender.sendMessage("§cSpieler nicht gefunden.");
                return;
            }
            int amt = ctx.get(amount);
            targetPlayer.setTag(Tag.Integer("coins"), amt);
            sender.sendMessage("§aCoins gesetzt: " + amt);
        }, action, target, amount);

        addSyntax((sender, ctx) -> {
            Player targetPlayer = getTarget(ctx.get(target));
            if (targetPlayer == null) {
                sender.sendMessage("§cSpieler nicht gefunden.");
                return;
            }
            int amt = ctx.get(amount);
            int current = targetPlayer.getTag(Tag.Integer("coins")) != null ? targetPlayer.getTag(Tag.Integer("coins")) : 0;
            targetPlayer.setTag(Tag.Integer("coins"), current + amt);
            sender.sendMessage("§a" + amt + " Coins hinzugefügt.");
        }, add, target, amount);

    }

    private Player getTarget(String name) {
        return MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(name);
    }
}
