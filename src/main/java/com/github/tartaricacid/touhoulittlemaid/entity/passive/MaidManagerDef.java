package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记 {@code *Manager}，供 Annotation Processor 生成 {@link MaidManagers} 与 {@link MaidManagerHost}。
 *
 * @param alias Manager 字段名（如 {@code itemManager}）；getter 名为 {@code getItemManager}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface MaidManagerDef {
    String alias();

    boolean exposeView() default false;
}
