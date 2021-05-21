package net.sorenon.qcreepers.impl;

import net.sorenon.qcreepers.api.PhasedBlockCallback;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public interface WorldChunkExt {
    Map<BlockPos, PhasedBlockCallback> getPhaseBlocks();
}
