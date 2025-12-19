package com.aiden.pvp.entities;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.items.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BridgeEggEntity extends ThrownItemEntity {
    private final Random random = new Random();
    public BridgeEggEntity(EntityType<? extends BridgeEggEntity> entityType, World world) {
        super(entityType, world);
        this.setGlowing(true);
    }

    public BridgeEggEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.BRIDGE_EGG, owner, world, stack);
    }

    public BridgeEggEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ModEntities.BRIDGE_EGG, x, y, z, world, stack);
    }



    @Override
    protected Item getDefaultItem() {
        return ModItems.BRIDGE_EGG;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult) hitResult);
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            this.onBlockHit((BlockHitResult) hitResult);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
    }
    @Override
    protected void onBlockCollision(BlockState state) {
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
    }

    @Override
    public void tick() {
        // 先判断世界是否为null（避免空指针）
        if (this.getEntityWorld() == null) {
            PvP.LOGGER.warn("BlockEggEntity实体所在世界为null！");
            return;
        }

        // 1. 处理实体生命周期（如是否已移除）
        if (this.isRemoved()) return;

        // 3. 应用重力（复刻ProjectileEntity的重力逻辑，而非EggEntity的）
        this.applyGravity();

        // 4. 更新位置（根据速度移动）
        this.setPos(
                this.getX() + getVelocity().x,
                this.getY() + getVelocity().y,
                this.getZ() + getVelocity().z
        );

        if (!this.getEntityWorld().isClient()) { // 仅服务器端执行
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }

        // 5. 处理碰撞检测（手动调用碰撞逻辑，替代父类的处理）
        HitResult hitResult = this.raycast(getVelocity().length(), 0.0f, false);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult); // 触发自定义碰撞处理
        }

        this.spawnContinuousParticles();
        this.placeBlocks();

        // 6. 同步客户端和服务器的位置（必要的网络同步）
        this.updatePositionAndAngles(
                this.getX(),
                this.getY(),
                this.getZ(),
                this.getYaw(),   // 实体当前偏航角
                this.getPitch()  // 实体当前俯仰角
        );

        if (this.age > 30) this.discard();
    }

    // 持续生成粒子的核心方法
    private void spawnContinuousParticles() {
        if (getEntityWorld().isClient()) {
            // 只在客户端生成粒子（优化性能，服务器无需处理）
            Vec3d pos = this.getEntityPos(); // 获取实体当前位置

            // 每次生成1种粒子，10-15个
            spawnParticles(pos, random.nextInt(6) + 10, ParticleTypes.SOUL_FIRE_FLAME);
            spawnParticles(pos, random.nextInt(6) + 10, ParticleTypes.FLAME);
        }
    }

    private void spawnParticles(Vec3d pos, int count, ParticleEffect type) {
        for (int i = 0; i < count; i++) {
            // 计算随机偏移量（围绕实体分布）
            double offsetX = (random.nextDouble(2.0) - 1.0) * this.getWidth() * 2;
            double offsetY = (random.nextDouble(2.0) - 1.0) * this.getHeight() * 2;
            double offsetZ = (random.nextDouble(2.0) - 1.0) * this.getWidth() * 2;

            // 生成粒子
            getEntityWorld().addParticleClient(
                    type,
                    pos.x + offsetX,   // 粒子X坐标
                    pos.y + offsetY,   // 粒子Y坐标
                    pos.z + offsetZ,   // 粒子Z坐标
                    0.01 * (random.nextDouble() - 0.5), // 微小X方向速度
                    0.01 * (random.nextDouble() - 0.5), // 微小Y方向速度
                    0.01 * (random.nextDouble() - 0.5)  // 微小Z方向速度
            );
        }
    }

    private void placeBlocks() {
        if (this.hasNoOtherEntitiesAround(1.2)) {
            if (getEntityWorld().getBlockState(this.blockPos(0, -2, 0)).isOf(Blocks.AIR) || getEntityWorld().getBlockState(this.blockPos(0, -2, 0)).isOf(Blocks.CAVE_AIR)) placeBlocks(0, -2, 0);
            if (getEntityWorld().getBlockState(this.blockPos(1, -2, 0)).isOf(Blocks.AIR) || getEntityWorld().getBlockState(this.blockPos(1, -2, 0)).isOf(Blocks.CAVE_AIR)) placeBlocks(1, -2, 0);
            if (getEntityWorld().getBlockState(this.blockPos(-1, -2, 0)).isOf(Blocks.AIR) || getEntityWorld().getBlockState(this.blockPos(-1, -2, 0)).isOf(Blocks.CAVE_AIR)) placeBlocks(-1, -2, 0);
            if (getEntityWorld().getBlockState(this.blockPos(0, -2, 1)).isOf(Blocks.AIR) || getEntityWorld().getBlockState(this.blockPos(0, -2, 1)).isOf(Blocks.CAVE_AIR)) placeBlocks(0, -2, 1);
            if (getEntityWorld().getBlockState(this.blockPos(0, -2, -1)).isOf(Blocks.AIR) || getEntityWorld().getBlockState(this.blockPos(0, -2, -1)).isOf(Blocks.CAVE_AIR)) placeBlocks(0, -2, -1);
        }
    }

    private void placeBlocks(int i, int j, int k) {
        getEntityWorld().setBlockState(
                this.blockPos(i, j, k),
                ModBlocks.EGG_BRIDGE.getDefaultState(),
                6
        );
    }

    private @NotNull BlockPos blockPos(int i, int j, int k) {
        return new BlockPos(
                this.getBlockPos().getX()+i,
                this.getBlockPos().getY()+j,
                this.getBlockPos().getZ()+k
        );
    }

    private boolean hasNoOtherEntitiesAround(double radius) {
        Box box = new Box(
                this.getX() - radius, this.getY()-2 - radius, this.getZ() - radius,
                this.getX() + radius, this.getY() + radius, this.getZ() + radius
        );
        return this.getEntityWorld().getOtherEntities(
                this,  // 排除的实体（自身）
                box,   // 检测范围
                entity -> entity instanceof LivingEntity  // 其他筛选条件
        ).isEmpty();
    }

    public BridgeEggEntity getThis() {
        return this;
    }
}
