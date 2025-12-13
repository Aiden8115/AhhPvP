package com.aiden.pvp.blocks;

import com.aiden.pvp.PvP;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {
    public static final Block SPECIAL_SLIME_BLOCK = register(
            "slime_block",
            SlimeBlock::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.SLIME)
    );
    public static final Block EGG_BRIDGE = register(
            "egg_bridge",
            EggBridgeBlock::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.WOOL)
                    .breakInstantly()
                    .dropsNothing()
                    .hardness(0.01F)
    );
    public static final Block TNT = register(
            "tnt",
            TntBlock::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.GRASS)
                    .breakInstantly()
                    .hardness(0.01F)
    );
    public static final Block THROWABLE_TNT = register(
            "throwable_tnt",
            TntBlock::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.GRASS)
                    .breakInstantly()
                    .hardness(0.01F)
    );
    public static final Block STRONG_GLASS = register(
            "strong_glass",
            TransparentBlock::new,
            AbstractBlock.Settings.create()
                    .sounds(BlockSoundGroup.GLASS)
                    .strength(0.3F, 2147483647.0F)
                    .nonOpaque()
                    .instrument(NoteBlockInstrument.HAT)
                    .allowsSpawning(Blocks::never)
                    .solidBlock(Blocks::never)
                    .suffocates(Blocks::never)
                    .blockVision(Blocks::never)
    );
    public static final Block GOLDEN_HEAD = register(
            "golden_head",
            GoldenHeadBlock::new,
            AbstractBlock.Settings.create()
                    .instrument(NoteBlockInstrument.CUSTOM_HEAD)
                    .strength(1200.0F)
                    .pistonBehavior(PistonBehavior.BLOCK)
    );
    public static final Block BOSS_SPAWNER = register(
            "boss_spawner",
            BossSpawnerBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.BLACK)
                    .strength(0)
                    .sounds(BlockSoundGroup.ANVIL)
                    .breakInstantly()
    );
    public static final Block BOSS_BATTLE_HANDLER = register(
            "boss_battle_handler",
            BossBattleHandler::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.BLACK)
                    .strength(0)
                    .sounds(BlockSoundGroup.ANVIL)
                    .breakInstantly()
    );

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings) {
        Identifier id = Identifier.of(PvP.MOD_ID, name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        // 注册方块
        return Registry.register(Registries.BLOCK, id, blockFactory.apply(settings.registryKey(blockKey)));
    }
    public static void initialize() {
        try {
            PvP.LOGGER.info("[Block Initializer] Mod Blocks Initialized!");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Block Initializer] An Error Occurred: " + e.getMessage());
        }
    }
}
