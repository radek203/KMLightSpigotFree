package pl.kwadratowamasakra.lightspigot.connection.packets.out.status;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketOut;
import pl.kwadratowamasakra.lightspigot.utils.ChatUtil;

@AllArgsConstructor
@NoArgsConstructor
public class PacketServerInfo extends PacketOut {

    private static final String TEMPLATE = "{ \"version\": { \"name\": \"%s\", \"protocol\": %d }, \"players\": { \"max\": %d, \"online\": %d, \"sample\": [] }, \"description\": %s }";

    private String version;
    private int protocol;
    private int maxPlayers;
    private int onlinePlayers;
    private String description;

    @Override
    public final void write(final PacketBuffer packetBuffer) {
        packetBuffer.writeString(String.format(TEMPLATE, version, protocol, maxPlayers, onlinePlayers, ChatUtil.fixColor("{\"text\": \"" + description + "\"}")));
    }

}
