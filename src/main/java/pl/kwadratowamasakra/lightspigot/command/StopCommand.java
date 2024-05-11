package pl.kwadratowamasakra.lightspigot.command;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;

public class StopCommand extends Command {

    private final LightSpigotServer server;

    public StopCommand(final LightSpigotServer server) {
        super("stop");
        this.server = server;

        server.getCommandManager().addCommand(this);
    }

    @Override
    public final void handle(final CommandSender sender, final String[] args) {
        if (sender instanceof PlayerConnection) {
            sender.sendMessage(ChatUtil.fixColor(server.getConfig().getCommandNoPermission()));
            return;
        }
        server.stop();
    }

}
