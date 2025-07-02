package fun.noah.server.onlyup;

import fun.noah.server.onlyup.database.Database;
import fun.noah.server.onlyup.manager.CommandManager;
import fun.noah.server.onlyup.manager.RankManager;
import fun.noah.server.onlyup.shop.PerkShop;
import fun.noah.server.onlyup.util.ranks.Rank;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.PacketRegistry;
import net.minestom.server.network.packet.server.play.PlayerListHeaderAndFooterPacket;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.tag.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OnlyUpServer {

    public static String getPrefix() {
        return "§c§lOnly§a§lUp §8§l⚊ §r§7";
    }

    public static String getLined() {
        return "§8§m⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊⚊§r";
    }

    public static RankManager rankManager;

    private static Map<String, Integer> coins = new HashMap<>();

    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();
        MojangAuth.init();
        /*BungeeCordProxy.enable();
        BungeeCordProxy.setBungeeGuardTokens(Set.of("tokens", "here"));*/
        Database.init();

        CommandManager commandManager = new CommandManager();
        commandManager.registerAll();

        // Ranks
        rankManager = new RankManager();


        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instance = instanceManager.createInstanceContainer();
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.AIR));

        instance.setChunkSupplier(LightingChunk::new);

        //generateParkour(instance);
        generateSpawnPlatform(instance);

        GlobalEventHandler events = MinecraftServer.getGlobalEventHandler();

        events.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();
            event.setSpawningInstance(instance);

            player.setRespawnPoint(new Pos(0, 41, 0));

            String uuid = player.getUuid().toString();

            ResultSet rs = Database.loadPlayer2(uuid);
            Pos spawnPos = new Pos(0, 43, 0);
            int coins = 100;

            try {
                if (rs != null && rs.next()) {
                    coins = rs.getInt("coins");
                    double x = rs.getDouble("last_x");
                    double y = rs.getDouble("last_y");
                    double z = rs.getDouble("last_z");
                    spawnPos = new Pos(x, y, z);
                }
            } catch (SQLException e) {
                throw new RuntimeException("SQL Error", e);
            }

            player.setTag(Tag.Integer("coins"), coins);
            player.setTag(Tag.String("onlyup:lastBlock"), spawnPos.toString());

            Rank rank = rankManager.getRank(player);

            rankManager.setRank(player, Rank.CREATOR);

            player.setCustomName(Component.text(rank.prefix + " ").append(Component.text(player.getUsername()).color(rank.color)));
            player.setCustomNameVisible(true);
            player.setDisplayName(Component.text(rank.prefix + " " + player.getUsername()).color(rank.color));

            instance.setBlock(spawnPos, Block.STONE);

            List<String> perks = Database.loadPerks(uuid);
            for (String perk : perks) {
                if (perk.equalsIgnoreCase("JUMP_BOOST")) {
                    PotionEffect potionEffect = PotionEffect.JUMP_BOOST;
                    int duration = 15 * 20;
                    byte amplifier = 1;
                    Potion potion = new Potion(potionEffect, amplifier, duration);
                    player.addEffect(potion);
                }
            }

            ItemStack perkShopItem = ItemStack.builder(Material.BLAZE_POWDER)
                            .customName(Component.text("§6Perk Shop"))
                                    .build();

            player.getInventory().setItemStack(4, perkShopItem);
            //coins.put(player.getUuid().toString(), 100);




            //player.sendPlayerListHeaderAndFooter(Component.text("§c§lOnly §a§lUP §8§l| §6§lMinestom §e§lServer"), Component.text(""));


        });

        events.addListener(PlayerChatEvent.class, event -> {
           Player player = event.getPlayer();
           Rank rank = rankManager.getRank(player);
           String message = event.getRawMessage();
           event.setFormattedMessage(Component.text(rank.prefix + " ").append(
                   Component.text(player.getUsername() + "§8: §7" + message)
           ));
        });

        events.addListener(AsyncPlayerPreLoginEvent.class, event -> {
            Player player = event.getConnection().getPlayer();

            GameProfile gp = new GameProfile(UUID.fromString("e9013c2f-da01-425f-a48b-516f55e94386"), "LxraWrld");
            event.setGameProfile(gp);

        });

        events.addListener(PlayerSkinInitEvent.class, event -> {
            PlayerSkin skinFromUsername = PlayerSkin.fromUsername("LxraWrld");
            event.setSkin(skinFromUsername);
        });

        events.addListener(PlayerMoveEvent.class, event -> {
           Player player = event.getPlayer();

           if (player.getPosition().y() < 30) {
               player.teleport(player.getRespawnPoint());
           }
        });

        events.addListener(ServerListPingEvent.class, event -> {
            ResponseData data = new ResponseData();
            data.setDescription(Component.text("§c§lOnly §a§lUP §8§l| §6§lMinestom §e§lServer\n§7by §4§lAus§f§liDevel§4§lopment"));
            data.setMaxPlayer(250);

            event.setResponseData(data);
        });

        events.addListener(PlayerUseItemEvent.class, event -> {
           Player player = event.getPlayer();
           if (event.getItemStack().material() == Material.BLAZE_POWDER) {
               PerkShop.openPerkShop(player, coins);
           }
        });

        events.addListener(InventoryClickEvent.class, event -> {
            Player player = event.getPlayer();
            ItemStack itemStack = event.getClickedItem();
            if (itemStack.material() == Material.BLAZE_POWDER) {
                PerkShop.openPerkShop(player, coins);
            }
        });

        events.addListener(PlayerDisconnectEvent.class, event -> {
            Player player = event.getPlayer();
            String uuid = player.getUuid().toString();
            String name = player.getUsername();

            int coins = player.getTag(Tag.Integer("coins")) != null ? player.getTag(Tag.Integer("coins")) : 0;
            Pos pos = parsePosFromString(player.getTag(Tag.String("onlyup:lastBlock")));
            if (pos == null) pos = new Pos(0, 43, 0);

            Database.savePlayer(uuid, name, coins, pos.x(), pos.y(), pos.z());
        });

        events.addListener(InventoryPreClickEvent.class, event -> {
            ItemStack item = event.getInventory().getItemStack(event.getSlot());
            if (item.material() == Material.BLAZE_POWDER) {
                event.setCancelled(true);
            }
        });

        events.addListener(PlayerBlockBreakEvent.class, event -> {
            event.setCancelled(true);
        });



        server.start("0.0.0.0", 25565);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            var players =  MinecraftServer.getConnectionManager().getOnlinePlayers();
            for (var player : players) {
                player.kick(Component.text(getLined() + "§8\n" + OnlyUpServer.getPrefix() + " §cServer wurde gestoppt\n§4\n§7Bitte reconnecte in §f§l10-15 §r§7Sekunden erneut§8!\n§8" + getLined()));
            }
            System.out.println("[STOP] >> Server gestoppt");
        }));

    }

    private static Pos parsePosFromString(String s) {
        s = s.replaceAll("[^0-9.,-]", "");
        String[] parts = s.split(",");
        if (parts.length < 3) return new Pos(0, 43, 0);

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);

        return new Pos(x, y, z);
    }

    private static void generateParkour(Instance instance) {
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            int x = rand.nextInt(3) - 1;
            int z = rand.nextInt(3) - 1;
            int y = 42 + i * 3;
            instance.setBlock(x, y, z, Block.STONE);
        }
    }

    private static void generateSpawnPlatform(Instance instance) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                instance.setBlock(x, 40, z, Block.GRASS_BLOCK);
            }
        }
        instance.setBlock(5, 40, 0, Block.STONE);
        instance.setBlock(-5, 40, 0, Block.STONE);
        instance.setBlock(0, 40, 5, Block.STONE);
        instance.setBlock(0, 40, -5, Block.STONE);
    }

}
