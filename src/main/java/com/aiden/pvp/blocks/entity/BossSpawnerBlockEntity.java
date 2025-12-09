package com.aiden.pvp.blocks.entity;

import com.aiden.pvp.PvP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class BossSpawnerBlockEntity extends BlockEntity {
    public BossSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BOSS_SPAWNER_BLOCK_ENTITY, pos, state);
    }

    public void placeStructure(ServerWorld world) {
        StructureTemplate structureTemplate =  world.getStructureTemplateManager().getTemplate(Identifier.of(PvP.MOD_ID, "battlefield")).orElse(null);
        if (structureTemplate != null) {
            StructurePlacementData structurePlacementData = new StructurePlacementData()
                    .setMirror(BlockMirror.NONE)
                    .setRotation(BlockRotation.NONE)
                    .setIgnoreEntities(false)
                    .setUpdateNeighbors(true);
            structureTemplate.place(
                    world,
                    getPos().add(new Vec3i(-17, -2, -17)),
                    getPos().add(new Vec3i(-17, -2, -17)),
                    structurePlacementData,
                    Random.create(Util.getMeasuringTimeMs()),
                    Block.NOTIFY_LISTENERS
            );
        }
    }
}
