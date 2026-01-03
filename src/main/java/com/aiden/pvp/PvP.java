package com.aiden.pvp;

import com.aiden.pvp.blocks.ModBlocks;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.commands.ModCommand;
import com.aiden.pvp.entities.ModEntities;
import com.aiden.pvp.gamerules.ModGameRules;
import com.aiden.pvp.items.ModItems;
import com.aiden.pvp.payloads.SetFBExplodePowerGameruleC2SPayload;
import com.aiden.pvp.payloads.ThrowTntC2SPayload;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvP implements ModInitializer {
	public static final String MOD_ID = "pvp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModGameRules.initialize();
		ModBlocks.initialize();
		ModItems.initialize();
		ModBlockEntityTypes.initialize();
		ModEntities.initialize();
		ModCommand.initialize();

        Item fireballItem = Registries.ITEM.get(Identifier.of(MOD_ID, "fireball"));
        ProjectileDispenserBehavior projectileDispenserBehavior = new ProjectileDispenserBehavior(fireballItem);
        DispenserBlock.registerBehavior(fireballItem, projectileDispenserBehavior);

		PayloadTypeRegistry.playC2S().register(ThrowTntC2SPayload.ID, ThrowTntC2SPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ThrowTntC2SPayload.ID, ((throwTntC2SPayload, context) -> {
			context.server().execute(() -> {
				Entity user = context.player().getEntityWorld().getEntityById(throwTntC2SPayload.userId());
                if (user instanceof LivingEntity livingEntityUser) {
					TntEntity tnt = new TntEntity(user.getEntityWorld(), user.getX(), user.getEyeY(), user.getZ(), livingEntityUser);
					// Shoot
					Vec3d vec3d = new Vec3d(
							-MathHelper.sin(user.getYaw() * (float) (Math.PI / 180.0)) * MathHelper.cos(user.getPitch() * (float) (Math.PI / 180.0)),
							-MathHelper.sin((user.getPitch() + 0.0F) * (float) (Math.PI / 180.0)),
							MathHelper.cos(user.getYaw() * (float) (Math.PI / 180.0)) * MathHelper.cos(user.getPitch() * (float) (Math.PI / 180.0))
					).normalize().add(Random.create().nextTriangular(0.0, 0.0172275 * 1.0F), Random.create().nextTriangular(0.0, 0.0172275 * 1.0F), Random.create().nextTriangular(0.0, 0.0172275 * 1.0F)).multiply(1.5F);

					tnt.setVelocity(vec3d);
					tnt.velocityDirty = true;
					double d = vec3d.horizontalLength();
					tnt.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
					tnt.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 180.0F / (float)Math.PI));
					tnt.lastYaw = tnt.getYaw();
					tnt.lastPitch = tnt.getPitch();
					tnt.setVelocity(tnt.getVelocity().add(user.getMovement().x, user.isOnGround() ? 0.0 : user.getMovement().y, user.getMovement().z));
					if (livingEntityUser.getMainHandStack().isOf(ModItems.THROWABLE_TNT)) {
						user.getEntityWorld().spawnEntity(tnt);
						livingEntityUser.getMainHandStack().decrementUnlessCreative(1, livingEntityUser);
						return;
					}
					if (livingEntityUser.getOffHandStack().isOf(ModItems.THROWABLE_TNT)) {
						user.getEntityWorld().spawnEntity(tnt);
						livingEntityUser.getOffHandStack().decrementUnlessCreative(1, livingEntityUser);
					}
                }
            });
		}));

		PayloadTypeRegistry.playC2S().register(SetFBExplodePowerGameruleC2SPayload.ID, SetFBExplodePowerGameruleC2SPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SetFBExplodePowerGameruleC2SPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
                ServerWorld serverWorld = context.player().getEntityWorld();
				serverWorld.getGameRules().setValue(ModGameRules.PvpMod_FIREBALL_EXPLODE_POWER, payload.value(), serverWorld.getServer());
			});
		});
		LOGGER.info("[Main]              Mod Initialized Successfully! ");
	}
}
