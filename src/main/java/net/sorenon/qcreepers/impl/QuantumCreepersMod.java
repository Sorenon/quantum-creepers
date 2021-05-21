package net.sorenon.qcreepers.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.util.registry.DefaultedRegistry;
import net.sorenon.qcreepers.api.GrowingPhasedBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.sorenon.qcreepers.api.InvisiblePhasedBlock;
import net.sorenon.qcreepers.api.PhasedBlockCallback;
import net.sorenon.qcreepers.api.PhasedBlockType;

/**
 * TODO
 * lie to vanilla players
 * rendering
 */
public class QuantumCreepersMod implements ModInitializer {

    public static Identifier QUANTUM_EXPLOSION = new Identifier("qcreepers:explosion");
    public static Identifier START_QUANTUM = new Identifier("qcreepers:start");
    public static Identifier END_QUANTUM = new Identifier("qcreepers:end");

    public static QuantumCreepersAPIImpl API = new QuantumCreepersAPIImpl();
    public static DefaultedRegistry<PhasedBlockType> REGISTRY;
    public static PhasedBlockType GROWING_TYPE;
    public static PhasedBlockType INVISIBLE_TYPE;

    static {
        REGISTRY = FabricRegistryBuilder.createDefaulted(
                PhasedBlockType.class,
                new Identifier("qcreepers", "phased_block_types"),
                new Identifier("qcreepers", "growing")
        ).buildAndRegister();
        GROWING_TYPE = Registry.register(REGISTRY, new Identifier("qcreepers", "growing"), new GrowingPhasedBlock.Type());
        INVISIBLE_TYPE = Registry.register(REGISTRY, new Identifier("qcreepers", "invisible"), new InvisiblePhasedBlock.Type());
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("qcreepers", "dbg"), new Item(new FabricItemSettings()) {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                if (context.getWorld() instanceof ServerWorld) {
                    ServerWorld world = (ServerWorld) context.getWorld();
                    BlockPos pos = context.getBlockPos();
                    API.phaseBlock(context.getWorld(), pos, new GrowingPhasedBlock(500));

//                    PacketByteBuf buf = PacketByteBufs.create();
//                    buf.writeBlockPos(pos);
//                    buf.writeInt(500);
//                    for (ServerPlayerEntity player : PlayerLookup.tracking(world, pos)) {
//                        ServerPlayNetworking.send(player, START_QUANTUM, buf);
//                    }
                }

                return ActionResult.SUCCESS;
            }
        });
    }
}