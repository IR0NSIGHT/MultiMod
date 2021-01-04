package me.jakev.extraeffects.particles.shipexplode;

import api.utils.StarRunnable;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.ExtraEffects;
import me.jakev.extraeffects.ExtraEffectsParticles;
import me.jakev.extraeffects.SpriteList;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 1/3/2021.
 * <insert description here>
 */
public class ShipEmitterTriggerParticle extends ModParticle {
    boolean ran = false;
    @Override
    public void update(long currentTime) {
        if(!ran) {
            final Vector3f pos = this.position;
            ran = true;
            ModParticleUtil.playClient(ExtraEffectsParticles.FLASH_EMITTER, pos, SpriteList.FLASH.getSprite(), new ModParticleUtil.Builder().setLifetime(500));
            new StarRunnable() {
                @Override
                public void run() {
                    ModParticleUtil.Builder builder = new ModParticleUtil.Builder().setLifetime(6000).setType(ModParticleUtil.Builder.Type.EMISSION_BURST).setSpeed(0.7F).setAmount(70).setRandomLife(1000);
                    ModParticleUtil.playClient(ExtraEffectsParticles.BIG_SMOKE, pos, SpriteList.BIGSMOKE.getSprite(), builder);

                }
            }.runLater(ExtraEffects.inst, 7);
            //130
            ModParticleUtil.playClient(ExtraEffectsParticles.ORANGE_FLASH, pos, SpriteList.FLASH.getSprite(), new ModParticleUtil.Builder().setLifetime(180));
            //30, 1.6
            ModParticleUtil.playClient(ExtraEffectsParticles.FLARE_EMITTER, pos, SpriteList.NOTHING.getSprite(),
                    //sped=1.8
//                                new ModParticleUtil.Builder().setLifetime(5000).setRandomLife(1000).setAmount(1000).setSpeed(3.3F).setUniformCircle(false).setType(ModParticleUtil.Builder.Type.EMISSION_BURST)
                    new ModParticleUtil.Builder().setLifetime(5000).setRandomLife(1000).setAmount(100).setSpeed(2.3F).setUniformCircle(false).setType(ModParticleUtil.Builder.Type.EMISSION_BURST)
            );
        }else{
            markForDelete();
        }
    }
}
