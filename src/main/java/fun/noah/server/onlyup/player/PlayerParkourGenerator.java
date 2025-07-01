package fun.noah.server.onlyup.player;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;

import java.util.Random;

public class PlayerParkourGenerator {

    private static final Random random = new Random();

    public static void generateNextPlatform(Player player, Pos lastPos) {
        int dx = random.nextInt(3) - 1;
        int dz = random.nextInt(3) - 1;
        int dy = 3;

        if (dx == 0 && dz == 0) dx = 1;

        Pos newBlock = lastPos.add(dx, dy, dz);

        Instance instance = player.getInstance();
        if (instance != null) {
            instance.setBlock(newBlock, Block.STONE);
        }

        player.setTag(Tag.String("onlyup:lastBlock"), newBlock.toString());
    }

}
