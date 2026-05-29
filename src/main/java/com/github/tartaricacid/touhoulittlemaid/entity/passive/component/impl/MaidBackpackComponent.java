package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.EmptyBackpack;
import com.github.tartaricacid.touhoulittlemaid.entity.data.BackpackData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponentDef;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.lifecycle.BaseTickComponent;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment;
import net.minecraft.resources.Identifier;

@MaidComponentDef("backpack")
public class MaidBackpackComponent implements MaidComponent, BaseTickComponent {
    private final EntityMaid maid;
    private IMaidBackpack backpack;
    private int backpackDelay = 0;

    public MaidBackpackComponent(EntityMaid maid) {
        this.maid = maid;
        this.backpack = BackpackManager.getEmptyBackpack();
    }

    @Override
    public int priority() {
        return 10;
    }

    public IMaidBackpack getMaidBackpackType() {
        BackpackData data = this.maid.getData(InitDataAttachment.BACKPACK);
        Identifier id = Identifier.parse(data.type());
        IMaidBackpack emptyBackpack = BackpackManager.getEmptyBackpack();
        return BackpackManager.findBackpack(id).orElse(emptyBackpack);
    }

    public boolean hasBackpack() {
        IMaidBackpack type = getMaidBackpackType();
        return !(type instanceof EmptyBackpack);
    }

    public void setMaidBackpackType(IMaidBackpack backpack) {
        if (backpack == this.backpack) {
            return;
        }
        this.backpack = backpack;
        BackpackData data = new BackpackData(backpack.getId().toString());
        this.maid.setData(InitDataAttachment.BACKPACK, data);
    }

    public void setBackpackDelay() {
        backpackDelay = 20;
    }

    public boolean backpackHasDelay() {
        return backpackDelay > 0;
    }

    @Override
    public void baseTick() {
        if (backpackDelay > 0) {
            backpackDelay--;
        }
    }
}
