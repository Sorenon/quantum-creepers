package net.sorenon.qcreepers.mixin;

import net.sorenon.qcreepers.impl.WorldChunkExt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.BlockLightStorage;
import net.minecraft.world.chunk.light.ChunkBlockLightProvider;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkBlockLightProvider.class)
public abstract class ChunkBlockLightProviderMixin extends ChunkLightProvider<BlockLightStorage.Data, BlockLightStorage> {

    @Shadow @Final private BlockPos.Mutable mutablePos;

    public ChunkBlockLightProviderMixin(ChunkProvider chunkProvider, LightType type, BlockLightStorage lightStorage) {
        super(chunkProvider, type, lightStorage);
    }

    //Performance could be improved with a redirect
    @Inject(at = @At(value = "RETURN"), method = "getLightSourceLuminance", cancellable = true)
    void getLightSourceLuminance(long pos, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() != 0) {
            mutablePos.set(pos);
            BlockView chunk = this.chunkProvider.getChunk(mutablePos.getX() >> 4, mutablePos.getZ() >> 4);
            if (chunk instanceof WorldChunkExt && ((WorldChunkExt) chunk).getPhaseBlocks().containsKey(mutablePos)) {
                cir.setReturnValue(0);
            }
        }
    }
}
