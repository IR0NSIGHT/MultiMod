package me.jakev.extraeffects.particles;

import api.DebugFile;
import api.mod.StarLoader;
import api.utils.particle.ModParticle;
import me.jakev.extraeffects.ExtraEffects;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 15.03.2021
 * TIME: 20:07
 */
public class FlashScalableParticle extends ModParticle {
    private float size;
    private float maxSize; //maximum size
    //TODO allow linear or exponential growth

    /**
     * a particle that spawns small, grows to given size at 50% lifetime and then shrinks back to zero.
     * @param size maximum size
     */
    public FlashScalableParticle(float size) {
        this.maxSize = size;
    }

    @Override
    public void spawn() {
        //startTime = System.currentTimeMillis();

        sizeX = 0.2f;
        sizeY = 0.2f;
    }

    @Override
    public void update(long currentTime) {
        float lifeTimePercent = getLifetimePercent(currentTime);
        if (lifeTimePercent < 0.5f ) {
            size = ExtraEffects.interpolate(0.1f,maxSize,lifeTimePercent);
        } else {
            size = ExtraEffects.interpolate(0.1f,maxSize,1-lifeTimePercent);
        }
        sizeX = size;
        sizeY = size;
        if (lifeTimePercent >= 1) {
            markForDelete();
        }
    }
}
