package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.api.backpack.IMaidBackpack;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.BackpackManager;
import com.github.tartaricacid.touhoulittlemaid.entity.backpack.EmptyBackpack;
import com.github.tartaricacid.touhoulittlemaid.entity.data.BackpackData;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment;
import net.minecraft.resources.Identifier;

public class MaidBackpackManager {
    private final EntityMaid maid;
    private IMaidBackpack backpack;
    private int backpackDelay = 0;

    MaidBackpackManager(EntityMaid maid) {
        this.maid = maid;
        this.backpack = BackpackManager.getEmptyBackpack();
    }

    public IMaidBackpack getMaidBackpackType() {
        BackpackData data = this.maid.getData(InitDataAttachment.BACKPACK);
        Identifier id = Identifier.parse(data.type());
        IMaidBackpack emptyBackpack = BackpackManager.getEmptyBackpack();
        return BackpackManager.findBackpack(id).orElse(emptyBackpack);
    }

    public boolean hasBackpack() {
        IMaidBackpack type = this.getMaidBackpackType();
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

    void tick() {
        if (backpackDelay > 0) {
            backpackDelay--;
        }
    }

    interface View {
        MaidBackpackManager getBackpackManager();

        default IMaidBackpack getMaidBackpackType() {
            return getBackpackManager().getMaidBackpackType();
        }

        default boolean hasBackpack() {
            return getBackpackManager().hasBackpack();
        }

        default void setMaidBackpackType(IMaidBackpack backpack) {
            getBackpackManager().setMaidBackpackType(backpack);
        }

        default void setBackpackDelay() {
            getBackpackManager().setBackpackDelay();
        }

        default boolean backpackHasDelay() {
            return getBackpackManager().backpackHasDelay();
        }
    }
}
