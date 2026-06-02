package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.block.*;
import com.github.tartaricacid.touhoulittlemaid.tileentity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class InitBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TouhouLittleMaid.MOD_ID);

    public static DeferredBlock<Block> MAID_BED = BLOCKS.register("maid_bed", BlockMaidBed::new);
    public static DeferredBlock<Block> ALTAR = BLOCKS.register("altar", BlockAltar::new);
    public static DeferredBlock<Block> STATUE = BLOCKS.register("statue", BlockStatue::new);
    public static DeferredBlock<Block> GARAGE_KIT = BLOCKS.register("garage_kit", BlockGarageKit::new);
    public static DeferredBlock<Block> MAID_BEACON = BLOCKS.register("maid_beacon", BlockMaidBeacon::new);
    public static DeferredBlock<Block> MODEL_SWITCHER = BLOCKS.register("model_switcher", BlockModelSwitcher::new);
    public static DeferredBlock<Block> PICNIC_MAT = BLOCKS.register("picnic_mat", BlockPicnicMat::new);
    public static DeferredBlock<Block> GOMOKU = BLOCKS.register("gomoku", BlockGomoku::new);
    public static DeferredBlock<Block> CCHESS = BLOCKS.register("cchess", BlockCChess::new);
    public static DeferredBlock<Block> WCHESS = BLOCKS.register("wchess", BlockWChess::new);
    public static DeferredBlock<Block> KEYBOARD = BLOCKS.register("keyboard", BlockKeyboard::new);
    public static DeferredBlock<Block> BOOKSHELF = BLOCKS.register("bookshelf", BlockBookshelf::new);
    public static DeferredBlock<Block> COMPUTER = BLOCKS.register("computer", BlockComputer::new);
    public static DeferredBlock<Block> SHRINE = BLOCKS.register("shrine", BlockShrine::new);
    public static DeferredBlock<Block> SCARECROW = BLOCKS.register("scarecrow", BlockScarecrow::new);
    public static DeferredBlock<Block> SNACK_CABINET = BLOCKS.register("snack_cabinet", BlockSnackCabinet::new);

    public static Supplier<BlockEntityType<TileEntityAltar>> ALTAR_TE = BLOCK_ENTITY_TYPES.register("altar",
            () -> new BlockEntityType<>(TileEntityAltar::new, ALTAR.get()));

    public static Supplier<BlockEntityType<TileEntityStatue>> STATUE_TE = BLOCK_ENTITY_TYPES.register("statue",
            () -> new BlockEntityType<>(TileEntityStatue::new, STATUE.get()));

    public static Supplier<BlockEntityType<TileEntityGarageKit>> GARAGE_KIT_TE = BLOCK_ENTITY_TYPES.register("garage_kit", () ->
            new BlockEntityType<>(TileEntityGarageKit::new, GARAGE_KIT.get()));

    public static Supplier<BlockEntityType<TileEntityMaidBeacon>> MAID_BEACON_TE = BLOCK_ENTITY_TYPES.register("maid_beacon",
            () -> new BlockEntityType<>(TileEntityMaidBeacon::new, MAID_BEACON.get()));

    public static Supplier<BlockEntityType<TileEntityModelSwitcher>> MODEL_SWITCHER_TE = BLOCK_ENTITY_TYPES.register("model_switcher",
            () -> new BlockEntityType<>(TileEntityModelSwitcher::new, MODEL_SWITCHER.get()));

    public static Supplier<BlockEntityType<TileEntityGomoku>> GOMOKU_TE = BLOCK_ENTITY_TYPES.register("gomoku",
            () -> new BlockEntityType<>(TileEntityGomoku::new, GOMOKU.get()));

    public static Supplier<BlockEntityType<TileEntityCChess>> CCHESS_TE = BLOCK_ENTITY_TYPES.register("cchess",
            () -> new BlockEntityType<>(TileEntityCChess::new, CCHESS.get()));

    public static Supplier<BlockEntityType<TileEntityWChess>> WCHESS_TE = BLOCK_ENTITY_TYPES.register("wchess",
            () -> new BlockEntityType<>(TileEntityWChess::new, WCHESS.get()));

    public static Supplier<BlockEntityType<TileEntityKeyboard>> KEYBOARD_TE = BLOCK_ENTITY_TYPES.register("keyboard",
            () -> new BlockEntityType<>(TileEntityKeyboard::new, KEYBOARD.get()));

    public static Supplier<BlockEntityType<TileEntityBookshelf>> BOOKSHELF_TE = BLOCK_ENTITY_TYPES.register("bookshelf",
            () -> new BlockEntityType<>(TileEntityBookshelf::new, BOOKSHELF.get()));

    public static Supplier<BlockEntityType<TileEntityComputer>> COMPUTER_TE = BLOCK_ENTITY_TYPES.register("computer",
            () -> new BlockEntityType<>(TileEntityComputer::new, COMPUTER.get()));

    public static Supplier<BlockEntityType<TileEntityShrine>> SHRINE_TE = BLOCK_ENTITY_TYPES.register("shrine",
            () -> new BlockEntityType<>(TileEntityShrine::new, SHRINE.get()));

    public static Supplier<BlockEntityType<TileEntityPicnicMat>> PICNIC_MAT_TE = BLOCK_ENTITY_TYPES.register("picnic_mat",
            () -> new BlockEntityType<>(TileEntityPicnicMat::new, PICNIC_MAT.get()));

    public static Supplier<BlockEntityType<TileEntityMaidBed>> MAID_BED_TE = BLOCK_ENTITY_TYPES.register("maid_bed",
            () -> new BlockEntityType<>(TileEntityMaidBed::new, MAID_BED.get()));

    public static Supplier<BlockEntityType<TileEntitySnackCabinet>> SNACK_CABINET_TE = BLOCK_ENTITY_TYPES.register("snack_cabinet",
            () -> new BlockEntityType<>(TileEntitySnackCabinet::new, SNACK_CABINET.get()));
}
