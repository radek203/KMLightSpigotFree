package pl.kwadratowamasakra.lightspigot.world;

public record WorldBounds(int firstChunkX, int firstChunkZ, int chunksX, int chunksZ) {

    public WorldBounds {
        if (chunksX <= 0 || chunksZ <= 0) {
            throw new IllegalArgumentException("World chunk dimensions must be positive");
        }
    }

    public static WorldBounds centered(final int chunksX, final int chunksZ) {
        return new WorldBounds(-Math.floorDiv(chunksX, 2), -Math.floorDiv(chunksZ, 2), chunksX, chunksZ);
    }

    public boolean containsBlock(final int blockX, final int blockZ) {
        return containsChunk(Math.floorDiv(blockX, World.CHUNK_SIZE), Math.floorDiv(blockZ, World.CHUNK_SIZE));
    }

    public boolean containsChunk(final int chunkX, final int chunkZ) {
        return chunkX >= firstChunkX
                && (long) chunkX < (long) firstChunkX + chunksX
                && chunkZ >= firstChunkZ
                && (long) chunkZ < (long) firstChunkZ + chunksZ;
    }
}
