package com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.builtin.query;

import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.context.IContext;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.function.entity.EntityFunction;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.util.MolangUtils;
import com.github.tartaricacid.touhoulittlemaid.molang.runtime.ExecutionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;

public class BiomeHasAllTags extends EntityFunction {
    @Override
    protected Object eval(ExecutionContext<IContext<Entity>> context, ArgumentCollection arguments) {
        Entity entity = context.entity().entity();
        Holder<Biome> biome = entity.level().getBiome(entity.blockPosition());

        for (int i = 0; i < arguments.size(); i++) {
            ResourceLocation id = MolangUtils.parseResourceLocation(context.entity(), arguments.getAsString(context, i));
            if (id == null) {
                return null;
            }
            TagKey<Biome> tag = TagKey.create(Registries.BIOME, id);
            if (!biome.is(tag)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean validateArgumentSize(int size) {
        return size >= 1;
    }
}
