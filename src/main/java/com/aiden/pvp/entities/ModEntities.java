package com.aiden.pvp.entities;

import com.aiden.pvp.PvP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<FireballEntity> FIREBALL;
    public static final EntityType<BridgeEggEntity> BRIDGE_EGG;
    public static final EntityType<FishingBobberEntity> FISHING_BOBBER;
    public static final EntityType<BedBugEntity> BED_BUG;
    public static final EntityType<DaggerEntity> DAGGER;

    public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> entityType) {
        return Registry.register(Registries.ENTITY_TYPE,
                Identifier.of(PvP.MOD_ID, id),
                entityType.build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(PvP.MOD_ID, id))));
    }

    public static void initialize() {
    }

    static {
        FIREBALL = register(
                "fireball",
                EntityType.Builder.<FireballEntity>create(FireballEntity::new, SpawnGroup.MISC)
                        .dimensions(0.75F, 0.75F)
                        .spawnBoxScale(1)
                        .maxTrackingRange(32).trackingTickInterval(5)
                        .vehicleAttachment(0.5F)
                        .nameTagAttachment(0.5F)
                        .alwaysUpdateVelocity(true)
                        .dropsNothing()
                        .eyeHeight(0.5F)
        );
        BRIDGE_EGG = register(
                "bridge_egg",
                EntityType.Builder.<BridgeEggEntity>create(BridgeEggEntity::new, SpawnGroup.MISC)
                        .dimensions(0.25F, 0.25F)
                        .spawnBoxScale(1)
                        .maxTrackingRange(32).trackingTickInterval(5)
                        .vehicleAttachment(0.01F)
                        .nameTagAttachment(0.01F)
                        .dropsNothing()
                        .alwaysUpdateVelocity(true)
                        .eyeHeight(0.15F)
        );
        FISHING_BOBBER = register(
                "fishing_bobber",
                EntityType.Builder.<FishingBobberEntity>create(FishingBobberEntity::new, SpawnGroup.MISC)
                        .dropsNothing()
                        .disableSaving()
                        .disableSummon()
                        .dimensions(0.25F, 0.25F)
                        .maxTrackingRange(4)
                        .trackingTickInterval(5)
        );
        BED_BUG = register(
                "bed_bug",
                EntityType.Builder.<BedBugEntity>create(BedBugEntity::new, SpawnGroup.MISC)
                        .dropsNothing()
                        .dimensions(0.25F, 0.25F)
                        .maxTrackingRange(4)
                        .trackingTickInterval(10)
        );
        DAGGER = register(
                "dagger",
                EntityType.Builder.create(DaggerEntity::new, SpawnGroup.MISC)
                        .dropsNothing()
                        .disableSaving()
                        .disableSummon()
                        .dimensions(0.5F, 0.5F)
                        .maxTrackingRange(4)
                        .trackingTickInterval(5)
        );
    }
}
