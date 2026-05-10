package pl.kwadratowamasakra.lightspigot.world;

import org.yaml.snakeyaml.Yaml;
import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutAbilities;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutChunkData;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutSpawnPosition;
import pl.kwadratowamasakra.lightspigot.connection.registry.PacketBuffer;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.Location;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

    private static final int CHUNK_SIZE = 16;
    private static final int WORLD_SECTION_Y = 4;
    private static final int WORLD_MIN_Y = WORLD_SECTION_Y * CHUNK_SIZE;
    private static final int LIGHT_BYTES = 2048;
    private static final int BIOME_BYTES = 256;
    private static final int VOID_BIOME = 127;
    private static final int FULL_CHUNK_MASK = 1 << WORLD_SECTION_Y;
    private static final String WORLD_FILE_NAME = "world.json";

    private final LightSpigotServer server;
    private final File worldFile = new File(WORLD_FILE_NAME);
    private volatile WorldSnapshot snapshot;

    public World(final LightSpigotServer server) {
        this.server = server;
        this.snapshot = createVoidSnapshot(0, 0);
        try {
            reload();
        } catch (final IOException | IllegalArgumentException ignored) {
        }
    }

    public final void reloadAndSendUpdates() throws IOException {
        reload();
        sendToPlayers();
    }

    public final void sendToPlayers() {
        for (final PlayerConnection connection : server.getConnectionManager().getConnections()) {
            sendToPlayer(connection);
        }
    }

    public final void sendToPlayer(final PlayerConnection connection) {
        List<ChunkSnapshot> chunks = getChunks();
        connection.sendPacket(new PacketPlayOutSpawnPosition(getSpawnBlockX(), getSpawnBlockY(), getSpawnBlockZ()));
        connection.sendPacket(new PacketPlayOutAbilities(0x02, chunks.isEmpty() ? 0.0F : 0.1F, 0.1F));
        for (final ChunkSnapshot chunk : chunks) {
            connection.sendPacket(new PacketPlayOutChunkData(chunk.chunkX(), chunk.chunkZ(), true, chunk.primaryBitMask(), chunk.chunkData()));
        }
    }

    public synchronized void reload() throws IOException {
        loadWorldFile();

        try (InputStream inputStream = worldFile.toURI().toURL().openStream()) {
            final Object loaded = new Yaml().load(inputStream);
            if (!(loaded instanceof Map<?, ?> rawMap)) {
                throw new IllegalArgumentException("Invalid world.json root object");
            }
            if (readString(rawMap, "levelType", "default").equals("void")) {
                snapshot = createVoidSnapshot(readInt(rawMap, "dimension", 0), readInt(rawMap, "difficulty", 0));
            } else {
                snapshot = createSnapshot(rawMap);
            }
        }
    }

    public Location getSpawnLocation() {
        final WorldSnapshot currentSnapshot = snapshot;
        return new Location(currentSnapshot.spawnLocation.getX(), currentSnapshot.spawnLocation.getY(), currentSnapshot.spawnLocation.getZ(), currentSnapshot.spawnLocation.getYaw(), currentSnapshot.spawnLocation.getPitch());
    }

    public int getSpawnBlockX() {
        return snapshot.spawnBlockX;
    }

    public int getSpawnBlockY() {
        return snapshot.spawnBlockY;
    }

    public int getSpawnBlockZ() {
        return snapshot.spawnBlockZ;
    }

    public int getDimension() {
        return snapshot.dimension;
    }

    public int getDifficulty() {
        return snapshot.difficulty;
    }

    public String getLevelType() {
        return snapshot.levelType;
    }

    public int getPrimaryBitMask() {
        return FULL_CHUNK_MASK;
    }

    public List<ChunkSnapshot> getChunks() {
        return snapshot.copyChunks();
    }

    private void loadWorldFile() throws IOException {
        if (worldFile.exists()) {
            return;
        }

        try (InputStream inputStream = World.class.getClassLoader().getResourceAsStream(WORLD_FILE_NAME)) {
            if (inputStream == null) {
                throw new FileNotFoundException(WORLD_FILE_NAME);
            }

            try (FileOutputStream outputStream = new FileOutputStream(worldFile)) {
                inputStream.transferTo(outputStream);
            }
        }
    }

    private WorldSnapshot createSnapshot(final Map<?, ?> rawMap) {
        final int spawnBlockX = readInt(rawMap, "spawnLocation", "x", 0);
        final int spawnBlockY = readInt(rawMap, "spawnLocation", "y", WORLD_MIN_Y);
        final int spawnBlockZ = readInt(rawMap, "spawnLocation", "z", 0);
        final float spawnYaw = (float) readDouble(rawMap, "spawnLocation", "yaw", 0.0D);
        final float spawnPitch = (float) readDouble(rawMap, "spawnLocation", "pitch", 0.0D);
        final Location spawnLocation = new Location(spawnBlockX + 0.5D, spawnBlockY + 1.0D, spawnBlockZ + 0.5D, spawnYaw, spawnPitch);

        final Map<ChunkCoordinate, int[][][]> chunkBlocks = new HashMap<>();
        chunkBlocks.put(new ChunkCoordinate(Math.floorDiv(spawnBlockX, CHUNK_SIZE), Math.floorDiv(spawnBlockZ, CHUNK_SIZE)), createEmptyChunkBlocks());

        final Object worldDataObject = rawMap.get("worldData");
        if (worldDataObject instanceof List<?> worldData) {
            for (final Object entryObject : worldData) {
                if (!(entryObject instanceof Map<?, ?> entry)) {
                    continue;
                }

                final int x = readInt(entry, "x", 0);
                final int z = readInt(entry, "z", 0);
                final int y = normalizeY(readInt(entry, "y", spawnBlockY));
                if (!isInsideChunk(y)) {
                    throw new IllegalArgumentException("Block Y coordinate is outside the supported section: y=" + y + " (supported: 0-15 local or 64-79 global)");
                }

                final int chunkX = Math.floorDiv(x, CHUNK_SIZE);
                final int chunkZ = Math.floorDiv(z, CHUNK_SIZE);
                final int localX = Math.floorMod(x, CHUNK_SIZE);
                final int localZ = Math.floorMod(z, CHUNK_SIZE);
                final ChunkCoordinate coordinate = new ChunkCoordinate(chunkX, chunkZ);
                final int[][][] blocks = chunkBlocks.computeIfAbsent(coordinate, ignored -> createEmptyChunkBlocks());
                blocks[y][localZ][localX] = resolveLegacyBlockValue(entry);
            }
        }

        return new WorldSnapshot(
                spawnLocation,
                spawnBlockX,
                spawnBlockY,
                spawnBlockZ,
                readInt(rawMap, "dimension", 0),
                readInt(rawMap, "difficulty", 0),
                readString(rawMap, "levelType", "default"),
                createChunkSnapshots(chunkBlocks)
        );
    }

    private WorldSnapshot createVoidSnapshot(final int dimension, final int difficulty) {
        return new WorldSnapshot(
                new Location(0.0, WORLD_MIN_Y + 1.0D, 0.0, 0.0f, 0.0f),
                0,
                WORLD_MIN_Y,
                0,
                dimension,
                difficulty,
                "default",
                List.of()
        );
    }

    private List<ChunkSnapshot> createChunkSnapshots(final Map<ChunkCoordinate, int[][][]> chunkBlocks) {
        return chunkBlocks.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<ChunkCoordinate, int[][][]> entry) -> entry.getKey().chunkX)
                        .thenComparingInt(entry -> entry.getKey().chunkZ))
                .map(entry -> new ChunkSnapshot(entry.getKey().chunkX, entry.getKey().chunkZ, FULL_CHUNK_MASK, createChunkData(entry.getValue())))
                .toList();
    }

    private int[][][] createEmptyChunkBlocks() {
        return new int[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    }

    private byte[] createChunkData(final int[][][] blocks) {
        final PacketBuffer buffer = new PacketBuffer();
        writeBlocks(buffer, blocks);
        writeBlockLight(buffer);
        writeSkyLight(buffer);
        writeBiomes(buffer);
        final byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        return data;
    }

    private void writeBlocks(final PacketBuffer buffer, final int[][][] blocks) {
        for (int y = 0; y < CHUNK_SIZE; y++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int x = 0; x < CHUNK_SIZE; x++) {
                    final int block = blocks[y][z][x];
                    buffer.writeByte(block & 0xFF);
                    buffer.writeByte(block >> 8 & 0xFF);
                }
            }
        }
    }

    private void writeBlockLight(final PacketBuffer buffer) {
        final byte[] blockLight = new byte[LIGHT_BYTES];
        Arrays.fill(blockLight, (byte) 0x00);
        buffer.writeBytes(blockLight);
    }

    private void writeSkyLight(final PacketBuffer buffer) {
        final byte[] skyLight = new byte[LIGHT_BYTES];
        Arrays.fill(skyLight, (byte) 0xFF);
        buffer.writeBytes(skyLight);
    }

    private void writeBiomes(final PacketBuffer buffer) {
        final byte[] biomes = new byte[BIOME_BYTES];
        Arrays.fill(biomes, (byte) VOID_BIOME);
        buffer.writeBytes(biomes);
    }

    private int resolveLegacyBlockValue(final Map<?, ?> entry) {
        final Object blockValue = entry.get("block");
        if (blockValue instanceof Number number) {
            return createLegacyBlockValue(number.intValue(), readInt(entry, "data", 0));
        }
        return createLegacyBlockValue(readInt(entry, "id", 0), readInt(entry, "data", 0));
    }

    private int createLegacyBlockValue(final int blockId, final int blockData) {
        return blockId << 4 | blockData & 0x0F;
    }

    private int normalizeY(final int y) {
        return y >= WORLD_MIN_Y ? y - WORLD_MIN_Y : y;
    }

    private boolean isInsideChunk(final int coordinate) {
        return coordinate >= 0 && coordinate < CHUNK_SIZE;
    }

    private int readInt(final Map<?, ?> map, final String key, final int defaultValue) {
        final Object value = map.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return defaultValue;
    }

    private int readInt(final Map<?, ?> map, final String sectionKey, final String key, final int defaultValue) {
        final Object value = map.get(sectionKey);
        if (value instanceof Map<?, ?> section) {
            return readInt(section, key, defaultValue);
        }
        return defaultValue;
    }

    private double readDouble(final Map<?, ?> map, final String sectionKey, final String key, final double defaultValue) {
        final Object value = map.get(sectionKey);
        if (value instanceof Map<?, ?> section) {
            final Object number = section.get(key);
            if (number instanceof Number numericValue) {
                return numericValue.doubleValue();
            }
        }
        return defaultValue;
    }

    private String readString(final Map<?, ?> map, final String key, final String defaultValue) {
        final Object value = map.get(key);
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return stringValue;
        }
        return defaultValue;
    }

    public record ChunkSnapshot(int chunkX, int chunkZ, int primaryBitMask, byte[] chunkData) {
        public ChunkSnapshot {
            chunkData = Arrays.copyOf(chunkData, chunkData.length);
        }
    }

    private record ChunkCoordinate(int chunkX, int chunkZ) {
    }

    private record WorldSnapshot(Location spawnLocation, int spawnBlockX, int spawnBlockY, int spawnBlockZ, int dimension, int difficulty, String levelType, List<ChunkSnapshot> chunks) {
        private List<ChunkSnapshot> copyChunks() {
            return chunks.stream().map(chunk -> new ChunkSnapshot(chunk.chunkX(), chunk.chunkZ(), chunk.primaryBitMask(), chunk.chunkData())).toList();
        }
    }
}
