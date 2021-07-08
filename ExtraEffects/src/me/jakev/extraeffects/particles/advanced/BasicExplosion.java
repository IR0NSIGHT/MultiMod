package me.jakev.extraeffects.particles.advanced;

import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.godpresets.SimpleScalingFlash;

import javax.vecmath.Vector3f;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 05.07.2021
 * TIME: 19:23
 */
public class BasicExplosion {
    Vector3f pos; //in sector
    int sectorID;

    Vector3f color; //rgb
    int duration; //millis, not exact but average
    int size; //abtract
    SpriteList bodySprite = SpriteList.SMOKEY_01;
    SpriteList fillerSprite = SpriteList.SMOKEY_SPARK_01;
    float range;
    /**
     * creates a particle explosion
     * @param pos in sector position
     * @param duration in millis on average
     * @param strength strength in 1..1000
     * @param color color of explosion in V3 rgb
     */
    public BasicExplosion(Vector3f pos, int duration, int strength, float colorrange, Vector3f color, int SectorID) {
        this.pos = pos;
        this.duration = duration;
        this.size = (int) Math.sqrt(strength/Math.PI);
        this.color = color;
        this.range = colorrange;
    }

    public void setSprites(SpriteList body, SpriteList filler) {
        this.fillerSprite = filler;
        this.bodySprite = body;
    }

    public void play() {


        for (int i = 0; i < 10; i++) {
            int lifetime = (int) ((0.7f*Math.random()+0.3f) * duration);
            //Flash particles, a bit randomized pos, smaller than sparks
            //flash/burn particle
            float randomX = (float) (-1 + Math.random() * 2) * 0.5f * size;
            float randomY = (float) (-1 + Math.random() * 2) * 0.5f * size;
            float randomZ = (float) (-1 + Math.random() * 2) * 0.5f * size;

            Vector3f posR = new Vector3f(pos.x + randomX, pos.y + randomY, pos.z + randomZ);
            SimpleScalingFlash bodyParticle = new SimpleScalingFlash(bodySprite.getSprite(), posR, lifetime,sectorID);

            bodyParticle.scaleByDamage(
                    1,
                    1000,
                    7* size * (0.6f + 0.4f * (float) Math.random()),
                    1,
                    200
            );
        //    color = new Vector3f(0.5f,0.5f,0.5f);
            bodyParticle.setColors(new float[][]{
                    getRandomColor(color,1,0.3f,range),
                    getRandomColor(color,0,1,range)
            });
            ModParticleUtil.playClientDirect(bodyParticle);

            SimpleScalingFlash fillerParticle = new SimpleScalingFlash(fillerSprite.getSprite(), posR, lifetime,sectorID);

            fillerParticle.scaleByDamage(
                    1,
                    1000,
                    10* size * (0.6f + 0.4f * (float) Math.random()),
                    1,
                    200
            );

            fillerParticle.setColors(new float[][]{
                getRandomColor(color,0.5f,0f,range),
                getRandomColor(color,0,1,range)
            });
            ModParticleUtil.playClientDirect(fillerParticle);
        }
    }
    private float[] getRandomColor(Vector3f color,float a,float t, float range) {
        float[] out = new float[]{color.x,color.y,color.z,0,0};
        for (int i = 0; i <= 2; i++) {
            out[i] = Math.min(Math.max((float) ((-1f+2*Math.random())*range + out[i]),0),1);
        }
        out[3] = a;
        out[4] = t;
        return out;
    }
}
