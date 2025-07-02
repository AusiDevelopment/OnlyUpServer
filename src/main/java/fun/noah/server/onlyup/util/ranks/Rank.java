package fun.noah.server.onlyup.util.ranks;

import net.kyori.adventure.text.format.NamedTextColor;

public enum Rank {

    ADMIN("Admin", "§4Admin §8§l|", NamedTextColor.RED, 12),
    MODERATOR("Moderator", "§2Moderator", NamedTextColor.DARK_GREEN, 11),
    DEVELOPER("Dev", "§bDeveloper §8§l|", NamedTextColor.AQUA, 10),
    STREAMER("Streamer", "§dStreamer §8§l|", NamedTextColor.LIGHT_PURPLE, 4),
    CREATOR("Media", "§5Media §8§l|", NamedTextColor.DARK_PURPLE, 3),
    PREMIUM("Premium", "§6Premium §8§l|", NamedTextColor.GOLD, 2),
    SPIELER("Spieler", "§7[Spieler]", NamedTextColor.DARK_GRAY, 1);

    public final String name;
    public final String prefix;
    public final NamedTextColor color;
    public final int power;


    Rank(String name, String prefix, NamedTextColor color, int power) {
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.power = power;

        System.out.println("Initialted Rank System");
    }

}
