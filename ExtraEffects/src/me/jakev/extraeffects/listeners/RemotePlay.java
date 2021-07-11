package me.jakev.extraeffects.listeners;

import api.DebugFile;
import api.common.GameServer;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.GameServerState;

import java.io.IOException;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 10.07.2021
 * TIME: 17:21
 */
public class RemotePlay extends Packet {
    private Playable exp;
    private String classType;
    private Class type;
    public RemotePlay(Playable myPlayable) {
        this.exp = myPlayable;
        this.classType = myPlayable.getClass().getName();
    }

    public RemotePlay() {
    }

    public void broadcastToAll() {
        for (PlayerState p: GameServerState.instance.getPlayerStatesByName().values()) {
            PacketUtil.sendPacket(p,this);
        }
    }

    @Override
    public void readPacketData(PacketReadBuffer buf) throws IOException {
        this.classType = buf.readString();
        Class c;
        try {
            c = Class.forName(this.classType);
            this.type = c;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return;
        }
        Object o = buf.readObject(c);
        this.exp = (Playable) o;
    }

    @Override
    public void writePacketData(PacketWriteBuffer buf) throws IOException {
        buf.writeString(classType);
        buf.writeObject(exp);
    }

    @Override
    public void processPacketOnClient() {
        DebugFile.log("remote explosion: " + exp.toString());
        exp.play();
    }

    @Override
    public void processPacketOnServer(PlayerState sender) {

    }
}
