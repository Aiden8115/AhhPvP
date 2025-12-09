package com.aiden.pvp.blocks;

import com.aiden.pvp.PvP;
import com.aiden.pvp.blocks.entity.BossSpawnerBlockEntity;
import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.client.PvPClient;
import com.aiden.pvp.items.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class BossSpawnerBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createColumnShape(16.0, 0.0, 8.0);

    public BossSpawnerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(BossSpawnerBlock::new);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(ModItems.BOSS_KEY) && world instanceof ServerWorld serverWorld) {
            if (
                    serverWorld.getBlockState(pos.down()).isOf(Blocks.TNT) &&
                            serverWorld.getBlockState(pos.down().east().north()).isOf(Blocks.TNT) &&
                            serverWorld.getBlockState(pos.down().east().south()).isOf(Blocks.TNT) &&
                            serverWorld.getBlockState(pos.down().west().north()).isOf(Blocks.TNT) &&
                            serverWorld.getBlockState(pos.down().west().south()).isOf(Blocks.TNT) &&
                            serverWorld.getBlockState(pos.down().east()).isOf(ModBlocks.STRONG_GLASS) &&
                            serverWorld.getBlockState(pos.down().west()).isOf(ModBlocks.STRONG_GLASS) &&
                            serverWorld.getBlockState(pos.down().north()).isOf(ModBlocks.STRONG_GLASS) &&
                            serverWorld.getBlockState(pos.down().south()).isOf(ModBlocks.STRONG_GLASS)
            ) {
                if (world.getBlockEntity(pos) instanceof BossSpawnerBlockEntity blockEntity) {
                    this.summonIllusioner(world, pos.up());
                    blockEntity.placeStructure(serverWorld);
                }
            }
            stack.decrementUnlessCreative(1, player);
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    @Override
    protected VoxelShape getCullingShape(BlockState state) {
        return SHAPE;
    }
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    private void summonIllusioner(World world, Vec3i pos) {
        if (world instanceof ServerWorld serverWorld) {
            IllusionerEntity illusionerEntity = new IllusionerEntity(EntityType.ILLUSIONER, world);
            illusionerEntity.setPos(pos.getX(), pos.getY(), pos.getZ());
            illusionerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2147483647, 14));

            serverWorld.spawnEntity(illusionerEntity);
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BossSpawnerBlockEntity(pos, state);
    }
}
