package me.jakev.extraeffects.particles;

import api.utils.particle.ModParticle;

import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/8/2020.
 * <insert description here>
 */
public class SimpleFireParticle extends ModParticle {
    static Vector4f startColor = new Vector4f(1,1,1,1);
    static Vector4f endColor = new Vector4f(0.8F,0,0,0F);
    private final float startSize;
    private final float endSize;

    public SimpleFireParticle(float startSize, float endSize) {
        this.startSize = startSize;
        this.endSize = endSize;
    }

    @Override
    public void update(long currentTime) {
        colorOverTime(this, currentTime, startColor, endColor);
        sizeOverTime(this, currentTime, startSize, endSize);
//        rotate(this, 0.9F);
        rotate(this, 0.25F+ticksLived/240F);
    }
}
