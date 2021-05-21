package net.sorenon.qcreepers.api;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface PhasedBlockCallback {

    boolean tick();

    void render(BlockState blockState, BlockPos pos, WorldRenderContext context);

    PhasedBlockType type();
}
