package me.jakev.extraeffects.particles;

import api.utils.particle.ModParticle;

/**
 * Created by Jake on 12/7/2020.
 * <insert description here>
 */
public class FlashParticle extends ModParticle {
    private float sizeGrow;

    public FlashParticle(float sizeGrow) {
        this.sizeGrow = sizeGrow;
    }

    @Override
    public void spawn() {
        sizeX = sizeGrow;
        sizeY = sizeGrow;
    }

    @Override
    public void update(long currentTime) {
        if (ticksLived < 3) {
            sizeX += sizeGrow;
            sizeY += sizeGrow;
        } else {
            sizeX -= 3;
            sizeY -= 3;
            if (sizeX < 0) {
                markForDelete();
            }
        }
    }
}
