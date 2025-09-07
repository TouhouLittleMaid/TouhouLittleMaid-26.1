package com.github.tartaricacid.touhoulittlemaid.compat.gun.swarfare.client;

import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.client.animation.script.ModelRendererWrapper;
import com.github.tartaricacid.touhoulittlemaid.compat.gun.swarfare.SWarfareCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GunBaseAnimation {
    public static boolean onHoldGun(IMaid maid, @Nullable ModelRendererWrapper armLeft, @Nullable ModelRendererWrapper armRight) {
        Mob mob = maid.asEntity();
        if (mob == null) {
            return false;
        }
        ItemStack handItem = mob.getMainHandItem();
        if (!(handItem.getItem() instanceof GunItem)) {
            return false;
        }
        ResourceLocation gunId = BuiltInRegistries.ITEM.getKey(handItem.getItem());

        // 因为现在还没有 minigun 的专属标签，故只能用特判
        if (gunId.equals(SWarfareCompat.MINIGUN_ID) || gunId.equals(SWarfareCompat.M_2_HB_ID)) {
            if (armLeft != null) {
                armLeft.setRotateAngleX(-1.45f);
                armLeft.setRotateAngleY(1f);
            }
            if (armRight != null) {
                armRight.setRotateAngleX(0.75f);
                armRight.setRotateAngleY(0);
            }
            return true;
        }

        if (armLeft != null) {
            armLeft.setRotateAngleX(-1.75f);
            armLeft.setRotateAngleY(0.5f);
        }
        if (armRight != null) {
            armRight.setRotateAngleX(-1.65f);
            armRight.setRotateAngleY(-0.174f);
        }
        return true;
    }
}
