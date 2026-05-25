package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.binding.PrimaryBinding;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.FloatValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.MolangValue;
import com.github.tartaricacid.touhoulittlemaid.molang.MolangEngine;
import com.github.tartaricacid.touhoulittlemaid.molang.parser.ParseException;

import java.util.Map;

public class MolangParser {
    private final MolangEngine engine;
    private final PrimaryBinding primaryBinding;

    public MolangParser(Map<String, Object> extraBindings) {
        primaryBinding = new PrimaryBinding(extraBindings);
        engine = MolangEngine.fromCustomBinding(primaryBinding);
    }

    public IValue parseExpression(String molangExpression) {
        return parseExpression(molangExpression, false);
    }

    @SuppressWarnings("unused")
    public IValue parseExpression(String molangExpression, boolean isUserFunc) {
        try {
            return parseExpressionUnsafe(molangExpression, isUserFunc);
        } catch (Exception e) {
            TouhouLittleMaid.LOGGER.debug("Failed to parse molang expression: {}\n{}", e.getMessage(), molangExpression);
            return FloatValue.ZERO;
        }
    }

    public IValue parseExpressionUnsafe(String molangExpression, boolean isUserFunc) throws ParseException {
        MolangValue value = new MolangValue(engine.parse(isUserFunc ? filterComment(molangExpression) : molangExpression), isUserFunc);
        primaryBinding.resetTransient();
        return value;
    }

    // C 风格注释
    private static String filterComment(String exp) {
        StringBuilder result = new StringBuilder(exp.length());
        boolean blockComment = false;
        boolean lineComment = false;
        boolean string = false;
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (string) {
                if (c == '\'') {
                    string = false;
                }
                result.append(c);
                continue;
            }
            if (lineComment) {
                if (c == '\r' || c == '\n') {
                    lineComment = false;
                    result.append('\n');
                }
                continue;
            }
            if (blockComment) {
                if (c == '*' && i + 1 < exp.length()) {
                    char next = exp.charAt(i + 1);
                    if (next == '/') {
                        blockComment = false;
                        i++;
                    }
                }
                continue;
            }
            if (c == '\'') {
                string = true;
                result.append('\'');
                continue;
            }
            if (c == '/' && i + 1 < exp.length()) {
                char next = exp.charAt(i + 1);
                if (next == '/') {
                    lineComment = true;
                    i++;
                    continue;
                } else if (next == '*') {
                    blockComment = true;
                    i++;
                    continue;
                }
            }
            result.append(c);
        }

        return result.toString();
    }

    
    @SuppressWarnings("unused")
    public IValue getConstant(float value) {
        return new FloatValue(value);
    }

    public void reset() {
        primaryBinding.resetScoped();
    }
}
