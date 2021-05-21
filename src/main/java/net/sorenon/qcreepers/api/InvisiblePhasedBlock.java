package net.sorenon.qcreepers.api;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.sorenon.qcreepers.impl.QuantumCreepersMod;

public class InvisiblePhasedBlock implements PhasedBlockCallback {

    public InvisiblePhasedBlock(int value) {
        this.durationLeft = value;
    }

    public int durationLeft;

    @Override
    public boolean tick() {
        return durationLeft-- >= 0;
    }

    @Override
    public void render(BlockState blockState, BlockPos pos, WorldRenderContext context) {

    }

    @Override
    public PhasedBlockType type() {
        return QuantumCreepersMod.INVISIBLE_TYPE;
    }

    public static class Type extends PhasedBlockType {

        private static final InvisiblePhasedBlock CLIENT_BLOCK = new InvisiblePhasedBlock(0);

        @Override
        public void serialize(PacketByteBuf buf, PhasedBlockCallback object) {

        }

        @Override
        public PhasedBlockCallback deserialize(PacketByteBuf buf) {
            return CLIENT_BLOCK;
        }
    }
}
