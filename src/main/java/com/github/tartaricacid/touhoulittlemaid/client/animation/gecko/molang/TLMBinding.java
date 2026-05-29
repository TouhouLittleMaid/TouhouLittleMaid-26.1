package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding.ContextBinding;
import com.github.tartaricacid.touhoulittlemaid.util.LazyValue;

public class TLMBinding extends ContextBinding {
    public static final LazyValue<TLMBinding> INSTANCE = new LazyValue<>(TLMBinding::new);

    private TLMBinding() {
        maidEntityVar("is_begging", ctx -> ctx.entity().components().animation.isBegging());
        maidEntityVar("is_sitting", ctx -> ctx.entity().isMaidInSittingPose());
        maidEntityVar("has_backpack", ctx -> ctx.entity().components().backpack.hasBackpack());
    }
}