package pl.kwadratowamasakra.lightspigot.world;

import pl.kwadratowamasakra.lightspigot.event.Location;

import java.util.List;

public record WorldSnapshot(Location spawnLocation, int spawnBlockX, int spawnBlockY, int spawnBlockZ, int dimension,
                            int difficulty, String levelType, List<ChunkSnapshot> chunks) {
    public List<ChunkSnapshot> copyChunks() {
        return chunks.stream().map(chunk -> new ChunkSnapshot(chunk.chunkX(), chunk.chunkZ(), chunk.primaryBitMask(), chunk.chunkData())).toList();
    }
}
