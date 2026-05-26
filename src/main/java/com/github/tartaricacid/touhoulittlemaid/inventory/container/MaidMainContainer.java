package com.github.tartaricacid.touhoulittlemaid.inventory.container;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.backpack.ITriggerSlotChange;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidBackpackChangeEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitCapabilities;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

import javax.annotation.Nullable;

import static net.minecraft.world.inventory.InventoryMenu.*;


public abstract class MaidMainContainer extends AbstractMaidContainer {
    protected static final int PLAYER_INVENTORY_SIZE = 36;
    protected static final Identifier EMPTY_MAINHAND_SLOT = Identifier.parse("item/empty_slot_sword");
    protected static final Identifier EMPTY_BACK_SHOW_SLOT = Identifier.fromNamespaceAndPath(TouhouLittleMaid.MOD_ID, "slot/empty_back_show_slot");
    protected static final Identifier[] TEXTURE_EMPTY_SLOTS = new Identifier[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
    protected static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public MaidMainContainer(MenuType<?> type, int id, Inventory inventory, int entityId) {
        super(type, id, inventory, entityId);
        if (maid != null) {
            this.addMaidArmorInv();
            this.addMaidHandInv();
            this.addMainDefaultInv();
            this.addBackpackInv(inventory);
        }
    }

    protected void addMaidHandInv() {
        ResourceHandler<ItemResource> capability = maid.getCapability(InitCapabilities.HAND_ITEM, Direction.DOWN);
        if (capability != null) {
            var indexModifier = ItemsUtil.createIndexModifier(capability);

            addSlot(new ResourceHandlerSlot(capability, indexModifier, 0, 87, 77) {
                @Override
                public Identifier getNoItemIcon() {
                    return EMPTY_MAINHAND_SLOT;
                }
            });
            addSlot(new ResourceHandlerSlot(capability, indexModifier, 1, 121, 77) {
                @Override
                public Identifier getNoItemIcon() {
                    return EMPTY_ARMOR_SLOT_SHIELD;
                }
            });
        }

    }

    protected void addMaidArmorInv() {
        ResourceHandler<ItemResource> capability = maid.getCapability(InitCapabilities.ARMOR_ITEM, Direction.DOWN);
        if (capability != null) {
            var indexModifier = ItemsUtil.createIndexModifier(capability);

            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; j++) {
                    final EquipmentSlot equipmentSlot = SLOT_IDS[2 * i + j];
                    addSlot(new ResourceHandlerSlot(capability, indexModifier, 3 - 2 * i - j, 94 + 20 * j, 37 + 20 * i) {
                        @Override
                        public int getMaxStackSize() {
                            return 1;
                        }

                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return stack.canEquip(equipmentSlot, maid) && stack.getItem().canFitInsideContainerItems();
                        }

                        @Override
                        public boolean mayPickup(Player playerIn) {
                            ItemStack itemstack = this.getItem();
                            boolean curseEnchant = !itemstack.isEmpty() && !playerIn.isCreative()
                                    && EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
                            return !curseEnchant && super.mayPickup(playerIn);
                        }

                        @Override
                        public Identifier getNoItemIcon() {
                            return TEXTURE_EMPTY_SLOTS[equipmentSlot.getIndex()];
                        }
                    });
                }
            }
        }

    }

    protected void addMainDefaultInv() {
        // 默认背包
        for (int i = 0; i < 6; i++) {
            addSlot(BackpackSlot.create(maid, i, 143 + 18 * i, 37));
            // 最后一格给予特殊图标
            if (i == 5) {
                ItemStacksResourceHandler maidInv = maid.getMaidInv();
                addSlot(new BackpackSlot(maid, maidInv::set, i, 143 + 18 * i, 37) {
                    @Override
                    public Identifier getNoItemIcon() {
                        return EMPTY_BACK_SHOW_SLOT;
                    }
                });
            }
        }
    }

    protected abstract void addBackpackInv(Inventory inventory);

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack1 = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack2 = slot.getItem();
            stack1 = stack2.copy();

            if (index < PLAYER_INVENTORY_SIZE) {
                if (!this.moveItemStackTo(stack2, PLAYER_INVENTORY_SIZE, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack2, 0, PLAYER_INVENTORY_SIZE, true)) {
                return ItemStack.EMPTY;
            }

            if (stack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack2.getCount() == stack1.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack2);
            // 触发 Shift 点击取出事件
            if (slot instanceof ITriggerSlotChange slotChange) {
                slotChange.onShiftTakeoff(player, stack1);
            }
        }
        return stack1;
    }

    public static class BackpackSlot extends ResourceHandlerSlot implements ITriggerSlotChange {
        private final EntityMaid maid;

        private BackpackSlot(EntityMaid maid, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition) {
            super(maid.getMaidInv(), slotModifier, index, xPosition, yPosition);
            this.maid = maid;
        }

        public static BackpackSlot create(EntityMaid maid, int index, int xPosition, int yPosition) {
            ItemStacksResourceHandler maidInv = maid.getMaidInv();
            return new BackpackSlot(maid, maidInv::set, index, xPosition, yPosition);
        }

        @Override
        public void onShiftTakeoff(@Nullable Player player, ItemStack stack) {
            if (!maid.level.isClientSide() && !stack.isEmpty()) {
                NeoForge.EVENT_BUS.post(new MaidBackpackChangeEvent.TakeOff(maid, stack));
            }
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);
            this.onShiftTakeoff(player, stack);
        }

        @Override
        public void setByPlayer(ItemStack stack) {
            super.setByPlayer(stack);
            if (!maid.level.isClientSide() && !stack.isEmpty()) {
                NeoForge.EVENT_BUS.post(new MaidBackpackChangeEvent.PutOn(maid, stack));
            }
        }
    }
}
