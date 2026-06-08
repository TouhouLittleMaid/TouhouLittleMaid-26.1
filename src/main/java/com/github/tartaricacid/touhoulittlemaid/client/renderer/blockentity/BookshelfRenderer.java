package com.github.tartaricacid.touhoulittlemaid.client.renderer.blockentity;

import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityBookshelf;
import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;

import static com.github.tartaricacid.touhoulittlemaid.client.resource.bedrock.InternalBedrockModelRegistry.BOOKSHELF;

public class BookshelfRenderer extends JoyRenderer<BlockEntityBookshelf> {
    private static final Identifier TEXTURE = IdentifierUtil.modLoc("textures/bedrock/block/bookshelf.png");

    public BookshelfRenderer(BlockEntityRendererProvider.Context context) {
        super(BOOKSHELF, TEXTURE);
    }
}
