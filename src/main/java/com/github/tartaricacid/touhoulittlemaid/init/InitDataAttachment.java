package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.data.ChatTokensAttachment;
import com.github.tartaricacid.touhoulittlemaid.data.MaidNumAttachment;
import com.github.tartaricacid.touhoulittlemaid.data.PowerAttachment;
import com.github.tartaricacid.touhoulittlemaid.entity.data.*;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public interface InitDataAttachment {
    DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TouhouLittleMaid.MOD_ID);

    // 玩家相关数据
    Supplier<AttachmentType<MaidNumAttachment>> MAID_NUM = ATTACHMENT_TYPES.register("maid_num", () -> MaidNumAttachment.TYPE);
    Supplier<AttachmentType<PowerAttachment>> POWER_NUM = ATTACHMENT_TYPES.register("power", () -> PowerAttachment.TYPE);
    Supplier<AttachmentType<ChatTokensAttachment>> CHAT_TOKENS = ATTACHMENT_TYPES.register("chat_tokens", () -> ChatTokensAttachment.TYPE);

    // 女仆相关数据

    // 模型和声音包 ID
    Supplier<AttachmentType<ProfileData>> PROFILE = ATTACHMENT_TYPES.register("profile", () -> ProfileData.TYPE);
    // 饥饿值、好感度、经验和雷击状态
    Supplier<AttachmentType<StatsData>> STATS = ATTACHMENT_TYPES.register("stats", () -> StatsData.TYPE);
    // 工作模式相关
    Supplier<AttachmentType<TaskData>> TASK = ATTACHMENT_TYPES.register("task", () -> TaskData.TYPE);
    // 动画状态相关
    Supplier<AttachmentType<AnimationData>> ANIMATION = ATTACHMENT_TYPES.register("animation", () -> AnimationData.TYPE);
    // 女仆行为配置
    Supplier<AttachmentType<ConfigData>> CONFIG = ATTACHMENT_TYPES.register("config", () -> ConfigData.TYPE);
    // 对弈记录和当前对弈状态
    Supplier<AttachmentType<GameData>> GAME = ATTACHMENT_TYPES.register("game", () -> GameData.TYPE);
}
