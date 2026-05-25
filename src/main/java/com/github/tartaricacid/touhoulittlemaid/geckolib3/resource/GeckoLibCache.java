package com.github.tartaricacid.touhoulittlemaid.geckolib3.resource;

import com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.molang.*;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.core.molang.MolangParser;
import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class GeckoLibCache {
    private static final Map<String, Object> EXTRA_BINDING = new HashMap<>();
    private static GeckoLibCache INSTANCE;
    public final ThreadLocal<MolangParser> parser = ThreadLocal.withInitial(GeckoLibCache::createMolangParser);
    private final Map<Identifier, GeckoContainer> models = Maps.newHashMap();

    public static GeckoLibCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeckoLibCache();
            return INSTANCE;
        }
        return INSTANCE;
    }

    public Map<Identifier, GeckoContainer> getModels() {
        return models;
    }

    private static MolangParser createMolangParser() {
        try {
            EXTRA_BINDING.put("ysm", YSMBinding.INSTANCE.get());
            EXTRA_BINDING.put("tlm", TLMBinding.INSTANCE.get());
            EXTRA_BINDING.put("ctrl", CtrlBinding.INSTANCE.get());
            EXTRA_BINDING.put("args", UserFunctionArgument.INSTANCE);
            EXTRA_BINDING.put("fn", new UserFunctionBinding());
            return new MolangParser(EXTRA_BINDING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
