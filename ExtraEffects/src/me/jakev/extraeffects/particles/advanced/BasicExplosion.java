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
public class basic_explosion {
    Vector3f pos; //in sector
    Vector3f color; //rgb
    int duration; //millis, not exact but average
    int size; //abtract
    SpriteList bodySprite = SpriteList.SMOKEY_SPARK_01;
    SpriteList fillerSprite = SpriteList.SMOKEY_01;
    /**
     * creates a particle explosion
     * @param pos in sector position
     * @param duration in millis on average
     * @param size size in 1..1000
     * @param color color of explosion in V3 rgb
     * @param play directly play (use exp.play() to trigger manually)
     */
    public basic_explosion(Vector3f pos, int duration, int size, Vector3f color, boolean play) {
        this.pos = pos;
        this.duration = duration;
        this.size = size;
        this.color = color;
        if (play) play();
    }

    public void setSprites(SpriteList body, SpriteList filler) {
        this.fillerSprite = filler;
        this.bodySprite = body;
    }

    public void play() {
        for (int i = 0; i < 10; i++) {
            SimpleScalingFlash sparksParticle = new SimpleScalingFlash(bodySprite.getSprite(), pos, (int) ((Math.random() + 0.5f) * duration)); //20*1000);//
            sparksParticle.scaleByDamage(
                    1,
                    1000,
                    size * (0.6f + 0.4f * (float) Math.random()),
                    1,
                    120
            );
            sparksParticle.setColors(new float[][]{
                    new float[]{
                            0.6f * (float) Math.random(),
                            color.y,//+ 0.2f * (float)Math.random(),
                            color.z,//+ 0.2f * (float)Math.random(),
                            1f,
                            0.5f},
                    new float[]{
                            0.6f * (float) Math.random(),
                            color.y,// + 0.2f * (float)Math.random(),
                            color.z,// + 0.2f * (float)Math.random(),
                            0f,
                            1f},
            });
            ModParticleUtil.playClientDirect(sparksParticle);

            //Flash particles, a bit randomized pos, smaller than sparks
            //flash/burn particle
            float randomX = (float) (-1 + Math.random() * 2) * size;
            float randomY = (float) (-1 + Math.random() * 2) * size;
            float randomZ = (float) (-1 + Math.random() * 2) * size;

            Vector3f posR = new Vector3f(pos.x + randomX, pos.y + randomY, pos.z + randomZ);
            SimpleScalingFlash flashParticle = new SimpleScalingFlash(fillerSprite.getSprite(), posR, (int) (Math.random() * 400 + 200));

            flashParticle.scaleByDamage(
                    10,
                    6000000,
                    size * (0.6f + 0.4f * (float) Math.random()),
                    1,
                    90
            );

            flashParticle.setColors(new float[][]{
                    new float[]{
                            0.6f * (float) Math.random(),
                            color.y,//+ 0.2f * (float)Math.random(),
                            color.z,//+ 0.2f * (float)Math.random(),
                            1f,
                            0.5f},
                    new float[]{
                            0.6f * (float) Math.random(),
                            color.y,// + 0.2f * (float)Math.random(),
                            color.z,// + 0.2f * (float)Math.random(),
                            0f,
                            1f},
            });
            ModParticleUtil.playClientDirect(flashParticle);
        }
    }
}
