package com.github.tartaricacid.touhoulittlemaid.client.tooltip;

import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.tooltip.ItemMaidTooltip;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.models.SpecialMaidModelResolver.EASTER_EGG_MODEL;
import static com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil.clearMaidDataResidue;

public class ClientMaidTooltip implements ClientTooltipComponent {
    private static final Vector3f ZERO = new Vector3f();

    private final @Nullable MaidModelInfo info;
    private final @Nullable Component customName;
    private final MutableComponent name;

    public ClientMaidTooltip(ItemMaidTooltip tooltip) {
        this.info = CustomPackLoader.MAID_MODELS.getInfo(tooltip.modelId()).orElse(null);
        this.customName = tooltip.customName();
        this.name = this.getName(this.info);
    }

    private MutableComponent getName(@Nullable MaidModelInfo info) {
        if (info == null) {
            return Component.empty();
        }
        String key = ParseI18n.getI18nKey(info.getName());
        return Component.translatable(key);
    }

    @Override
    public int getHeight(Font font) {
        return 70;
    }

    @Override
    public int getWidth(Font font) {
        return Math.max(font.width(this.name), 50);
    }

    @Override
    public void extractImage(Font font, int pX, int pY, int w, int h, GuiGraphicsExtractor guiGraphics) {
        if (info == null) {
            return;
        }

        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        if (this.customName instanceof MutableComponent mutableComponent) {
            guiGraphics.text(font, mutableComponent.withStyle(ChatFormatting.GRAY), pX, pY + 2, 0xFFFFFFFF);
        } else {
            guiGraphics.text(font, name.withStyle(ChatFormatting.GRAY), pX, pY + 2, 0xFFFFFFFF);
        }

        float scale = 25 * info.getRenderItemScale();
        int width = this.getWidth(font);

        int x0 = pX;
        int x1 = pX + width;
        int y0 = pY + 16;
        int y1 = (int) (y0 + 4 * scale);

        double rot = ((System.currentTimeMillis() / 25.0) % 360);
        Quaternionf pose = (new Quaternionf()).rotateZ(Mth.PI);
        Quaternionf rotation = (new Quaternionf()).rotateY((float) Math.toRadians(rot));
        pose.mul(rotation);

        EntityMaid maid = EntityCacheUtil.getMaid(world, EntitySpawnReason.EVENT);
        maid.renderState = MaidRenderState.GUI;
        clearMaidDataResidue(maid, false);
        if (customName != null) {
            maid.setCustomName(customName);
        }
        if (info.getEasterEgg() != null) {
            maid.setModelId(EASTER_EGG_MODEL);
        } else {
            maid.setModelId(info.getModelId().toString());
        }

        EntityRenderState renderState = extractRenderState(maid);
        guiGraphics.entity(renderState, scale, ZERO, pose, null, x0, y0, x1, y1);
    }

    private static EntityRenderState extractRenderState(LivingEntity entity) {
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> renderer = entityRenderDispatcher.getRenderer(entity);
        EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;
        return renderState;
    }
}
