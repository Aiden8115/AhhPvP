package com.aiden.pvp.blocks;

import com.aiden.pvp.blocks.entity.ModBlockEntityTypes;
import com.aiden.pvp.blocks.entity.SlimeBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SlimeBlock extends net.minecraft.block.SlimeBlock implements BlockEntityProvider {
    public SlimeBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SlimeBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(
            World world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        // 方块放置时，在服务器端启动计时
        if (!world.isClient()) {
            world.getBlockEntity(pos, ModBlockEntityTypes.SLIME_BLOCK_ENTITY)
                    .ifPresent(SlimeBlockEntity::startCountdown);
        }
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : (w, p, s, be) -> SlimeBlockEntity.tick(w, p, s, (SlimeBlockEntity) be);
    }
}
