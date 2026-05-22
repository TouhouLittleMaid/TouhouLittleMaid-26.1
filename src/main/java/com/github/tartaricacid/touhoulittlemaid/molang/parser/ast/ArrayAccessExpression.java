package com.github.tartaricacid.touhoulittlemaid.molang.parser.ast;

import org.jetbrains.annotations.NotNull;

public class ArrayAccessExpression implements Expression {
    private final Expression array;
    private final Expression index;

    public ArrayAccessExpression(Expression array, Expression index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public <R> R visit(@NotNull ExpressionVisitor<R> visitor) {
        return visitor.visitArray(this);
    }

    public Expression array() {
        return array;
    }

    public Expression index() {
        return index;
    }
}
