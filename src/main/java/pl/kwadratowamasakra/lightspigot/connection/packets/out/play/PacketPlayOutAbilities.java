package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayOutAbilities extends PacketOut {

    /*
    private boolean isInvulnerable;
    private boolean isFlying;
    private boolean canFly;
    private boolean canInstantlyBuild;
     */
    private int flags;
    private float flyingSpeed;
    private float fieldOfView;

    @Override
    public final void write(final PlayerConnection connection, final PacketBuffer packetBuffer) {
        /*
        byte flags = 0;
        if (this.a()) {
            flags = (byte)(flags | 1);
        }

        if (this.b()) {
            flags = (byte)(flags | 2);
        }

        if (this.c()) {
            flags = (byte)(flags | 4);
        }

        if (this.d()) {
            flags = (byte)(flags | 8);
        }
         */

        packetBuffer.writeByte(flags);
        packetBuffer.writeFloat(flyingSpeed);
        packetBuffer.writeFloat(fieldOfView);
    }
}
