package com.aiden.pvp.items;

import com.aiden.pvp.entities.FireballEntity;
import com.aiden.pvp.gamerules.ModGameRules;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ProjectileItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FireballItem extends Item implements ProjectileItem {
    public FireballItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (world instanceof ServerWorld serverWorld) {
            FireballEntity fireballEntity = new FireballEntity(user, world, itemStack);
            fireballEntity.setItem(itemStack);

            float f = -MathHelper.sin(user.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(user.getPitch() * ((float)Math.PI / 180));
            float g = -MathHelper.sin((user.getPitch() + 0.0F) * ((float)Math.PI / 180));
            float h = MathHelper.cos(user.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(user.getPitch() * ((float)Math.PI / 180));
            fireballEntity.setVelocity(f, g, h, (float) serverWorld.getGameRules().getValue(ModGameRules.PvpMod_FIREBALL_SHOOT_POWER) / 10, 0.0F);

            if (serverWorld.getGameRules().getValue(ModGameRules.PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY)) {
                Vec3d vec3d = user.getMovement();
                fireballEntity.setVelocity(fireballEntity.getVelocity().add(vec3d.x, user.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
            }

            world.spawnEntity(fireballEntity);
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        return new FireballEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity user = context.getPlayer();
        Hand hand = context.getHand();
        if (user == null) return ActionResult.PASS;

        // shoot
        ItemStack itemStack = user.getStackInHand(hand);
        if (world instanceof ServerWorld serverWorld) {
            FireballEntity fireballEntity = new FireballEntity(user, world, itemStack);
            fireballEntity.setItem(itemStack);

            float f = -MathHelper.sin(user.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(user.getPitch() * ((float)Math.PI / 180));
            float g = -MathHelper.sin((user.getPitch() + 0.0F) * ((float)Math.PI / 180));
            float h = MathHelper.cos(user.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(user.getPitch() * ((float)Math.PI / 180));
            fireballEntity.setVelocity(f, g, h, (float) serverWorld.getGameRules().getValue(ModGameRules.PvpMod_FIREBALL_SHOOT_POWER) / 10, 0.0F);

            if (serverWorld.getGameRules().getValue(ModGameRules.PvpMod_DO_SHOOTER_VELOCITY_AFFECTS_FIREBALL_VELOCITY)) {
                Vec3d vec3d = user.getMovement();
                fireballEntity.setVelocity(fireballEntity.getVelocity().add(vec3d.x, user.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
            }

            world.spawnEntity(fireballEntity);
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);

        // summon fire block
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState)) {
            blockPos = blockPos.offset(context.getSide());
            if (AbstractFireBlock.canPlaceAt(world, blockPos, context.getHorizontalPlayerFacing())) {
                world.setBlockState(blockPos, AbstractFireBlock.getState(world, blockPos));
                world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_PLACE, blockPos);
            }
        } else {
            world.setBlockState(blockPos, blockState.with(Properties.LIT, true));
            world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
        }

        return ActionResult.SUCCESS;
    }
}
