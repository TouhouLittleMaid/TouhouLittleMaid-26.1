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

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.github.tartaricacid.touhoulittlemaid.molang.parser.ast.Expression;

public interface ExecutionContext<TEntity> {
    TEntity entity();

    @Nullable Object evalSingleExpressionUnsafe(final @NotNull Expression expression);

    @Nullable Object evalMultiExpressionUnsafe(final @NotNull Iterable<Expression> multiExpression, boolean returnThrough);

    default @Nullable Object evalSingleExpression(final @NotNull Expression expression) {
        try {
            return evalSingleExpressionUnsafe(expression);
        } catch (Exception e) {
            TouhouLittleMaid.LOGGER.debug("Failed to evaluate molang expression.", e);
            return null;
        }
    }

    default @Nullable Object evalMultiExpression(final @NotNull Iterable<Expression> multiExpression, boolean returnThrough) {
        try {
            return evalMultiExpressionUnsafe(multiExpression, returnThrough);
        } catch (Exception e) {
            TouhouLittleMaid.LOGGER.debug("Failed to evaluate molang expression.", e);
            return null;
        }
    }
}
