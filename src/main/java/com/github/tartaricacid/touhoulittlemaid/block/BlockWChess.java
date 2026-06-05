package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.advancements.maid.TriggerType;
import com.github.tartaricacid.touhoulittlemaid.api.block.IBoardGameBlock;
import com.github.tartaricacid.touhoulittlemaid.api.game.chess.Position;
import com.github.tartaricacid.touhoulittlemaid.block.properties.GomokuPart;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityJoy;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityWChess;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.favorability.Type;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntitySit;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.init.InitTrigger;
import com.github.tartaricacid.touhoulittlemaid.network.message.WChessToClientPackage;
import com.github.tartaricacid.touhoulittlemaid.util.WChessUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BlockWChess extends BlockJoy implements IBoardGameBlock {
    public static final EnumProperty<GomokuPart> PART = EnumProperty.create("part", GomokuPart.class);
    public static final VoxelShape AABB = Block.box(0, 0, 0, 16, 2, 16);

    private static final MapCodec<BlockWChess> CODEC = simpleCodec(BlockWChess::new);

    public BlockWChess(Identifier id) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .mapColor(MapColor.WOOD).sound(SoundType.WOOD)
                .strength(2.0F, 3.0F)
                .forceSolidOn()
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, GomokuPart.CENTER)
                .setValue(FACING, Direction.NORTH));
    }

    public BlockWChess(Properties properties) {
        super(properties);
    }

    private static void handleWChessRemove(Level world, BlockPos pos, BlockState state, @Nullable Player player) {
        if (world.isClientSide()) {
            return;
        }

        GomokuPart part = state.getValue(PART);
        BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
        if (player == null || !player.isCreative()) {
            popResource(world, centerPos, InitItems.WCHESS.get().getDefaultInstance());
        }

        BlockEntity te = world.getBlockEntity(centerPos);
        if (te instanceof BlockEntityWChess) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    world.setBlockAndUpdate(centerPos.offset(i, 0, j), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }


    public static void maidMove(ServerPlayer player, Level level, BlockPos pos, int move, boolean maidLost, boolean playerLost) {
        if (!(level.getBlockEntity(pos) instanceof BlockEntityWChess chess)) {
            return;
        }
        if (chess.isPlayerTurn()) {
            return;
        }

        Position chessData = chess.getChessData();
        UUID sitId = chess.getSitId();

        // 女仆输，以防作弊，再检查一次
        if (maidLost && WChessUtil.isMaid(chessData) && chessData.isMate()) {
            chess.setCheckmate(true);
            chess.refresh();

            if (level instanceof ServerLevel serverLevel
                && serverLevel.getEntity(sitId) instanceof EntitySit sit
                && sit.getFirstPassenger() instanceof EntityMaid maid
                && maid.isOwnedBy(player)
            ) {
                // 暂时不加段位系统
                maid.getFavorabilityManager().apply(Type.WCHESS_WIN);
                maid.getGameManager().markStatue(false);
                InitTrigger.MAID_EVENT.get().trigger(player, TriggerType.WIN_WCHESS);
            }

            return;
        }

        // 如果吃子/移兵了，那么重置计数器（该计数器用于判断限着和长将）
        if (chessData.makeMove(move)) {
            int pcSrc = chessData.squares[Position.SRC(move)];
            if (chessData.captured() || Position.PIECE_TYPE(pcSrc) == Position.PIECE_PAWN) {
                chessData.setIrrev();
            }
        }
        chess.setSelectChessPoint(Position.DST(move));
        chess.setCheckmate(playerLost);

        // 如果玩家没输，那么检查其他和局情况
        if (!playerLost) {
            if (WChessUtil.reachMoveLimit(chessData)) {
                // 判断是否 50 回合限制
                chess.setMoveNumberLimit(true);
            } else if (WChessUtil.isRepeat(chessData)) {
                // 判断是否长打
                chess.setRepeat(true);
            }
        }

        if (level instanceof ServerLevel serverLevel
            && serverLevel.getEntity(sitId) instanceof EntitySit sit
            && sit.getFirstPassenger() instanceof EntityMaid maid
        ) {
            maid.swing(InteractionHand.MAIN_HAND);
            if (playerLost) {
                maid.getGameManager().markStatue(true);
            }
        }

        chess.refresh();
        level.playSound(null, pos, InitSounds.GOMOKU.get(), SoundSource.BLOCKS,
                1.0f, 0.8F + level.getRandom().nextFloat() * 0.4F);
    }

    @Override
    public void startMaidSit(EntityMaid maid, BlockState state, Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(level.getBlockEntity(pos) instanceof BlockEntityJoy joy)) {
            return;
        }
        Entity oldSitEntity = serverLevel.getEntity(joy.getSitId());
        if (oldSitEntity != null && oldSitEntity.isAlive()) {
            return;
        }

        Direction face = state.getValue(FACING).getOpposite();
        Vec3 position = new Vec3(0.5 + face.getStepX() * 2, 0.1, 0.5 + face.getStepZ() * 2);
        Vec3 corner = Vec3.atLowerCornerWithOffset(pos, position.x, position.y, position.z);
        EntitySit newSitEntity = new EntitySit(level, corner, this.getTypeName(), pos);
        newSitEntity.setYRot(face.getOpposite().toYRot() + this.sitYRot());
        level.addFreshEntity(newSitEntity);

        joy.setSitId(newSitEntity.getUUID());
        joy.setChanged();

        maid.startRiding(newSitEntity);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        handleWChessRemove(world, pos, state, player);
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, ServerLevel world, BlockPos pos, Explosion explosion) {
        handleWChessRemove(world, pos, state, null);
        super.onBlockExploded(state, world, pos, explosion);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos centerPos = context.getClickedPos();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos searchPos = centerPos.offset(i, 0, j);
                if (!context.getLevel().getBlockState(searchPos).canBeReplaced(context)) {
                    return null;
                }
            }
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide()) {
            return;
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos searchPos = pos.offset(i, 0, j);
                GomokuPart part = GomokuPart.getPartByPos(i, j);
                if (part != null && !part.isCenter()) {
                    worldIn.setBlock(searchPos, state.setValue(PART, part), Block.UPDATE_ALL);
                }
            }
        }
    }

    @Override
    public InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos,
                                       Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        GomokuPart part = state.getValue(PART);
        BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
        BlockEntity te = level.getBlockEntity(centerPos);

        if (!(te instanceof BlockEntityWChess chess)) {
            return InteractionResult.FAIL;
        }

        // 女仆思考时间，不允许玩家操作
        if (!chess.isPlayerTurn() && !chess.isCheckmate()) {
            return InteractionResult.FAIL;
        }

        ItemStack heldItem = player.getMainHandItem();
        // 只能空手操作
        if (!heldItem.isEmpty()) {
            return InteractionResult.FAIL;
        }

        // 点击坐标的转换
        Direction facing = state.getValue(FACING);
        Vec3 clickPos = hit.getLocation()
                .subtract(pos.getX(), pos.getY(), pos.getZ())
                .add(part.getPosX() - 0.5, 0, part.getPosY() - 0.5)
                .yRot(facing.toYRot() * Mth.DEG_TO_RAD);

        // 重置棋盘
        boolean clickResetArea = WChessUtil.isClickResetArea(clickPos);
        if (clickResetArea) {
            level.playSound(null, centerPos, InitSounds.GOMOKU_RESET.get(),
                    SoundSource.BLOCKS, 1.0f, 1.0f);
            chess.reset();
            chess.refresh();

            // 重置女仆棋类动画
            Entity sitEntity = serverLevel.getEntity(chess.getSitId());
            if (sitEntity != null
                && sitEntity.isAlive()
                && sitEntity.getFirstPassenger() instanceof EntityMaid maid
            ) {
                maid.getGameManager().resetStatue();
            }

            return InteractionResult.SUCCESS_SERVER;
        }

        // 检查女仆
        Entity sitEntity = serverLevel.getEntity(chess.getSitId());
        if (sitEntity == null || !sitEntity.isAlive() || !(sitEntity.getFirstPassenger() instanceof EntityMaid maid)) {
            player.sendSystemMessage(Component.translatable("message.touhou_little_maid.gomoku.no_maid"));
            return InteractionResult.FAIL;
        }

        // 检查是不是自己的女仆
        if (MaidConfig.MAID_GOMOKU_OWNER_LIMIT.get() && !maid.isOwnedBy(player)) {
            player.sendSystemMessage(Component.translatable("message.touhou_little_maid.gomoku.not_owner"));
            return InteractionResult.FAIL;
        }

        // 没有点击到棋盘上，返回
        int nowClick = WChessUtil.getClickPosition(clickPos);
        if (nowClick < 0 || !Position.IN_BOARD(nowClick)) {
            return InteractionResult.FAIL;
        }

        // 玩家已经输了，不能下棋
        if (chess.isCheckmate() && chess.isPlayerTurn()) {
            return InteractionResult.FAIL;
        }

        // 50 回合自然限着、长将不能下棋
        if (chess.isMoveNumberLimit() || chess.isRepeat()) {
            return InteractionResult.FAIL;
        }

        // 处理点击棋子的逻辑
        Position chessData = chess.getChessData();
        byte[] squares = chessData.squares;
        int preClick = chess.getSelectChessPoint();
        if (preClick < 0 || squares.length <= preClick) {
            preClick = 0;
        }
        byte prePiece = squares[preClick];
        byte nowPiece = squares[nowClick];

        // 如果前一个选择为空，或者选中的是黑方，说明没有选中棋子
        if (prePiece <= 0 || WChessUtil.isBlack(prePiece)) {
            // 当前点击的是白方棋子
            if (WChessUtil.isWhite(nowPiece)) {
                chess.setSelectChessPoint(nowClick);
                chess.refresh();
                level.playSound(null, pos, InitSounds.GOMOKU.get(), SoundSource.BLOCKS, 1.0f, 0.8F + level.getRandom().nextFloat() * 0.4F);
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        // 如果选的都是白方棋子，重选
        if (WChessUtil.isWhite(prePiece) && WChessUtil.isWhite(nowPiece)) {
            chess.setSelectChessPoint(nowClick);
            chess.refresh();
            level.playSound(null, pos, InitSounds.GOMOKU.get(), SoundSource.BLOCKS, 1.0f, 0.8F + level.getRandom().nextFloat() * 0.4F);
            return InteractionResult.SUCCESS_SERVER;
        }

        // 判断移动是否合法
        int move = Position.MOVE(preClick, nowClick);
        if (!chessData.legalMove(move)) {
            return InteractionResult.FAIL;
        }

        // 没有将军，正常移动
        boolean notChecked = chessData.makeMove(move);
        if (notChecked) {
            int pcSrc = chessData.squares[Position.SRC(move)];
            // 如果吃子、动兵了，那么重置计数器（该计数器用于判断自然限着和长将）
            if (chessData.captured() || Position.PIECE_TYPE(pcSrc) == Position.PIECE_PAWN) {
                chessData.setIrrev();
            }
            chess.addChessCounter();
            chess.setSelectChessPoint(nowClick);
            chess.refresh();
            level.playSound(null, pos, InitSounds.GOMOKU.get(), SoundSource.BLOCKS, 1.0f, 0.8F + level.getRandom().nextFloat() * 0.4F);
            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new WChessToClientPackage(centerPos, chessData.toFen()));
            }
            return InteractionResult.SUCCESS_SERVER;
        }

        // 如果将军，那么给予提示
        player.sendSystemMessage(Component.translatable("message.touhou_little_maid.cchess.check"));
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.BLOCKS, 1.0f, 0.8F + level.getRandom().nextFloat() * 0.4F);
        return InteractionResult.FAIL;
    }

    @Override
    protected Vec3 sitPosition() {
        return Vec3.ZERO;
    }

    @Override
    protected String getTypeName() {
        return Type.GOMOKU.getTypeName();
    }

    @Override
    protected int sitYRot() {
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(PART).isCenter()) {
            return new BlockEntityWChess(pos, state);
        }
        return null;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }
}
