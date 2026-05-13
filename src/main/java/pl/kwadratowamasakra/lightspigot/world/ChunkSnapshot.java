package pl.kwadratowamasakra.lightspigot.world;

import java.util.Arrays;

public record ChunkSnapshot(int chunkX, int chunkZ, int primaryBitMask, byte[] chunkData) {
    public ChunkSnapshot {
        chunkData = Arrays.copyOf(chunkData, chunkData.length);
    }
}
