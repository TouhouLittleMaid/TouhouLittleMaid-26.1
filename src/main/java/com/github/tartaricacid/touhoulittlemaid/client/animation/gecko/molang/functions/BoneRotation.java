package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.struct.Vec3fStruct;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor.IBoneView;
import org.jetbrains.annotations.NotNull;

public final class BoneRotation extends BoneParamFunction {
    @Override
    protected Vec3fStruct getParam(@NotNull IBoneView bone) {
        return new BoneRotationStruct(bone);
    }

    private static final class BoneRotationStruct extends Vec3fStruct {
        private final IBoneView bone;

        public BoneRotationStruct(IBoneView bone) {
            this.bone = bone;
        }

        @Override
        protected float getX() {
            return -(float) Math.toDegrees(bone.getRotation().x);
        }

        @Override
        protected float getY() {
            return -(float) Math.toDegrees(bone.getRotation().y);
        }

        @Override
        protected float getZ() {
            return (float) Math.toDegrees(bone.getRotation().z);
        }
    }
}
