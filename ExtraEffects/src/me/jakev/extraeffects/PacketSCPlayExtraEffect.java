package me.jakev.extraeffects;

import api.common.GameServer;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import api.utils.StarRunnable;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.particles.FadeParticle;
import me.jakev.extraeffects.particles.FlashParticle;
import me.jakev.extraeffects.particles.shipexplode.ColorFlashParticle;
import me.jakev.extraeffects.particles.shipexplode.DebrisFlairParticle;
import me.jakev.extraeffects.particles.shipexplode.FlashFieldEmitterParticle;
import me.jakev.extraeffects.particles.shipexplode.InvisibleEmitterParticle;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.player.PlayerState;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jake on 1/1/2021.
 * <insert description here>
 */
public class PacketSCPlayExtraEffect extends Packet {
    Type type;
    Vector3f pos;

    public static void executeMissileHit(Vector3i sector, Vector3f loc) {
        System.err.println("Should be playing missile hit");
        for (PlayerState player : getPlayers(sector)) {
            System.err.println("Playing for: " + player.getName());
            PacketSCPlayExtraEffect packet = new PacketSCPlayExtraEffect(Type.MISSILE_HIT, loc);
            PacketUtil.sendPacket(player, packet);
        }
    }
    private static ArrayList<PlayerState> getPlayers(Vector3i sec){
        ArrayList<PlayerState> ar = new ArrayList<>();
        for (PlayerState value : GameServer.getServerState().getPlayerStatesByName().values()) {
            Vector3i currentSector = new Vector3i(value.getCurrentSector());
            currentSector.sub(sec);
            float dist = currentSector.lengthSquared();
            if(dist < 3){
                ar.add(value);
            }
        }
        return ar;
    }

    public static void executeShipExplode(Vector3i sector, Vector3f loc) {
        System.err.println("SHIP EXPLODE");
        PacketSCPlayExtraEffect packet = new PacketSCPlayExtraEffect(Type.SHIP_EXPLODE, loc);
        for (PlayerState player : getPlayers(sector)) {
            System.err.println("SENDING TO: " + player);
            PacketUtil.sendPacket(player, packet);
        }
    }

    public PacketSCPlayExtraEffect(Type type, Vector3f pos) {
        this.type = type;
        this.pos = pos;
    }

    public PacketSCPlayExtraEffect() {
    }

    @Override
    public void readPacketData(PacketReadBuffer buffer) throws IOException {
        System.err.println("Read packet data");
        byte ty = buffer.readByte();
        this.type = Type.values()[ty];
        pos = buffer.readVector3f();
    }

    @Override
    public void writePacketData(PacketWriteBuffer buffer) throws IOException {
        System.err.println("Wrote packet data");
        buffer.writeByte((byte) type.ordinal());
        buffer.writeVector3f(pos);
    }

    @Override
    public void processPacketOnClient() {
        if (type == Type.SHIP_EXPLODE) {
            ModParticleUtil.playClient(pos, SpriteList.NOTHING.getSprite(), 1, 500, new Vector3f(0,0,0), new ModParticleFactory() {
                @Override
                public ModParticle newParticle() {
                    return new FlashFieldEmitterParticle(60);
                }
            });
            new StarRunnable(){
                @Override
                public void run() {
                    new StarRunnable() {
                        @Override
                        public void run() {
                            ModParticleUtil.playClient(pos, SpriteList.BIGSMOKE.getSprite(), 70, 3000, 0.3F, false, new ModParticleFactory() {
                                @Override
                                public ModParticle newParticle() {
                                    return new FadeParticle(10, 500);
                                }
                            });
                        }
                    }.runLater(ExtraEffects.inst, 2);
                    //130
                    ModParticleUtil.playClient(pos, SpriteList.FLASH.getSprite(), 1, 180, new Vector3f(0, 0, 0), new ModParticleFactory() {
                        @Override
                        public ModParticle newParticle() {//15
                            return new ColorFlashParticle(25, new Vector4f(1, 1, 0, 1), new Vector4f(1F, 0, 0, 1F));
                        }
                    });

                    //30, 1.6
                    ModParticleUtil.playClient(pos, SpriteList.NOTHING.getSprite(), 40, 5000, 1.8F, 0, 0, 0, new ModParticleFactory() {
                        @Override
                        public ModParticle newParticle() {
                            return new InvisibleEmitterParticle(SpriteList.FIREFLASH.getSprite(), 1, 5000, new Vector3f(0, 0, 0), 1000, new ModParticleFactory() {
                                @Override
                                public ModParticle newParticle() {
                                    return new DebrisFlairParticle();
                                }
                            });
                        }
                    });
                }
            }.runLater(ExtraEffects.inst, 13);

        }else if(type == Type.MISSILE_HIT){
            ModParticleUtil.playClient(pos, SpriteList.FLASH.getSprite(), 1, 5000, new Vector3f(0,0,0), new ModParticleFactory() {
                @Override
                public ModParticle newParticle() {
                    return new FlashParticle(10);
                }
            });
            ModParticleUtil.playClient(pos, SpriteList.SMOKE.getSprite(), 200, 500, 0.4F,0,0,0,new ModParticleFactory() {
                @Override
                public ModParticle newParticle() {
                    return new FadeParticle();
                }
            });
        }
    }

    @Override
    public void processPacketOnServer(PlayerState playerState) {

    }

    enum Type {
        MISSILE_HIT,
        SHIP_EXPLODE
    }
}