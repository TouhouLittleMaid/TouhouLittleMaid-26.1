package com.github.tartaricacid.touhoulittlemaid.client.animation.gecko.condition;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import static com.github.tartaricacid.touhoulittlemaid.util.IdentifierUtil.isValid;

public class ConditionalSwing {
    private static final String EMPTY = "";

    private final InteractionHand hand;
    private final int preSize;
    private final String idPre;
    private final String tagPre;
    private final String extraPre;

    private final Set<Identifier> idTest = new ReferenceOpenHashSet<>();
    private final Set<TagKey<Item>> tagTest = new ReferenceOpenHashSet<>();
    private final Set<ItemUseAnimation> extraTest = new ReferenceOpenHashSet<>();
    private final Set<String> innerTest = new ReferenceOpenHashSet<>();

    public ConditionalSwing(InteractionHand hand) {
        this.hand = hand;
        if (hand == InteractionHand.MAIN_HAND) {
            idPre = "swing$";
            tagPre = "swing#";
            extraPre = "swing:";
            preSize = 6;
        } else {
            idPre = "swing_offhand$";
            tagPre = "swing_offhand#";
            extraPre = "swing_offhand:";
            preSize = 14;
        }
    }

    public void addTest(String name) {
        if (name.length() <= preSize) {
            return;
        }
        String substring = name.substring(preSize);
        if (name.startsWith(idPre) && isValid(substring)) {
            idTest.add(Identifier.parse(substring));
        }
        if (name.startsWith(tagPre) && isValid(substring)) {
            tagTest.add(TagKey.create(
                    Registries.ITEM,
                    Identifier.parse(substring)
            ));
        }
        if (name.startsWith(extraPre)) {
            if (substring.equals(ItemUseAnimation.NONE.name().toLowerCase(Locale.US))) {
                return;
            }
            Arrays.stream(ItemUseAnimation.values()).filter(a -> a.name().toLowerCase(Locale.US).equals(substring)).findFirst().ifPresent(extraTest::add);
            innerTest.add(name);
        }
    }

    public String doTest(EntityMaid maid) {
        if (maid.getItemInHand(hand).isEmpty()) {
            return EMPTY;
        }
        String result = doIdTest(maid);
        if (result.isEmpty()) {
            result = doTagTest(maid);
            if (result.isEmpty()) {
                return doExtraTest(maid);
            }
            return result;
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private String doIdTest(EntityMaid maid) {
        if (idTest.isEmpty()) {
            return EMPTY;
        }
        ItemStack itemInHand = maid.getItemInHand(hand);
        Identifier registryName = itemInHand.getItem().builtInRegistryHolder().key().identifier();
        if (idTest.contains(registryName)) {
            return idPre + registryName;
        }
        return EMPTY;
    }

    private String doTagTest(EntityMaid maid) {
        if (tagTest.isEmpty()) {
            return EMPTY;
        }
        ItemStack itemInHand = maid.getItemInHand(hand);
        return tagTest.stream().filter(itemInHand::is).findFirst().map(itemTagKey -> tagPre + itemTagKey.location()).orElse(EMPTY);
    }

    private String doExtraTest(EntityMaid maid) {
        if (extraTest.isEmpty() && innerTest.isEmpty()) {
            return EMPTY;
        }
        String innerName = InnerClassify.doClassifyTest(extraPre, maid, hand);
        if (StringUtils.isNotBlank(innerName) && this.innerTest.contains(innerName)) {
            return innerName;
        }
        ItemUseAnimation anim = maid.getItemInHand(hand).getUseAnimation();
        if (this.extraTest.contains(anim)) {
            return extraPre + anim.name().toLowerCase(Locale.US);
        }
        return EMPTY;
    }
}
