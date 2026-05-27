package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.entity.data.StatsData;

import static com.github.tartaricacid.touhoulittlemaid.init.InitDataAttachment.STATS;

public class MaidStatsManager {
    private final EntityMaid maid;

    public MaidStatsManager(EntityMaid entityMaid) {
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

    public interface View {
        MaidStatsManager getStatsManager();

        default int getHunger() {
            return getStatsManager().getHunger();
        }

        default void setHunger(int hunger) {
            getStatsManager().setHunger(hunger);
        }

        default int getFavorability() {
            return getStatsManager().getFavorability();
        }

        default void setFavorability(int favorability) {
            getStatsManager().setFavorability(favorability);
        }

        default int getExperience() {
            return getStatsManager().getExperience();
        }

        default void setExperience(int experience) {
            getStatsManager().setExperience(experience);
        }

        default boolean isStruckByLightning() {
            return getStatsManager().isStruckByLightning();
        }

        default void setStruckByLightning(boolean isStruck) {
            getStatsManager().setStruckByLightning(isStruck);
        }
    }
}
