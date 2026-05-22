package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value;

import com.github.tartaricacid.touhoulittlemaid.molang.parser.ast.Expression;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;

import java.util.List;

public class MolangValue implements IValue {
    private final List<Expression> expressions;
    private final boolean isUserFunc;

    public MolangValue(List<Expression> expressions, boolean isUserFunc) {
        this.expressions = expressions;
        this.isUserFunc = isUserFunc;
    }

    @Override
    public Object evalUnsafe(ExpressionEvaluator<?> evaluator) {
        return evaluator.evalMultiExpressionUnsafe(expressions, isUserFunc);
    }
}
