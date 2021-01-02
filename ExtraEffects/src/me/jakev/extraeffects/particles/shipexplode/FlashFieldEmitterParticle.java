package me.jakev.extraeffects.particles.shipexplode;

import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.FlashParticle;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 1/2/2021.
 * <insert description here>
 */
public class FlashFieldEmitterParticle extends ModParticle {
    private float radius;
    private final ModParticleFactory factory;

    public FlashFieldEmitterParticle(float radius) {
        this.radius = radius;
        this.factory = new ModParticleFactory() {
            @Override
            public ModParticle newParticle() {
                return new FlashParticle(10);
            }
        };
    }

    @Override
    public void spawn() {
        colorA = 0;
        super.spawn();
    }

    @Override
    public void update(long currentTime) {
        Vector3f v = new Vector3f(this.position);
        v.add(ModParticleUtil.getRandomDir(radius));
        ModParticleUtil.playClient(v, SpriteList.FLASH.getSprite(), 1, 1000, new Vector3f(0,0,0), factory);
    }
}
