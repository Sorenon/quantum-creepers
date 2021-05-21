package net.sorenon.qcreepers.mixin;

import net.sorenon.qcreepers.impl.WorldChunkExt;
import net.sorenon.qcreepers.api.PhasedBlockCallback;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashMap;
import java.util.Map;

@Mixin(WorldChunk.class)
public class WorldChunkMixin implements WorldChunkExt {

    private final Map<BlockPos, PhasedBlockCallback> antiPhaseBlocks = new HashMap<>();

    @Override
    public Map<BlockPos, PhasedBlockCallback> getPhaseBlocks() {
        return antiPhaseBlocks;
    }
}
