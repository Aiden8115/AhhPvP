package com.aiden.pvp.datagen;

import com.aiden.pvp.items.ModItemTags;
import com.aiden.pvp.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PvPRecipeProvider extends FabricRecipeProvider {
    private final RegistryEntryLookup<Item> itemLookup;

    public PvPRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
        try {
            this.itemLookup = registriesFuture.get().getOrThrow(RegistryKeys.ITEM);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected @NotNull RecipeGenerator getRecipeGenerator(RegistryWrapper.@NotNull WrapperLookup wrapperLookup, @NotNull RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                createShaped(RecipeCategory.MISC, ModItems.THROWABLE_DAGGER)
                        .input('S', Items.STICK)
                        .input('I', Items.IRON_INGOT)
                        .input('B', Items.IRON_BLOCK)
                        .pattern(" IB")
                        .pattern(" S ")
                        .pattern("S  ")
                        .criterion("has_iron_ingot", this.conditionsFromItem(Items.IRON_INGOT))
                        .group("throwable_dagger")
                        .offerTo(this.exporter);
                createShaped(RecipeCategory.MISC, ModItems.FIREBALL, 4)
                        .input('A', Items.BLAZE_POWDER)
                        .input('B', Items.GUNPOWDER)
                        .input('C', Items.NETHER_STAR)
                        .pattern("ABA")
                        .pattern("BCB")
                        .pattern("ABA")
                        .criterion("has_nether_star", this.conditionsFromItem(Items.NETHER_STAR))
                        .group("fireball")
                        .offerTo(this.exporter);
                createShaped(RecipeCategory.MISC, ModItems.GOLDEN_HEAD)
                        .input('A', Items.RESIN_BRICK)
                        .input('B', Items.GOLD_INGOT)
                        .input('C', Items.PLAYER_HEAD)
                        .pattern("ABA")
                        .pattern("BCB")
                        .pattern("ABA")
                        .criterion("has_player_head", this.conditionsFromItem(Items.PLAYER_HEAD))
                        .group("golden_head")
                        .offerTo(this.exporter);
                createShaped(RecipeCategory.MISC, ModItems.SELF_RES_PLATFORM, 4)
                        .input('A', Items.SLIME_BLOCK)
                        .input('B', Items.HEART_OF_THE_SEA)
                        .input('C', Items.BLAZE_ROD)
                        .pattern("ABA")
                        .pattern("BCB")
                        .pattern("ABA")
                        .criterion("has_blaze_rod", this.conditionsFromItem(Items.BLAZE_ROD))
                        .group("self-res_platform")
                        .offerTo(this.exporter);
                createShaped(RecipeCategory.MISC, Items.PLAYER_HEAD)
                        .input('A', Items.POISONOUS_POTATO)
                        .input('B', Items.ROTTEN_FLESH)
                        .input('C', Items.LINGERING_POTION)
                        .input('D', Items.SKELETON_SKULL)
                        .pattern("ABC")
                        .pattern("BDB")
                        .pattern("CBA")
                        .criterion("has_skeleton_skull", this.conditionsFromItem(Items.SKELETON_SKULL))
                        .group("player_head")
                        .offerTo(this.exporter);

                offerBBUUpgradeRecipe(Items.WOODEN_SWORD, ModItems.WOODEN_SWORD);
                offerBBUUpgradeRecipe(Items.STONE_SWORD, ModItems.STONE_SWORD);
                offerBBUUpgradeRecipe(Items.IRON_SWORD, ModItems.IRON_SWORD);
                offerBBUUpgradeRecipe(Items.DIAMOND_SWORD, ModItems.DIAMOND_SWORD);
            }

            public void offerBBUUpgradeRecipe(Item input, Item result) {
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(ModItems.BBU_UPGRADE_SMITHING_TEMPLATE),
                                Ingredient.ofItem(input),
                                Ingredient.ofTag(itemLookup.getOrThrow(ModItemTags.EMPTY)),
                                RecipeCategory.COMBAT,
                                result
                        )
                        .criterion("has_bbu_upgrade_template", this.conditionsFromItem(ModItems.BBU_UPGRADE_SMITHING_TEMPLATE))
                        .offerTo(this.exporter, "bbu_" + getItemPath(result) + "_smithing");
            }
        };
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
