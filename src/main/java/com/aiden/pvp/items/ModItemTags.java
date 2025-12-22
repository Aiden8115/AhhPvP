package com.aiden.pvp.items;

import com.aiden.pvp.PvP;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItemTags {
    public static final TagKey<Item> EMPTY = TagKey.of(RegistryKeys.ITEM, Identifier.of(PvP.MOD_ID, "empty"));
}
