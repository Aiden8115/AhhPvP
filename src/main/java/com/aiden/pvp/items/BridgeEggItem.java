package com.aiden.pvp.items;

import com.aiden.pvp.entities.BridgeEggEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.EggItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BridgeEggItem extends EggItem {
    private static final float THROW_POWER = 1.2F;
    public BridgeEggItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        // 播放投掷声音（客户端和服务器都能听到）
        world.playSound(
                null,
                user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_EGG_THROW,
                SoundCategory.PLAYERS
        );

        // 仅在服务器端生成实体（确保同步到客户端）
        if (world instanceof ServerWorld serverWorld) {
            // 使用定义的投掷力度，替换原POWER
            ProjectileEntity.spawnWithVelocity(
                    BridgeEggEntity::new,  // 实体构造器
                    serverWorld,          // 服务器世界
                    itemStack,            // 物品栈
                    user,                 // 投掷者
                    0.0F,                 // 垂直偏移
                    THROW_POWER,          // 投掷力度
                    1.0F                  // 散布范围（1.0F为原版鸡蛋散布）
            );
        }

        // 更新统计信息和物品数量
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        itemStack.decrementUnlessCreative(1, user);

        return ActionResult.SUCCESS;
    }
}
