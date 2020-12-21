package me.jakev.extraeffects.particleblock.network;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import me.jakev.extraeffects.ExtraEffects;
import me.jakev.extraeffects.particleblock.ParticleBlockConfig;
import me.jakev.extraeffects.particleblock.ParticleSpawnerMCModule;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.player.PlayerState;

import java.io.IOException;

/**
 * Created by Jake on 12/19/2020.
 * <insert description here>
 */
public class PacketCSUpdateParticleData extends Packet {
    private ManagedUsableSegmentController<?> controller;
    private long block;

    public PacketCSUpdateParticleData(ManagedUsableSegmentController<?> controller, long block) {
        this.controller = controller;
        this.block = block;
    }

    public PacketCSUpdateParticleData() {

    }

    @Override
    public void readPacketData(PacketReadBuffer buf) throws IOException {
        controller = (ManagedUsableSegmentController<?>) buf.readSendable();
        block = buf.readLong();
        ParticleSpawnerMCModule module = (ParticleSpawnerMCModule) controller.getManagerContainer().getModMCModule(ExtraEffects.emitterId);
        ParticleBlockConfig config = module.blockData.get(block);
        config.onTagDeserialize(buf);
    }

    @Override
    public void writePacketData(PacketWriteBuffer buf) throws IOException {
        buf.writeSendable(controller);
        buf.writeLong(block);
        ParticleSpawnerMCModule module = (ParticleSpawnerMCModule) controller.getManagerContainer().getModMCModule(ExtraEffects.emitterId);
        ParticleBlockConfig config = module.blockData.get(block);
        config.onTagSerialize(buf);
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState packetSender) {
        //Update module for all attached players TODO: Update for all players in the same sector
        PacketSCUpdateParticleData packet = new PacketSCUpdateParticleData(controller, ExtraEffects.emitterId);
        for (PlayerState player : controller.getAttachedPlayers()) {
            if(packetSender != player){
                PacketUtil.sendPacket(player, packet);
            }
        }

    }
}