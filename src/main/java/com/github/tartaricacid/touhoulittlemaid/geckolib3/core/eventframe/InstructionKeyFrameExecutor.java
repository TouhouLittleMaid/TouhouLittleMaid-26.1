package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.eventframe;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.keyframe.event.EventKeyFrame;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.MolangContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.value.IValue;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExpressionEvaluator;

import java.util.List;

public class InstructionKeyFrameExecutor {
    private final List<EventKeyFrame<IValue[]>> list;
    private int nextIndex = 0;

    public InstructionKeyFrameExecutor(List<EventKeyFrame<IValue[]>> list) {
        this.list = list;
    }

    private void evalValues(ExpressionEvaluator<?> evaluator, IValue[] values) {
        for (IValue value : values) {
            value.eval(evaluator);
        }
    }

    public void executeTo(ExpressionEvaluator<MolangContext<?>> evaluator, float currentTick, boolean allowEmitting) {
        evaluator.entity().setAllowEmitting(allowEmitting);
        while (!reachEnd()) {
            EventKeyFrame<IValue[]> keyFrame = list.get(nextIndex);
            if (keyFrame.getStartTick() > currentTick) {
                break;
            }
            evalValues(evaluator, keyFrame.getEventData());
            nextIndex++;
        }
        evaluator.entity().setAllowEmitting(false);
    }

    public void executeRemaining(ExpressionEvaluator<MolangContext<?>> evaluator, boolean allowEmitting) {
        evaluator.entity().setAllowEmitting(allowEmitting);
        for (int i = nextIndex; i < list.size(); i++) {
            evalValues(evaluator, list.get(i).getEventData());
        }
        evaluator.entity().setAllowEmitting(false);
        nextIndex = list.size();
    }

    public boolean reachEnd() {
        return nextIndex >= list.size();
    }

    public void reset() {
        nextIndex = 0;
    }
}
