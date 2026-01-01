package com.aiden.pvp.entities;

import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.Explosion;

import java.util.Optional;
import java.util.function.Function;

public class FireballEntity extends ThrownItemEntity {
    private float explosionPower = 12.0F;
    private final float explosionDamage = 2.0F;

    public FireballEntity(double x, double y, double z, World world, ItemStack stack) {
        super(ModEntities.FIREBALL, x, y, z, world, stack);
    }

    public FireballEntity(LivingEntity owner, World world, ItemStack stack) {
        super(ModEntities.FIREBALL, owner, world, stack);
    }

    public FireballEntity(EntityType<? extends FireballEntity> entityType, World world) {
        super(entityType, world);
    }

    public FireballEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.FIREBALL, x, y, z, world, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.FIREBALL;
    }

    @Override
    public void tick() {
        Vec3d velocity = this.getVelocity();
        super.tick();
        this.setVelocity(velocity);

        this.setOnFire(true);

        if (this.age > 400) this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        World var3 = this.getEntityWorld();
        if (var3 instanceof ServerWorld serverWorld) {
            explosionPower = (float) serverWorld.getGameRules().getValue(ModGameRules.PvpMod_FIREBALL_EXPLODE_POWER) / 10;
            this.explode(
                    0.5F, true, false, true, 0F, 1.0F,
                    Pool.<BlockParticleEffect>builder().build(), 0, 0, 0
            );
            this.explode(
                    12F, false, false, false, 0F, 0F,
                    Pool.<BlockParticleEffect>builder()
                            .add(new BlockParticleEffect(ParticleTypes.POOF, 0.5F, 1.0F))
                            .add(new BlockParticleEffect(ParticleTypes.SMOKE, 1.0F, 1.0F))
                            .build(), 0, 0, 0
            );

            this.explode(
                    this.explosionPower, false, true, false, (float) 1 / 600, 1.0F,
                    Pool.<BlockParticleEffect>builder().build(),
                    0, 0, 0
            );
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
            explosionPower = (float) serverWorld.getGameRules().getValue(ModGameRules.PvpMod_FIREBALL_EXPLODE_POWER) / 10;
            this.explode(
                    0.5F,
                    true, false, true,
                    0F, 1.0F,
                    Pool.<BlockParticleEffect>builder().build(),
                    0, 0, 0
            );
            this.explode(
                    12F,
                    false, false, false,
                    0F, 0F,
                    Pool.<BlockParticleEffect>builder()
                            .add(new BlockParticleEffect(ParticleTypes.POOF, 0.5F, 1.0F))
                            .add(new BlockParticleEffect(ParticleTypes.SMOKE, 1.0F, 1.0F))
                            .build(),
                    0, 0, 0
            );
            this.explode(
                    this.explosionPower,
                    false, true, false,
                    (float) 1 / 600, 1.0F,
                    Pool.<BlockParticleEffect>builder().build(),
                    0, 0, 0
            );
            if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                livingEntity.damage(serverWorld, getDamageSources().explosion(this, this.getOwner()), 2.0F);
            }
        }
        this.discard();
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
    }

    @Override
    protected void applyGravity() {
    }

    private void explode(float power, boolean destroyBlocks, boolean damageEntities, boolean createFire, float damageFactor, float kbModifierFactor, Pool<BlockParticleEffect> blockParticles, double dx, double dy, double dz) {
        if (getEntityWorld() instanceof ServerWorld serverWorld) {
            this.getEntityWorld().createExplosion(
                    this,
                    /* DamageSource: */serverWorld.getDamageSources().explosion(this, this.getOwner()),
                    new AdvancedExplosionBehavior(
                            destroyBlocks,
                            damageEntities,
                            Optional.of(explosionDamage),
                            Optional.empty()
                    ) {
                        @Override
                        public float calculateDamage(Explosion explosion, Entity entity, float amount) {
                            return super.calculateDamage(explosion, entity, amount) * damageFactor;
                        }

                        @Override
                        public float getKnockbackModifier(Entity entity) {
                            return super.getKnockbackModifier(entity) * kbModifierFactor;
                        }
                    },
                    this.getX() + dx,
                    this.getY() + dy,
                    this.getZ() + dz,
                    power,
                    createFire,
                    World.ExplosionSourceType.MOB,
                    ParticleTypes.EXPLOSION,
                    ParticleTypes.EXPLOSION_EMITTER,
                    blockParticles,
                    SoundEvents.ENTITY_GENERIC_EXPLODE
            );
        }
    }
}
