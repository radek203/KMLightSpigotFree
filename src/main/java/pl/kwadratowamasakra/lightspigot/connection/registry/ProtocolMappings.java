package pl.kwadratowamasakra.lightspigot.connection.registry;

import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.utils.ItemStack;
import pl.kwadratowamasakra.lightspigot.world.utils.LegacyStairShape;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Protocol registry translations introduced by the 1.13 flattening.
 */
public final class ProtocolMappings {

    private static final int MAGIC = 0x4B4D5031; // KMP1
    private static final int LEGACY_BLOCK_VALUES = 4096;
    private static final Map<Version, Mapping> MAPPINGS = loadMappings();

    private ProtocolMappings() {
    }

    /**
     * Forces the complete 1.13+ mapping table to be loaded during server startup.
     */
    public static void preload() {
        // Accessing this method initializes MAPPINGS before the server accepts players.
    }

    public static int blockState(final Version version, final int blockId, final int blockData) {
        return blockState(version, blockId, blockData, LegacyStairShape.Shape.STRAIGHT);
    }

    public static int blockState(final Version version, final int blockId, final int blockData,
                                 final LegacyStairShape.Shape stairShape) {
        if (version.isLessThan(Version.V1_13)) {
            return blockId << 4 | blockData & 0x0F;
        }
        final Mapping mapping = mapping(version);
        final int legacyValue = blockId << 4 | blockData & 0x0F;
        final int straightState = legacyValue < mapping.blockStates.length ? mapping.blockStates[legacyValue] : 0;
        // Every flattened stair state orders shape before the trailing two-value
        // waterlogged property, so consecutive shapes are two state IDs apart.
        return straightState != 0 && LegacyStairShape.isStairs(legacyValue)
                ? straightState + stairShape.ordinal() * 2 : straightState;
    }

    public static int itemId(final Version version, final ItemStack itemStack) {
        if (version.isLessThan(Version.V1_13)) {
            return itemStack.getItemId();
        }
        final Mapping mapping = mapping(version);
        return mapping.items.getOrDefault(itemStack.getItemId() << 4 | itemStack.getData() & 0x0F,
                mapping.items.getOrDefault(itemStack.getItemId() << 4, 0));
    }

    public static ItemStack legacyItem(final Version version, final int protocolItemId, final int count) {
        if (version.isLessThan(Version.V1_13)) {
            return new ItemStack(protocolItemId, count, 0);
        }
        final int legacyValue = mapping(version).reverseItems.getOrDefault(protocolItemId, 0);
        return new ItemStack(legacyValue >>> 4, count, legacyValue & 0x0F);
    }

    private static Mapping mapping(final Version version) {
        final Mapping mapping = MAPPINGS.get(version);
        if (mapping == null) {
            throw new IllegalArgumentException("No registry mapping for " + version);
        }
        return mapping;
    }

    private static Map<Version, Mapping> loadMappings() {
        final Map<Version, Mapping> mappings = new EnumMap<>(Version.class);
        try (InputStream resource = ProtocolMappings.class.getResourceAsStream("/protocol/legacy-mappings.bin")) {
            if (resource == null) {
                throw new IllegalStateException("Missing /protocol/legacy-mappings.bin");
            }
            try (DataInputStream input = new DataInputStream(new BufferedInputStream(resource))) {
                if (input.readInt() != MAGIC) {
                    throw new IllegalStateException("Invalid protocol mapping resource");
                }
                final int versionCount = input.readUnsignedShort();
                for (int versionIndex = 0; versionIndex < versionCount; versionIndex++) {
                    final Version version = Version.of(input.readInt());
                    final int[] blockStates = new int[LEGACY_BLOCK_VALUES];
                    for (int i = 0; i < blockStates.length; i++) {
                        blockStates[i] = input.readInt();
                    }
                    final int itemCount = input.readUnsignedShort();
                    final Map<Integer, Integer> items = new HashMap<>(itemCount * 2);
                    final Map<Integer, Integer> reverseItems = new HashMap<>(itemCount * 2);
                    for (int i = 0; i < itemCount; i++) {
                        final int legacyValue = input.readInt();
                        final int protocolId = input.readInt();
                        items.put(legacyValue, protocolId);
                        reverseItems.putIfAbsent(protocolId, legacyValue);
                    }
                    mappings.put(version, new Mapping(blockStates, items, reverseItems));
                }
            }
        } catch (final IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
        return mappings;
    }

    private record Mapping(int[] blockStates, Map<Integer, Integer> items,
                           Map<Integer, Integer> reverseItems) {
    }
}
