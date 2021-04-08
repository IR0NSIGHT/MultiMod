package me.jakev.extraeffects.particles.godpresets;

import me.jakev.extraeffects.ExtraEffects;
import me.jakev.extraeffects.particles.GodParticle;

import javax.vecmath.Vector3f;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 07.04.2021
 * TIME: 23:33
 */

/**
 * a simple particle that grows to 4 times its size over lifetime. intended for weapon impact flashes that scale with damage. use scaleByDamage.
 */
public class SimpleScalingFlash extends GodParticle {

    public SimpleScalingFlash(int spriteID, Vector3f pos, int lifetime) {
        super(spriteID, pos, lifetime);
    }

    /**
     * will pick a size based on the range of damage and size.
     * @param minDmg
     * @param maxDmg
     * @param damage
     * @param minSize
     * @param maxSize
     */
    public void scaleByDamage(float minDmg, float maxDmg, float damage, float minSize, float maxSize) {
        float rangePoint = ExtraEffects.extrapolate(minDmg, maxDmg, damage); //allowed damage range
        float baseSize = ExtraEffects.interpolate(  //sprite size range dependenent on damage
                minSize,
                maxSize,
                rangePoint
        );
        float startSize = 0.5f * baseSize + 0.5f * (float) Math.random() * baseSize;
        float endSize = 4 * baseSize + (float)  Math.random() *baseSize;

        this.setSizes(new Vector3f[]{
                new Vector3f( startSize ,startSize,0),
                new Vector3f(endSize,endSize,1f),
        });
    }
}
