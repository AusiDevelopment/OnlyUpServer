package fun.noah.server.onlyup.manager;

import fun.noah.server.onlyup.util.ranks.Rank;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

import java.util.HashMap;
import java.util.Map;

public class RankManager {

    private static final Tag<String> RANK_TAG = Tag.String("onlyup:rank");
    private static final Map<String, Rank> CACHE = new HashMap<>();

    public RankManager(){
        System.out.println("Init");
    }

    public void setRank(Player player, Rank rank) {
        player.setTag(RANK_TAG, rank.name());
        CACHE.put(player.getUuid().toString(), rank);
    }

    public Rank getRank(Player player) {
        if (CACHE.containsKey(player.getUuid().toString()))
            return CACHE.get(player.getUuid().toString());

        String stored = player.getTag(RANK_TAG);
        if (stored == null) return Rank.SPIELER;

        try {
            Rank rank = Rank.valueOf(stored.toUpperCase());
            CACHE.put(player.getUuid().toString(), rank);
            return rank;
        } catch (IllegalArgumentException e) {
            return Rank.SPIELER;
        }
    }

    public boolean hasPermission(Player player, String required) {
        Rank rank = getRank(player);
        return switch (required.toUpperCase()) {
            case "ADMIN_ONLY" -> rank == Rank.ADMIN;
            case "MOD_OR_HIGHER" -> rank.power >= Rank.MODERATOR.power;
            case "PREMIUM_FEATURE" -> rank.power >= Rank.PREMIUM.power;
            default -> rank.power >= 0; // fallback: alle
        };
    }

}
