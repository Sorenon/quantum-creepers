package net.sorenon.qcreepers.api;

import net.minecraft.network.PacketByteBuf;

public abstract class PhasedBlockType {

    public abstract void serialize(PacketByteBuf buf, PhasedBlockCallback object);

    public abstract PhasedBlockCallback deserialize(PacketByteBuf buf);
}
