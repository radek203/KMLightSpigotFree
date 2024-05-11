package pl.kwadratowamasakra.lightspigot.connection.packets.out.login;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

import java.security.PublicKey;

public class PacketEncryptionRequest extends PacketOut {

    private String hashedServerId;
    private PublicKey publicKey;
    private byte[] verifyToken;

    public PacketEncryptionRequest(final String hashedServerId, final PublicKey publicKey, final byte[] verifyToken) {
        this.hashedServerId = hashedServerId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }

    public PacketEncryptionRequest() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeString(hashedServerId);
        packetBuffer.writeBytesArray(publicKey.getEncoded());
        packetBuffer.writeBytesArray(verifyToken);
    }
}
