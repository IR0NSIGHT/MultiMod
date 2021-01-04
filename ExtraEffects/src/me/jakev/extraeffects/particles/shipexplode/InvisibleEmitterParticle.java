package me.jakev.extraeffects.particles.shipexplode;

import api.ModPlayground;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleUtil;

/**
 * Created by Jake on 12/9/2020.
 * <insert description here>
 */
public class InvisibleEmitterParticle extends ModParticle {

    private static final float r = 0.005F;
    private final int spr;
    private final int factory;
    private final ModParticleUtil.Builder builder;
    float rx = ModPlayground.randFloat(-r,r);
    float ry = ModPlayground.randFloat(-r,r);
    float rz = ModPlayground.randFloat(-r,r);

    public InvisibleEmitterParticle(int spr, int factory, ModParticleUtil.Builder builder) {
        this.spr = spr;
        this.factory = factory;
        this.builder = builder;
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
        this.velocity.scale(0.999F);
        ModParticleUtil.playClient(factory, this.position, spr, builder);
    }
}
