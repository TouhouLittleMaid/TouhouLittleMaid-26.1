package com.github.tartaricacid.touhoulittlemaid.api.event.client;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.GeckoContainerBuilder;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.file.AnimationFile;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;

/**
 * 在客户端加载额外的默认 Gecko 动画文件。
 * <p>
 * 注意：此事件执行的时间很早，甚至早于 LittleMaidExtension 注解的识别加载的时间点。<br>
 * 故此事件需要在模组初始化时，手动注册到 MinecraftForge.EVENT_BUS 上。
 */
public class DefaultGeckoAnimationEvent extends Event {
    private final EnumMap<AnimationType, AnimationFile> animationFiles;

    public DefaultGeckoAnimationEvent(EnumMap<AnimationType, AnimationFile> animationFiles) {
        this.animationFiles = animationFiles;
    }

    public AnimationFile getAnimationFile(AnimationType type) {
        return animationFiles.get(type);
    }

    public void addAnimation(AnimationFile animationFile, Identifier file) {
        try (InputStream stream = Minecraft.getInstance().getResourceManager().open(file)) {
            animationFile.animations().putAll(GeckoContainerBuilder.getAnimationFile(stream).animations());
        } catch (IOException e) {
            TouhouLittleMaid.LOGGER.error("Failed to load animation file", e);
        }
    }

    public void addAnimation(AnimationType type, Identifier file) {
        AnimationFile animationFile = animationFiles.get(type);
        if (animationFile != null) {
            addAnimation(animationFile, file);
        }
    }

    public enum AnimationType {
        /**
         * 女仆主动画
         */
        MAID,
        /**
         * 枪械动画
         */
        TAC,
        /**
         * 坐垫动画
         */
        CHAIR,
        /**
         * 铁魔法
         */
        ISS,
        /**
         * 沉浸式奏乐
         */
        IM
    }
}
