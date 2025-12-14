package com.aiden.pvp.items;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.ModBlocks;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.function.Function;

public class ModItems {
    private ModItems() {}

    public static final Item FIREBALL = register("fireball", FireballItem::new, new Item.Settings().maxCount(64).fireproof().useCooldown(0.1F));
    public static final Item SELF_RES_PLATFORM = register("self-res_platform", SelfRescuePlatformItem::new, new Item.Settings().maxCount(64).useCooldown(20.0F).recipeRemainder(Items.BLAZE_ROD));
    public static final Item BRIDGE_EGG = register("bridge_egg", BridgeEggItem::new, new Item.Settings().maxCount(64).fireproof());
    public static final Item FISHING_ROD = register("fishing_rod", FishingRodItem::new, new Item.Settings().maxCount(1).fireproof().maxDamage(64));
    public static final Item BED_BUG = register("bed_bug", BedBugItem::new, new Item.Settings().maxCount(16));

    public static final Item CARBON_RUNE = register("carbon_rune", CarbonRuneItem::new, new Item.Settings().maxCount(64));
    public static final Item IRON_RUNE = register("iron_rune", IronRuneItem::new, new Item.Settings().maxCount(64));
    public static final Item BOSS_KEY = register("boss_key", Item::new, new Item.Settings().maxCount(64));
    public static final Item BBU_UPGRADE_SMITHING_TEMPLATE = register("bbu_upgrade_smithing_template",
            settings -> new SmithingTemplateItem(
                    Text.translatable("item.pvp.smithing_template.bbu_upgrade.applies_to").formatted(Formatting.BLUE),
                    Text.translatable("item.pvp.smithing_template.bbu_upgrade.ingredients").formatted(Formatting.BLUE),
                    Text.translatable("item.pvp.smithing_template.bbu_upgrade.base_slot_description"),
                    Text.translatable("item.pvp.smithing_template.bbu_upgrade.additions_slot_description"),
                    List.of(Identifier.ofVanilla("container/slot/sword")),
                    List.of(Identifier.of(PvP.MOD_ID, "container/slot/null")),
                    settings
            ),
            new Item.Settings()
    );

    public static final Item TNT = Items.register(ModBlocks.TNT, TntBlockItem::new, new Item.Settings().rarity(Rarity.EPIC).maxCount(64).translationKey("block.pvp.tnt"));
    public static final Item THROWABLE_TNT = Items.register(ModBlocks.THROWABLE_TNT, ThrowableTntBlockItem::new, new Item.Settings().rarity(Rarity.EPIC).maxCount(64).translationKey("block.pvp.throwable_tnt"));
    public static final Item STRONG_GLASS = Items.register(ModBlocks.STRONG_GLASS, new Item.Settings().rarity(Rarity.EPIC).maxCount(64).translationKey("block.pvp.strong_glass"));
    public static final Item GOLDEN_HEAD = Items.register(ModBlocks.GOLDEN_HEAD, GoldenHeadItem::new, new Item.Settings().rarity(Rarity.EPIC).maxCount(2).translationKey("block.pvp.golden_head"));
    public static final Item BOSS_SPAWNER = Items.register(ModBlocks.BOSS_SPAWNER, new Item.Settings().rarity(Rarity.EPIC).maxCount(64).translationKey("block.pvp.boss_spawner"));

    public static final Item WOODEN_SWORD = register("wooden_sword", Item::new, new Item.Settings().sword(ToolMaterial.WOOD, 3, 251));
    public static final Item STONE_SWORD = register("stone_sword", Item::new, new Item.Settings().sword(ToolMaterial.STONE, 3, 251));
    public static final Item IRON_SWORD = register("iron_sword", Item::new, new Item.Settings().sword(ToolMaterial.IRON, 3, 251));
    public static final Item DIAMOND_SWORD = register("diamond_sword", Item::new, new Item.Settings().sword(ToolMaterial.DIAMOND, 3, 251));

    public static final Item THROWABLE_DAGGER = register("throwable_dagger", ThrowableDaggerItem::new, new Item.Settings().sword(ToolMaterial.IRON, 2, 251).useCooldown(5));

    public static final Potion SHORT_INVISIBILITY_POTION = Registry.register(Registries.POTION, Identifier.of(PvP.MOD_ID, "short_invisibility_potion"), new Potion("invisibility", new StatusEffectInstance(StatusEffects.INVISIBILITY, 600, 0)));
    public static final Potion LONG_INVISIBILITY_POTION = Registry.register(Registries.POTION, Identifier.of(PvP.MOD_ID, "long_invisibility_potion"), new Potion("invisibility", new StatusEffectInstance(StatusEffects.INVISIBILITY, 12000, 0)));


    public static final ItemGroup PVP_ITEM_GROUP = ItemGroup
            .create(ItemGroup.Row.BOTTOM, 6)
            .icon(() -> new ItemStack(Items.FISHING_ROD))
            .displayName(Text.translatable("itemGroup.pvp_mod"))
            .build();

    public static Item register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(PvP.MOD_ID, path));
        return Items.register(registryKey, factory, settings.translationKey("item.pvp." + path).rarity(Rarity.EPIC));
    }

    public static void initialize() {
        try {
            Registry.register(Registries.ITEM_GROUP, RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(PvP.MOD_ID, "assets/pvp")), PVP_ITEM_GROUP);

            BrewingRecipeRegistry.Builder.BUILD.register(builder -> {
                builder.registerPotionRecipe(
                        Potions.WATER,
                        Items.GLASS,
                        Registries.POTION.getEntry(SHORT_INVISIBILITY_POTION)
                );
                builder.registerPotionRecipe(
                        Potions.WATER,
                        STRONG_GLASS,
                        Registries.POTION.getEntry(LONG_INVISIBILITY_POTION)
                );
            });

            ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(PvP.MOD_ID, "assets/pvp"))).register(i -> {
                i.add(ModItems.FIREBALL);
                i.add(ModItems.SELF_RES_PLATFORM);
                i.add(ModItems.BRIDGE_EGG);
                i.add(ModItems.FISHING_ROD);
                i.add(ModItems.BED_BUG);
                i.add(ModItems.TNT);
                i.add(ModItems.THROWABLE_TNT);
                i.add(ModItems.STRONG_GLASS);
                i.add(ModItems.BOSS_SPAWNER);
                i.add(ModItems.BOSS_KEY);
                i.add(ModItems.GOLDEN_HEAD);
                i.add(ModItems.CARBON_RUNE);
                i.add(ModItems.IRON_RUNE);
                i.add(ModItems.WOODEN_SWORD);
                i.add(ModItems.STONE_SWORD);
                i.add(ModItems.IRON_SWORD);
                i.add(ModItems.DIAMOND_SWORD);
                i.add(ModItems.THROWABLE_DAGGER);
                i.add(ModItems.BBU_UPGRADE_SMITHING_TEMPLATE);
            });
            ItemTooltipCallback.EVENT.register((i, context, type, list) -> {
                if (i.isOf(FIREBALL)) list.add(Text.literal("Use it... and watch it explode!"));
                if (i.isOf(SELF_RES_PLATFORM)) list.add(Text.literal("Breaking the fall!"));
                if (i.isOf(WOODEN_SWORD) || i.isOf(STONE_SWORD) || i.isOf(IRON_SWORD) || i.isOf(DIAMOND_SWORD)) list.add(Text.literal("No attack CDs"));
                if (i.isOf(GOLDEN_HEAD)) list.add(Text.literal("Powerful! "));
            });
            PvP.LOGGER.info("[Item Initializer]  Mod Items Initialized! ");
        } catch (Exception e) {
            PvP.LOGGER.warn("[Item Initializer]  An Error Occurred! ");
        }
    }
}
