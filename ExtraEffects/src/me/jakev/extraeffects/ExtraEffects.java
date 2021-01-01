package me.jakev.extraeffects;

import api.common.GameClient;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.input.KeyPressEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.network.Packet;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import com.bulletphysics.linearmath.Transform;
import me.jakev.extraeffects.listeners.ExtraEffectBeamListener;
import me.jakev.extraeffects.listeners.ExtraEffectCannonListener;
import me.jakev.extraeffects.listeners.ExtraEffectExplodeListener;
import me.jakev.extraeffects.listeners.ExtraEffectMissileListener;
import me.jakev.extraeffects.particles.FadeParticle;

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

    @Override
    public void onBlockConfigLoad(BlockConfig config) {

    }

    public static ExtraEffects inst;

    @Override
    public void onClientCreated(ClientInitializeEvent event) {
    }

    @Override
    public void onLoadModParticles(ModParticleUtil.LoadEvent event) {
        SpriteList.init(this, event);
    }

    @Override
    public void onEnable() {
        Packet.dumpPacketLookup();
        ExtraEffectMissileListener.init(this);
        ExtraEffectBeamListener.init(this);
        ExtraEffectExplodeListener.init(this);
        ExtraEffectCannonListener.init(this);

        StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
            @Override
            public void onEvent(KeyPressEvent event) {
                    Transform transform = new Transform(GameClient.getClientState().getCurrentPosition());
//                    Vector3f vector3f = new Vector3f();
//                    transform.basis.getColumn(2, vector3f);
//                    transform.origin.add(vector3f);
                if(event.getChar() == 'l'){

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