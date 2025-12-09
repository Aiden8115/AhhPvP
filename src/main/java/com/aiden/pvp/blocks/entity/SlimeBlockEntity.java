package com.aiden.pvp.blocks.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimeBlockEntity extends BlockEntity {
    private int remainingTicks = 0;
    private static final int TOTAL_TICKS = 400;

    public SlimeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SLIME_BLOCK_ENTITY, pos, state);
    }

    public void startCountdown() {
        this.remainingTicks = TOTAL_TICKS;
        markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, SlimeBlockEntity entity) {
        if (world.isClient()) return; // 只在服务器端处理

        if (entity.remainingTicks > 0) {
            entity.remainingTicks--;

            if (entity.remainingTicks <= 0) {
                // 时间到，移除方块
                world.removeBlock(pos, false);
            }

            entity.markDirty();
        }
    }
}
