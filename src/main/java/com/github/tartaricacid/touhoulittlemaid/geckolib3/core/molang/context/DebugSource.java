package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context;

import net.minecraft.network.chat.Component;

public interface DebugSource {
    void print(String message, Object...args);

    void print(Component message);
}
