package com.github.tartaricacid.touhoulittlemaid.client.extensions;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

import java.util.Optional;

public class BlockAltarExtensions implements IClientBlockExtensions {
    @Override
    public boolean addHitEffects(BlockState state, Level world, HitResult target, ParticleEngine manager) {
        if (target instanceof BlockHitResult blockTarget && world instanceof ClientLevel clientLevel) {
            BlockPos pos = blockTarget.getBlockPos();
            this.getAltar(world, pos).ifPresent(altar ->
                    this.crack(clientLevel, pos, altar.getStorageState(), blockTarget.getDirection()));
        }
        return true;
    }

    @Override
    public boolean addDestroyEffects(BlockState state, Level world, BlockPos pos, ParticleEngine manager) {
        this.getAltar(world, pos).ifPresent(altar -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                return;
            }
            level.addDestroyBlockEffect(pos, altar.getStorageState());
        });
        return true;
    }

    private Optional<BlockEntityAltar> getAltar(BlockGetter world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof BlockEntityAltar altar) {
            return Optional.of(altar);
        }
        return Optional.empty();
    }

    private void crack(ClientLevel world, BlockPos pos, BlockState state, Direction side) {
        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            int posX = pos.getX();
            int posY = pos.getY();
            int posZ = pos.getZ();
            AABB aabb = state.getShape(world, pos).bounds();
            RandomSource random = world.getRandom();
            double x = posX + random.nextDouble() * (aabb.maxX - aabb.minX - 0.2) + 0.1 + aabb.minX;
            double y = posY + random.nextDouble() * (aabb.maxY - aabb.minY - 0.2) + 0.1 + aabb.minY;
            double z = posZ + random.nextDouble() * (aabb.maxZ - aabb.minZ - 0.2) + 0.1 + aabb.minZ;
            if (side == Direction.DOWN) {
                y = posY + aabb.minY - 0.1;
            }
            if (side == Direction.UP) {
                y = posY + aabb.maxY + 0.1;
            }
            if (side == Direction.NORTH) {
                z = posZ + aabb.minZ - 0.1;
            }
            if (side == Direction.SOUTH) {
                z = posZ + aabb.maxZ + 0.1;
            }
            if (side == Direction.WEST) {
                x = posX + aabb.minX - 0.1;
            }
            if (side == Direction.EAST) {
                x = posX + aabb.maxX + 0.1;
            }
            TerrainParticle diggingParticle = new TerrainParticle(world, x, y, z, 0, 0, 0, state);
            Minecraft.getInstance().particleEngine.add(diggingParticle.updateSprite(state, pos).setPower(0.2f).scale(0.6f));
        }
    }
}
