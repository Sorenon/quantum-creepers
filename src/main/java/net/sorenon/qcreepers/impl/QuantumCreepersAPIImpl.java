package net.sorenon.qcreepers.impl;

import net.minecraft.util.registry.Registry;
import net.sorenon.qcreepers.api.PhasedBlockCallback;
import net.sorenon.qcreepers.api.PhasedBlockType;
import net.sorenon.qcreepers.api.QuantumCreepersAPI;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class QuantumCreepersAPIImpl implements QuantumCreepersAPI {

    @Override
    public void phaseBlock(World world, BlockPos pos, PhasedBlockCallback callback) {
        Chunk chunk = world.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        if (chunk instanceof WorldChunk) {
            if (((WorldChunkExt) chunk).getPhaseBlocks().put(pos, callback) == null) {
                world.getProfiler().push("queueCheckLight");
                world.getChunkManager().getLightingProvider().checkBlock(pos);
                world.getProfiler().pop();
                if (world.isClient) {
                    MinecraftClient.getInstance().worldRenderer.updateBlock(null, pos, null, null, 0);
                }
            }

            if (world instanceof ServerWorld) {
                Collection<ServerPlayerEntity> tracking = PlayerLookup.tracking((ServerWorld) world, pos);
                if (!tracking.isEmpty()) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(pos);
                    buf.writeInt(getRegistry().getRawId(callback.type()));
                    callback.type().serialize(buf, callback);

                    for (ServerPlayerEntity player : tracking) {
                        ServerPlayNetworking.send(player, QuantumCreepersMod.START_QUANTUM, buf);
                    }
                }
            }
        }
    }

    @Override
    public void unphaseBlock(WorldChunk chunk, BlockPos pos) {
        if (((WorldChunkExt) chunk).getPhaseBlocks().remove(pos) != null) {
            onUnphase(chunk.getWorld(), pos);
        }
    }

    @Override
    public boolean isBlockPhased(WorldChunk chunk, BlockPos pos) {
        return ((WorldChunkExt) chunk).getPhaseBlocks().containsKey(pos);
    }

    @Override
    public Registry<PhasedBlockType> getRegistry() {
        return QuantumCreepersMod.REGISTRY;
    }

    public static void tickWorldChunk(WorldChunk chunk) {
        Map<BlockPos, PhasedBlockCallback> phaseBlocks = ((WorldChunkExt) chunk).getPhaseBlocks();

        final Iterator<Map.Entry<BlockPos, PhasedBlockCallback>> each = phaseBlocks.entrySet().iterator();
        while (each.hasNext()) {
            Map.Entry<BlockPos, PhasedBlockCallback> entry = each.next();
            if (!entry.getValue().tick() && !chunk.getWorld().isClient) {
                each.remove();
                onUnphase(chunk.getWorld(), entry.getKey());
            }
        }
    }

    public static void onUnphase(World world, BlockPos pos) {
        world.getProfiler().push("queueCheckLight");
        world.getChunkManager().getLightingProvider().checkBlock(pos);
        world.getProfiler().pop();

        if (world.isClient) {
            MinecraftClient.getInstance().worldRenderer.updateBlock(null, pos, null, null, 0);
        } else {
            PacketByteBuf buf = PacketByteBufs.create().writeBlockPos(pos);
            for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) world, pos)) {
                ServerPlayNetworking.send(player, QuantumCreepersMod.END_QUANTUM, buf);
            }
        }
    }
}
