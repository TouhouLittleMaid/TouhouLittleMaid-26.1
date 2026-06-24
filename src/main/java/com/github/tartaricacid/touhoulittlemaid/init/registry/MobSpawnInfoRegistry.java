package com.github.tartaricacid.touhoulittlemaid.init.registry;

import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.util.EntityTypeUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.List;

import static com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig.MAID_FAIRY_BLACKLIST_DIMENSION;

@EventBusSubscriber
public final class MobSpawnInfoRegistry {
    private static Weighted<MobSpawnSettings.SpawnerData> SPAWNER_DATA;

    @SubscribeEvent
    public static void addMobSpawnInfo(LevelEvent.PotentialSpawns event) {
        if (event.getLevel() instanceof ServerLevel level) {
            int spawnProbability = MiscConfig.MAID_FAIRY_SPAWN_PROBABILITY.get();
            if (spawnProbability <= 0) {
                // 优先判断等于 0 的情况，减少性能消耗
                return;
            }
            Identifier dimension = level.dimension().identifier();
            if (event.getMobCategory() == MobCategory.MONSTER && dimensionIsOkay(dimension)) {
                List<Weighted<MobSpawnSettings.SpawnerData>> spawnerData = event.getSpawnerDataList();
                boolean canZombieSpawn = spawnerData.stream().anyMatch(data -> data.value().type().equals(EntityTypeUtil.zombie()));
                if (SPAWNER_DATA == null || SPAWNER_DATA.weight() != spawnProbability) {
                    var data = new MobSpawnSettings.SpawnerData(InitEntities.FAIRY.get(), 2, 4);
                    SPAWNER_DATA = new Weighted<>(data, spawnProbability);
                }
                if (canZombieSpawn) {
                    event.addSpawnerData(SPAWNER_DATA);
                }
            }
        }
    }

    private static boolean dimensionIsOkay(Identifier id) {
        return !MAID_FAIRY_BLACKLIST_DIMENSION.get().contains(id.toString());
    }
}
