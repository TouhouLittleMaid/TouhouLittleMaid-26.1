package com.github.tartaricacid.touhoulittlemaid.compat.jade.provider;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.item.ItemMaidBeacon;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityMaidBeacon;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum ShrineLampProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final Identifier UID = IdentifierUtil.modLoc("shrine_lamp");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof BlockEntityMaidBeacon lamp) {
            float storagePower = lamp.getStoragePower();
            float costPower = lamp.getEffectCost() * 900;
            iTooltip.add(Component.translatable("tooltips.touhou_little_maid.maid_beacon.desc", ItemMaidBeacon.DECIMAL_FORMAT.format(storagePower)));
            if (lamp.getPotionIndex() == -1) {
                iTooltip.add(Component.translatable("gui.touhou_little_maid.maid_beacon.cost_power", ItemMaidBeacon.DECIMAL_FORMAT.format(0)));
            } else {
                iTooltip.add(Component.translatable("gui.touhou_little_maid.maid_beacon.cost_power", ItemMaidBeacon.DECIMAL_FORMAT.format(costPower)));
            }
        }
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
