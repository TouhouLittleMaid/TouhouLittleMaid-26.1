package com.github.tartaricacid.touhoulittlemaid.compat.jei.handler;

import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.AbstractMaidContainerGui;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import java.util.List;

public class MaidContainerHandler implements IGuiContainerHandler<AbstractMaidContainerGui<?>> {
    @Override
    public List<Rect2i> getGuiExtraAreas(AbstractMaidContainerGui<?> containerScreen) {
        return containerScreen.getExclusionArea();
    }
}
