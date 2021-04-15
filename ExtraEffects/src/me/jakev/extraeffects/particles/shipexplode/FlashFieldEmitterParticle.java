package me.jakev.extraeffects.particles.shipexplode;

import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.ExtraEffectsParticles;
import me.jakev.extraeffects.SpriteList;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 1/2/2021.
 * <insert description here>
 */
public class FlashFieldEmitterParticle extends ModParticle {
    private float radius;

    public FlashFieldEmitterParticle(float radius) {
        this.radius = radius;
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
        ModParticleUtil.playClient(sectorId, ExtraEffectsParticles.SIMPLE_FLASH, v, SpriteList.FLASH.getSprite(), new ModParticleUtil.Builder());
    }
}
