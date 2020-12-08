package me.jakev.extraeffects.particles;

import api.utils.particle.ModParticle;

/**
 * Created by Jake on 12/7/2020.
 * <insert description here>
 */
public class FlashParticle extends ModParticle {
    @Override
    public void spawn() {
        sizeX = 0;
        sizeY = 0;
    }

    @Override
    public void update(long currentTime) {
        if (ticksLived < 3) {
            sizeX += 4;
            sizeY += 4;
        } else {
            sizeX -= 5;
            sizeY -= 5;
            if (sizeX < 0) {
                markForDelete();
            }
        }
    }
}
