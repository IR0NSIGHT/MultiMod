package me.jakev.extraeffects;

import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.input.KeyPressEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.network.Packet;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.listeners.ExtraEffectBeamListener;
import me.jakev.extraeffects.listeners.ExtraEffectCannonListener;
import me.jakev.extraeffects.listeners.ExtraEffectExplodeListener;
import me.jakev.extraeffects.listeners.ExtraEffectMissileListener;
import org.schema.game.common.data.element.ElementInformation;

/**
 * Created by Jake on 12/3/2020.
 * <insert description here>
 */
public class ExtraEffects extends StarMod {

    @Override
    public void onLoad() {
        inst = this;
    }

    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        ElementInformation myBlock = BlockConfig.newElement(this, "MyBlock", new short[]{55, 56, 57, 58, 59, 60});
        BlockConfig.setBasicInfo(myBlock, "test", 1, 1, true, false, 444);
        BlockConfig.add(myBlock);
    }

    public static ExtraEffects inst;

    @Override
    public void onClientCreated(ClientInitializeEvent event) {
    }


    @Override
    public void onLoadModParticles(ModParticleUtil.LoadEvent event) {
        SpriteList.init(this, event);
        ExtraEffectsParticles.init(event);
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
               //     Transform transform = new Transform(GameClient.getClientState().getCurrentPosition());
//                    Vector3f vector3f = new Vector3f();
//                    transform.basis.getColumn(2, vector3f);
//                    transform.origin.add(vector3f);
             //   if(event.getChar() == 'l'){

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
            //    }
            }
        }, this);
    };

    /**
     * get percentage of point on range [min,max]. f.e. 0,10,3 -> 0.3 (30% between min and max)
     * @param min
     * @param max
     * @param point
     */
    public static float extrapolate (float min, float max, float point) {
        //technote: benchmarked this version vs branchless, no improvement.
        if (point > max) {
            return 1;
        };
        if (point < min) {
            return 0;
        }
        float range = max - min;
        float percentage = ((point-min)/range);
        return percentage;
    };

    /**
     * get absolute value of point between min and max, defined by % on scale. f.e. 0,10,0.3 -> 3 (30% between min and max = 3)
     * @param min
     * @param max
     * @param percentage
     * @return
     */
    public static float interpolate (float min, float max, float percentage) {
        //technote: benchmarked this version vs branchless, no improvement.
        if (percentage >= 1) {
            return max;
        };
        if (percentage <= 0) {
            return min;
        }
        return (percentage * (max - min)) + min;
    };
}