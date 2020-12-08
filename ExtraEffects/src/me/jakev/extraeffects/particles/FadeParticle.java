package me.jakev.extraeffects.particles;

import api.utils.particle.ModParticle;

import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/8/2020.
 * <insert description here>
 */
public class FadeParticle extends ModParticle {
    static Vector4f startColor = new Vector4f(1,1,1,1);
    static Vector4f endColor = new Vector4f(1,1,1,0);
    @Override
    public void spawn() {
        super.spawn();
    }

    @Override
    public void update(long currentTime) {
        colorOverTime(this, currentTime, startColor, endColor);
    }
}
