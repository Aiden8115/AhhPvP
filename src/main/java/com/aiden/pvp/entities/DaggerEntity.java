package com.aiden.pvp.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

import java.util.ArrayList;
import java.util.List;

public class DaggerEntity extends ProjectileEntity {
    public List<LivingEntity> hitEntities = new ArrayList<>();

    public DaggerEntity(EntityType<? extends DaggerEntity> entityType, World world) {
        super(ModEntities.DAGGER, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity hitEntity =  entityHitResult.getEntity();
        World entityWorld = entityHitResult.getEntity().getEntityWorld();

        if (hitEntity instanceof LivingEntity hitLivingEntity) {
            if (entityWorld instanceof ServerWorld serverWorld && getOwner() instanceof LivingEntity attacker) {
                if (!hitEntities.contains(hitLivingEntity)) hitLivingEntity.damage(
                        serverWorld,
                        this.getOwner().getDamageSources().mobProjectile(this, attacker),
                        7.0F
                );
                hitEntities.add(hitLivingEntity);
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        BlockState hitBlockState = getEntityWorld().getBlockState(blockHitResult.getBlockPos());
        if (
                hitBlockState.isOf(Blocks.GLASS)
                        || hitBlockState.isOf(Blocks.WHITE_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.LIGHT_GRAY_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.GRAY_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.BLACK_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.BROWN_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.RED_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.ORANGE_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.YELLOW_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.LIME_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.GREEN_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.CYAN_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.LIGHT_BLUE_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.BLUE_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.PURPLE_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.MAGENTA_STAINED_GLASS)
                        || hitBlockState.isOf(Blocks.PINK_STAINED_GLASS)
                || hitBlockState.isOf(Blocks.GLASS_PANE)
                        || hitBlockState.isOf(Blocks.WHITE_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.GRAY_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.BLACK_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.BROWN_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.RED_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.ORANGE_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.YELLOW_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.LIME_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.GREEN_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.CYAN_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.BLUE_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.PURPLE_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.MAGENTA_STAINED_GLASS_PANE)
                        || hitBlockState.isOf(Blocks.PINK_STAINED_GLASS_PANE)
        ) {
            getEntityWorld().breakBlock(blockHitResult.getBlockPos(), true, this.getOwner());
            return;
        }
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
