package com.github.tartaricacid.touhoulittlemaid.compat.jade.provider;

import com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil;
import com.github.tartaricacid.touhoulittlemaid.blockentity.BlockEntityPicnicMat;
import com.google.common.collect.Lists;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.*;

import java.util.List;

public enum PicnicMatProvider implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
    INSTANCE;

    private static final Identifier UID = IdentifierUtil.modLoc("picnic_mat");

    @Override
    public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> list) {
        return ClientViewGroup.map(list, ItemView::new, null);
    }

    @Override
    public @Nullable List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        if (accessor.getTarget() instanceof BlockEntityPicnicMat picnicMat) {
            if (accessor.getLevel().getBlockEntity(picnicMat.getCenterPos()) instanceof BlockEntityPicnicMat picnicMatCenter) {
                var handler = picnicMatCenter.getHandler();
                List<ItemStack> list = Lists.newArrayList();
                for (int i = 0; i < handler.size(); i++) {
                    ItemStack stack = ItemUtil.getStack(handler, i);
                    if (stack.isEmpty()) {
                        continue;
                    }
                    list.add(stack.copy());
                }
                return List.of(new ViewGroup<>(list));
            }
        }
        return null;
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
