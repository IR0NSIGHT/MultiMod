package me.jakev.extraeffects;

import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleUtil;
import api.utils.particle.simpleimpl.BasicModParticleFactory;
import api.utils.particle.simpleimpl.ParticleNoExtraData;
import me.jakev.extraeffects.particles.*;
import me.jakev.extraeffects.particles.shipexplode.*;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * Created by Jake on 1/2/2021.
 * <insert description here>
 */
public class ExtraEffectsParticles {
    public static int BEAM_HIT;
    public static int BEAM_SHOOT;

    public static int CANNON_HIT;
    public static int CANNON_SHOOT;

    public static int MISSILE_FIRE_TRAIL;
    public static int MISSILE_SHOOT;
    public static int SIMPLE_FLASH;

    public static int MINOR_SMOKE;
    public static int NORMAL_SMOKE;

    public static int FLASH_EMITTER;
    public static int BIG_SMOKE;
    public static int ORANGE_FLASH;

    public static int FLARE_EMITTER;
    public static int FLARE;
    public static int RED_FLASH;

    public static int EXPLOSION_TRIGGER;

    public static void init(ModParticleUtil.LoadEvent event) {
        BEAM_HIT = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                RingHitParticle ringHitParticle = new RingHitParticle();
                ringHitParticle.colorR = (byte) (offset.x*127);
                ringHitParticle.colorG = (byte) (offset.y*127);
                ringHitParticle.colorB = (byte) (offset.z*127);
                return ringHitParticle;
            }
        }, ExtraEffects.inst);

        BEAM_SHOOT = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new FadeParticle(10);
            }
        }, ExtraEffects.inst);

        CANNON_HIT = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new CannonShotParticle();
            }
        }, ExtraEffects.inst);

        CANNON_SHOOT = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new DirectionFadeParticle(offset, 0.01F, 0.5F,5F);
            }
        }, ExtraEffects.inst);

        MISSILE_FIRE_TRAIL = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new SimpleFireParticle(4F, 19F);
            }
        }, ExtraEffects.inst);

        MISSILE_SHOOT = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new MissileShootParticle(offset, 0.15F, 1F);
            }
        }, ExtraEffects.inst);

        SIMPLE_FLASH = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new FlashParticle(10);
            }
        }, ExtraEffects.inst);

        MINOR_SMOKE = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new FadeParticle();
            }
        }, ExtraEffects.inst);

        NORMAL_SMOKE = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new FadeParticle(10);
            }
        }, ExtraEffects.inst);

        FLASH_EMITTER = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new FlashFieldEmitterParticle(60);
            }
        }, ExtraEffects.inst);

        BIG_SMOKE = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new FadeParticle(100, 500);
            }
        }, ExtraEffects.inst);

        ORANGE_FLASH = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new ColorFlashParticle(25, new Vector4f(1, 1, 0, 1), new Vector4f(1F, 0, 0, 1F));
            }
        }, ExtraEffects.inst);

        RED_FLASH = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new ColorFlashParticle(10, new Vector4f(1, 0, 0, 1), new Vector4f(1F, 1F, 0, 1F));
            }
        }, ExtraEffects.inst);

        FLARE = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new DebrisFlairParticle();
            }
        }, ExtraEffects.inst);

        FLARE_EMITTER = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new InvisibleEmitterParticle(SpriteList.FIREFLASH.getSprite(), FLARE, new ModParticleUtil.Builder().setLifetime(1000));
            }
        }, ExtraEffects.inst);

        EXPLOSION_TRIGGER = event.addParticle(new BasicModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, int lifetime, Vector3f worldPos, Vector3f offset, float speed, boolean uniformCircle, ParticleNoExtraData extraData) {
                return new ShipEmitterTriggerParticle();
            }
        }, ExtraEffects.inst);


    }
}