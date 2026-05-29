package com.github.tartaricacid.touhoulittlemaid.compat.jade.provider;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.event.AddJadeInfoEvent;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl.FavorabilityComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.common.NeoForge;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MaidProvider implements IEntityComponentProvider {

    INSTANCE;

    private static final Identifier UID = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "maid");

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        if (entityAccessor.getEntity() instanceof EntityMaid maid) {
            if (maid.isTame()) {
                IMaidTask task = maid.components().task.getTask();
                iTooltip.add(Component.translatable("top.touhou_little_maid.entity_maid.task").append(task.getName()));

                // 添加于 Mode 之下，用于给 Mode 添加额外的信息
                NeoForge.EVENT_BUS.post(new AddJadeInfoEvent(maid, iTooltip, iPluginConfig));

                iTooltip.add(Component.translatable("top.touhou_little_maid.entity_maid.schedule").append(getActivityTransText(maid)));
                iTooltip.add(Component.translatable("top.touhou_little_maid.entity_maid.favorability", maid.components().favorability.getLevel()));
                iTooltip.add(Component.translatable("top.touhou_little_maid.entity_maid.nex_favorability_point", maid.components().favorability.nextLevelPoint()));
                if (maid.getSyncInvulnerable()) {
                    iTooltip.add(Component.translatable("top.touhou_little_maid.entity_maid.invulnerable").withStyle(ChatFormatting.DARK_PURPLE));
                }
            }
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }

    private MutableComponent getActivityTransText(EntityMaid maid) {
        return getActivityTransText(maid.components().task.getScheduleDetail());
    }

    private MutableComponent getActivityTransText(Activity activity) {
        return Component.translatable("gui.touhou_little_maid.activity." + activity.getName());
    }
}
