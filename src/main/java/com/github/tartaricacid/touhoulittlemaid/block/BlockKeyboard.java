package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityKeyboard;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockKeyboard extends BlockJoy {
    public static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 10, 12);
    private static final MapCodec<BlockKeyboard> CODEC = simpleCodec(BlockKeyboard::new);

    public BlockKeyboard(Properties properties) {
        super(properties);
    }

    public BlockKeyboard(Identifier id) {
        super(id);
    }

    @Override
    protected Vec3 sitPosition() {
        return new Vec3(0.5, 0.625, 0.5);
    }

    @Override
    protected int sitYRot() {
        return 0;
    }

    @Override
    protected String getTypeName() {
        return Type.KEYBOARD.getTypeName();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BlockEntityKeyboard(pPos, pState);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
