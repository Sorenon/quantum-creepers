package net.sorenon.qcreepers.mixin;

import net.sorenon.qcreepers.impl.QuantumCreepersMod;
import net.sorenon.qcreepers.api.QuantumCreepersAPI;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Unique
    private static final QuantumCreepersAPI API = QuantumCreepersMod.API;

    //Used for collision
    @Inject(at = @At("RETURN"), method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", cancellable = true)
    private void overrideColShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityShapeContext && !cir.getReturnValue().isEmpty() && API.isBlockPhased(world, pos)) {
            cir.setReturnValue(VoxelShapes.empty());
        }
    }

    //Used for camera clipping
    @Inject(at = @At("HEAD"), method = "getVisualShape", cancellable = true)
    private void overrideOutlineShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityShapeContext && API.isBlockPhased(world, pos)) {
            cir.setReturnValue(VoxelShapes.empty());
        }
    }

    //Used for raycasting
    @Inject(at = @At("HEAD"), method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", cancellable = true)
    private void overrideVisShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityShapeContext && API.isBlockPhased(world, pos)) {
            cir.setReturnValue(VoxelShapes.empty());
        }
    }

    @Inject(at = @At("RETURN"), method = "canReplace", cancellable = true)
    private void overrideCanReplace(ItemPlacementContext context, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && API.isBlockPhased(context.getWorld(), context.getBlockPos())) {
            cir.setReturnValue(false);
        }
    }

    //TODO stop mobs jumping when trying to pathfind through phased blocks
    //maybe set path node type as DANGER_FIRE to encourage mobs to leave the area
    @Inject(at = @At("RETURN"), method = "canPathfindThrough", cancellable = true)
    private void overrideCanPathfind(BlockView world, BlockPos pos, NavigationType type, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && API.isBlockPhased(world, pos)) {
            cir.setReturnValue(true);
        }
    }
}
