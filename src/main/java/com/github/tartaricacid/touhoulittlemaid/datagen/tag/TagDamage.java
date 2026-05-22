package com.github.tartaricacid.touhoulittlemaid.datagen.tag;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitDamage;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import java.util.concurrent.CompletableFuture;

public class TagDamage extends DamageTypeTagsProvider {
    public TagDamage(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider) {
        super(pOutput, pLookupProvider, TouhouLittleMaid.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(DamageTypeTags.IS_PROJECTILE).add(InitDamage.DANMAKU);
        this.tag(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS)
                .add(InitDamage.DANMAKU_ENDER_KILLER);
    }
}
