package me.jakev.extraeffects.particles.shipexplode;

import api.utils.particle.ModParticle;

import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/9/2020.
 * <insert description here>
 */
public class ColorFlashParticle extends ModParticle {
    private final float size;
    private final Vector4f startColor;
    private final Vector4f endColor;

    public ColorFlashParticle(float size, Vector4f startColor, Vector4f endColor) {
        this.size = size;
        this.startColor = startColor;
        this.endColor = endColor;
    }

    @Override
    public void spawn() {
        super.spawn();
    }

    @Override
    public void update(long currentTime) {
        float pct = getLifetimePercent(currentTime);
        if (pct < 0.25F) {
            sizeX += size*3;
            sizeY += size*3;
        } else {
            sizeX -= size;
            sizeY -= size;
        }
        colorOverTime(this, currentTime, startColor, endColor);
    }
}
