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

package com.github.tartaricacid.touhoulittlemaid.molang.runtime.binding;

import com.github.tartaricacid.touhoulittlemaid.molang.parser.ast.*;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluatorImpl;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.Function;

/**
 * Class holding some default bindings and
 * static utility methods for ease working
 * with bindings
 */
public final class StandardBindings {
    private static final int MAX_LOOP_ROUND = 1024;

    public static final Function LOOP_FUNC = (ctx, args) -> {
        if (args.size() != 2) {
            return null;
        }

        int n = Math.min(Math.round(args.getAsFloat(ctx, 0)), MAX_LOOP_ROUND);

        if (args.getExpression(1) instanceof ExecutionScopeExpression exeExpr) {
            ((ExpressionEvaluatorImpl<?>) ctx).visitLoop(exeExpr, n);
        }

        return null;
    };

    public static final Function FOR_EACH_FUNC = (ctx, args) -> {
        if (args.size() != 3) {
            return null;
        }

        if (args.getExpression(0) instanceof AssignableVariableExpression variableExpr) {
            if (args.getExpression(2) instanceof ExecutionScopeExpression exeExpr) {
                final Object array = args.getValue(ctx, 1);
                if (array instanceof Iterable<?> iterable) {
                    ((ExpressionEvaluatorImpl<?>) ctx).visitForEach(exeExpr, variableExpr.target(), iterable);
                }
            }
        }

        return null;
    };
}
