package com.github.tartaricacid.touhoulittlemaid.entity.passive.component.impl;

import com.github.tartaricacid.touhoulittlemaid.entity.data.StatsData;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.MaidComponentDef;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.component.lifecycle.SaveComponent;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.STATS;

/**
 * 女仆状态管理类，主要维护一些非 Attribute 的属性，比如饥饿度、好感度、经验值等
 */
@MaidComponentDef("stats")
public class MaidStatsComponent implements MaidComponent, SaveComponent {
    private static final String STRUCTURE_SPAWN_TAG = "StructureSpawn";

    /**
     * 女仆现在可以在前哨站生成，那么会打上这个标签
     */
    public boolean structureSpawn = false;

    private final EntityMaid maid;

    public MaidStatsComponent(EntityMaid entityMaid) {
        this.maid = entityMaid;
    }

    private StatsData getStatsData() {
        return this.maid.getData(STATS);
    }

    private void setStatsData(StatsData data) {
        this.maid.setData(STATS, data);
    }

    public int getHunger() {
        return this.getStatsData().hunger();
    }

    public void setHunger(int hunger) {
        this.setStatsData(this.getStatsData().withHunger(hunger));
    }

    public int getFavorability() {
        return this.getStatsData().favorability();
    }

    public void setFavorability(int favorability) {
        this.setStatsData(this.getStatsData().withFavorability(favorability));
    }

    public int getExperience() {
        return this.getStatsData().experience();
    }

    public void setExperience(int experience) {
        this.setStatsData(this.getStatsData().withExperience(experience));
    }

    public boolean isStruckByLightning() {
        return this.getStatsData().struckByLightning();
    }

    public void setStruckByLightning(boolean isStruck) {
        this.setStatsData(this.getStatsData().withStruckByLightning(isStruck));
    }

    public boolean isStructureSpawn() {
        return structureSpawn;
    }

    @Override
    public void load(ValueInput input) {
        input.read(STRUCTURE_SPAWN_TAG, Codec.BOOL).ifPresent(v -> this.structureSpawn = v);
    }

    @Override
    public void save(ValueOutput output) {
        output.store(STRUCTURE_SPAWN_TAG, Codec.BOOL, this.structureSpawn);
    }
}
