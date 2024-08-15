package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

@AllArgsConstructor
@NoArgsConstructor
public class PacketKickDisconnect extends PacketOut {

    private String reason;

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", reason));
    }

}
