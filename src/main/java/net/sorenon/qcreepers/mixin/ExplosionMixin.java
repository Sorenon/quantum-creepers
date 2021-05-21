package net.sorenon.qcreepers.mixin;

import net.sorenon.qcreepers.impl.QuantumCreepersMod;
import net.sorenon.qcreepers.api.QuantumCreepersAPI;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Unique
    private static final QuantumCreepersAPI API = QuantumCreepersMod.API;

    @Redirect(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionBehavior;getBlastResistance(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)Ljava/util/Optional;"))
    Optional<Float> getBlastResistance(ExplosionBehavior explosionBehavior, Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        Optional<Float> blastResistance = explosionBehavior.getBlastResistance(explosion, world, pos, blockState, fluidState);
        if (blastResistance.isPresent() && API.isBlockPhased(world, pos)) {
            return Optional.empty();
        }
        return blastResistance;
    }
}
