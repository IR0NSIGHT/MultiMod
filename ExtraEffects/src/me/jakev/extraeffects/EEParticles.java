package me.jakev.extraeffects;

import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactoryBasic;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.particles.RingHitParticle;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 1/2/2021.
 * <insert description here>
 */
public class EEParticles {
    public static int BEAM_HIT;

    public static void init(ModParticleUtil.LoadEvent event){
        BEAM_HIT = event.addParticle(new ModParticleFactoryBasic() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle) {
                return new RingHitParticle();
            }
        }, ExtraEffects.inst);
    }
}