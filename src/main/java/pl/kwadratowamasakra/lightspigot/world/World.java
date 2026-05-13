package pl.kwadratowamasakra.lightspigot.world;

import org.yaml.snakeyaml.Yaml;
import pl.kwadratowamasakra.lightspigot.LightSpigotServer;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutAbilities;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutChunkData;
import pl.kwadratowamasakra.lightspigot.connection.packets.out.play.PacketPlayOutSpawnPosition;
import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;
import pl.kwadratowamasakra.lightspigot.event.Location;
import pl.kwadratowamasakra.lightspigot.world.utils.BlockUtils;
import pl.kwadratowamasakra.lightspigot.world.utils.ChunkUtils;
import pl.kwadratowamasakra.lightspigot.world.utils.MapUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World {

    public static final int CHUNK_SIZE = 16;
    private static final int WORLD_SECTION_Y = 4;
    public static final int WORLD_MIN_Y = WORLD_SECTION_Y * CHUNK_SIZE;
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
            if (MapUtils.readString(rawMap, "levelType", "default").equals("void")) {
                snapshot = createVoidSnapshot(MapUtils.readInt(rawMap, "dimension", 0), MapUtils.readInt(rawMap, "difficulty", 0));
            } else {
                snapshot = createSnapshot(rawMap);
            }
        }
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
        final int spawnBlockX = MapUtils.readInt(rawMap, "spawnLocation", "x", 0);
        final int spawnBlockY = MapUtils.readInt(rawMap, "spawnLocation", "y", WORLD_MIN_Y);
        final int spawnBlockZ = MapUtils.readInt(rawMap, "spawnLocation", "z", 0);
        final float spawnYaw = (float) MapUtils.readDouble(rawMap, "spawnLocation", "yaw", 0.0D);
        final float spawnPitch = (float) MapUtils.readDouble(rawMap, "spawnLocation", "pitch", 0.0D);
        final Location spawnLocation = new Location(spawnBlockX + 0.5D, spawnBlockY + 1.0D, spawnBlockZ + 0.5D, spawnYaw, spawnPitch);

        final Map<ChunkCoordinate, int[][][]> chunkBlocks = new HashMap<>();
        chunkBlocks.put(new ChunkCoordinate(Math.floorDiv(spawnBlockX, CHUNK_SIZE), Math.floorDiv(spawnBlockZ, CHUNK_SIZE)), ChunkUtils.createEmptyChunkBlocks());

        final Object worldDataObject = rawMap.get("worldData");
        if (worldDataObject instanceof List<?> worldData) {
            for (final Object entryObject : worldData) {
                if (!(entryObject instanceof Map<?, ?> entry)) {
                    continue;
                }

                final int x = MapUtils.readInt(entry, "x", 0);
                final int z = MapUtils.readInt(entry, "z", 0);
                final int y = BlockUtils.normalizeY(MapUtils.readInt(entry, "y", spawnBlockY));
                if (!BlockUtils.isInsideChunk(y)) {
                    throw new IllegalArgumentException("Block Y coordinate is outside the supported section: y=" + y + " (supported: 0-15 local or 64-79 global)");
                }

                final int chunkX = Math.floorDiv(x, CHUNK_SIZE);
                final int chunkZ = Math.floorDiv(z, CHUNK_SIZE);
                final int localX = Math.floorMod(x, CHUNK_SIZE);
                final int localZ = Math.floorMod(z, CHUNK_SIZE);
                final ChunkCoordinate coordinate = new ChunkCoordinate(chunkX, chunkZ);
                final int[][][] blocks = chunkBlocks.computeIfAbsent(coordinate, ignored -> ChunkUtils.createEmptyChunkBlocks());
                blocks[y][localZ][localX] = BlockUtils.resolveLegacyBlockValue(entry);
            }
        }

        for (int i = - server.getConfig().getDefaultChunksX() / 2; i < server.getConfig().getDefaultChunksX() / 2; i++) {
            for (int j = - server.getConfig().getDefaultChunksZ() / 2; j < server.getConfig().getDefaultChunksZ() / 2; j++) {
                final ChunkCoordinate coordinate = new ChunkCoordinate(i, j);
                chunkBlocks.computeIfAbsent(coordinate, ignored -> ChunkUtils.createEmptyChunkBlocks());
            }
        }

        return new WorldSnapshot(
                spawnLocation,
                spawnBlockX,
                spawnBlockY,
                spawnBlockZ,
                MapUtils.readInt(rawMap, "dimension", 0),
                MapUtils.readInt(rawMap, "difficulty", 0),
                MapUtils.readString(rawMap, "levelType", "default"),
                ChunkUtils.createChunkSnapshots(chunkBlocks, FULL_CHUNK_MASK)
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
}
