package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition;

import com.github.tartaricacid.touhoulittlemaid.compat.gun.common.GunCommonUtil;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.isValid;

public class ConditionTAC {
    private static final String EMPTY = "";

    private final Set<String> nameTest = new ReferenceOpenHashSet<>();
    private final Set<Identifier> idTest = new ReferenceOpenHashSet<>();

    public void addTest(String name) {
        if (!name.startsWith("tac:") || !name.contains("$")) {
            return;
        }
        String[] split = StringUtils.split(name, "$", 2);
        if (split.length < 2) {
            return;
        }
        String itemId = split[1];
        if (isValid(itemId)) {
            nameTest.add(name);
            idTest.add(Identifier.parse(itemId));
        }
    }

    public String doTest(ItemStack itemInHand, String prefix) {
        if (itemInHand.isEmpty()) {
            return EMPTY;
        }
        Identifier gunId = GunCommonUtil.getGunId(itemInHand);
        if (gunId == null) {
            return EMPTY;
        }
        if (idTest.contains(gunId)) {
            String animationName = prefix.substring(0, prefix.length() - 1) + "$" + gunId;
            if (nameTest.contains(animationName)) {
                return animationName;
            }
        }
        return EMPTY;
    }
}
