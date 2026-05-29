package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.lifecycle;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface SaveComponent extends MaidComponent {
    void save(ValueOutput output);

    void load(ValueInput input);
}
