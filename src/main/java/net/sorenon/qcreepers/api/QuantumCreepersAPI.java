package net.sorenon.qcreepers.api;

import net.minecraft.util.registry.Registry;
import net.sorenon.qcreepers.impl.QuantumCreepersMod;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

public interface QuantumCreepersAPI {

    static QuantumCreepersAPI getInstance() {
        return QuantumCreepersMod.API;
    }

    void phaseBlock(World world, BlockPos pos, PhasedBlockCallback callback);

    void unphaseBlock(WorldChunk chunk, BlockPos pos);

    boolean isBlockPhased(WorldChunk chunk, BlockPos pos);

    Registry<PhasedBlockType> getRegistry();

    default void unphaseBlock(World world, BlockPos pos) {
        Chunk chunk = world.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        if (chunk instanceof WorldChunk) {
            unphaseBlock((WorldChunk) chunk, pos);
        }
    }

    default boolean isBlockPhased(BlockView view, BlockPos pos) {
        if (view instanceof World) {
            return isBlockPhased((World) view, pos);
        }
        return false;
    }

    default boolean isBlockPhased(World world, BlockPos pos) {
        Chunk chunk = world.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        if (chunk instanceof WorldChunk) {
            return isBlockPhased((WorldChunk) chunk, pos);
        }
        return false;
    }
}
