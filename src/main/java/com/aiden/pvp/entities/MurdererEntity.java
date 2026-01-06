package com.aiden.pvp.entities;

import com.aiden.pvp.items.ModItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class MurdererEntity extends HostileEntity {
    public MurdererEntity(EntityType<? extends MurdererEntity> type, World world) {
        super(ModEntities.MURDERER, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new DaggerAttackGoal(this, 1.0, 20, 30.0F));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, true));
        this.targetSelector.add(6, new ActiveTargetGoal<>(this, MobEntity.class, true));
        this.goalSelector.add(4, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(5, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
    }

    public static DefaultAttributeContainer.Builder createMurdererAttributes() {
        return new DefaultAttributeContainer.Builder()
                .add(EntityAttributes.MAX_HEALTH, 40)
                .add(EntityAttributes.KNOCKBACK_RESISTANCE)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.4)
                .add(EntityAttributes.ARMOR, 100)
                .add(EntityAttributes.ARMOR_TOUGHNESS)
                .add(EntityAttributes.MAX_ABSORPTION)
                .add(EntityAttributes.STEP_HEIGHT)
                .add(EntityAttributes.SCALE)
                .add(EntityAttributes.GRAVITY)
                .add(EntityAttributes.SAFE_FALL_DISTANCE)
                .add(EntityAttributes.FALL_DAMAGE_MULTIPLIER)
                .add(EntityAttributes.JUMP_STRENGTH)
                .add(EntityAttributes.OXYGEN_BONUS)
                .add(EntityAttributes.BURNING_TIME)
                .add(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE)
                .add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY)
                .add(EntityAttributes.MOVEMENT_EFFICIENCY)
                .add(EntityAttributes.ATTACK_KNOCKBACK)
                .add(EntityAttributes.CAMERA_DISTANCE)
                .add(EntityAttributes.WAYPOINT_TRANSMIT_RANGE)
                .add(EntityAttributes.FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 15.0)
                .add(EntityAttributes.ATTACK_SPEED, 10)
                .add(EntityAttributes.ENTITY_INTERACTION_RANGE, 3);
    }

    public State getState() {
        if (this.isAttacking()) {
            return State.ATTACKING;
        } else {
            return State.CROSSED;
        }
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        EntityData entityData2 = super.initialize(world, difficulty, spawnReason, entityData);
        this.getNavigation().setCanOpenDoors(true);
        Random random = world.getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(world, random, difficulty);
        return entityData2;
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.THROWABLE_DAGGER));
    }

    public enum State {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        BOW_AND_ARROW,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        CELEBRATING,
        NEUTRAL;
    }

    static class MeleeAttackGoal extends Goal {
        protected final PathAwareEntity mob;
        private final double speed;
        private final boolean pauseWhenMobIdle;
        private Path path;
        private double targetX;
        private double targetY;
        private double targetZ;
        private int updateCountdownTicks;
        private int cooldown;
        private final int attackIntervalTicks = 20;
        private long lastUpdateTime;
        private static final long MAX_ATTACK_TIME = 20L;

        public MeleeAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
            this.mob = mob;
            this.speed = speed;
            this.pauseWhenMobIdle = pauseWhenMobIdle;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            long l = this.mob.getEntityWorld().getTime();
            if (l - this.lastUpdateTime < 20L) {
                return false;
            } else {
                this.lastUpdateTime = l;
                LivingEntity livingEntity = this.mob.getTarget();
                if (livingEntity == null) {
                    return false;
                } else if (!livingEntity.isAlive()) {
                    return false;
                } else {
                    this.path = this.mob.getNavigation().findPathTo(livingEntity, 0);
                    return (this.path != null || this.mob.isInAttackRange(livingEntity)) && this.mob.squaredDistanceTo(livingEntity) <= 100;
                }
            }
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else if (!this.pauseWhenMobIdle) {
                return !this.mob.getNavigation().isIdle();
            } else {
                return this.mob.isInPositionTargetRange(livingEntity.getBlockPos()) && !(livingEntity instanceof PlayerEntity playerEntity && (playerEntity.isSpectator() || playerEntity.isCreative()));
            }
        }

        @Override
        public void start() {
            this.mob.getNavigation().startMovingAlong(this.path, this.speed);
            this.mob.setAttacking(true);
            this.updateCountdownTicks = 0;
            this.cooldown = 0;
        }

        @Override
        public void stop() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                this.mob.setTarget(null);
            }

            this.mob.setAttacking(false);
            this.mob.getNavigation().stop();
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null) {
                this.mob.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
                this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
                if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity))
                        && this.updateCountdownTicks <= 0
                        && (
                        this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
                                || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0
                                || this.mob.getRandom().nextFloat() < 0.05F
                )) {
                    this.targetX = livingEntity.getX();
                    this.targetY = livingEntity.getY();
                    this.targetZ = livingEntity.getZ();
                    this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
                    double d = this.mob.squaredDistanceTo(livingEntity);
                    if (d > 1024.0) {
                        this.updateCountdownTicks += 10;
                    } else if (d > 256.0) {
                        this.updateCountdownTicks += 5;
                    }

                    if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {
                        this.updateCountdownTicks += 15;
                    }

                    this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
                }

                this.cooldown = Math.max(this.cooldown - 1, 0);
                this.attack(livingEntity);
            }
        }

        protected void attack(LivingEntity target) {
            if (this.canAttack(target)) {
                this.resetCooldown();
                this.mob.swingHand(Hand.MAIN_HAND);
                this.mob.tryAttack(getServerWorld(this.mob), target);
            }
        }

        protected void resetCooldown() {
            this.cooldown = this.getTickCount(20);
        }

        protected boolean isCooledDown() {
            return this.cooldown <= 0;
        }

        protected boolean canAttack(LivingEntity target) {
            return this.isCooledDown() && this.mob.isInAttackRange(target) && this.mob.getVisibilityCache().canSee(target);
        }

        protected int getCooldown() {
            return this.cooldown;
        }

        protected int getMaxCooldown() {
            return this.getTickCount(20);
        }
    }

    static class DaggerAttackGoal extends Goal {
        private final MurdererEntity actor;
        private final double speed;
        private int attackInterval;
        private final float squaredRange;
        private int cooldown = -1;
        private int targetSeeingTicker;
        private boolean movingToLeft;
        private boolean backward;
        private int combatTicks = -1;

        public DaggerAttackGoal(MurdererEntity actor, double speed, int attackInterval, float range) {
            this.actor = actor;
            this.speed = speed;
            this.attackInterval = attackInterval;
            this.squaredRange = range * range;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        public void setAttackInterval(int attackInterval) {
            this.attackInterval = attackInterval;
        }

        @Override
        public boolean canStart() {
            return this.actor.getTarget() != null && this.isHoldingDagger() && this.actor.getTarget().squaredDistanceTo(this.actor) > 100;
        }

        protected boolean isHoldingDagger() {
            return this.actor.isHolding(ModItems.THROWABLE_DAGGER);
        }

        @Override
        public boolean shouldContinue() {
            return (this.canStart() || !this.actor.getNavigation().isIdle()) && this.isHoldingDagger();
        }

        @Override
        public void start() {
            super.start();
            this.actor.setAttacking(true);
        }

        @Override
        public void stop() {
            super.stop();
            this.actor.setAttacking(false);
            this.targetSeeingTicker = 0;
            this.cooldown = -1;
            this.actor.clearActiveItem();
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.actor.getTarget();
            if (livingEntity != null) {
                double d = this.actor.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                boolean bl = this.actor.getVisibilityCache().canSee(livingEntity);
                boolean bl2 = this.targetSeeingTicker > 0;
                this.actor.lookAtEntity(livingEntity, 30.0F, 30.0F);
                if (bl != bl2) {
                    this.targetSeeingTicker = 0;
                }

                if (bl) {
                    this.targetSeeingTicker++;
                } else {
                    this.targetSeeingTicker--;
                }

                if (!(d > this.squaredRange) && this.targetSeeingTicker >= 20) {
                    this.actor.getNavigation().stop();
                    this.combatTicks++;
                } else {
                    this.combatTicks = -1;
                }

                if (this.combatTicks >= 20) {
                    if (this.actor.getRandom().nextFloat() < 0.3) {
                        this.movingToLeft = !this.movingToLeft;
                    }

                    if (this.actor.getRandom().nextFloat() < 0.3) {
                        this.backward = !this.backward;
                    }

                    this.combatTicks = 0;
                }

                if (this.combatTicks > -1) {
                    if (d > this.squaredRange * 0.75F) {
                        this.backward = false;
                    } else if (d < this.squaredRange * 0.25F) {
                        this.backward = true;
                    }

                    this.actor.getMoveControl().strafeTo(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
                    if (this.actor.getControllingVehicle() instanceof MobEntity mobEntity) {
                        mobEntity.lookAtEntity(livingEntity, 30.0F, 30.0F);
                    }

                    this.actor.lookAtEntity(livingEntity, 30.0F, 30.0F);
                } else {
                    this.actor.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
                }

                if (this.actor.isUsingItem()) {
                    if (!bl && this.targetSeeingTicker < -60) {
                        this.actor.clearActiveItem();
                    } else if (bl) {
                        int i = this.actor.getItemUseTime();
                        if (i >= 20) {
                            this.actor.clearActiveItem();

                            DaggerEntity daggerEntity = getDaggerEntity();
                            this.actor.getEntityWorld().spawnEntity(daggerEntity);

                            this.actor.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (this.actor.getRandom().nextFloat() * 0.4F + 0.8F));


                            this.cooldown = this.attackInterval;
                        }
                    }
                } else if (--this.cooldown <= 0 && this.targetSeeingTicker >= -60) {
                    this.actor.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.actor, ModItems.THROWABLE_DAGGER));
                }
            }
        }

        private @NotNull DaggerEntity getDaggerEntity() {
            DaggerEntity daggerEntity = new DaggerEntity(ModEntities.DAGGER, this.actor.getEntityWorld());
            daggerEntity.setOwner(this.actor);

            daggerEntity.setPos(this.actor.getX(), this.actor.getEyeY(), this.actor.getZ());

            float f = -MathHelper.sin(this.actor.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(this.actor.getPitch() * ((float)Math.PI / 180));
            float g = -MathHelper.sin((this.actor.getPitch() + 0.0F) * ((float)Math.PI / 180));
            float h = MathHelper.cos(this.actor.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(this.actor.getPitch() * ((float)Math.PI / 180));
            daggerEntity.setVelocity(f, g, h, 1.2F, 0.0F);
            return daggerEntity;
        }
    }
}
