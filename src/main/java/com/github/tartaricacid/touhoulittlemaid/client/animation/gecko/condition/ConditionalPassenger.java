package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.Set;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.isValid;

public class ConditionalPassenger {
    private static final String EMPTY = "";
    private final Set<Identifier> idTest = new ReferenceOpenHashSet<>();
    private final Set<TagKey<EntityType<?>>> tagTest = new ReferenceOpenHashSet<>();
    private final String idPre;
    private final String tagPre;

    public ConditionalPassenger() {
        this.idPre = "passenger$";
        this.tagPre = "passenger#";
    }

    public void addTest(String name) {
        int preSize = this.idPre.length();
        if (name.length() <= preSize) {
            return;
        }
        String substring = name.substring(preSize);
        if (name.startsWith(idPre) && isValid(substring)) {
            idTest.add(Identifier.parse(substring));
        }
        if (name.startsWith(tagPre) && isValid(substring)) {
            tagTest.add(TagKey.create(
                    Registries.ENTITY_TYPE,
                    Identifier.parse(substring)
            ));
        }
    }

    public String doTest(Mob maid) {
        Entity passenger = maid.getFirstPassenger();
        if (passenger == null || !passenger.isAlive()) {
            return EMPTY;
        }
        String result = doIdTest(passenger);
        if (result.isEmpty()) {
            return doTagTest(passenger);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private String doIdTest(Entity passenger) {
        if (idTest.isEmpty()) {
            return EMPTY;
        }
        Identifier registryName = passenger.getType().builtInRegistryHolder().key().identifier();
        if (idTest.contains(registryName)) {
            return idPre + registryName;
        }
        return EMPTY;
    }

    private String doTagTest(Entity passenger) {
        if (tagTest.isEmpty()) {
            return EMPTY;
        }
        return tagTest.stream()
                .filter(passenger::is)
                .findFirst()
                .map(itemTagKey -> tagPre + itemTagKey.location())
                .orElse(EMPTY);
    }
}
