package com.github.tartaricacid.touhoulittlemaid.ai.manager.entity;

import com.github.tartaricacid.touhoulittlemaid.client.resource.loader.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.compat.ysm.YsmCompat;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public record ChatClientInfo(String language, String name, List<String> description) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(language);
        buf.writeUtf(name);
        buf.writeVarInt(description.size());
        for (String line : description) {
            buf.writeUtf(line);
        }
    }

    public static ChatClientInfo decode(FriendlyByteBuf buf) {
        String language = buf.readUtf();
        String name = buf.readUtf();
        int size = buf.readVarInt();
        List<String> description = Lists.newArrayListWithExpectedSize(size);
        for (int i = 0; i < size; i++) {
            description.add(buf.readUtf());
        }
        return new ChatClientInfo(language, name, description);
    }

    public static ChatClientInfo fromMaid(EntityMaid maid) {
        String language = getClientLanguage();
        String name = getMaidName(maid);
        List<String> description = getMaidDescription(maid);
        return new ChatClientInfo(language, name, description);
    }

    private static String getClientLanguage() {
        return Minecraft.getInstance().getLanguageManager().getSelected();
    }

    private static String getMaidName(EntityMaid maid) {
        return maid.getName().getString();
    }

    private static List<String> getMaidDescription(EntityMaid maid) {
        List<String> description = Lists.newArrayList();
        // YSM 模型没有描述文本
        if (YsmCompat.isInstalled() && maid.isYsmModel()) {
            return description;
        }
        // 然后才是默认描述文本
        Optional<MaidModelInfo> info = CustomPackLoader.MAID_MODELS.getInfo(maid.getModelId());
        if (info.isPresent()) {
            MaidModelInfo maidModelInfo = info.get();
            List<Component> parse = ParseI18n.parse(maidModelInfo.getDescription());
            parse.forEach(component -> description.add(component.getString()));
        }
        return description;
    }
}
