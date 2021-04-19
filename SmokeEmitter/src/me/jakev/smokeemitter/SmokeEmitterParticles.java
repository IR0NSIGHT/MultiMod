package me.jakev.smokeemitter;

import api.utils.particle.IModParticleFactory;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleUtil;

import javax.vecmath.Vector3f;
import java.awt.image.BufferedImage;

/**
 * Created by Jake on 4/19/2021.
 * <insert description here>
 */
public class SmokeEmitterParticles {
    public static int SMOKE_PARTICLE_FACTORY;
    public static int SMOKE_PARTICLE_SPRITE;

    public static void init(ModParticleUtil.LoadEvent event) {

        SMOKE_PARTICLE_FACTORY = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new SmokeParticle();
            }
        }, SmokeEmitterMod.inst);
        BufferedImage jarBufferedImage = SmokeEmitterMod.inst.getJarBufferedImage("me/jakev/smokeemitter/bigsmoke.png");
        SMOKE_PARTICLE_SPRITE = event.addParticleSprite(jarBufferedImage, SmokeEmitterMod.inst);
    }
}
