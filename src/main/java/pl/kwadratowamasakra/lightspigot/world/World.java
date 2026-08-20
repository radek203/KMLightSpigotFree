package pl.kwadratowamasakra.lightspigot.world;

import org.yaml.snakeyaml.Yaml;
import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.Version;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.*;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.Location;
import pl.kwadratowamasakra.lightspigot.world.utils.BlockUtils;
import pl.kwadratowamasakra.lightspigot.world.utils.ChunkUtils;
import pl.kwadratowamasakra.lightspigot.world.utils.MapUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class World {

    public static final int CHUNK_SIZE = 16;
    public static final int WORLD_HEIGHT = 256;
    private static final int WORLD_SECTION_Y = 4;
    public static final int WORLD_MIN_Y = WORLD_SECTION_Y * CHUNK_SIZE;
    private static final int FULL_CHUNK_MASK = 1 << WORLD_SECTION_Y;
    private static final String WORLD_FILE_NAME = "world.json";

    private final LightSpigotServer server;
    private final WorldBounds bounds;
    private final File worldFile = new File(WORLD_FILE_NAME);
    private volatile WorldSnapshot snapshot;
    private volatile List<ChunkSnapshot> modernClientChunks;
    private Map<ChunkCoordinate, int[][][]> chunkBlocks = new HashMap<>();

    public World(final LightSpigotServer server) {
        this.server = server;
        this.bounds = WorldBounds.centered(
                server.getConfig().getDefaultChunksX(),
                server.getConfig().getDefaultChunksZ());
        this.snapshot = createVoidSnapshot(0, 0);
        this.modernClientChunks = createModernClientChunks();
        try {
            reload();
        } catch (final IOException | IllegalArgumentException exception) {
            server.getLogger().error("Failed to load world.json.", exception.getMessage());
        }
    }

    private static void appendBlocks(final List<String> output, final ChunkCoordinate coordinate, final int[][][] blocks) {
        for (int y = 0; y < WORLD_HEIGHT; y++) {
            for (int localZ = 0; localZ < CHUNK_SIZE; localZ++) {
                for (int localX = 0; localX < CHUNK_SIZE; localX++) {
                    final int blockValue = blocks[y][localZ][localX];
                    if (blockValue == 0) {
                        continue;
                    }
                    final int x = coordinate.chunkX() * CHUNK_SIZE + localX;
                    final int z = coordinate.chunkZ() * CHUNK_SIZE + localZ;
                    output.add("    {\"x\": " + x + ", \"y\": " + y + ", \"absoluteY\": true, \"z\": " + z
                            + ", \"block\": " + (blockValue >>> 4) + ", \"data\": " + (blockValue & 0x0F) + "}");
                }
            }
        }
    }

    private static String escapeJson(final String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
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
        List<ChunkSnapshot> chunks = getChunksForClient(connection.getVersion());
        connection.sendPacket(new PacketPlayOutSpawnPosition(getSpawnBlockX(), getSpawnBlockY(), getSpawnBlockZ()));
        connection.sendPacket(new PacketPlayOutAbilities(
                PacketPlayOutAbilities.FLYING | PacketPlayOutAbilities.ALLOW_FLYING,
                PacketPlayOutAbilities.DEFAULT_FLYING_SPEED,
                PacketPlayOutAbilities.DEFAULT_FIELD_OF_VIEW));
        final boolean batchedChunks = connection.getVersion().isEqualOrHigher(Version.V1_20_2);
        if (batchedChunks) connection.writePacket(new PacketPlayOutChunkBatchStart());
        for (final ChunkSnapshot chunk : chunks) {
            final PacketPlayOutChunkData chunkPacket = new PacketPlayOutChunkData(
                    chunk.chunkX(), chunk.chunkZ(), true, chunk.primaryBitMask(), chunk.chunkData(), this);
            if (batchedChunks) connection.writePacket(chunkPacket);
            else connection.sendPacket(chunkPacket);
            if (connection.getVersion().isInRange(Version.V1_14, Version.V1_17_1)) {
                connection.sendPacket(new PacketPlayOutUpdateLight(chunk.chunkX(), chunk.chunkZ(), chunk.primaryBitMask(), chunk.chunkData()));
            }
        }
        if (batchedChunks) connection.sendPacket(new PacketPlayOutChunkBatchFinish(chunks.size()));
    }

    public synchronized void reload() throws IOException {
        loadWorldFile();

        try (InputStream inputStream = worldFile.toURI().toURL().openStream()) {
            final Object loaded = new Yaml().load(inputStream);
            if (!(loaded instanceof Map<?, ?> rawMap)) {
                throw new IllegalArgumentException("Invalid world.json root object");
            }
            if (MapUtils.readString(rawMap, "levelType", "default").equals("void")) {
                chunkBlocks = new HashMap<>();
                snapshot = createVoidSnapshot(MapUtils.readInt(rawMap, "dimension", 0), MapUtils.readInt(rawMap, "difficulty", 0));
            } else {
                final LoadedWorld loadedWorld = createSnapshot(rawMap);
                chunkBlocks = loadedWorld.chunkBlocks();
                snapshot = loadedWorld.snapshot();
            }
            modernClientChunks = createModernClientChunks();
        }
    }

    public synchronized void save() throws IOException {
        final File absoluteWorldFile = worldFile.getAbsoluteFile();
        final File parent = absoluteWorldFile.getParentFile();
        if (parent != null) {
            Files.createDirectories(parent.toPath());
        }

        final File temporaryFile = File.createTempFile(WORLD_FILE_NAME, ".tmp", parent);
        try {
            Files.writeString(temporaryFile.toPath(), serializeSnapshot(), StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile.toPath(), absoluteWorldFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (final AtomicMoveNotSupportedException ignored) {
                Files.move(temporaryFile.toPath(), absoluteWorldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile.toPath());
        }
    }

    public synchronized boolean setBlock(final int x, final int y, final int z, final int blockId, final int blockData) {
        if (!bounds.containsBlock(x, z) || !BlockUtils.isInsideWorld(y)
                || blockId < 0 || blockId > 255 || blockData < 0 || blockData > 15) {
            return false;
        }

        final ChunkCoordinate coordinate = new ChunkCoordinate(Math.floorDiv(x, CHUNK_SIZE), Math.floorDiv(z, CHUNK_SIZE));
        final int[][][] blocks = chunkBlocks.get(coordinate);
        if (blocks == null) {
            return false;
        }
        blocks[y][Math.floorMod(z, CHUNK_SIZE)][Math.floorMod(x, CHUNK_SIZE)] =
                BlockUtils.createLegacyBlockValue(blockId, blockData);
        snapshot = copySnapshotWithUpdatedChunk(ChunkUtils.createChunkSnapshot(coordinate, blocks, FULL_CHUNK_MASK));
        modernClientChunks = createModernClientChunks();
        return true;
    }

    public synchronized int getBlockValue(final int x, final int y, final int z) {
        if (!bounds.containsBlock(x, z) || !BlockUtils.isInsideWorld(y)) {
            return 0;
        }
        final int[][][] blocks = chunkBlocks.get(new ChunkCoordinate(Math.floorDiv(x, CHUNK_SIZE), Math.floorDiv(z, CHUNK_SIZE)));
        if (blocks == null) {
            return 0;
        }
        return blocks[y][Math.floorMod(z, CHUNK_SIZE)][Math.floorMod(x, CHUNK_SIZE)];
    }

    public boolean hasSectionAt(final int blockX, final int blockY, final int blockZ) {
        if (!bounds.containsBlock(blockX, blockZ) || !BlockUtils.isInsideWorld(blockY)) {
            return false;
        }
        final ChunkSnapshot chunk = getChunkAt(blockX, blockZ);
        return chunk != null && (chunk.primaryBitMask() & 1 << blockY / CHUNK_SIZE) != 0;
    }

    public ChunkSnapshot getChunkAt(final int blockX, final int blockZ) {
        final int chunkX = Math.floorDiv(blockX, CHUNK_SIZE);
        final int chunkZ = Math.floorDiv(blockZ, CHUNK_SIZE);
        if (!bounds.containsChunk(chunkX, chunkZ)) {
            return null;
        }
        return snapshot.chunks().stream()
                .filter(chunk -> chunk.chunkX() == chunkX && chunk.chunkZ() == chunkZ)
                .findFirst()
                .map(chunk -> new ChunkSnapshot(chunk.chunkX(), chunk.chunkZ(), chunk.primaryBitMask(), chunk.chunkData()))
                .orElse(null);
    }

    public Location getSpawnLocation() {
        final WorldSnapshot currentSnapshot = snapshot;
        return new Location(currentSnapshot.spawnLocation().getX(), currentSnapshot.spawnLocation().getY(), currentSnapshot.spawnLocation().getZ(), currentSnapshot.spawnLocation().getYaw(), currentSnapshot.spawnLocation().getPitch());
    }

    public int getSpawnBlockX() {
        return snapshot.spawnBlockX();
    }

    public int getSpawnBlockY() {
        return snapshot.spawnBlockY();
    }

    public int getSpawnBlockZ() {
        return snapshot.spawnBlockZ();
    }

    public int getDimension() {
        return snapshot.dimension();
    }

    public int getDifficulty() {
        return snapshot.difficulty();
    }

    public String getLevelType() {
        return snapshot.levelType();
    }

    public List<ChunkSnapshot> getChunks() {
        return snapshot.copyChunks();
    }

    /**
     * Modern clients only compile a chunk section when the neighbouring chunks
     * needed by the renderer are loaded. Advertise the actual world radius and
     * fill its square with empty chunks instead of claiming the old hard-coded
     * distance of ten while sending only the configured world rectangle.
     */
    public int getViewDistance() {
        final int spawnChunkX = Math.floorDiv(getSpawnBlockX(), CHUNK_SIZE);
        final int spawnChunkZ = Math.floorDiv(getSpawnBlockZ(), CHUNK_SIZE);
        final int lastChunkX = bounds.firstChunkX() + bounds.chunksX() - 1;
        final int lastChunkZ = bounds.firstChunkZ() + bounds.chunksZ() - 1;
        return Math.max(2, Math.max(
                Math.max(Math.abs(bounds.firstChunkX() - spawnChunkX), Math.abs(lastChunkX - spawnChunkX)),
                Math.max(Math.abs(bounds.firstChunkZ() - spawnChunkZ), Math.abs(lastChunkZ - spawnChunkZ))));
    }

    private List<ChunkSnapshot> getChunksForClient(final Version version) {
        if (version.isLessThan(Version.V1_21)) {
            return snapshot.chunks();
        }
        return modernClientChunks;
    }

    private List<ChunkSnapshot> createModernClientChunks() {
        final Map<ChunkCoordinate, ChunkSnapshot> chunks = new HashMap<>();
        for (final ChunkSnapshot chunk : snapshot.chunks()) {
            chunks.put(new ChunkCoordinate(chunk.chunkX(), chunk.chunkZ()), chunk);
        }

        final int spawnChunkX = Math.floorDiv(getSpawnBlockX(), CHUNK_SIZE);
        final int spawnChunkZ = Math.floorDiv(getSpawnBlockZ(), CHUNK_SIZE);
        final int viewDistance = getViewDistance();
        for (int chunkX = spawnChunkX - viewDistance; chunkX <= spawnChunkX + viewDistance; chunkX++) {
            for (int chunkZ = spawnChunkZ - viewDistance; chunkZ <= spawnChunkZ + viewDistance; chunkZ++) {
                final ChunkCoordinate coordinate = new ChunkCoordinate(chunkX, chunkZ);
                chunks.computeIfAbsent(coordinate, ignored -> ChunkUtils.createChunkSnapshot(
                        coordinate, ChunkUtils.createEmptyChunkBlocks(), 0));
            }
        }
        return chunks.values().stream()
                .sorted(Comparator.comparingInt(ChunkSnapshot::chunkX).thenComparingInt(ChunkSnapshot::chunkZ))
                .toList();
    }

    public boolean isInsideWorldBounds(final int blockX, final int blockZ) {
        return bounds.containsBlock(blockX, blockZ);
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

    private LoadedWorld createSnapshot(final Map<?, ?> rawMap) {
        final int spawnBlockX = MapUtils.readInt(rawMap, "spawnLocation", "x", 0);
        final int spawnBlockY = MapUtils.readInt(rawMap, "spawnLocation", "y", WORLD_MIN_Y);
        final int spawnBlockZ = MapUtils.readInt(rawMap, "spawnLocation", "z", 0);
        final float spawnYaw = (float) MapUtils.readDouble(rawMap, "spawnLocation", "yaw", 0.0D);
        final float spawnPitch = (float) MapUtils.readDouble(rawMap, "spawnLocation", "pitch", 0.0D);
        final Location spawnLocation = new Location(spawnBlockX + 0.5D, spawnBlockY + 1.0D, spawnBlockZ + 0.5D, spawnYaw, spawnPitch);

        final Map<ChunkCoordinate, int[][][]> chunkBlocks = new HashMap<>();
        for (int offsetX = 0; offsetX < bounds.chunksX(); offsetX++) {
            for (int offsetZ = 0; offsetZ < bounds.chunksZ(); offsetZ++) {
                chunkBlocks.put(new ChunkCoordinate(
                        bounds.firstChunkX() + offsetX,
                        bounds.firstChunkZ() + offsetZ), ChunkUtils.createEmptyChunkBlocks());
            }
        }

        final Object worldDataObject = rawMap.get("worldData");
        if (worldDataObject instanceof List<?> worldData) {
            for (final Object entryObject : worldData) {
                if (!(entryObject instanceof Map<?, ?> entry)) {
                    continue;
                }

                final int x = MapUtils.readInt(entry, "x", 0);
                final int z = MapUtils.readInt(entry, "z", 0);
                if (!bounds.containsBlock(x, z)) {
                    continue;
                }
                final int configuredY = MapUtils.readInt(entry, "y", spawnBlockY);
                final int y = Boolean.TRUE.equals(entry.get("absoluteY")) ? configuredY : BlockUtils.normalizeY(configuredY);
                if (!BlockUtils.isInsideWorld(y)) {
                    throw new IllegalArgumentException("Block Y coordinate is outside the supported world height: y=" + y + " (supported: 0-255)");
                }

                final int chunkX = Math.floorDiv(x, CHUNK_SIZE);
                final int chunkZ = Math.floorDiv(z, CHUNK_SIZE);
                final int localX = Math.floorMod(x, CHUNK_SIZE);
                final int localZ = Math.floorMod(z, CHUNK_SIZE);
                final ChunkCoordinate coordinate = new ChunkCoordinate(chunkX, chunkZ);
                final int[][][] blocks = chunkBlocks.get(coordinate);
                blocks[y][localZ][localX] = BlockUtils.resolveLegacyBlockValue(entry);
            }
        }

        return new LoadedWorld(new WorldSnapshot(
                spawnLocation,
                spawnBlockX,
                spawnBlockY,
                spawnBlockZ,
                MapUtils.readInt(rawMap, "dimension", 0),
                MapUtils.readInt(rawMap, "difficulty", 0),
                MapUtils.readString(rawMap, "levelType", "default"),
                ChunkUtils.createChunkSnapshots(chunkBlocks, FULL_CHUNK_MASK)
        ), chunkBlocks);
    }

    private WorldSnapshot createVoidSnapshot(final int dimension, final int difficulty) {
        return new WorldSnapshot(
                new Location(0.0, WORLD_MIN_Y + 1.0D, 0.0, 0.0f, 0.0f),
                0,
                WORLD_MIN_Y,
                0,
                dimension,
                difficulty,
                "void",
                List.of()
        );
    }

    private WorldSnapshot copySnapshotWithUpdatedChunk(final ChunkSnapshot updatedChunk) {
        final WorldSnapshot current = snapshot;
        final List<ChunkSnapshot> chunks = new ArrayList<>(current.chunks().size() + 1);
        for (final ChunkSnapshot chunk : current.chunks()) {
            if (chunk.chunkX() != updatedChunk.chunkX() || chunk.chunkZ() != updatedChunk.chunkZ()) {
                chunks.add(chunk);
            }
        }
        chunks.add(updatedChunk);
        chunks.sort(Comparator.comparingInt(ChunkSnapshot::chunkX).thenComparingInt(ChunkSnapshot::chunkZ));
        return new WorldSnapshot(current.spawnLocation(), current.spawnBlockX(), current.spawnBlockY(), current.spawnBlockZ(),
                current.dimension(), current.difficulty(), current.levelType(), List.copyOf(chunks));
    }

    private String serializeSnapshot() {
        final WorldSnapshot current = snapshot;
        final List<String> blocks = new ArrayList<>();
        chunkBlocks.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparingInt(ChunkCoordinate::chunkX).thenComparingInt(ChunkCoordinate::chunkZ)))
                .forEach(entry -> appendBlocks(blocks, entry.getKey(), entry.getValue()));

        final StringBuilder json = new StringBuilder(256 + blocks.size() * 64);
        json.append("{\n")
                .append("  \"spawnLocation\": {\n")
                .append("    \"x\": ").append(current.spawnBlockX()).append(",\n")
                .append("    \"y\": ").append(current.spawnBlockY()).append(",\n")
                .append("    \"z\": ").append(current.spawnBlockZ()).append(",\n")
                .append("    \"yaw\": ").append(current.spawnLocation().getYaw()).append(",\n")
                .append("    \"pitch\": ").append(current.spawnLocation().getPitch()).append("\n")
                .append("  },\n")
                .append("  \"dimension\": ").append(current.dimension()).append(",\n")
                .append("  \"difficulty\": ").append(current.difficulty()).append(",\n")
                .append("  \"levelType\": \"").append(escapeJson(current.levelType())).append("\",\n")
                .append("  \"worldData\": [");
        if (!blocks.isEmpty()) {
            json.append('\n').append(String.join(",\n", blocks)).append('\n').append("  ");
        }
        return json.append("]\n}\n").toString();
    }

    private record LoadedWorld(WorldSnapshot snapshot, Map<ChunkCoordinate, int[][][]> chunkBlocks) {
    }
}
