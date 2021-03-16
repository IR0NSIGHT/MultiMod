package me.jakev.extraeffects.particles;

import api.utils.particle.ModParticle;

import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/8/2020.
 * <insert description here>
 */
public class RingHitParticle extends ModParticle {
 //  static Vector4f startColor = new Vector4f(1,1,1,1);
 //  static Vector4f endColor = new Vector4f(1,1,1,0);
    @Override
    public void spawn() {
        super.spawn();
        sizeX = 0.2f;
        sizeY = 0.2F;
    }

    @Override
    public void update(long currentTime) {
        sizeX+=0.5F;
        sizeY+=0.5F;
        fadeOverTime(this, currentTime);
    }
}
