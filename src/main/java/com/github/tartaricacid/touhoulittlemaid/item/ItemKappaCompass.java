package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.google.common.collect.Maps;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.KAPPA_COMPASS_ACTIVITY_POS;
import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.KAPPA_COMPASS_DIMENSION;

@SuppressWarnings("deprecation")
public class ItemKappaCompass extends Item {
    public ItemKappaCompass(Identifier id) {
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    public static void addPoint(Activity activity, BlockPos pos, ItemStack compass) {
        Map<String, BlockPos> activityPos = compass.getOrDefault(KAPPA_COMPASS_ACTIVITY_POS, Maps.newHashMap());
        activityPos.put(activity.getName(), pos);
        compass.set(KAPPA_COMPASS_ACTIVITY_POS, activityPos);
    }

    public static void addDimension(Identifier dimension, ItemStack compass) {
        compass.set(KAPPA_COMPASS_DIMENSION, dimension.toString());
    }

    @Nullable
    public static BlockPos getPoint(Activity activity, ItemStack compass) {
        Map<String, BlockPos> activityPos = compass.get(KAPPA_COMPASS_ACTIVITY_POS);
        if (activityPos != null) {
            // 尝试获取指定活动的坐标，如果没有则尝试复用 IDLE 和 WORK 的坐标
            String name = activity.getName();
            if (activityPos.containsKey(name)) {
                return activityPos.get(name);
            }
            name = Activity.IDLE.getName();
            if (activityPos.containsKey(name)) {
                return activityPos.get(name);
            }
            name = Activity.WORK.getName();
            if (activityPos.containsKey(name)) {
                return activityPos.get(name);
            }
        }
        return null;
    }

    @Nullable
    public static Identifier getDimension(ItemStack compass) {
        String dim = compass.get(KAPPA_COMPASS_DIMENSION);
        if (dim != null) {
            return Identifier.parse(dim);
        }
        return null;
    }

    public static int getRecordCount(ItemStack compass) {
        Map<String, BlockPos> activityPos = compass.get(KAPPA_COMPASS_ACTIVITY_POS);
        if (activityPos != null) {
            return activityPos.size();
        }
        return 0;
    }

    public static boolean hasKappaCompassData(ItemStack compass) {
        return compass.has(KAPPA_COMPASS_ACTIVITY_POS) && compass.has(KAPPA_COMPASS_DIMENSION);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack compass, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (!(livingEntity instanceof EntityMaid maid) || maid.level.isClientSide()) {
            return super.interactLivingEntity(compass, player, livingEntity, hand);
        }

        // 潜行清除记录
        if (player.isDiscrete()) {
            maid.getSchedulePos().clear(maid);
            player.sendSystemMessage(Component.translatable("message.touhou_little_maid.kappa_compass.maid_clear"));
            playCompassSound(player);
            return InteractionResult.SUCCESS;
        }

        Identifier dimension = getDimension(compass);
        if (!hasKappaCompassData(compass) || dimension == null) {
            player.sendSystemMessage(Component.translatable("message.touhou_little_maid.kappa_compass.no_data"));
            return InteractionResult.CONSUME;
        }
        if (!maid.level.dimension().identifier().equals(dimension)) {
            player.sendSystemMessage(Component.translatable("message.touhou_little_maid.kappa_compass.maid_dimension_check"));
            return InteractionResult.CONSUME;
        }

        writeCompassDataToMaid(compass, maid, dimension);
        player.sendSystemMessage(Component.translatable("message.touhou_little_maid.kappa_compass.maid_write"));
        playCompassSound(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack compass = context.getItemInHand();
        BlockPos pos = context.getClickedPos();

        if (player == null) {
            return super.useOn(context);
        }

        // 潜行清除数据
        if (player.isDiscrete()) {
            compass.remove(KAPPA_COMPASS_ACTIVITY_POS);
            compass.remove(KAPPA_COMPASS_DIMENSION);
            sendMessage(player, Component.translatable("message.touhou_little_maid.kappa_compass.clear"));
            playCompassSound(player);
            return InteractionResult.SUCCESS;
        }

        int recordCount = getRecordCount(compass);
        if (recordCount >= 3) {
            sendMessage(player, Component.translatable("message.touhou_little_maid.kappa_compass.full"));
            addDimension(player.level.dimension().identifier(), compass);
            playCompassSound(player);
            return InteractionResult.SUCCESS;
        }

        Activity activity = getNextRecordActivity(recordCount);
        BlockPos previousPos = getPreviousRecordPos(activity, compass);
        Identifier dimension = getDimension(compass);

        // 距离过远，不允许设置，以免玩家弄丢女仆
        if (previousPos != null && previousPos.distSqr(pos) > 64 * 64) {
            sendMessage(player, Component.translatable("message.touhou_little_maid.kappa_compass.far_away"));
            return super.useOn(context);
        }

        // 维度不同，不允许设置
        if (dimension != null && !player.level.dimension().identifier().equals(dimension)) {
            sendMessage(player, Component.translatable("message.touhou_little_maid.kappa_compass.diff_dimension"));
            return super.useOn(context);
        }

        addPoint(activity, pos, compass);
        addDimension(player.level.dimension().identifier(), compass);

        String key = getPointMessageKey(activity);
        MutableComponent msg = Component.translatable(key, pos.getX(), pos.getY(), pos.getZ());
        sendMessage(player, msg);

        playCompassSound(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext worldIn, TooltipDisplay display,
                                Consumer<Component> components, TooltipFlag flagIn) {
        if (hasKappaCompassData(stack)) {
            Identifier dimension = getDimension(stack);
            if (dimension != null) {
                components.accept(Component
                        .translatable("tooltips.touhou_little_maid.fox_scroll.dimension", dimension.toString())
                        .withStyle(ChatFormatting.GOLD)
                );
            }
            appendPointTooltip(components, Activity.WORK, stack, ChatFormatting.RED);
            appendPointTooltip(components, Activity.IDLE, stack, ChatFormatting.GREEN);
            appendPointTooltip(components, Activity.REST, stack, ChatFormatting.BLUE);
            components.accept(Component.empty());
        }

        components.accept(Component.translatable("message.touhou_little_maid.kappa_compass.usage.set_pos"));
        components.accept(Component.translatable("message.touhou_little_maid.kappa_compass.usage.clear_pos"));
        components.accept(Component.translatable("message.touhou_little_maid.kappa_compass.usage.write_pos_to_maid"));
        components.accept(Component.translatable("message.touhou_little_maid.kappa_compass.usage.clear_maid_pos"));
    }

    private void writeCompassDataToMaid(ItemStack compass, EntityMaid maid, Identifier dimension) {
        maid.getSchedulePos().setDimension(dimension);
        BlockPos point = getPoint(Activity.WORK, compass);
        if (point != null) {
            maid.getSchedulePos().setWorkPos(point);
        }
        point = getPoint(Activity.IDLE, compass);
        if (point != null) {
            maid.getSchedulePos().setIdlePos(point);
        }
        point = getPoint(Activity.REST, compass);
        if (point != null) {
            maid.getSchedulePos().setSleepPos(point);
        }
        maid.getSchedulePos().setConfigured(true);
        maid.getSchedulePos().restrictTo(maid);
    }

    private Activity getNextRecordActivity(int recordCount) {
        if (recordCount == 0) {
            return Activity.WORK;
        }
        if (recordCount == 1) {
            return Activity.IDLE;
        }
        return Activity.REST;
    }

    @Nullable
    private BlockPos getPreviousRecordPos(Activity activity, ItemStack compass) {
        if (activity == Activity.IDLE) {
            return getPoint(Activity.WORK, compass);
        }
        if (activity == Activity.REST) {
            return getPoint(Activity.IDLE, compass);
        }
        return null;
    }

    private void appendPointTooltip(Consumer<Component> components, Activity activity,
                                    ItemStack stack, ChatFormatting formatting) {
        BlockPos pos = getPoint(activity, stack);
        if (pos != null) {
            String key = getPointMessageKey(activity);
            MutableComponent msg = Component
                    .translatable(key, pos.getX(), pos.getY(), pos.getZ())
                    .withStyle(formatting);
            components.accept(msg);
        }
    }

    private String getPointMessageKey(Activity activity) {
        if (activity == Activity.WORK) {
            return "message.touhou_little_maid.kappa_compass.work";
        }
        if (activity == Activity.IDLE) {
            return "message.touhou_little_maid.kappa_compass.idle";
        }
        return "message.touhou_little_maid.kappa_compass.sleep";
    }

    private void sendMessage(Player player, Component component) {
        if (!player.level.isClientSide()) {
            player.sendSystemMessage(component);
        }
    }

    private void playCompassSound(Player player) {
        player.level.playSound(null, player.blockPosition(),
                InitSounds.COMPASS_POINT.get(), SoundSource.PLAYERS,
                0.8f, 1.5f);
    }
}