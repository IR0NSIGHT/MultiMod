package me.jakev.extraeffects.particles;

import api.utils.particle.ModParticle;

/**
 * Created by Jake on 12/7/2020.
 * <insert description here>
 */
public class EnergyParticle extends ModParticle {
    @Override
    public void spawn() {

    }

    @Override
    public void update(long currentTime) {
        rotate(this, 0.3F);
        sizeOverTime(this, currentTime, 4,0);
        velocity.scale(0.9F);
    }
}
