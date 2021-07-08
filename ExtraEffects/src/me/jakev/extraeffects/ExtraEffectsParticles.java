package me.jakev.extraeffects;

import api.utils.particle.IModParticleFactory;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleUtil;
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
    public static int GOD_PARTICLE;

    public static int MISSILE_FIRE_TRAIL;
    public static int MISSILE_SHOOT;
    public static int SIMPLE_FLASH;
    public static int SIMPLE_FLASH_SCALABLE;

    public static int MINOR_SMOKE;
    public static int NORMAL_SMOKE;

    public static int FLASH_EMITTER;
    public static int BIG_SMOKE;
    public static int ORANGE_FLASH;

    public static int FLARE_EMITTER;
    public static int FLARE;
    public static int RED_FLASH;

    public static int EXPLOSION_TRIGGER;
    public static int TEST_PARTICLE;

    public static void init(ModParticleUtil.LoadEvent event) {

        BEAM_HIT = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new RingHitParticle();
            }
        }, ExtraEffects.inst);

        BEAM_SHOOT = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new FadeParticle(5);
            }
        }, ExtraEffects.inst);

        CANNON_HIT = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new CannonShotParticle();
            }
        }, ExtraEffects.inst);

        CANNON_SHOOT = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new DirectionFadeParticle(builder.getVelocity(), 0.01F, 0.5F,5F);
            }
        }, ExtraEffects.inst);

        GOD_PARTICLE = event.addParticle( //this does not allow passing of sector ID, dont recommend using.
                new IModParticleFactory() {
                    @Override
                    public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                        return new GodParticle(sprite,worldPos, builder.getLifetime(), 0);
                    }
                }, ExtraEffects.inst);

        MISSILE_FIRE_TRAIL = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new SimpleFireParticle(4F, 19F);
            }
        }, ExtraEffects.inst);

        MISSILE_SHOOT = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new MissileShootParticle(builder.getVelocity(), 0.15F, 1F);
            }
        }, ExtraEffects.inst);

        SIMPLE_FLASH = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new FlashParticle(10);
            }
        }, ExtraEffects.inst);

        SIMPLE_FLASH_SCALABLE = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new FlashScalableParticle(builder.getSize().x);
            }
        },ExtraEffects.inst);

        MINOR_SMOKE = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new FadeParticle();
            }
        }, ExtraEffects.inst);

        NORMAL_SMOKE = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new FadeParticle(10);
            }
        }, ExtraEffects.inst);

        FLASH_EMITTER = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new FlashFieldEmitterParticle(60);
            }
        }, ExtraEffects.inst);

        BIG_SMOKE = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new FadeParticle(100, 500);
            }
        }, ExtraEffects.inst);

        ORANGE_FLASH = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new ColorFlashParticle(25, new Vector4f(1, 1, 0, 1), new Vector4f(1F, 0, 0, 1F));
            }
        }, ExtraEffects.inst);

        RED_FLASH = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new ColorFlashParticle(10, new Vector4f(1, 0, 0, 1), new Vector4f(1F, 1F, 0, 1F));
            }
        }, ExtraEffects.inst);

        FLARE = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new DebrisFlairParticle();
            }
        }, ExtraEffects.inst);

        FLARE_EMITTER = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new InvisibleEmitterParticle(SpriteList.FIREFLASH.getSprite(), FLARE, new ModParticleUtil.Builder().setLifetime(1000));
            }
        }, ExtraEffects.inst);

        EXPLOSION_TRIGGER = event.addParticle(new IModParticleFactory() {
            @Override
            public ModParticle newParticle(int factoryId, int sprite, Vector3f worldPos, ModParticleUtil.Builder builder) {
                return new ShipEmitterTriggerParticle();
            }
        }, ExtraEffects.inst);


    }
}