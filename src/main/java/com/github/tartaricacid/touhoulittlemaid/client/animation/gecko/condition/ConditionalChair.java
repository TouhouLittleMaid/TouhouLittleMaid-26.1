package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition;

import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityChair;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.Set;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.isValid;

public class ConditionalChair {
    private static final String EMPTY = "";
    private final Set<String> idTest = new ReferenceOpenHashSet<>();
    private final String idPre;

    public ConditionalChair() {
        this.idPre = "chair$";
    }

    public void addTest(String name) {
        int preSize = this.idPre.length();
        if (name.length() <= preSize) {
            return;
        }
        String substring = name.substring(preSize);
        if (name.startsWith(idPre) && isValid(substring)) {
            idTest.add(substring);
        }
    }

    public String doTest(Mob maid) {
        Entity vehicle = maid.getVehicle();
        if (!(vehicle instanceof EntityChair chair)) {
            return EMPTY;
        }
        return doIdTest(chair);
    }

    private String doIdTest(EntityChair chair) {
        if (idTest.isEmpty()) {
            return EMPTY;
        }
        String modelId = chair.getModelId();
        if (idTest.contains(modelId)) {
            return idPre + modelId;
        }
        return EMPTY;
    }
}
