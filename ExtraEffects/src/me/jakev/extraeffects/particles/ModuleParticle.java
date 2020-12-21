package me.jakev.extraeffects.particles;

import api.ModPlayground;
import api.utils.particle.ModParticle;
import me.jakev.extraeffects.particleblock.ParticleBlockConfig;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 12/17/2020.
 * <insert description here>
 */
public class ModuleParticle extends ModParticle {
    private ParticleBlockConfig module;

    public ModuleParticle(ParticleBlockConfig module) {
        this.module = module;
    }

    @Override
    public void spawn() {
        Vector3f rand = new Vector3f(
                ModPlayground.randFloat(-1, 1),
                ModPlayground.randFloat(-1, 1),
                ModPlayground.randFloat(-1, 1));
        rand.normalize();
        rand.scale(module.randomVelocity);
        velocity.add(rand);

        position.x += ModPlayground.randFloat(-module.randomOffsetX, module.randomOffsetX);
        position.y += ModPlayground.randFloat(-module.randomOffsetY, module.randomOffsetY);
        position.z += ModPlayground.randFloat(-module.randomOffsetZ, module.randomOffsetZ);
    }

    @Override
    public void update(long currentTime) {
        float pct = getLifetimePercent(currentTime);
        sizeByPercent(this, pct, module.startSize, module.endSize);
        rotate(this, module.rotationSpeed);
        colorOverTime(this, currentTime, module.startColor, module.endColor);
        velocity.scale(module.speedDampener);
    }
}
