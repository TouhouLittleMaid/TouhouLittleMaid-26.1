package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidDeathEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTombstoneEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.item.EntityTombstone;
import com.github.tartaricacid.touhoulittlemaid.world.data.MaidWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Consumer;

@MaidManagerDef(alias = "deathManager", exposeView = false)
public class MaidDeathManager {
    private final EntityMaid maid;
    /**
     * 一个记录女仆已经生成墓碑的变量，避免死亡重复生成墓碑
     */
    private boolean alreadyDropped = false;

    public MaidDeathManager(EntityMaid maid) {
        this.maid = maid;
    }

    void die(DamageSource cause, Consumer<DamageSource> superDie) {
        boolean baubleCancel = maid.getMaidBauble().fireEvent((b, s) -> b.onDeath(maid, s, cause));
        if (!baubleCancel && !NeoForge.EVENT_BUS.post(new MaidDeathEvent(maid, cause)).isCanceled()) {
            // 清除死亡时需要清除的内容
            maid.clearFire();
            maid.setTicksFrozen(0);
            maid.setSharedFlagOnFire(false);
            maid.removeAllEffects();
            // 最后父类方法
            superDie.accept(cause);
            // 额外发送女仆所处坐标
            this.sendMaidPos();
        }
    }

    void dropEquipment(ServerLevel level) {
        var owner = maid.getOwnerReference();
        if (owner != null) {
            // 掉出世界的判断
            Vec3 position = Vec3.atBottomCenterOf(maid.blockPosition());
            // 防止卡在基岩里？
            if (maid.getY() < maid.level.getMaxY() + 5) {
                position = new Vec3(position.x, maid.level.getMinY() + 5, position.z);
            }
            if (maid.getY() > maid.level.getMaxY()) {
                position = new Vec3(position.x, maid.level.getMaxY(), position.z);
            }
            EntityTombstone tombstone = new EntityTombstone(level, owner.getUUID(), position);
            tombstone.setMaidName(maid.getDisplayName());

            maid.getItemManager().addItemsToTomb(tombstone);

            // 事件触发，既可以阻断墓碑生成，也可以修改墓碑内容
            MaidTombstoneEvent tombstoneEvent = new MaidTombstoneEvent(maid, tombstone);
            if (NeoForge.EVENT_BUS.post(tombstoneEvent).isCanceled()) {
                // 如果事件被取消了，那么就不生成墓碑了
                return;
            }

            // 全局记录
            MaidWorldData maidWorldData = MaidWorldData.get(level);
            if (maidWorldData != null) {
                maidWorldData.addTombstones(maid, tombstone);
            }

            // 记录墓碑已经生成，避免重复生成
            alreadyDropped = true;
            level.addFreshEntity(tombstone);
        }
    }

    void remove(Entity.RemovalReason reason) {
        // TODO: 尝试修复可能存在的目标生成丢失问题，可能会有问题
        if (reason == Entity.RemovalReason.KILLED && !alreadyDropped) {
            // 女仆被指令杀后也正常生成墓碑
            if (maid.level instanceof ServerLevel level) {
                this.dropEquipment(level);
            }
        }
    }

    private void sendMaidPos() {
        if (maid.isDead() && maid.level instanceof ServerLevel level
            && level.getGameRules().get(GameRules.SHOW_DEATH_MESSAGES)
            && maid.getOwner() instanceof ServerPlayer serverPlayer) {
            // 支持旅行地图格式
            // [name:"name", x:-136, y:36, z:48, dim:minecraft:the_nether]
            BlockPos blockPos = maid.blockPosition();
            String name = Identifier.parse(maid.getModelId()).getPath();
            String msg = """
                    [name:"%s", x:%d, y:%d, z:%d, dim:%s]""".formatted(
                    name,
                    blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                    maid.level.dimension().identifier().toString()
            );
            OutgoingChatMessage message = OutgoingChatMessage.create(PlayerChatMessage.system(msg));
            serverPlayer.sendChatMessage(message, false, ChatType.bind(ChatType.CHAT, serverPlayer));
        }
    }

    interface View {
        MaidDeathManager getDeathManager();
    }
}
