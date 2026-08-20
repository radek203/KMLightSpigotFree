package pl.kwadratowamasakra.lightspigot.connection.packets.in.configuration;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.packets.in.login.LoginFlow;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketConfigurationInFinish extends PacketIn {
    @Override
    public void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
    }

    @Override
    public void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.CONFIGURATION);
        LoginFlow.enterPlay(connection, server);
    }

    @Override
    public int getLimit() {
        return 1;
    }
}
