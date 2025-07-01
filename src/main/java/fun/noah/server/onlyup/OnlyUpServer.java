package fun.noah.server.onlyup;

import fun.noah.server.onlyup.database.Database;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.ping.ResponseData;

import java.util.Random;

public class OnlyUpServer {

    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();
        MojangAuth.init();
        Database.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instance = instanceManager.createInstanceContainer();
        instance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.AIR));

        instance.setChunkSupplier(LightingChunk::new);

        generateParkour(instance);

        GlobalEventHandler events = MinecraftServer.getGlobalEventHandler();

        events.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            Player player = event.getPlayer();
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(0, 41, 0));
        });

        events.addListener(PlayerMoveEvent.class, event -> {
           Player player = event.getPlayer();

           if (player.getPosition().y() < 30) {
               player.teleport(player.getRespawnPoint());
           }
        });

        events.addListener(ServerListPingEvent.class, event -> {
            ResponseData data = new ResponseData();
            data.setDescription(Component.text("§c§lOnly §a§lUp §rServer"));
            data.setMaxPlayer(250);

            event.setResponseData(data);
        });

        events.addListener(PlayerDisconnectEvent.class, event -> {
            Player player = event.getPlayer();
            double maxY = player.getPosition().y();
            long totalTime = System.currentTimeMillis() - player.getLastKeepAlive();
            Database.savePlayer(player.getUuid().toString(), player.getUsername(), maxY, totalTime);
        });

        server.start("0.0.0.0", 25565);

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

}
