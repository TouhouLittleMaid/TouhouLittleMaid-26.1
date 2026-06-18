package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.block.*;
import com.github.tartaricacid.touhoulittlemaid.blockentity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class InitBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TouhouLittleMaid.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TouhouLittleMaid.MOD_ID);

    public static DeferredBlock<Block> PINK_MAID_BED = BLOCKS.register("pink_maid_bed", id -> new BlockMaidBed(id, DyeColor.PINK));
    public static DeferredBlock<Block> WHITE_MAID_BED = BLOCKS.register("white_maid_bed", id -> new BlockMaidBed(id, DyeColor.WHITE));
    public static DeferredBlock<Block> BLACK_MAID_BED = BLOCKS.register("black_maid_bed", id -> new BlockMaidBed(id, DyeColor.BLACK));
    public static DeferredBlock<Block> YELLOW_MAID_BED = BLOCKS.register("yellow_maid_bed", id -> new BlockMaidBed(id, DyeColor.YELLOW));
    public static DeferredBlock<Block> BLUE_MAID_BED = BLOCKS.register("blue_maid_bed", id -> new BlockMaidBed(id, DyeColor.BLUE));
    public static DeferredBlock<Block> GREEN_MAID_BED = BLOCKS.register("green_maid_bed", id -> new BlockMaidBed(id, DyeColor.GREEN));
    public static DeferredBlock<Block> PURPLE_MAID_BED = BLOCKS.register("purple_maid_bed", id -> new BlockMaidBed(id, DyeColor.PURPLE));

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

    public static Supplier<BlockEntityType<BlockEntityAltar>> ALTAR_BE = BLOCK_ENTITY_TYPES.register("altar",
            () -> new BlockEntityType<>(BlockEntityAltar::new, ALTAR.get()));

    public static Supplier<BlockEntityType<BlockEntityStatue>> STATUE_BE = BLOCK_ENTITY_TYPES.register("statue",
            () -> new BlockEntityType<>(BlockEntityStatue::new, STATUE.get()));

    public static Supplier<BlockEntityType<BlockEntityGarageKit>> GARAGE_KIT_BE = BLOCK_ENTITY_TYPES.register("garage_kit", () ->
            new BlockEntityType<>(BlockEntityGarageKit::new, GARAGE_KIT.get()));

    public static Supplier<BlockEntityType<BlockEntityMaidBeacon>> MAID_BEACON_BE = BLOCK_ENTITY_TYPES.register("maid_beacon",
            () -> new BlockEntityType<>(BlockEntityMaidBeacon::new, MAID_BEACON.get()));

    public static Supplier<BlockEntityType<BlockEntityModelSwitcher>> MODEL_SWITCHER_BE = BLOCK_ENTITY_TYPES.register("model_switcher",
            () -> new BlockEntityType<>(BlockEntityModelSwitcher::new, MODEL_SWITCHER.get()));

    public static Supplier<BlockEntityType<BlockEntityGomoku>> GOMOKU_BE = BLOCK_ENTITY_TYPES.register("gomoku",
            () -> new BlockEntityType<>(BlockEntityGomoku::new, GOMOKU.get()));

    public static Supplier<BlockEntityType<BlockEntityCChess>> CCHESS_BE = BLOCK_ENTITY_TYPES.register("cchess",
            () -> new BlockEntityType<>(BlockEntityCChess::new, CCHESS.get()));

    public static Supplier<BlockEntityType<BlockEntityWChess>> WCHESS_BE = BLOCK_ENTITY_TYPES.register("wchess",
            () -> new BlockEntityType<>(BlockEntityWChess::new, WCHESS.get()));

    public static Supplier<BlockEntityType<BlockEntityKeyboard>> KEYBOARD_BE = BLOCK_ENTITY_TYPES.register("keyboard",
            () -> new BlockEntityType<>(BlockEntityKeyboard::new, KEYBOARD.get()));

    public static Supplier<BlockEntityType<BlockEntityBookshelf>> BOOKSHELF_BE = BLOCK_ENTITY_TYPES.register("bookshelf",
            () -> new BlockEntityType<>(BlockEntityBookshelf::new, BOOKSHELF.get()));

    public static Supplier<BlockEntityType<BlockEntityComputer>> COMPUTER_BE = BLOCK_ENTITY_TYPES.register("computer",
            () -> new BlockEntityType<>(BlockEntityComputer::new, COMPUTER.get()));

    public static Supplier<BlockEntityType<BlockEntityShrine>> SHRINE_BE = BLOCK_ENTITY_TYPES.register("shrine",
            () -> new BlockEntityType<>(BlockEntityShrine::new, SHRINE.get()));

    public static Supplier<BlockEntityType<BlockEntityPicnicMat>> PICNIC_MAT_BE = BLOCK_ENTITY_TYPES.register("picnic_mat",
            () -> new BlockEntityType<>(BlockEntityPicnicMat::new, PICNIC_MAT.get()));

    public static Supplier<BlockEntityType<BlockEntitySnackCabinet>> SNACK_CABINET_BE = BLOCK_ENTITY_TYPES.register("snack_cabinet",
            () -> new BlockEntityType<>(BlockEntitySnackCabinet::new, SNACK_CABINET.get()));

    public static Supplier<BlockEntityType<BlockEntityMaidBed>> MAID_BED_BE = BLOCK_ENTITY_TYPES.register("maid_bed",
            () -> new BlockEntityType<>(BlockEntityMaidBed::new,
                    PINK_MAID_BED.get(), WHITE_MAID_BED.get(), BLACK_MAID_BED.get(),
                    YELLOW_MAID_BED.get(), BLUE_MAID_BED.get(), GREEN_MAID_BED.get(),
                    PURPLE_MAID_BED.get()
            )
    );
}
