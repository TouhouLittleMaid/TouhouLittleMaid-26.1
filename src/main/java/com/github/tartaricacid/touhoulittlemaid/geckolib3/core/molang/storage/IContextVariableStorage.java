package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.storage;

public interface IContextVariableStorage {
    Object getContext(int name);
    void setContext(int name, Object value);
}
