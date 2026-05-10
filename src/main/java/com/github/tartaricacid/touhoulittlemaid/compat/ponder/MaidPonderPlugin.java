package com.github.tartaricacid.touhoulittlemaid.compat.ponder;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.Identifier;

public class MaidPonderPlugin implements PonderPlugin {
    static void register() {
        PonderIndex.addPlugin(new MaidPonderPlugin());
    }

    @Override
    public String getModId() {
        return TouhouLittleMaid.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<Identifier> helper) {
        MaidPonderScenes.register(helper);
    }
}
