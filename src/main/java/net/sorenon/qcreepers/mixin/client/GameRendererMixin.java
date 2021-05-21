package net.sorenon.qcreepers.mixin.client;

import net.sorenon.qcreepers.impl.QuantumCreepersModClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    void renderStart(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        //TODO move these to better places
        //TODO add these to sodium
        QuantumCreepersModClient.isRendering = true;//START
    }

    @Inject(at = @At("RETURN"), method = "render", cancellable = true)
    void renderEnd(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        QuantumCreepersModClient.isRendering = false;//BEFORE ENTITIES
    }
}
