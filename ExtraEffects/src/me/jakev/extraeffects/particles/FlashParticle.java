package me.jakev.extraeffects.particles;

import api.utils.particle.ModParticle;

/**
 * Created by Jake on 12/7/2020.
 * <insert description here>
 */
public class FlashParticle extends ModParticle {
    private float size;

    public FlashParticle(float size) {
        this.size = size;
    }

    @Override
    public void spawn() {
        sizeX = size;
        sizeY = size;
    }

    @Override
    public void update(long currentTime) {
        if (ticksLived < 3) {
            sizeX += size;
            sizeY += size;
        } else {
            sizeX -= 3;
            sizeY -= 3;
            if (sizeX < 0) {
                markForDelete();
            }
        }
    }
}
