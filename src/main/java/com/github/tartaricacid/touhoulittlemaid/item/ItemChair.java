package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent.*;

@SuppressWarnings("deprecation")
public class ItemChair extends Item {
    private static final String DEFAULT_MODEL_ID = "touhou_little_maid:cushion";

    public ItemChair(Identifier id) {
        super((new Properties())
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
    }

    public static Data getData(ItemStack stack) {
        if (stack.is(InitItems.CHAIR)) {
            return Data.deserialization(stack);
        }
        return new Data(DEFAULT_MODEL_ID, 0f, true, false);
    }

    public static ItemStack setData(ItemStack stack, Data data) {
        if (stack.is(InitItems.CHAIR)) {
            Data.serialization(stack, data);
        }
        return stack;
    }

    public static void fillItemCategory(CreativeModeTab.Output items) {
        for (String key : CustomPackLoader.CHAIR_MODELS.getModelIdSet()) {
            float height = CustomPackLoader.CHAIR_MODELS.getModelMountedYOffset(key);
            boolean canRide = CustomPackLoader.CHAIR_MODELS.getModelTameableCanRide(key);
            boolean isNoGravity = CustomPackLoader.CHAIR_MODELS.getModelNoGravity(key);

            ItemStack stack = InitItems.CHAIR.get().getDefaultInstance();
            Data data = new Data(key, height, canRide, isNoGravity);
            items.accept(setData(stack, data));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.FAIL;
        }

        Level world = context.getLevel();
        BlockPos clickedPos = new BlockPlaceContext(context).getClickedPos();
        Vec3 centerOf = Vec3.atBottomCenterOf(clickedPos);
        AABB boundingBox = EntityChair.TYPE.getDimensions().makeBoundingBox(centerOf);

        if (!world.noCollision(boundingBox) || !world.getEntities(null, boundingBox).isEmpty()) {
            return InteractionResult.FAIL;
        }

        ItemStack stack = context.getItemInHand();
        if (world instanceof ServerLevel serverWorld) {
            EntityChair chair = getSpawnChair(
                    serverWorld, context.getPlayer(), stack,
                    context.getClickedPos(), context.getRotation()
            );
            if (chair == null) {
                return InteractionResult.FAIL;
            }
            world.addFreshEntity(chair);
            world.playSound(null, chair.getX(), chair.getY(), chair.getZ(),
                    SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
        }

        stack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Nullable
    public static EntityChair getSpawnChair(ServerLevel serverWorld, @Nullable Player player,
                                            ItemStack stack, BlockPos pos, float rotation) {
        EntityChair chair = EntityChair.TYPE.create(serverWorld, e -> {
            Component customName = stack.get(DataComponents.CUSTOM_NAME);
            if (customName != null) {
                e.setCustomName(customName);
            }
        }, pos, EntitySpawnReason.SPAWN_ITEM_USE, true, true);
        if (chair != null) {
            addExtraData(player, stack, chair, rotation);
        }
        return chair;
    }

    private static void addExtraData(@Nullable Player player, ItemStack stack, EntityChair chair, float rotation) {
        Data data = Data.deserialization(stack);

        chair.setModelId(data.modelId());
        chair.setMountedHeight(data.height());
        chair.setTameableCanRide(data.canRide());
        chair.setNoGravity(data.isNoGravity());
        chair.setOwner(player);

        float wrapped = Mth.wrapDegrees(rotation - 180.0F);
        float yaw = Mth.floor(wrapped / 45.0F + 0.5) * 45.0F;
        chair.snapTo(chair.getX(), chair.getY(), chair.getZ(), yaw, 0.0F);

        chair.setYBodyRot(yaw);
        chair.setYHeadRot(yaw);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            // 添加坐垫前缀，方便搜索
            MutableComponent prefix = Component.translatable("item.touhou_little_maid.chair.prefix");
            ItemChair.Data data = getData(stack);
            String modelId = data.modelId();
            if (CustomPackLoader.CHAIR_MODELS.getInfo(modelId).isPresent()) {
                String name = CustomPackLoader.CHAIR_MODELS.getInfo(modelId).get().getName();
                return prefix.append(ParseI18n.parse(name));
            }
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext worldIn, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("tooltips.touhou_little_maid.chair.place.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltips.touhou_little_maid.chair.destroy.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltips.touhou_little_maid.chair.gui.desc").withStyle(ChatFormatting.GRAY));

        // 调试模式，不加国际化
        if (flagIn.isAdvanced() && flagIn.hasShiftDown()) {
            Data data = Data.deserialization(stack);
            tooltip.accept(Component.literal("Model Id: " + data.modelId()).withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.literal("Mounted Height: " + data.height()).withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.literal("Tameable Can Ride: " + data.canRide()).withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.literal("Is No Gravity: " + data.isNoGravity()).withStyle(ChatFormatting.GRAY));
        }
    }

    public record Data(String modelId, float height, boolean canRide, boolean isNoGravity) {
        public static void serialization(ItemStack stack, Data data) {
            stack.set(MODEL_ID_TAG, data.modelId());
            stack.set(MOUNTED_HEIGHT_TAG, data.height());
            stack.set(TAMEABLE_CAN_RIDE_TAG, data.canRide());
            stack.set(IS_NO_GRAVITY_TAG, data.isNoGravity());
        }

        public static Data deserialization(ItemStack stack) {
            String modelId = Objects.requireNonNullElse(stack.get(MODEL_ID_TAG), DEFAULT_MODEL_ID);
            float height = Objects.requireNonNullElse(stack.get(MOUNTED_HEIGHT_TAG), 0f);
            boolean canRide = Objects.requireNonNullElse(stack.get(TAMEABLE_CAN_RIDE_TAG), true);
            boolean isNoGravity = Objects.requireNonNullElse(stack.get(IS_NO_GRAVITY_TAG), false);
            return new Data(modelId, height, canRide, isNoGravity);
        }
    }
}
