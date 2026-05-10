package com.github.tartaricacid.touhoulittlemaid.compat.kubejs;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.event.RegisterKubeJSEvent;
import com.github.tartaricacid.touhoulittlemaid.client.overlay.MaidTipsOverlay;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.CommonEventsPostJS;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.MaidEventsJS;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.compat.JadeEventsPostJS;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.event.compat.TopEventsPostJS;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.recipe.AltarRecipeSchema;
import com.github.tartaricacid.touhoulittlemaid.compat.kubejs.register.MaidRegisterJS;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.tartaricacid.touhoulittlemaid.init.InitRecipes;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

public class ModKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void init() {
        ModKubeJSCompat.ENABLE = true;
        NeoForge.EVENT_BUS.register(new CommonEventsPostJS());
        if (ModList.get().isLoaded("jade")) {
            NeoForge.EVENT_BUS.register(new JadeEventsPostJS());
        }
        if (ModList.get().isLoaded("theoneprobe")) {
            NeoForge.EVENT_BUS.register(new TopEventsPostJS());
        }
    }

    @Override
    public void afterScriptsLoaded(ScriptManager manager) {
        // 1.21 KJS 终于给 afterScriptsLoaded 了，哭死
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MaidTipsOverlay.init();
        }
        BaubleManager.init();
        TaskManager.init();
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        EventGroup group = MaidEventsJS.GROUP;
        NeoForge.EVENT_BUS.post(new RegisterKubeJSEvent(group));
        registry.register(group);
    }

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        registry.namespace(TouhouLittleMaid.MOD_ID)
                .register(InitRecipes.ALTAR_RECIPE_SERIALIZER.getId().getPath(), AltarRecipeSchema.SCHEMA);
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("MaidRegister", MaidRegisterJS.class);
        bindings.add("MaidItemsUtil", ItemsUtil.class);
    }
}
