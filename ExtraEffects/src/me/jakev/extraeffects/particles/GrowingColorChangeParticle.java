package me.jakev.extraeffects.particles;

import api.DebugFile;
import api.utils.particle.ModParticle;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 16.03.2021
 * TIME: 13:58
 */
public class GrowingColorChangeParticle extends ModParticle {
    private Vector4f startColor;
    private Vector4f endColor;

    private float startSize;
    private float endSize;
//TODO find way to input parameters with extraInfo
    public GrowingColorChangeParticle(float size, Vector4f color) {
        super();
        this.startSize = size;
        this.endSize = size;

        this.startColor = color;
        this.endColor = color;
    }

    public GrowingColorChangeParticle(float startSize,float endSize,Vector4f startColor, Vector4f endColor) {
        super();
        this.startSize = startSize;
        this.endSize = endSize;
        this.startColor = startColor;
        this.endColor = endColor;
    }

    @Override
    public void spawn() {
        super.spawn();
    }

    @Override
    public void update(long currentTime) {
        if (startSize != endSize) {
            sizeOverTime(this, currentTime, startSize, endSize);
        }

        if (!startColor.equals(endColor)) {
            colorOverTime(this, currentTime, startColor, endColor);
        }
    }
}
