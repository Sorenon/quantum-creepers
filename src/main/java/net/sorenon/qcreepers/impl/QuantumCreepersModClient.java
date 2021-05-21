package net.sorenon.qcreepers.impl;

import net.fabricmc.api.ClientModInitializer;
import net.sorenon.qcreepers.api.PhasedBlockCallback;
import net.sorenon.qcreepers.api.GrowingPhasedBlock;
import net.sorenon.qcreepers.api.PhasedBlockType;
import net.sorenon.qcreepers.api.QuantumCreepersAPI;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * TODO
 * lie to vanilla players
 * rendering
 */
public class QuantumCreepersModClient implements ClientModInitializer {

    public static boolean isRendering = false;
    private static final QuantumCreepersAPI API = QuantumCreepersAPI.getInstance();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(QuantumCreepersMod.START_QUANTUM, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            int id = buf.readInt();
            PhasedBlockType type = API.getRegistry().get(id);
            if (type == null) {
                throw new RuntimeException("Received phased block type that doesn't exist:" + id);
            }
            PhasedBlockCallback callback = type.deserialize(buf);
            client.execute(() -> API.phaseBlock(handler.getWorld(), pos, callback));
        });

        ClientPlayNetworking.registerGlobalReceiver(QuantumCreepersMod.END_QUANTUM, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            client.execute(() -> API.unphaseBlock(handler.getWorld(), pos));
        });

        ClientPlayNetworking.registerGlobalReceiver(QuantumCreepersMod.QUANTUM_EXPLOSION, (client, handler, buf, responseSender) -> {
            ExplosionS2CPacket packet = new ExplosionS2CPacket();
            try {
                packet.read(buf);
                client.execute(() -> {
                    client.player.setVelocity(client.player.getVelocity().add(packet.getPlayerVelocityX(), packet.getPlayerVelocityY(), packet.getPlayerVelocityZ()));

//                    for (BlockPos pos : packet.getAffectedBlocks()) {
//                        API.phaseBlock(handler.getWorld(), pos, new GrowingPhasedBlock(500));
//                    }

                    client.world.playSound(packet.getX(), packet.getY(), packet.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (client.world.random.nextFloat() - client.world.random.nextFloat()) * 0.2F) * 0.7F, false);

                    if (!(packet.getRadius() < 2.0F)) {
                        client.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, packet.getX(), packet.getY(), packet.getZ(), 1.0D, 0.0D, 0.0D);
                    } else {
                        client.world.addParticle(ParticleTypes.EXPLOSION, packet.getX(), packet.getY(), packet.getZ(), 1.0D, 0.0D, 0.0D);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            AtomicReferenceArray<WorldChunk> chunks = world.getChunkManager().chunks.chunks;
            for (int i = 0; i < chunks.length(); i++) {
                WorldChunk chunk = chunks.get(i);
                if (chunk != null) {
                    QuantumCreepersAPIImpl.tickWorldChunk(chunk);
                }
            }
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            isRendering = false;

            ClientWorld world = context.world();
            MatrixStack matrixStack = context.matrixStack();
            matrixStack.push();
            Vec3d cam = context.camera().getPos();
            matrixStack.translate(-cam.x, -cam.y, -cam.z);

            Box chunkBox = new Box(0, 0, 0, 16, world.getHeight(), 16);

            AtomicReferenceArray<WorldChunk> chunks = world.getChunkManager().chunks.chunks;
            for (int i = 0; i < chunks.length(); i++) {
                WorldChunk chunk = chunks.get(i);
                if (chunk != null && context.frustum().isVisible(chunkBox.offset(chunk.getPos().x << 4, 0, chunk.getPos().z << 4))) {
                    for (Map.Entry<BlockPos, PhasedBlockCallback> entry : ((WorldChunkExt) chunk).getPhaseBlocks().entrySet()) {
                        BlockPos bpos = entry.getKey();
                        if (!bpos.isWithinDistance(cam, 128)) {
                            continue;
                        }

                        BlockState blockState = chunk.getBlockState(bpos);
                        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                            entry.getValue().render(blockState, bpos, context);
                        }
                    }
                }
            }


            matrixStack.pop();

            isRendering = true;
        });
    }
}
