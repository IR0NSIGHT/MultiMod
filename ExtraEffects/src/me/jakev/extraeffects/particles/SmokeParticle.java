package me.jakev.extraeffects.particles;

import api.ModPlayground;
import api.utils.particle.ModParticle;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/5/2020.
 * <insert description here>
 */
public class SmokeParticle extends ModParticle {
    private Vector3f dir;
    private final float offset;
    static Vector4f startColor = new Vector4f(1,1,1,1);
    static Vector4f endColor = new Vector4f(0F,0,0,0F);

    public SmokeParticle(Vector3f dir, float offset){
        this.dir = dir;
        this.offset = offset;
    }

    @Override
    public void spawn() {
        velocity.set(dir);
        Vector3f v = randomV3f();
        v.scale(offset);
        velocity.add(v);
        sizeX = 9;
        sizeY = 9;
    }

    private static Vector3f randomV3f(){
        Vector3f vector3f = new Vector3f(ModPlayground.randFloat(-1,1),ModPlayground.randFloat(-1,1),ModPlayground.randFloat(-1,1));
        vector3f.normalize();
        return vector3f;
    }

    @Override
    public void update(long currentTime) {
        colorOverTime(this, currentTime, startColor, endColor);

    }
}
