package com.aiden.pvp;

import com.aiden.pvp.datagen.PvPAdvancementProvider;
import com.aiden.pvp.datagen.PvPBlockLootTableProvider;
import com.aiden.pvp.datagen.PvPRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PvPDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(PvPBlockLootTableProvider::new);
        pack.addProvider(PvPRecipeProvider::new);
        pack.addProvider(PvPAdvancementProvider::new);
	}
}
