package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.texture.SizeTexture;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.github.tartaricacid.touhoulittlemaid.client.sound.pojo.SoundPackInfo;
import com.github.tartaricacid.touhoulittlemaid.util.GuiTools;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SoundPackButton extends FlatColorButton {
    private static final Identifier ICON = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "textures/gui/maid_custom_sound.png");
    private final SoundPackInfo info;
    private boolean isUse = false;

    public SoundPackButton(int pX, int pY, SoundPackInfo info, OnPress onPress) {
        super(pX, pY, 230, 43, Component.empty(), onPress);
        this.info = info;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pPartialTick) {
        super.extractContents(graphics, mouseX, mouseY, pPartialTick);
        Identifier icon = info.getIcon();
        if (icon == null) {
            GuiTools.blit(graphics, ICON, this.getX() + 4, this.getY() + 5, 32, 32, 0, 16, 32, 32, 256, 256);
        } else {
            if (info.getIconAnimation() == CustomModelPack.AnimationState.UNCHECK) {
                checkIconAnimation(info, icon);
            }
            if (info.getIconAnimation() == CustomModelPack.AnimationState.FALSE) {
                GuiTools.blit(graphics, icon, this.getX() + 4, this.getY() + 5,
                        32, 32, 0, 0, 32, 32, 32, 32);
            } else {
                int time = getTickTime() / info.getIconDelay();
                int iconIndex = time % info.getIconAspectRatio();
                GuiTools.blit(graphics, icon, this.getX() + 4, this.getY() + 5,
                        32, 32, 0, iconIndex * 32, 32, 32, 32, 32 * info.getIconAspectRatio());
            }
        }
        if (isUse) {
            GuiTools.blit(graphics, ICON, this.getX() + this.getWidth() - 20, this.getY() + 13, 16, 16, 32, 0, 16, 16, 256, 256);
        }
    }

    @Override
    @SuppressWarnings("all")
    public void renderString(GuiGraphicsExtractor graphics, Font font, int pColor) {
        int startX = this.getX() + 42;
        int startY = this.getY() + 7;

        MutableComponent packName = ParseI18n.parse(info.getPackName());
        String version = info.getVersion();
        List<String> author = info.getAuthor();
        String date = info.getDate();

        graphics.text(font, packName, startX, startY, ChatFormatting.WHITE.getColor());

        if (StringUtils.isNotBlank(version)) {
            int titleWidth = font.width(packName);
            graphics.text(font, "§nv" + version, startX + titleWidth + 5, startY, ChatFormatting.AQUA.getColor());
        }

        if (!author.isEmpty()) {
            startY += 10;
            String authorListText = StringUtils.joinWith(I18n.get("gui.touhou_little_maid.resources_download.author.delimiter"), author);
            String authorText = I18n.get("gui.touhou_little_maid.resources_download.author", authorListText);
            graphics.text(font, authorText, startX, startY, ChatFormatting.GOLD.getColor());
        }

        if (StringUtils.isNotBlank(date)) {
            startY += 10;
            MutableComponent dateText = Component.translatable("gui.touhou_little_maid.skin.text.date", date);
            graphics.text(font, dateText, startX, startY, ChatFormatting.GREEN.getColor());
        }
    }

    private int getTickTime() {
        return (int) System.currentTimeMillis() / 50;
    }

    private void checkIconAnimation(SoundPackInfo info, Identifier icon) {
        AbstractTexture iconText = Minecraft.getInstance().getTextureManager().getTexture(icon);
        if (iconText instanceof SizeTexture) {
            int width = ((SizeTexture) iconText).getWidth();
            int height = ((SizeTexture) iconText).getHeight();
            if (width >= height) {
                info.setIconAnimation(CustomModelPack.AnimationState.FALSE);
            } else {
                info.setIconAnimation(CustomModelPack.AnimationState.TRUE);
                info.setIconAspectRatio(height / width);
            }
        } else {
            info.setIconAnimation(CustomModelPack.AnimationState.FALSE);
        }
    }

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }
}
