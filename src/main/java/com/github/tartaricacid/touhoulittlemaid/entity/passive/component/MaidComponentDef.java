package com.github.tartaricacid.touhoulittlemaid.entity.passive.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记女仆组件实现，供 Annotation Processor 生成 {@link MaidComponents} 字段与注册逻辑。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface MaidComponentDef {
    String value();
}
