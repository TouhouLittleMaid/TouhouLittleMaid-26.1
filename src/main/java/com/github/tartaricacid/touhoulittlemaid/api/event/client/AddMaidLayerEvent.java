package com.github.tartaricacid.touhoulittlemaid.api.event.client;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.gecko.GeckoEntityMaidRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.bus.api.Event;

/**
 * 为女仆实体添加 Layer 的事件
 */
public abstract class AddMaidLayerEvent extends Event {
    private final EntityRendererProvider.Context context;

    public AddMaidLayerEvent(EntityRendererProvider.Context context) {
        this.context = context;
    }

    public EntityRendererProvider.Context getContext() {
        return context;
    }

    public static class Legacy extends AddMaidLayerEvent {
        private final EntityMaidRenderer renderer;

        public Legacy(EntityRendererProvider.Context context, EntityMaidRenderer renderer) {
            super(context);
            this.renderer = renderer;
        }

        public EntityMaidRenderer getRenderer() {
            return renderer;
        }
    }

    public static class Gecko extends AddMaidLayerEvent {
        private final GeckoEntityMaidRenderer renderer;

        public Gecko(EntityRendererProvider.Context context, GeckoEntityMaidRenderer renderer) {
            super(context);
            this.renderer = renderer;
        }

        public GeckoEntityMaidRenderer getRenderer() {
            return renderer;
        }
    }
}
