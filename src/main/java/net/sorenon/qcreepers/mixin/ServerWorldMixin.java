package net.sorenon.qcreepers.mixin;

import net.sorenon.qcreepers.impl.QuantumCreepersAPIImpl;
import net.sorenon.qcreepers.impl.QuantumCreepersConfig;
import net.sorenon.qcreepers.impl.QuantumCreepersMod;
import net.sorenon.qcreepers.api.GrowingPhasedBlock;
import net.sorenon.qcreepers.api.QuantumCreepersAPI;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    private static final QuantumCreepersAPI API = QuantumCreepersAPI.getInstance();

    @Inject(at = @At("HEAD"), method = "tickChunk")
    void tickChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        QuantumCreepersAPIImpl.tickWorldChunk(chunk);
    }

    @Inject(at = @At("HEAD"), method = "createExplosion", cancellable = true)
    void createExplosion(Entity entity, DamageSource damageSource, ExplosionBehavior explosionBehavior, double d, double e, double f, float g, boolean bl, Explosion.DestructionType destructionType, CallbackInfoReturnable<Explosion> cir) {
        if (destructionType != Explosion.DestructionType.NONE && QuantumCreepersConfig.isExplosionQuantum(entity)) {
            ServerWorld world = (ServerWorld) (Object) this;

            Explosion explosion = new Explosion(world, entity, damageSource, explosionBehavior, d, e, f, g, bl, Explosion.DestructionType.NONE);
            explosion.collectBlocksAndDamageEntities();
            explosion.affectWorld(false);

            explosion.getAffectedBlocks().removeIf(blockPos -> world.getBlockState(blockPos).isAir());

            for (BlockPos pos : explosion.getAffectedBlocks()) {
                API.phaseBlock(world, pos, new GrowingPhasedBlock(500));
            }

            for (ServerPlayerEntity player : PlayerLookup.around(world, new Vec3d(d, e, f), 4096.0D)) {
                ExplosionS2CPacket packet = new ExplosionS2CPacket(d, e, f, g, explosion.getAffectedBlocks(), explosion.getAffectedPlayers().get(player));
                PacketByteBuf buf = PacketByteBufs.create();
                try {
                    packet.write(buf);
                    ServerPlayNetworking.send(player, QuantumCreepersMod.QUANTUM_EXPLOSION, buf);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            cir.cancel();
        }
    }
}
