package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.struct.Vec3fStruct;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor.IBoneView;
import org.jetbrains.annotations.NotNull;

public final class BoneAbsolutePivot extends BoneParamFunction {
    @Override
    protected Vec3fStruct getParam(@NotNull IBoneView bone) {
        bone.setTracking(true);
        return new BonePivotStruct(bone);
    }

    private static final class BonePivotStruct extends Vec3fStruct {
        private final IBoneView bone;

        public BonePivotStruct(IBoneView bone) {
            this.bone = bone;
        }

        @Override
        protected float getX() {
            return bone.getGlobalPivot().x;
        }

        @Override
        protected float getY() {
            return bone.getGlobalPivot().y;
        }

        @Override
        protected float getZ() {
            return bone.getGlobalPivot().z;
        }
    }
}
