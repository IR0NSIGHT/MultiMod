package me.jakev.extraeffects.particles;

import api.ModPlayground;
import api.utils.particle.ModParticle;

import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/8/2020.
 * <insert description here>
 */
public class FadeParticle extends ModParticle {
    static Vector4f startColor = new Vector4f(1,1,1,1);
    static Vector4f endColor = new Vector4f(1,1,1,0);
    private float size = 1F;
    private int randomLife;

    public FadeParticle(float size) {
        this.size = size;
    }
    public FadeParticle(float size, int randomLife) {
        this.size = size;
        this.randomLife = randomLife;
    }
    public FadeParticle() {
    }

    @Override
    public void spawn() {
        super.spawn();
        sizeX = size;
        sizeY = size;
        if(randomLife != 0) {
            lifetimeMs += ModPlayground.randInt(-randomLife, randomLife);
        }
    }

    @Override
    public void update(long currentTime) {
        colorOverTime(this, currentTime, startColor, endColor);
    }
}
