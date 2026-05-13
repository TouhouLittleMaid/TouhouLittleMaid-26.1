package com.github.tartaricacid.touhoulittlemaid.client.gui.entity.cache;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.texture.CacheIconTexture;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.IModelInfo;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import com.github.tartaricacid.touhoulittlemaid.util.IconCache;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@OnlyIn(Dist.CLIENT)
public class CacheScreen<T extends LivingEntity, E extends IModelInfo> extends Screen {
    protected final Screen parent;
    protected final EntityType<T> entityType;
    protected final Queue<E> modelInfos;
    protected final EntityRender<T, E> entityRender;
    protected final int totalCount;
    protected final StopWatch stopWatch;

    public CacheScreen(Screen parent, EntityType<T> entityType, Queue<E> modelInfos, EntityRender<T, E> entityRender) {
        super(Component.literal("Cache Screen"));
        this.parent = parent;
        this.entityType = entityType;
        this.modelInfos = modelInfos;
        this.entityRender = entityRender;
        this.totalCount = modelInfos.size();
        this.stopWatch = StopWatch.createStarted();
    }

    @SuppressWarnings("unchecked")
    private void drawEntity(GuiGraphicsExtractor graphics, int posX, int posY, E modelInfo, int scaleModified) {
        Level world = getMinecraft().level;
        if (world == null) {
            return;
        }
        T entity;
        try {
            entity = (T) EntityCacheUtil.ENTITY_CACHE.get(entityType, () -> entityType.create(world, EntitySpawnReason.COMMAND));
        } catch (ExecutionException | ClassCastException e) {
            e.fillInStackTrace();
            return;
        }
        entityRender.render(graphics, posX, posY, modelInfo, scaleModified, entity);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (modelInfos.isEmpty()) {
            stopWatch.stop();
            double timeCost = stopWatch.getTime(TimeUnit.MILLISECONDS) / 1000.0;
            TouhouLittleMaid.LOGGER.info("Cache icon time: {} seconds", timeCost);
            Minecraft.getInstance().setScreen(parent);
            return;
        }

        for (int i = 0; i < 5; i++) {
            if (modelInfos.isEmpty()) {
                return;
            }
            doCacheIcon(graphics);
        }

        int finishSize = totalCount - modelInfos.size();
        graphics.centeredText(font, Component.translatable("gui.touhou_little_maid.cache_screen.progress", finishSize, totalCount), this.width / 2, this.height - 42, 0xFFFFFFFF);
        graphics.centeredText(font, Component.translatable("gui.touhou_little_maid.cache_screen.desc"), this.width / 2, this.height - 30, 0xFFFFFFFF);
    }

    protected void doCacheIcon(GuiGraphicsExtractor graphics) {
        E modelInfo = modelInfos.poll();
        if (modelInfo != null) {
            double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
            int scaleModified = (int) Math.ceil((256 / guiScale));

            graphics.fill(0, 0, scaleModified, scaleModified + 2, IconCache.BACKGROUND_COLOR);
            this.drawEntity(graphics, 0, 0, modelInfo, scaleModified);
            NativeImage nativeImage = IconCache.exportImageFromScreenshot(256, IconCache.BACKGROUND_COLOR_SHIFTED);

            Identifier modelId = modelInfo.getModelId();
            CacheIconTexture cacheIconTexture = new CacheIconTexture(modelId, nativeImage);
            Minecraft.getInstance().getTextureManager().register(modelInfo.getCacheIconId(), cacheIconTexture);
        }
    }

    public interface EntityRender<T extends LivingEntity, E extends IModelInfo> {
        void render(GuiGraphicsExtractor graphics, int posX, int posY, E modelInfo, int scaleModified, T entity);
    }
}
