package pl.kwadratowamasakra.lightspigot.connection.registry;

import java.lang.reflect.Constructor;

/**
 * The RegisteredPacket class is used to store specific {@link Packet} class and its constructor.
 * This is done to avoid getting/creating a new one all the time.
 */
class RegisteredPacket {

    private final Class<? extends Packet> packetClass;
    private final Constructor<? extends Packet> constructor;

    /**
     * Constructs a new RegisteredPacket with the specified packet class.
     * It also gets the constructor of the packet class and stores it.
     *
     * @param packetClass The class of the packet.
     * @throws NoSuchMethodException If the class does not have a no-argument constructor.
     */
    RegisteredPacket(final Class<? extends Packet> packetClass) throws NoSuchMethodException {
        this.packetClass = packetClass;
        constructor = packetClass.getConstructor();
    }

    /**
     * @return The class of the packet.
     */
    final Class<? extends Packet> getPacketClass() {
        return packetClass;
    }

    /**
     * @return The constructor of the packet class.
     */
    final Constructor<? extends Packet> getConstructor() {
        return constructor;
    }
}
