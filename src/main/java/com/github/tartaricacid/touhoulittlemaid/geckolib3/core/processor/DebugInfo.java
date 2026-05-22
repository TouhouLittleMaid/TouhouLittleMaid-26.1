package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.processor;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import java.text.DecimalFormat;
import java.util.function.BiConsumer;

public class DebugInfo {
    private static final DecimalFormat FORMATTER = new DecimalFormat("#.#####");
    private final ReferenceArrayList<DebugItem> items = new ReferenceArrayList<>();

    public void add(Phase phase, String name, IValue exp) {
        items.add(new DebugItem(name, exp, phase));
    }

    public void remove(String name) {
        items.removeIf(item -> item.name.equals(name));
    }

    public void clear() {
        items.clear();
    }

    public void evaluatePre(ExpressionEvaluator<?> evaluator) {
        for (DebugItem item : items) {
            if (item.phase == Phase.PRE_ANIMATION) {
                item.eval(evaluator);
            }
        }
    }

    public void evaluatePost(ExpressionEvaluator<?> evaluator) {
        for (DebugItem item : items) {
            if (item.phase == Phase.POST_ANIMATION) {
                item.eval(evaluator);
            }
        }
    }

    public void enumerate(BiConsumer<String, String> enumerator) {
        for (DebugItem item : items) {
            enumerator.accept(item.name, item.result);
        }
    }

    private static class DebugItem {
        private final String name;
        private final IValue value;
        private final Phase phase;
        private volatile String result;

        public DebugItem(String name, IValue value, Phase phase) {
            this.name = name;
            this.value = value;
            this.phase = phase;
        }

        public void eval(ExpressionEvaluator<?> evaluator) {
            try {
                var ret = value.evalUnsafe(evaluator);
                if (ret == null) {
                    result = "null";
                } else if (ret instanceof Number) {
                    result = FORMATTER.format(ret);
                } else {
                    result = ret.toString();
                }
            } catch (Throwable e) {
                result = "Error: " + e.getMessage();
            }
        }

        public String result() {
            return result;
        }

        public String name() {
            return name;
        }

        public Phase phase() {
            return phase;
        }
    }

    public enum Phase {
        PRE_ANIMATION,
        POST_ANIMATION
    }
}
