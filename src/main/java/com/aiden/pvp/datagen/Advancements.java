package com.aiden.pvp.datagen;

import com.aiden.pvp.PvP;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class Advancements implements Consumer<Consumer<AdvancementEntry>> {
    @Override
    public void accept(Consumer<AdvancementEntry> advancementEntryConsumer) {
        AdvancementEntry rootAdvancement = Advancement.Builder.create()
                .display(
                        new ItemStack(Items.FISHING_ROD),
                        Text.translatable("advancements.pvp_mod.root.title"),
                        Text.translatable("advancements.pvp_mod.root.description"),
                        Identifier.of(PvP.MOD_ID, "pvp_mod"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
                .criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity())
                .criterion("killed_by_something", OnKilledCriterion.Conditions.createEntityKilledPlayer())
                .build(advancementEntryConsumer, PvP.MOD_ID + ":pvp_mod/root");
    }
}
