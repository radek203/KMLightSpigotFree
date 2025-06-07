package pl.kwadratowamasakra.lightspigot.command;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;

public class ConsoleCommandSender implements CommandSender {

    private final LightSpigotServer server;

    public ConsoleCommandSender(final LightSpigotServer server) {
        this.server = server;
    }

    @Override
    public final String getNameString() {
        return "CONSOLE";
    }

    @Override
    public final boolean hasPermission(final String permission) {
        return true;
    }

    @Override
    public final void sendMessage(final String message) {
        server.getLogger().info(message);
    }
}
