package me.jakev.extraeffects.particles;

import api.ModPlayground;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import org.schema.schine.graphicsengine.forms.Sprite;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 12/9/2020.
 * <insert description here>
 */
public class InvisibleEmitterParticle extends ModParticle {
    private final Sprite spr;
    private final int count;
    private final int lifetime;
    private final Vector3f dir;
    private final ModParticleFactory factory;

    private static final float r = 0.1F;
    float rx = ModPlayground.randFloat(-r,r);
    float ry = ModPlayground.randFloat(-r,r);
    float rz = ModPlayground.randFloat(-r,r);

    public InvisibleEmitterParticle(Sprite spr, int count, int lifetime, Vector3f dir, ModParticleFactory factory) {

        this.spr = spr;
        this.count = count;
        this.lifetime = lifetime;
        this.dir = dir;
        this.factory = factory;
    }

    @Override
    public void spawn() {
        colorA = 0;
        super.spawn();
    }

    @Override
    public void update(long currentTime) {
        this.velocity.x += rx;
        this.velocity.y += ry;
        this.velocity.z += rz;
        ModParticleUtil.playClient(this.position, spr, count, this.lifetime, dir, factory);
    }
}
