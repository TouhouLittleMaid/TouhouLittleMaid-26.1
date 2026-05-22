/*
 * This file is part of molang, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.tartaricacid.touhoulittlemaid.molang.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.tartaricacid.touhoulittlemaid.molang.parser.ast.*;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.binding.ValueConversions;

import java.util.List;

@SuppressWarnings("rawtypes,unchecked")
public final class ExpressionEvaluatorImpl<TEntity> implements ExpressionEvaluator<TEntity>, ExpressionVisitor<Object> {
    private static final Evaluator[] BINARY_EVALUATORS = {
            bool((a, b) -> a.eval() && b.eval()),
            bool((a, b) -> a.eval() || b.eval()),
            compare((a, b) -> a.eval() < b.eval()),
            compare((a, b) -> a.eval() <= b.eval()),
            compare((a, b) -> a.eval() > b.eval()),
            compare((a, b) -> a.eval() >= b.eval()),
            (evaluator, a, b) -> {
                final Object aVal = a.visit(evaluator);
                final Object bVal = b.visit(evaluator);
                return ValueConversions.asFloat(aVal) + ValueConversions.asFloat(bVal);
            },
            arithmetic((a, b) -> a.eval() - b.eval()),
            arithmetic((a, b) -> a.eval() * b.eval()),
            arithmetic((a, b) -> {
                // Molang allows division by zero,
                // which is always equal to 0
                float dividend = a.eval();
                float divisor = b.eval();
                if (divisor == 0) return 0;
                else return dividend / divisor;
            }),
            (evaluator, a, b) -> { // arrow
                final Object val = a.visit(evaluator);
                if (val == null) {
                    return null;
                } else {
                    var childEvaluator = evaluator.createChild(val);
                    var result = b.visit(childEvaluator);
                    // 暂时先这样
                    evaluator.returnValue = childEvaluator.returnValue;
                    return result;
                }
            },
            (evaluator, a, b) -> { // null coalesce
                Object val = a.visit(evaluator);
                if (val == null) {
                    return b.visit(evaluator);
                } else {
                    return val;
                }
            },
            (evaluator, a, b) -> { // assignation
                Object val = b.visit(evaluator);
                if (a instanceof AssignableVariableExpression) {
                    AssignableVariable var = ((AssignableVariableExpression) a).target();
                    if (val instanceof Struct) {
                        val = ((Struct) val).copy();
                    }
                    var.assign(evaluator, val);
                } else if (a instanceof StructAccessExpression) {
                    if (val instanceof Struct) {
                        // 不允许结构体嵌套
                        return val;
                    }
                    StructAccessExpression exp = (StructAccessExpression) a;
                    Object value = exp.left().visit(evaluator);
                    if (value instanceof Struct) {
                        ((Struct) value).putProperty(exp.path(), val);
                    } else if (exp.left() instanceof AssignableVariableExpression) {
                        AssignableVariable variable = ((AssignableVariableExpression) exp.left()).target();
                        Struct struct = new HashMapStruct();
                        struct.putProperty(exp.path(), val);
                        variable.assign(evaluator, struct);
                    }
                }
                // TODO: (else case) This isn't fail-fast, we can only assign to access expressions
                return val;
            },
            (evaluator, a, b) -> { // conditional
                Object condition = a.visit(evaluator);
                if (ValueConversions.asBoolean(condition)) {
                    return b.visit(evaluator);
                }
                return null;
            },
            (evaluator, a, b) -> {
                Object left = a.visit(evaluator);
                Object right = b.visit(evaluator);
                if (left == right)
                    return true;
                if (right instanceof Number || left instanceof Number)
                    return ValueConversions.asFloat(right) == ValueConversions.asFloat(left);
                if (right == null || left == null)
                    return false;
                if (right instanceof StringExpression expr)
                    return expr.equals(left);
                if (left instanceof StringExpression expr)
                    return expr.equals(right);
                return left.equals(right);
            }, // eq
            (evaluator, a, b) -> {
                Object left = a.visit(evaluator);
                Object right = b.visit(evaluator);
                if (left == right)
                    return false;
                if (right instanceof Number || left instanceof Number)
                    return ValueConversions.asFloat(right) != ValueConversions.asFloat(left);
                if (right == null || left == null)
                    return true;
                if (right instanceof StringExpression expr)
                    return !expr.equals(left);
                if (left instanceof StringExpression expr)
                    return !expr.equals(right);
                return !left.equals(right);
            }
    };

    private final TEntity entity;
    private @Nullable Object returnValue;
    private @Nullable StatementExpression.@Nullable Op controlOp;
    private int inLoop = 0;
    private int returnThrough = 0;

    public ExpressionEvaluatorImpl(final @Nullable TEntity entity) {
        this.entity = entity;
    }

    private static Evaluator bool(BooleanOperator op) {
        return (evaluator, a, b) -> op.operate(
                () -> ValueConversions.asBoolean(a.visit(evaluator)),
                () -> ValueConversions.asBoolean(b.visit(evaluator))
        );
    }

    private static Evaluator compare(Comparator comp) {
        return (evaluator, a, b) -> comp.compare(
                () -> ValueConversions.asFloat(a.visit(evaluator)),
                () -> ValueConversions.asFloat(b.visit(evaluator))
        );
    }

    private static Evaluator arithmetic(ArithmeticOperator op) {
        return (evaluator, a, b) -> op.operate(
                () -> ValueConversions.asFloat(a.visit(evaluator)),
                () -> ValueConversions.asFloat(b.visit(evaluator))
        );
    }

    @Override
    public TEntity entity() {
        return entity;
    }

    @Override
    public @Nullable Object evalSingleExpressionUnsafe(@NotNull Expression expression) {
        try {
            return expression.visit(this);
        } finally {
            returnValue = null;
            controlOp = null;
        }
    }

    @Override
    public @Nullable Object evalMultiExpressionUnsafe(@NotNull Iterable<Expression> multiExpression, boolean returnThrough) {
        if (returnThrough) {
            this.returnThrough++;
        }
        Object lastResult = 0d;

        try {
            for (Expression expression : multiExpression) {
                lastResult = expression.visit(this);
                Object returnValue = popReturnValue();
                if (returnValue != null) {
                    lastResult = returnValue;
                    break;
                }
            }
        } finally {
            returnValue = null;
            controlOp = null;
            if (returnThrough) {
                this.returnThrough--;
            }
        }

        return lastResult;
    }

    public @NotNull <TNewEntity> ExpressionEvaluatorImpl<TNewEntity> createChild(final @Nullable TNewEntity entity) {
        return new ExpressionEvaluatorImpl<>(entity);
    }

    private @Nullable Object popReturnValue() {
        var ret = returnValue;
        if (returnThrough == 0) {
            returnValue = null;
        }
        return ret;
    }

    @Override
    public @Nullable Object visitCall(final @NotNull CallExpression expression) {
        final Function function = expression.function();
        return function.evaluate(this, expression.arguments());
    }

    @Override
    public Object visitFloat(@NotNull FloatExpression expression) {
        return expression.value();
    }

    @Override
    public Object visitExecutionScope(@NotNull ExecutionScopeExpression executionScope) {
        List<Expression> expressions = executionScope.expressions();
        Object lastResult = null;
        for (Expression expression : expressions) {
            // eval expression, ignore result
            lastResult = expression.visit(this);
            // check for return values
            Object returnValue = popReturnValue();
            if (returnValue != null) {
                return returnValue;
            }
            if (inLoop > 0 && controlOp != null) {
                return null;
            }
        }
        return lastResult;
    }

    private boolean visitExecutionScopeInLoop(@NotNull ExecutionScopeExpression executionScope) {
        inLoop++;
        try {
            List<Expression> expressions = executionScope.expressions();
            for (Expression expression : expressions) {
                // eval expression, ignore result
                expression.visit(this);
                // check for return values
                Object returnValue = popReturnValue();
                if (returnValue != null) {
                    return true;
                }
                var controlOp = this.controlOp;
                this.controlOp = null;
                if (controlOp == StatementExpression.Op.CONTINUE) {
                    break;
                }
                if (controlOp == StatementExpression.Op.BREAK) {
                    return true;
                }
            }
        } finally {
            inLoop--;
        }
        return false;
    }

    public void visitLoop(@NotNull ExecutionScopeExpression executionScope, int times) {
        for (var i = 0; i < times; i++) {
            if (visitExecutionScopeInLoop(executionScope)) {
                break;
            }
        }
    }

    public void visitForEach(@NotNull ExecutionScopeExpression executionScope, AssignableVariable variable, Iterable<?> list) {
        for (var param : list) {
            variable.assign(this, param);
            if (visitExecutionScopeInLoop(executionScope)) {
                break;
            }
        }
    }

    @Override
    public Object visitIdentifier(@NotNull IdentifierExpression expression) {
        throw new RuntimeException("Unknown identifier type");
    }

    @Override
    public Object visitVariable(final @NotNull VariableExpression expression) {
        return expression.target().evaluate(this);
    }

    public Object visitAssignableVariable(final @NotNull AssignableVariableExpression expression) {
        return expression.target().evaluate(this);
    }

    @Override
    public Object visitStruct(final @NotNull StructAccessExpression expression) {
        Object value = expression.left().visit(this);
        if (value instanceof Struct) {
            return ((Struct) value).getProperty(expression.path());
        } else {
            return null;
        }
    }

    @Override
    public Object visitArray(ArrayAccessExpression expression) {
        Object value = expression.array().visit(this);
        Object indexObj = expression.index().visit(this);
        if (indexObj instanceof Number) {
            int index = ((Number) indexObj).intValue();
            if (index < 0) {
                index = 0;  // molang 文档是这么要求的
            }
            if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                if (list.size() > index) {
                    return list.get(index);
                }
            }
        }
        return null;
    }

    @Override
    public Object visitBinary(@NotNull BinaryExpression expression) {
        return BINARY_EVALUATORS[expression.op().index()].eval(
                this,
                expression.left(),
                expression.right()
        );
    }

    @Override
    public Object visitUnary(@NotNull UnaryExpression expression) {
        Object value = expression.expression().visit(this);
        switch (expression.op()) {
            case LOGICAL_NEGATION:
                return !ValueConversions.asBoolean(value);
            case ARITHMETICAL_NEGATION:
                return -ValueConversions.asFloat(value);
            case RETURN: {
                this.returnValue = value;
                return 0D;
            }
            default:
                throw new IllegalStateException("Unknown operation");
        }
    }

    @Override
    public Object visitStatement(@NotNull StatementExpression expression) {
        switch (expression.op()) {
            case BREAK: {
                this.controlOp = StatementExpression.Op.BREAK;
                break;
            }
            case CONTINUE: {
                this.controlOp = StatementExpression.Op.CONTINUE;
                break;
            }
        }
        return null;
    }

    @Override
    public Object visitString(@NotNull StringExpression expression) {
        return expression;
    }

    @Override
    public Object visitTernaryConditional(@NotNull TernaryConditionalExpression expression) {
        Object obj = expression.condition().visit(this);
        obj = ValueConversions.asBoolean(obj)
                ? expression.trueExpression().visit(this)
                : expression.falseExpression().visit(this);
        return obj;
    }

    @Override
    public Object visit(@NotNull Expression expression) {
        throw new UnsupportedOperationException("Unsupported expression type: " + expression);
    }

    private interface Evaluator<TEntity> {
        Object eval(ExpressionEvaluatorImpl<TEntity> evaluator, Expression a, Expression b);
    }

    private interface BooleanOperator {
        boolean operate(LazyEvaluableBoolean a, LazyEvaluableBoolean b);
    }

    interface LazyEvaluableBoolean {
        boolean eval();
    }

    interface LazyEvaluableFloat {
        float eval();
    }

    private interface Comparator {
        boolean compare(LazyEvaluableFloat a, LazyEvaluableFloat b);

    }

    private interface ArithmeticOperator {
        float operate(LazyEvaluableFloat a, LazyEvaluableFloat b);
    }
}
