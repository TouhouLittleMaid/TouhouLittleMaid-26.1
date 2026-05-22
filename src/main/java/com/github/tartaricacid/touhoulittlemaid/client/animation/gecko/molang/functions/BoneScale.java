package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.struct.Vec3fStruct;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor.IBoneView;
import org.jetbrains.annotations.NotNull;

public final class BoneScale extends BoneParamFunction {
    @Override
    protected Vec3fStruct getParam(@NotNull IBoneView bone) {
        return new BoneScaleStruct(bone);
    }

    private static final class BoneScaleStruct extends Vec3fStruct {
        private final IBoneView bone;

        public BoneScaleStruct(IBoneView bone) {
            this.bone = bone;
        }

        @Override
        protected float getX() {
            return bone.getScale().x;
        }

        @Override
        protected float getY() {
            return bone.getScale().y;
        }

        @Override
        protected float getZ() {
            return bone.getScale().z;
        }
    }
}
