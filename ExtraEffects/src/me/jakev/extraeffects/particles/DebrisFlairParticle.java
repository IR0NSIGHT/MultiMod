package me.jakev.extraeffects.particles;

import api.utils.particle.ModParticle;

import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/9/2020.
 * <insert description here>
 */
public class DebrisFlairParticle extends ModParticle {
    static Vector4f startTint = new Vector4f(1, 1, 0, 1);
    static Vector4f midTint = new Vector4f(1, 0.3F, 0, 1F);
    static Vector4f smokeTint = new Vector4f(0.06F, 0F, 0F, 1F);
    static Vector4f endTint = new Vector4f(0, 0, 0, 0F);

    @Override
    public void update(long currentTime) {
        float pct = getLifetimePercent(currentTime);
        float firePercent = 0.15F;
        float transitionPercent = 0.25F;
        float endPercent = 1F;
        if (pct < firePercent) {
            colorByPercent(pct / firePercent, startTint, midTint);
        }else if(pct < transitionPercent){
            colorByPercent((pct-firePercent) / (transitionPercent-firePercent), midTint, smokeTint);
        }else if(pct < endPercent){
            colorByPercent((pct-transitionPercent) / (endPercent-transitionPercent), smokeTint, endTint);
        }
        sizeOverTime(this, currentTime, 1.3F,2.3F);
    }

    public void colorByPercent(float pct, Vector4f start, Vector4f end) {
        float startPercent = 1 - pct;
        colorR = (byte) ((int) ((start.x * startPercent + end.x * pct) * 127.0F));
        colorG = (byte) ((int) ((start.y * startPercent + end.y * pct) * 127.0F));
        colorB = (byte) ((int) ((start.z * startPercent + end.z * pct) * 127.0F));
        colorA = (byte) ((int) ((start.w * startPercent + end.w * pct) * 127.0F));
    }
}
