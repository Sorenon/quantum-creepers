package net.sorenon.qcreepers.mixin.client;

import net.sorenon.qcreepers.impl.QuantumCreepersModClient;
import net.sorenon.qcreepers.impl.WorldChunkExt;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {

    @Shadow @Final private World world;

    @Inject(at = @At("HEAD"), method = "getBlockState", cancellable = true)
    void getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (QuantumCreepersModClient.isRendering && world.isClient && ((WorldChunkExt)this).getPhaseBlocks().containsKey(pos)) {
            //TODO only mixin into ChunkRenderRegion instead?
            cir.setReturnValue(Blocks.VOID_AIR.getDefaultState());
        }
    }
}
