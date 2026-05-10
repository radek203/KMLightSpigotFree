package pl.kwadratowamasakra.lightspigot.connection.packets.in.play;

import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.ConnectionState;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutAbilities;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketIn;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

public class PacketPlayInAbilities extends PacketIn {

    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flyingSpeed;
    private float walkingSpeed;

    @Override
    public final void read(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        final byte flags = packetBuffer.readByte();
        invulnerable = (flags & 0x01) != 0;
        flying = (flags & 0x02) != 0;
        allowFlying = (flags & 0x04) != 0;
        creativeMode = (flags & 0x08) != 0;
        if (connection.getVersion().isEqual(Version.V1_8)) {
            flyingSpeed = packetBuffer.readFloat();
            walkingSpeed = packetBuffer.readFloat();
        }
    }

    @Override
    public final void handle(final PlayerConnection connection, final LightSpigotServer server) {
        connection.verifyState(ConnectionState.PLAY);
        final int flags = PacketPlayOutAbilities.ALLOW_FLYING | (flying ? PacketPlayOutAbilities.FLYING : 0);
        connection.sendPacket(new PacketPlayOutAbilities(flags, PacketPlayOutAbilities.DEFAULT_FLYING_SPEED, PacketPlayOutAbilities.DEFAULT_FIELD_OF_VIEW));
    }

    @Override
    public final int getLimit() {
        return 15;
    }
}
