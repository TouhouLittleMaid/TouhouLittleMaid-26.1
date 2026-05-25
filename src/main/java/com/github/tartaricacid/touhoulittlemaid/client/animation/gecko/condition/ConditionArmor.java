package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.isValidResourceLocation;

public class ConditionArmor {
    private static final Pattern ID_PRE_REG = Pattern.compile("^(.+?)\\$(.*?)$");
    private static final Pattern TAG_PRE_REG = Pattern.compile("^(.+?)#(.*?)$");
    private static final String EMPTY = "";

    private final Map<EquipmentSlot, Set<Identifier>> idTest = new Reference2ReferenceOpenHashMap<>();
    private final Map<EquipmentSlot, Set<TagKey<Item>>> tagTest = new Reference2ReferenceOpenHashMap<>();

    public void addTest(String name) {
        Matcher matcherId = ID_PRE_REG.matcher(name);
        if (matcherId.find()) {
            EquipmentSlot type = getType(matcherId.group(1));
            if (type == null) {
                return;
            }
            String id = matcherId.group(2);
            if (!isValidResourceLocation(id)) {
                return;
            }
            Identifier res = Identifier.parse(id);
            idTest.computeIfAbsent(type, t -> new ReferenceOpenHashSet<>()).add(res);
            return;
        }

        Matcher matcherTag = TAG_PRE_REG.matcher(name);
        if (matcherTag.find()) {
            EquipmentSlot type = getType(matcherTag.group(1));
            if (type == null) {
                return;
            }
            String id = matcherTag.group(2);
            if (!isValidResourceLocation(id)) {
                return;
            }
            TagKey<Item> tagKey = TagKey.create(
                    Registries.ITEM,
                    Identifier.parse(id)
            );
            tagTest.computeIfAbsent(type, t -> new ReferenceOpenHashSet<>()).add(tagKey);
        }
    }

    public boolean hasTest(EquipmentSlot slot) {
        return idTest.containsKey(slot) ||  tagTest.containsKey(slot);
    }

    public String doTest(EntityMaid maid, EquipmentSlot slot) {
        ItemStack item = maid.getItemBySlot(slot);
        if (item.isEmpty()) {
            return EMPTY;
        }
        String result = doIdTest(maid, slot);
        if (result.isEmpty()) {
            return doTagTest(maid, slot);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private String doIdTest(EntityMaid maid, EquipmentSlot slot) {
        if (idTest.isEmpty()) {
            return EMPTY;
        }
        if (!idTest.containsKey(slot) || idTest.get(slot).isEmpty()) {
            return EMPTY;
        }
        Set<Identifier> idListTest = idTest.get(slot);
        ItemStack item = maid.getItemBySlot(slot);
        Identifier registryName = item.getItem().builtInRegistryHolder().key().identifier();
        if (idListTest.contains(registryName)) {
            return slot.getName() + "$" + registryName;
        }
        return EMPTY;
    }

    private String doTagTest(EntityMaid maid, EquipmentSlot slot) {
        if (tagTest.isEmpty()) {
            return EMPTY;
        }
        if (!tagTest.containsKey(slot) || tagTest.get(slot).isEmpty()) {
            return EMPTY;
        }
        Set<TagKey<Item>> tagListTest = tagTest.get(slot);
        ItemStack item = maid.getItemBySlot(slot);
        return tagListTest.stream()
                .filter(item::is)
                .findFirst()
                .map(itemTagKey -> slot.getName() + "#" + itemTagKey.location())
                .orElse(EMPTY);
    }


    @Nullable
    public static EquipmentSlot getType(String type) {
        for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            if (equipmentslot.getName().equals(type)) {
                return equipmentslot;
            }
        }
        return null;
    }
}
