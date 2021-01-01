package me.jakev.extraeffects.particles;

import api.ModPlayground;
import api.utils.particle.ModParticle;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/5/2020.
 * <insert description here>
 */
public class DirectionFadeParticle extends ModParticle {
    private Vector3f dir;
    private final float offset;
    private final float startSize;
    private final float endSize;
    static Vector4f startColor = new Vector4f(1,1,1,1);
    static Vector4f endColor = new Vector4f(0F,0,0,0F);

    public DirectionFadeParticle(Vector3f dir, float offset, float startSize, float endSize){
        this.dir = dir;
        this.offset = offset;
        this.startSize = startSize;
        this.endSize = endSize;
    }

    @Override
    public void spawn() {
        velocity.set(dir);
        Vector3f v = randomV3f();
        v.scale(offset);
        velocity.add(v);
    }

    private static Vector3f randomV3f(){
        Vector3f vector3f = new Vector3f(ModPlayground.randFloat(-1,1),ModPlayground.randFloat(-1,1),ModPlayground.randFloat(-1,1));
        vector3f.normalize();
        return vector3f;
    }

    @Override
    public void update(long currentTime) {
        colorOverTime(this, currentTime, startColor, endColor);
        sizeOverTime(this, currentTime, startSize, endSize);

    }
}
