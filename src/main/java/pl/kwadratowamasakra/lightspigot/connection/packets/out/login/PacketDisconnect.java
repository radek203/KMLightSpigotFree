package pl.kwadratowamasakra.lightspigot.connection.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

@AllArgsConstructor
@NoArgsConstructor
public class PacketDisconnect extends PacketOut {

    private String reason;

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", reason));
    }
}
