package net.sorenon.qcreepers.impl;

import dev.inkwell.conrad.api.Config;
import dev.inkwell.conrad.api.value.ValueKey;
import dev.inkwell.conrad.api.value.data.SaveType;
import dev.inkwell.conrad.api.value.serialization.ConfigSerializer;
import dev.inkwell.conrad.api.value.serialization.FlatOwenSerializer;
import dev.inkwell.owen.OwenElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import org.jetbrains.annotations.NotNull;

public class QuantumCreepersConfig extends Config<OwenElement> {

//    public static final ValueKey<CreeperExplosions> CREEPER_EXPLOSIONS = value(() -> CreeperExplosions.ALL);
//    public static final ValueKey<TNTExplosions> TNT_EXPLOSIONS = value(() -> TNTExplosions.NONE);

    public static final ValueKey<Boolean> CREEPER_EXPLOSIONS = value(true);
    public static final ValueKey<Boolean> ALL_EXPLOSIONS = value(false);

    @Override
    public @NotNull ConfigSerializer<OwenElement> getSerializer() {
        return FlatOwenSerializer.INSTANCE;
    }

    @Override
    public @NotNull SaveType getSaveType() {
        return SaveType.LEVEL;
    }

//    public enum CreeperExplosions {
//        NONE(creeperEntity -> false),
//        NOT_CHARGED(creeperEntity -> !creeperEntity.shouldRenderOverlay()),
//        ALL(creeperEntity -> true);
//
//        public final Predicate<CreeperEntity> predicate;
//        CreeperExplosions(Predicate<CreeperEntity> predicate) {
//            this.predicate = predicate;
//        }
//    }

//    public enum TNTExplosions {
//        NONE(tntEntity -> false),
//        PLAYER_LIT(tntEntity -> tntEntity.getCausingEntity() instanceof PlayerEntity),
//        NOT_PLAYER_LIT(tntEntity -> !(tntEntity.getCausingEntity() instanceof PlayerEntity)),
//        ENTITY_LIT(tntEntity -> tntEntity.getCausingEntity() != null),
//        NOT_ENTITY_LIT(tntEntity -> tntEntity.getCausingEntity() == null),
//        ALL(tntEntity -> true);
//
//        public final Predicate<TntEntity> predicate;
//        TNTExplosions(Predicate<TntEntity> predicate) {
//            this.predicate = predicate;
//        }
//    }

    public static boolean isExplosionQuantum(Entity entity) {
       /* if (entity instanceof CreeperEntity) {
            return CREEPER_EXPLOSIONS.getValue().predicate.test((CreeperEntity) entity);
        }*/ /*else if (entity instanceof TntEntity) {
            return TNT_EXPLOSIONS.getValue().predicate.test((TntEntity) entity);
        }*/
        if (ALL_EXPLOSIONS.getValue()) {
            return true;
        }
        if (entity instanceof CreeperEntity) {
            return CREEPER_EXPLOSIONS.getValue();
        }
        return false;
    }
}
