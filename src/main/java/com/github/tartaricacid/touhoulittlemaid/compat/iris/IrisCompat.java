package com.github.tartaricacid.touhoulittlemaid.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import net.neoforged.fml.ModList;

public class IrisCompat {
    private static final String MOD_ID = "iris";
    private static boolean INSTALLED = false;

    public static void init() {
        if (ModList.get().isLoaded(MOD_ID)) {
            try {
                isRenderingShadow();
                // PBRLoader.register();
                INSTALLED = true;
            } catch (Throwable e) {
                INSTALLED = false;
            }
        }
    }

    public static boolean isInstalled() {
        return INSTALLED;
    }

    public static boolean isRenderingShadow() {
        return INSTALLED && IrisApi.getInstance().isRenderingShadowPass();
    }

/*
    private static class PBRLoader implements PBRTextureLoader<NativeTexture> {
        private static final PBRLoader INSTANCE = new PBRLoader();

        private PBRLoader() {
        }

        @Override
        public void load(NativeTexture texture, ResourceManager resourceManager, PBRTextureConsumer pbrTextureConsumer) {
            var normalTexture = texture.getPBRTextures().get(PBRTextureType.NORMAL);
            if (normalTexture != null) {
                pbrTextureConsumer.acceptNormalTexture(normalTexture);
            }
            var specularTexture = texture.getPBRTextures().get(PBRTextureType.SPECULAR);
            if (specularTexture != null) {
                pbrTextureConsumer.acceptSpecularTexture(specularTexture);
            }
        }

        public static void register() {
            PBRTextureLoaderRegistry.INSTANCE.register(NativeTexture.class, INSTANCE);
        }
    }
*/
}
