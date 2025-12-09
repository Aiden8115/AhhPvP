package com.aiden.pvp.blocks.entity;

import com.aiden.pvp.PvP;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BossBattleHandlerBlockEntity extends BlockEntity {
    public ArrayList<Entity> players = new ArrayList<>();
    public ServerBossBar bossBar = new ServerBossBar(
            Text.literal("Illusioner"),
            BossBar.Color.YELLOW,
            BossBar.Style.NOTCHED_20
    );

    public BossBattleHandlerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BOSS_BATTLE_HANDLER_BLOCK_ENTITY, pos, state);
    }

    public <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T entity) {
        tick(world, pos, state, (BossBattleHandlerBlockEntity) entity);
    }

    private void tick(World world, BlockPos pos, BlockState state, BossBattleHandlerBlockEntity blockEntity) {
        int x= pos.getX(); int y = pos.getY(); int z = pos.getZ();
        Box box = new Box(
                x + 17, y + 6, z + 17,
                x - 17, y, z - 17
        );
        ArrayList<Entity> illusioners = (ArrayList<Entity>) world.getOtherEntities(
                null,
                box,
                entity -> entity instanceof IllusionerEntity
        );

        if (illusioners.isEmpty()) {
            for (Entity player : players) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    this.bossBar.removePlayer(serverPlayer);
                    serverPlayer.changeGameMode(GameMode.SURVIVAL);
                }
            }
            this.summonChest(world, pos, state, blockEntity);
            return;
        }

        int bossHP = 0;
        for (Entity entity : illusioners) {
            if (entity instanceof IllusionerEntity illusioner) {
                bossHP = (int) (bossHP + illusioner.getHealth());
                bossHP = (int) (bossHP + illusioner.getAbsorptionAmount());
            }
        }

        this.bossBar.setPercent(bossHP <= 80 ? (float) (bossHP / 80.0) : 1);

        Box box2 = new Box(
                x + 17, y + 6, z + 17,
                x - 17, y, z - 17
        );

        for (Entity entity : players) {
            if (entity instanceof ServerPlayerEntity serverPlayer) {
                if (entity.squaredDistanceTo(x, y, z) <= 17 * 17 && serverPlayer.getEntityPos().getY() - y <= 6 && serverPlayer.getEntityPos().getY() - y > 0) {
                    this.bossBar.addPlayer(serverPlayer);
                    serverPlayer.changeGameMode(GameMode.ADVENTURE);
                } else {
                    this.bossBar.removePlayer(serverPlayer);
                    serverPlayer.changeGameMode(GameMode.SURVIVAL);
                }

                if (world.getBlockEntity(pos) != blockEntity) {
                    bossBar.removePlayer(serverPlayer);
                    serverPlayer.changeGameMode(GameMode.SURVIVAL);
                    return;
                }
            }
        }

        this.players = (ArrayList<Entity>) world.getOtherEntities(
                null,
                box2,
                entity -> entity instanceof PlayerEntity
        );
    }

    public void removeBossBarPlayers() {
        for (Entity player : this.players) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                this.bossBar.removePlayer(serverPlayer);
                serverPlayer.changeGameMode(GameMode.SURVIVAL);
            }
        }
    }

    private void summonChest(World world, BlockPos pos, BlockState state, BossBattleHandlerBlockEntity blockEntity) {
        world.setBlockState(pos.up(), Blocks.BARREL.getDefaultState(), 6);
        if (world.getBlockEntity(pos.up()) instanceof BarrelBlockEntity barrelBlockEntity) {
            Identifier lootTableId = Identifier.of(PvP.MOD_ID, "battlefield_boss");
            RegistryKey<LootTable> lootTableRegistryKey = RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId);
            barrelBlockEntity.setLootTable(lootTableRegistryKey, world.random.nextLong());
        }
        world.setBlockState(pos, Blocks.JUNGLE_PLANKS.getDefaultState(), 6);
    }
}
