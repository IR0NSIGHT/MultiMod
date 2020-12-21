package me.jakev.extraeffects;

import api.DebugFile;
import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.input.KeyPressEvent;
import api.listener.events.weapon.CannonProjectileAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.network.Packet;
import api.network.packets.PacketUtil;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import api.utils.textures.StarLoaderTexture;
import com.bulletphysics.linearmath.Transform;
import me.jakev.extraeffects.listeners.EEParticleEmitterListener;
import me.jakev.extraeffects.listeners.ExtraEffectBeamListener;
import me.jakev.extraeffects.listeners.ExtraEffectExplodeListener;
import me.jakev.extraeffects.listeners.ExtraEffectMissileListener;
import me.jakev.extraeffects.particleblock.network.PacketCSRequestParticleData;
import me.jakev.extraeffects.particleblock.network.PacketCSUpdateParticleData;
import me.jakev.extraeffects.particleblock.network.PacketSCUpdateParticleData;
import me.jakev.extraeffects.particles.EnergyParticle;
import me.jakev.extraeffects.particles.FireParticle;
import me.jakev.extraeffects.particles.SmokeParticle;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Jake on 12/3/2020.
 * <insert description here>
 */
public class ExtraEffects extends StarMod {
    public static void main(String[] args) {

    }

    @Override
    public void onGameStart() {
        setModName("ExtraEffects");
        setModVersion("0.1");
        setModAuthor("JakeV");
        setModDescription("Various particles and sounds");
        inst = this;
        setSMDResourceId(44);
    }
    private static short[] emitterIds;
    public static short emitterId;
    public static short emitterIconId;
    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        ElementInformation crystal = ElementKeyMap.getInfoFast(ElementKeyMap.CRYS_NOCX);
        crystal.setOrientatable(true);

        assert emitterIds != null;
        ElementInformation emitter = config.newElement(this, "Particle Emitter", emitterIds);
        BlockConfig.setBasicInfo(emitter, "Emits particles", 100, 0.1F, true, true, emitterIconId);
        emitter.setOrientatable(true);
        emitter.setHasActivationTexure(true);
        emitterId = emitter.getId();
        config.add(emitter);

    }

    public static ExtraEffects inst;

    @Override
    public void onClientCreated(ClientInitializeEvent event) {
    }
    @Override
    public void onEnable() {
        PacketUtil.registerPacket(PacketCSUpdateParticleData.class);
        PacketUtil.registerPacket(PacketSCUpdateParticleData.class);
        PacketUtil.registerPacket(PacketCSRequestParticleData.class);

        Packet.dumpPacketLookup();

        SpriteList.init(this);
        ExtraEffectMissileListener.init(this);
        ExtraEffectBeamListener.init(this);
        ExtraEffectExplodeListener.init(this);
        EEParticleEmitterListener.init(this);

        try {
            BufferedImage blockTexture = ImageIO.read(ExtraEffects.class.getResourceAsStream("res/particle_block.png"));
            BufferedImage iconTexture = ImageIO.read(ExtraEffects.class.getResourceAsStream("res/particle_icon.png"));
            StarLoaderTexture slIcon = StarLoaderTexture.newIconTexture(iconTexture);
            emitterIconId = (short) slIcon.getTextureId();
            int res = 256;
            StarLoaderTexture front = StarLoaderTexture.newBlockTexture(blockTexture.getSubimage(0, 0, res, res));
            StarLoaderTexture sides = StarLoaderTexture.newBlockTexture(blockTexture.getSubimage(res, 0, res, res));
            StarLoaderTexture rear = StarLoaderTexture.newBlockTexture(blockTexture.getSubimage(res*2, 0, res, res));
            short sideId = (short) sides.getTextureId();
            short rearId = (short) rear.getTextureId();
            emitterIds = new short[]{(short) front.getTextureId(), sideId,rearId,rearId,sideId, sideId};
            DebugFile.log("Loaded block textures", this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StarLoader.registerListener(CannonProjectileAddEvent.class, new Listener<CannonProjectileAddEvent>() {
            @Override
            public void onEvent(CannonProjectileAddEvent event) {
                final Vector3f dir = new Vector3f();
                Vector3f pos = new Vector3f();
                event.getContainer().getVelocity(event.getIndex(), dir);
                event.getContainer().getPos(event.getIndex(), pos);
                /* Transform
                Origin: [x, y, z]
                Basis:
                [rightX, upX, forwardX]
                [rightY, upY, forwardY]
                [rightZ, upZ, forwardZ]
                 */
                Transform world = new Transform();
                world.origin.set(pos);
                dir.normalize();
                dir.scale(10);

                ModParticleUtil.playClient(pos, SpriteList.SPARK.getSprite(), 20, 1000, new Vector3f(0, 0F, 0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new FireParticle(dir, 1);
                    }
                });
                dir.scale(0.5F);
                ModParticleUtil.playClient(pos, SpriteList.SPARK.getSprite(), 10, 500, new Vector3f(0, 0F, 0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new SmokeParticle(dir, 3);
                    }
                });
            }
        }, this);
        StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
            @Override
            public void onEvent(KeyPressEvent event) {
                    Transform transform = new Transform(GameClient.getClientState().getCurrentPosition());
//                    Vector3f vector3f = new Vector3f();
//                    transform.basis.getColumn(2, vector3f);
//                    transform.origin.add(vector3f);
                if(event.getChar() == 'l') {
//                    ModParticleUtil.playClient(transform.origin, SpriteList.FIRESPARK.getSprite(), 600, 2000, 1.5F,false, new ModParticleFactory() {
//                        @Override
//                        public ModParticle newParticle() {
//                            return new FadeParticle();
//                        }
//                    });

                }else if(event.getChar() == ';'){

                    ModParticleUtil.playClient(transform.origin, SpriteList.THEONE.getSprite(), 50, 5000, 0F, 22,22,22, new ModParticleFactory() {
                        @Override
                        public ModParticle newParticle() {
                            return new EnergyParticle();
                        }
                    });
//                    ModParticleUtil.playClient(transform.origin, SpriteList.ENERGY.getSprite(), 400, 400, 2F, 0,0,0, new ModParticleFactory() {
//                        @Override
//                        public ModParticle newParticle() {
//                            return new EnergyParticle();
//                        }
//                    });
//                    ModParticleUtil.play(transform.origin, SpriteList.FLASH.getSprite(), 1, 500, new Vector3f(0, 0F, 0), new ModParticleFactory() {
//                        @Override
//                        public ModParticle newParticle() {
//                            return new FlashParticle();
//                        }
//                    });
                }
            }
        }, this);
    }
}