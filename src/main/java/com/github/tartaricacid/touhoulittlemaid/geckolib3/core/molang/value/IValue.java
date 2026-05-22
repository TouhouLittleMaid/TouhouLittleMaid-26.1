package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.binding.ValueConversions;

public interface IValue {
    /**
     * 依次执行表达式，返回最后一个表达式的值，或第一个 return 语句的值，并转换为 float 类型。
     */
    default float evalAsFloat(ExpressionEvaluator<?> evaluator) {
        return ValueConversions.asFloat(eval(evaluator));
    }
    /**
     * 依次执行表达式，返回最后一个表达式的值，或第一个 return 语句的值，并转换为 int 类型。
     */
    default int evalAsInt(ExpressionEvaluator<?> evaluator) {
        return ValueConversions.asInt(eval(evaluator));
    }

    /**
     * 依次执行表达式，返回最后一个表达式的值，或第一个 return 语句的值，并转换为 boolean 类型。
     */
    default boolean evalAsBoolean(ExpressionEvaluator<?> evaluator) {
        return ValueConversions.asBoolean(eval(evaluator));
    }

    /**
     * 依次执行表达式，返回最后一个表达式的值，或第一个 return 语句的值，返回值的类型不确定。可能抛出异常。
     */
    Object evalUnsafe(ExpressionEvaluator<?> evaluator);

    /**
     * 依次执行表达式，返回最后一个表达式的值，或第一个 return 语句的值，返回值的类型不确定。
     */
    default Object eval(ExpressionEvaluator<?> evaluator) {
        try {
            return evalUnsafe(evaluator);
        } catch (Throwable t) {
            TouhouLittleMaid.LOGGER.debug("Failed to evaluate molang expression.", t);
            return null;
        }
    }
}
