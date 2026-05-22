package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.functions;

import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputCheck {
    public static class Keyboard implements Function {
        @Override
        public @Nullable Object evaluate(@NotNull ExecutionContext<?> context, @NotNull ArgumentCollection arguments) {
            return false;
        }

        @Override
        public boolean validateArgumentSize(int size) {
            return size >= 1;
        }
    }

    public static class Mouse implements Function {
        @Override
        public @Nullable Object evaluate(@NotNull ExecutionContext<?> context, @NotNull ArgumentCollection arguments) {
            return false;
        }

        @Override
        public boolean validateArgumentSize(int size) {
            return size == 1;
        }
    }
}
