package com.aiden.pvp.client;

import com.aiden.pvp.PvP;
import com.aiden.pvp.client.render.entity.FishingBobberEntityRenderer;
import com.aiden.pvp.entities.ModEntities;
import com.aiden.pvp.payloads.ThrowTntC2SPayload;
import com.aiden.pvp.screen.SettingsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.aiden.pvp.blocks.ModBlocks.*;
import static com.aiden.pvp.client.keybinding.ModKeyBindings.*;

@Environment(EnvType.CLIENT)
public class PvPClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererFactories.register(
                ModEntities.FIREBALL,  // 你的实体类型常量
                FlyingItemEntityRenderer::new  // 使用投掷物默认渲染器
        );
        EntityRendererFactories.register(ModEntities.BRIDGE_EGG, context ->
                new FlyingItemEntityRenderer<>(context, 1.0F, true)
        );
        EntityRendererFactories.register(ModEntities.BED_BUG, context ->
                new FlyingItemEntityRenderer<>(context, 1.0F, true)
        );
        EntityRendererFactories.register(
                ModEntities.FISHING_BOBBER,
                FishingBobberEntityRenderer::new
        );

        BlockRenderLayerMap.putBlock(STRONG_GLASS, BlockRenderLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(SPECIAL_SLIME_BLOCK, BlockRenderLayer.TRANSLUCENT);
        BlockRenderLayerMap.putBlock(BOSS_SPAWNER, BlockRenderLayer.TRANSLUCENT);

        pvpKeyCategory = new KeyBinding.Category(Identifier.of(PvP.MOD_ID, "pvp"));

        throwTntKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.pvp.throw_tnt",
                        InputUtil.Type.MOUSE,
                        GLFW.GLFW_MOUSE_BUTTON_LEFT,
                        pvpKeyCategory
                )
        );
        openSettingsKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.pvp.open_settings",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_F7,
                        pvpKeyCategory
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            while (throwTntKeyBinding.wasPressed()) {
                ClientPlayNetworking.send(new ThrowTntC2SPayload(minecraftClient.player.getId()));
            }
            while (openSettingsKeyBinding.wasPressed()) {
                MinecraftClient client = MinecraftClient.getInstance();
                SettingsScreen settingsScreen = new SettingsScreen(null);
                if (client.currentScreen == null) {
                    client.setScreen(settingsScreen);
                    break;
                }
                if (client.currentScreen instanceof SettingsScreen) {
                    client.setScreen(null);
                }
            }
        });
    }
}
