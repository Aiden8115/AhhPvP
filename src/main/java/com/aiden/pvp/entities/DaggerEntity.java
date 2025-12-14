package com.aiden.pvp.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DaggerEntity extends ProjectileEntity {
    public DaggerEntity(EntityType<? extends DaggerEntity> entityType, World world) {
        super(ModEntities.DAGGER, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity hitEntity =  entityHitResult.getEntity();
        World entityWorld = entityHitResult.getEntity().getEntityWorld();

        if (hitEntity instanceof LivingEntity livingEntity) {
            if (entityWorld instanceof ServerWorld serverWorld && getOwner() instanceof LivingEntity livingEntity1) {
                livingEntity.damage(
                        serverWorld,
                        this.getOwner().getDamageSources().mobProjectile(this, livingEntity1),
                        7.0F
                );
            }
        }
        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.discard();
    }

    @Override
    public void tick() {
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        Vec3d vec3d;
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d = hitResult.getPos();
        } else {
            vec3d = this.getEntityPos().add(this.getVelocity());
        }

        this.setPosition(vec3d);
        this.updateRotation();
        this.tickBlockCollision();
        super.tick();
        if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
            this.hitOrDeflect(hitResult);
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }
}
