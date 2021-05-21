package net.sorenon.qcreepers.api;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sorenon.qcreepers.impl.QuantumCreepersMod;

import java.util.Random;

public class GrowingPhasedBlock implements PhasedBlockCallback {

    public GrowingPhasedBlock(int value) {
        this.duration = value;
        this.durationLeft = value;
    }

    public final int duration;
    public int durationLeft;

    @Override
    public boolean tick() {
        return durationLeft-- >= 0;
    }

    @Override
    public void render(BlockState blockState, BlockPos pos, WorldRenderContext context) {
        MatrixStack matrixStack = context.matrixStack();
        World world = context.world();
        matrixStack.push();

        matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        float scale = Math.min(1 - (durationLeft - context.tickDelta()) / duration, 1);
        if (pos.isWithinDistance(context.camera().getPos(), 1.0f)) {
//            scale = Math.max(scale * 0.1f, scale - 0.5f);
            blockState = Blocks.GLASS.getDefaultState();
        }

//        scale *= scale;
        matrixStack.scale(scale, scale, scale);
        matrixStack.translate(-0.5f, -0.5f, -0.5f);
        if (blockState.getRenderType() == BlockRenderType.MODEL) {
            BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
            blockRenderManager.getModelRenderer().render(world, blockRenderManager.getModel(blockState), blockState, pos, matrixStack, context.consumers().getBuffer(RenderLayers.getMovingBlockLayer(blockState)), false, new Random(), blockState.getRenderingSeed(pos), OverlayTexture.DEFAULT_UV);
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            BlockEntityRenderDispatcher.INSTANCE.render(blockEntity, context.tickDelta(), matrixStack, context.consumers());
        }

        matrixStack.pop();
    }

    @Override
    public PhasedBlockType type() {
        return QuantumCreepersMod.GROWING_TYPE;
    }

    public static class Type extends PhasedBlockType {

        @Override
        public void serialize(PacketByteBuf buf, PhasedBlockCallback object) {
            buf.writeInt(((GrowingPhasedBlock)object).durationLeft);
        }

        @Override
        public PhasedBlockCallback deserialize(PacketByteBuf buf) {
            return new GrowingPhasedBlock(buf.readInt());
        }
    }
}
