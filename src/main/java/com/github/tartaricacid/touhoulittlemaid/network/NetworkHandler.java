package com.github.tartaricacid.touhoulittlemaid.network;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.network.message.*;
import com.github.tartaricacid.touhoulittlemaid.network.message.ai.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID)
public class NetworkHandler {
    private static final String VERSION = "2.0.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);

        registrar.playToServer(MaidModelPackage.TYPE, MaidModelPackage.STREAM_CODEC, MaidModelPackage::handle);
        registrar.playToServer(ChairModelPackage.TYPE, ChairModelPackage.STREAM_CODEC, ChairModelPackage::handle);
        registrar.playToClient(OpenChairGuiPackage.TYPE, OpenChairGuiPackage.STREAM_CODEC, OpenChairGuiPackage::handle);
        registrar.playToServer(MaidConfigPackage.TYPE, MaidConfigPackage.STREAM_CODEC, MaidConfigPackage::handle);
        registrar.playToServer(MaidTaskPackage.TYPE, MaidTaskPackage.STREAM_CODEC, MaidTaskPackage::handle);
        registrar.playToServer(SendNameTagPackage.TYPE, SendNameTagPackage.STREAM_CODEC, SendNameTagPackage::handle);
        registrar.playToClient(ItemBreakPackage.TYPE, ItemBreakPackage.STREAM_CODEC, ItemBreakPackage::handle);
        registrar.playToClient(SpawnParticlePackage.TYPE, SpawnParticlePackage.STREAM_CODEC, SpawnParticlePackage::handle);
        registrar.playToClient(SyncDataPackage.TYPE, SyncDataPackage.STREAM_CODEC, SyncDataPackage::handle);
        registrar.playToServer(WirelessIOGuiPackage.TYPE, WirelessIOGuiPackage.STREAM_CODEC, WirelessIOGuiPackage::handle);
        registrar.playToServer(WirelessIOSlotConfigPackage.TYPE, WirelessIOSlotConfigPackage.STREAM_CODEC, WirelessIOSlotConfigPackage::handle);
        registrar.playToClient(OpenBeaconGuiPackage.TYPE, OpenBeaconGuiPackage.STREAM_CODEC, OpenBeaconGuiPackage::handle);
        registrar.playToServer(SetBeaconPotionPackage.TYPE, SetBeaconPotionPackage.STREAM_CODEC, SetBeaconPotionPackage::handle);
        registrar.playToServer(StorageAndTakePowerPackage.TYPE, StorageAndTakePowerPackage.STREAM_CODEC, StorageAndTakePowerPackage::handle);
        registrar.playToServer(SetBeaconOverflowPackage.TYPE, SetBeaconOverflowPackage.STREAM_CODEC, SetBeaconOverflowPackage::handle);
        registrar.playToClient(BeaconAbsorbPackage.TYPE, BeaconAbsorbPackage.STREAM_CODEC, BeaconAbsorbPackage::handle);
        registrar.playToClient(OpenSwitcherGuiPackage.TYPE, OpenSwitcherGuiPackage.STREAM_CODEC, OpenSwitcherGuiPackage::handle);
        registrar.playToServer(SaveSwitcherDataPackage.TYPE, SaveSwitcherDataPackage.STREAM_CODEC, SaveSwitcherDataPackage::handle);
        registrar.playToServer(ToggleTabPackage.TYPE, ToggleTabPackage.STREAM_CODEC, ToggleTabPackage::handle);
        registrar.playToServer(RequestEffectPackage.TYPE, RequestEffectPackage.STREAM_CODEC, RequestEffectPackage::handle);
        registrar.playToClient(SendEffectPackage.TYPE, SendEffectPackage.STREAM_CODEC, SendEffectPackage::handle);
        registrar.playToClient(PlayMaidSoundPackage.TYPE, PlayMaidSoundPackage.STREAM_CODEC, PlayMaidSoundPackage::handle);
        registrar.playToClient(PlayMaidSoundAtPosPackage.TYPE, PlayMaidSoundAtPosPackage.STREAM_CODEC, PlayMaidSoundAtPosPackage::handle);
        registrar.playToServer(SetMaidSoundIdPackage.TYPE, SetMaidSoundIdPackage.STREAM_CODEC, SetMaidSoundIdPackage::handle);
        registrar.playToClient(GomokuClientPackage.TYPE, GomokuClientPackage.STREAM_CODEC, GomokuClientPackage::handle);
        registrar.playToServer(GomokuServerPackage.TYPE, GomokuServerPackage.STREAM_CODEC, GomokuServerPackage::handle);
        registrar.playToClient(FoxScrollPackage.TYPE, FoxScrollPackage.STREAM_CODEC, FoxScrollPackage::handle);
        registrar.playToServer(SetScrollPackage.TYPE, SetScrollPackage.STREAM_CODEC, SetScrollPackage::handle);
        registrar.playToClient(CheckSchedulePosPacket.TYPE, CheckSchedulePosPacket.STREAM_CODEC, CheckSchedulePosPacket::handle);
        registrar.playToClient(SyncMaidAreaPackage.TYPE, SyncMaidAreaPackage.STREAM_CODEC, SyncMaidAreaPackage::handle);
        registrar.playToServer(ServantBellSetPackage.TYPE, ServantBellSetPackage.STREAM_CODEC, ServantBellSetPackage::handle);
        registrar.playToServer(SetAttackListPackage.TYPE, SetAttackListPackage.STREAM_CODEC, SetAttackListPackage::handle);
        registrar.playToServer(RefreshMaidBrainPackage.TYPE, RefreshMaidBrainPackage.STREAM_CODEC, RefreshMaidBrainPackage::handle);
        registrar.playToServer(MaidSubConfigPackage.TYPE, MaidSubConfigPackage.STREAM_CODEC, MaidSubConfigPackage::handle);
        registrar.playToClient(CChessToClientPackage.TYPE, CChessToClientPackage.STREAM_CODEC, CChessToClientPackage::handle);
        registrar.playToServer(CChessToServerPackage.TYPE, CChessToServerPackage.STREAM_CODEC, CChessToServerPackage::handle);
        registrar.playToClient(WChessToClientPackage.TYPE, WChessToClientPackage.STREAM_CODEC, WChessToClientPackage::handle);
        registrar.playToServer(WChessToServerPackage.TYPE, WChessToServerPackage.STREAM_CODEC, WChessToServerPackage::handle);
        registrar.playToServer(SendUserChatPackage.TYPE, SendUserChatPackage.STREAM_CODEC, SendUserChatPackage::handle);
        registrar.playToClient(TTSAudioToClientPackage.TYPE, TTSAudioToClientPackage.STREAM_CODEC, TTSAudioToClientPackage::handle);
        registrar.playToServer(SaveMaidAIDataPackage.TYPE, SaveMaidAIDataPackage.STREAM_CODEC, SaveMaidAIDataPackage::handle);
        registrar.playToClient(TTSSystemAudioToClientPackage.TYPE, TTSSystemAudioToClientPackage.STREAM_CODEC, TTSSystemAudioToClientPackage::handle);
        registrar.playToServer(ClearMaidAIDataPacket.TYPE, ClearMaidAIDataPacket.STREAM_CODEC, ClearMaidAIDataPacket::handle);
        registrar.playToServer(OpenMaidGuiPackage.TYPE, OpenMaidGuiPackage.STREAM_CODEC, OpenMaidGuiPackage::handle);
        registrar.playToClient(OpenPlayerInventoryPackage.TYPE, OpenPlayerInventoryPackage.STREAM_CODEC, OpenPlayerInventoryPackage::handle);
        registrar.playToServer(DismountPackage.TYPE, DismountPackage.STREAM_CODEC, DismountPackage::handle);
        registrar.playToClient(MaidAnimationPackage.TYPE, MaidAnimationPackage.STREAM_CODEC, MaidAnimationPackage::handle);
        registrar.playToClient(SyncBaublePackage.TYPE, SyncBaublePackage.STREAM_CODEC, SyncBaublePackage::handle);
        registrar.playToClient(CuriosS2CUpdatePacket.TYPE, CuriosS2CUpdatePacket.STREAM_CODEC, CuriosS2CUpdatePacket::handle);

        registrar.playToServer(OpenMaidAIChatPacket.TYPE, OpenMaidAIChatPacket.STREAM_CODEC, OpenMaidAIChatPacket::handle);
        registrar.playToClient(SyncMaidAIDataPacket.TYPE, SyncMaidAIDataPacket.STREAM_CODEC, SyncMaidAIDataPacket::handle);
        registrar.playToServer(OpenAIConfigPacket.TYPE, OpenAIConfigPacket.STREAM_CODEC, OpenAIConfigPacket::handle);
        registrar.playToClient(SyncAISitesPacket.TYPE, SyncAISitesPacket.STREAM_CODEC, SyncAISitesPacket::handle);
        registrar.playToServer(SaveLLMSitePacket.TYPE, SaveLLMSitePacket.STREAM_CODEC, SaveLLMSitePacket::handle);
        registrar.playToServer(SaveTTSSitePacket.TYPE, SaveTTSSitePacket.STREAM_CODEC, SaveTTSSitePacket::handle);
    }

    public static void sendToClientPlayer(CustomPacketPayload payload, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    public static void sendToNearby(Entity entity, CustomPacketPayload toSend) {
        if (entity.level instanceof ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, toSend);
        }
    }

    public static void sendToNearby(Entity entity, CustomPacketPayload toSend, int distance) {
        if (entity.level instanceof ServerLevel serverLevel) {
            BlockPos pos = entity.blockPosition();
            PacketDistributor.sendToPlayersNear(serverLevel, null, pos.getX(), pos.getY(), pos.getZ(), distance, toSend);
        }
    }
}
