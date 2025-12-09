package com.aiden.pvp.blocks;

import com.aiden.pvp.blocks.entity.BossBattleHandlerBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BossBattleHandler extends BlockWithEntity {

    protected BossBattleHandler(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(BossBattleHandler::new);
    }

    @Override
    public @Nullable BossBattleHandlerBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BossBattleHandlerBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return null;
        } else {
            return (w, p, s, be) -> {
                if (w.getBlockEntity(p) instanceof BossBattleHandlerBlockEntity bossBattleHandlerBlockEntity) bossBattleHandlerBlockEntity.tick(w, p, s, be);
            };
        }
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (blockEntity instanceof BossBattleHandlerBlockEntity bossBattleHandlerBlockEntity) {
            bossBattleHandlerBlockEntity.removeBossBarPlayers();
        }
        super.afterBreak(world, player, pos, state, blockEntity, tool);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof BossBattleHandlerBlockEntity bossBattleHandlerBlockEntity) {
            bossBattleHandlerBlockEntity.removeBossBarPlayers();
        }
        return super.onBreak(world, pos, state, player);
    }
}
