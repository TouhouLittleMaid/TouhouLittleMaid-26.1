package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.struct.Vec3fStruct;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor.IBoneView;
import org.jetbrains.annotations.NotNull;

public final class BonePosition extends BoneParamFunction {
    @Override
    protected Vec3fStruct getParam(@NotNull IBoneView bone) {
        return new BonePositionStruct(bone);
    }

    private static final class BonePositionStruct extends Vec3fStruct {
        private final IBoneView bone;

        public BonePositionStruct(IBoneView bone) {
            this.bone = bone;
        }

        @Override
        protected float getX() {
            return bone.getPosition().x;
        }

        @Override
        protected float getY() {
            return bone.getPosition().y;
        }

        @Override
        protected float getZ() {
            return bone.getPosition().z;
        }
    }
}
