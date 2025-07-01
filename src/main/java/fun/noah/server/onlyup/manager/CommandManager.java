package fun.noah.server.onlyup.manager;

import fun.noah.server.onlyup.commands.CoinCommand;
import fun.noah.server.onlyup.commands.InfoCommand;
import net.minestom.server.MinecraftServer;

public class CommandManager {

    public CommandManager(){

    }

    public void registerAll(){
        var cm = MinecraftServer.getCommandManager();

        cm.register(new CoinCommand());
        cm.register(new InfoCommand());

        System.out.println("Registered Commands trough CommandManager");
    }

}
