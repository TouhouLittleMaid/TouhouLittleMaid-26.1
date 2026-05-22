package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.Set;

import static com.github.tartaricacid.touhoulittlemaid.util.ResourceLocationUtil.isValidResourceLocation;

public class ConditionalVehicle {
    private static final String EMPTY = "";
    private final Set<Identifier> idTest = new ReferenceOpenHashSet<>();
    private final Set<TagKey<EntityType<?>>> tagTest = new ReferenceOpenHashSet<>();
    private final String idPre;
    private final String tagPre;

    public ConditionalVehicle() {
        this.idPre = "vehicle$";
        this.tagPre = "vehicle#";
    }

    public void addTest(String name) {
        int preSize = this.idPre.length();
        if (name.length() <= preSize) {
            return;
        }
        String substring = name.substring(preSize);
        if (name.startsWith(idPre) && isValidResourceLocation(substring)) {
            idTest.add(Identifier.parse(substring));
        }
        if (name.startsWith(tagPre) && isValidResourceLocation(substring)) {
            tagTest.add(TagKey.create(
                    Registries.ENTITY_TYPE,
                    Identifier.parse(substring)
            ));
        }
    }

    public String doTest(Mob maid) {
        Entity vehicle = maid.getVehicle();
        if (vehicle == null || !vehicle.isAlive()) {
            return EMPTY;
        }
        String result = doIdTest(vehicle);
        if (result.isEmpty()) {
            return doTagTest(vehicle);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private String doIdTest(Entity vehicle) {
        if (idTest.isEmpty()) {
            return EMPTY;
        }
        Identifier registryName = vehicle.getType().builtInRegistryHolder().key().identifier();
        if (idTest.contains(registryName)) {
            return idPre + registryName;
        }
        return EMPTY;
    }

    private String doTagTest(Entity vehicle) {
        if (tagTest.isEmpty()) {
            return EMPTY;
        }
        return tagTest.stream()
                .filter(vehicle::is)
                .findFirst()
                .map(itemTagKey -> tagPre + itemTagKey.location())
                .orElse(EMPTY);
    }
}
