package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityComputer;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry.COMPUTER;

public class ComputerRenderer extends JoyRenderer<BlockEntityComputer> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/computer.png");

    public ComputerRenderer(BlockEntityRendererProvider.Context context) {
        super(COMPUTER, TEXTURE);
    }
}