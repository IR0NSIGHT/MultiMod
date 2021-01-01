package me.jakev.particleemitter.network;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.player.PlayerState;

import java.io.IOException;

/**
 * Created by Jake on 12/19/2020.
 * <insert description here>
 */
public class PacketCSRequestParticleData extends Packet {

    long block;
    ManagedUsableSegmentController<?> ship;

    public PacketCSRequestParticleData(ManagedUsableSegmentController<?> ship, long block) {
        this.block = block;
        this.ship = ship;
    }
    public PacketCSRequestParticleData() {

    }

    @Override
    public void readPacketData(PacketReadBuffer buffer) throws IOException {
        block = buffer.readLong();
        ship = (ManagedUsableSegmentController<?>) buffer.readSendable();
    }

    @Override
    public void writePacketData(PacketWriteBuffer buffer) throws IOException {
        buffer.writeLong(block);
        buffer.writeSendable(ship);
    }

    @Override
    public void processPacketOnClient() {

    }

    @Override
    public void processPacketOnServer(PlayerState player) {
        PacketSCUpdateParticleData packet = new PacketSCUpdateParticleData(ship, block);
        PacketUtil.sendPacket(player, packet);
    }
}
