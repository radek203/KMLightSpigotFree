package pl.kwadratowamasakra.lightspigot.connection.packets.out.play;

import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;

public class PacketPlayerListHeaderFooter extends PacketOut {

    private String header;
    private String footer;

    public PacketPlayerListHeaderFooter(final String header, final String footer) {
        this.header = header;
        this.footer = footer;
    }

    public PacketPlayerListHeaderFooter() {

    }

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", header));
        packetBuffer.writeString(String.format("{\"text\": \"%s\"}", footer));
    }
}
