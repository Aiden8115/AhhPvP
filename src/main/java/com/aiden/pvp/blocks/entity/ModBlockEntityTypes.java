package com.aiden.pvp.blocks.entity;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ModBlockEntityTypes {

    public static BlockEntityType<? extends SlimeBlockEntity> SLIME_BLOCK_ENTITY;
    public static BlockEntityType<? extends BossSpawnerBlockEntity> BOSS_SPAWNER_BLOCK_ENTITY;
    public static BlockEntityType<? extends BossBattleHandlerBlockEntity> BOSS_BATTLE_HANDLER_BLOCK_ENTITY;

    @NotNull
    public static <T extends BlockEntity> BlockEntityType<? extends T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> factory,
            Block block)
    {
         return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(PvP.MOD_ID, name),
                FabricBlockEntityTypeBuilder.create(factory, block)
                        .addBlock(block)
                        .build()
         );
    }

    public static void initialize() {
        SLIME_BLOCK_ENTITY = register("slime_block_entity", SlimeBlockEntity::new, ModBlocks.SPECIAL_SLIME_BLOCK);
        BOSS_SPAWNER_BLOCK_ENTITY = register("boss_spawner_block_entity", BossSpawnerBlockEntity::new, ModBlocks.BOSS_SPAWNER);
        BOSS_BATTLE_HANDLER_BLOCK_ENTITY = register("boss_battle_handler_block_entity", BossBattleHandlerBlockEntity::new, ModBlocks.BOSS_BATTLE_HANDLER);
    }
}
