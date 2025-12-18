package com.aiden.pvp.items;

import com.aiden.pvp.entities.DaggerEntity;
import com.aiden.pvp.entities.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ThrowableDaggerItem extends Item {
    public ThrowableDaggerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);
        if (world instanceof ServerWorld serverWorld) {
            DaggerEntity daggerEntity = new DaggerEntity(ModEntities.DAGGER, world);
            daggerEntity.setOwner(user);

            daggerEntity.setPos(user.getX(), user.getEyeY(), user.getZ());

            float f = -MathHelper.sin(user.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(user.getPitch() * ((float)Math.PI / 180));
            float g = -MathHelper.sin((user.getPitch() + 0.0F) * ((float)Math.PI / 180));
            float h = MathHelper.cos(user.getYaw() * ((float)Math.PI / 180)) * MathHelper.cos(user.getPitch() * ((float)Math.PI / 180));
            daggerEntity.setVelocity(f, g, h, 1.2F, 0.0F);

            serverWorld.spawnEntity(daggerEntity);
        }

        return ActionResult.SUCCESS;
    }
}
