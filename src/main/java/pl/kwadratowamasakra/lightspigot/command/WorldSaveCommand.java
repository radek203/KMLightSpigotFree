package pl.kwadratowamasakra.lightspigot.command;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;

import java.io.IOException;
import java.util.List;

public class WorldSaveCommand extends Command {

    private final LightSpigotServer server;

    public WorldSaveCommand(final LightSpigotServer server) {
        super("worldsave", List.of("saveworld"));
        this.server = server;
        server.getCommandManager().addCommand(this);
    }

    @Override
    public void handle(final CommandSender sender, final String[] args) {
        if (sender instanceof PlayerConnection player && !player.isOp()) {
            sender.sendMessage(ChatUtil.fixColor(server.getConfig().getCommandNoPermission()));
            return;
        }

        try {
            server.getWorld().save();
            sender.sendMessage(ChatUtil.fixColor("&aWorld saved to world.json."));
        } catch (final IOException exception) {
            sender.sendMessage(ChatUtil.fixColor("&cFailed to save world.json: &f" + exception.getMessage()));
            server.getLogger().error("An error occurred while saving world.json.", exception.getMessage());
        }
    }
}
