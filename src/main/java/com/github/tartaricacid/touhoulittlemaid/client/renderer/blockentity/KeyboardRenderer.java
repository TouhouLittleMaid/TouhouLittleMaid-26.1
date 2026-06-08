package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityKeyboard;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry.KEYBOARD;

public class KeyboardRenderer extends JoyRenderer<BlockEntityKeyboard> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/keyboard.png");

    public KeyboardRenderer(BlockEntityRendererProvider.Context context) {
        super(KEYBOARD, TEXTURE);
    }
}
