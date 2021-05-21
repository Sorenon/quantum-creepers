package net.sorenon.qcreepers.mixin;

import net.sorenon.qcreepers.impl.WorldChunkExt;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkLightProvider.class)
public abstract class ChunkLightProviderMixin {

    @Shadow
    @Final
    protected BlockPos.Mutable reusableBlockPos;

    @Shadow @Nullable protected abstract BlockView getChunk(int chunkX, int chunkZ);

    /**
     * TODO create mixins for phosphor and starlight support
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos$Mutable;set(J)Lnet/minecraft/util/math/BlockPos$Mutable;", shift = At.Shift.AFTER), method = "getStateForLighting", cancellable = true)
    void getStateForLighting(long pos, MutableInt mutableInt, CallbackInfoReturnable<BlockState> cir) {
        BlockView chunk = getChunk(reusableBlockPos.getX() >> 4, reusableBlockPos.getZ() >> 4);
        if (chunk instanceof WorldChunkExt && ((WorldChunkExt) chunk).getPhaseBlocks().containsKey(reusableBlockPos)) {
            if (mutableInt != null) {
                mutableInt.setValue(0);
            }
            cir.setReturnValue(Blocks.AIR.getDefaultState());
        }
    }
}
