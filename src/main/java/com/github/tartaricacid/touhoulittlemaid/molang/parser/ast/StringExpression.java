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

package com.github.tartaricacid.touhoulittlemaid.molang.parser.ast;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.util.StringPool;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * String literal expression implementation for Molang.
 *
 * <p>Example string expressions: {@code 'hello world'},
 * {@code 'hey there'}, {@code 'name'}, {@code 'the game'}</p>
 *
 * @since 3.0.0
 */
public final class StringExpression implements Expression {
    private final String value;
    private final int pooledValue;
    private Identifier cachedValue;

    public StringExpression(final @NotNull String value) {
        this.value = Objects.requireNonNull(value, "value");
        this.pooledValue = StringPool.computeIfAbsent(value);
    }

    /**
     * Gets the string value for this expression.
     *
     * @return The string value.
     * @since 3.0.0
     */
    public @NotNull String value() {
        return value;
    }

    public int pooledValue() {
        return pooledValue;
    }

    @Override
    public <R> R visit(final @NotNull ExpressionVisitor<R> visitor) {
        return visitor.visitString(this);
    }

    @Override
    public String toString() {
        return value;
    }

    @Nullable
    public Identifier getCachedValue() {
        return cachedValue;
    }

    public void setCachedValue(@Nullable final Identifier cachedValue) {
        this.cachedValue = cachedValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof String str) {
            return value.equals(str);
        }
        if (o instanceof StringExpression expr) {
            return pooledValue == expr.pooledValue;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}