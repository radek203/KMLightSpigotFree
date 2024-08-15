package pl.kwadratowamasakra.lightspigot.connection.packets.out.login;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

import java.security.PublicKey;

@AllArgsConstructor
@NoArgsConstructor
public class PacketEncryptionRequest extends PacketOut {

    private String hashedServerId;
    private PublicKey publicKey;
    private byte[] verifyToken;

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeString(hashedServerId);
        packetBuffer.writeBytesArray(publicKey.getEncoded());
        packetBuffer.writeBytesArray(verifyToken);
    }
}
