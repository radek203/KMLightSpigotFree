package pl.kwadratowamasakra.lightspigot.command;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutBlockChange;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;

import java.io.IOException;

public class WorldReloadCommand extends Command {

    private final LightSpigotServer server;

    public WorldReloadCommand(final LightSpigotServer server) {
        super("worldreload");
        this.server = server;

        server.getCommandManager().addCommand(this);
    }

    @Override
    public void handle(final CommandSender sender, final String[] args) {
        if (sender instanceof PlayerConnection) {
            sender.sendMessage(ChatUtil.fixColor(server.getConfig().getCommandNoPermission()));
            return;
        }

        /*
        try {
            server.getWorld().reloadAndSendUpdates();
            sender.sendMessage(ChatUtil.fixColor("&aWorld reloaded from world.json and sent to online players."));
        } catch (final IOException | IllegalArgumentException exception) {
            sender.sendMessage(ChatUtil.fixColor("&cFailed to reload world.json: &f" + exception.getMessage()));
            server.getLogger().error("An error occurred while reloading world.json.", exception.getMessage());
        }

         */
        server.getConnectionManager().getConnections().forEach(player -> {
            PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(0, 1, 0, 57, 0);
            player.sendPacket(packet);
        });
    }
}
