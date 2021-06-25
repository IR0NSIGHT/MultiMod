package me.jakev.extraeffects.listeners;

import api.common.GameCommon;
import api.listener.Listener;
import api.listener.events.entity.SegmentHitByProjectileEvent;
import api.listener.events.weapon.CannonProjectileAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleUtil;
import com.bulletphysics.linearmath.Transform;
import me.jakev.extraeffects.ExtraEffects;
import me.jakev.extraeffects.ExtraEffectsParticles;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.GodParticle;
import me.jakev.extraeffects.particles.godpresets.SimpleScalingFlash;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ShieldAddOn;
import org.schema.game.common.controller.elements.ShieldContainerInterface;
import org.schema.game.common.data.SegmentPiece;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/9/2020.
 * <insert description here>
 */
public class ExtraEffectCannonListener {
    private static Vector4f startColor = new Vector4f(0, 0.6F, 1, 1);
    private static Vector4f endColor = new Vector4f(0.4F, 0.4F, 1F, 1);
    static float dampening = 0.025F;

    public static void init(StarMod mod) {

        StarLoader.registerListener(SegmentHitByProjectileEvent.class, new Listener<SegmentHitByProjectileEvent>() {
            @Override
            public void onEvent(SegmentHitByProjectileEvent event) {
                final Vector3f dir = new Vector3f();
                Vector3f toPos = new Vector3f();
                toPos.set(event.getShotHandler().posAfterUpdate);

                float damageInitial = event.getShotHandler().initialDamage;
                float percent = ExtraEffects.extrapolate(0,500000,damageInitial);
                int sprite = SpriteList.BRIGHT_FLASH_WIDE_01.getSprite(); //SpriteList.MULTISPARK_MANY.getSprite()
                Vector3f baseColor = new Vector3f(1f,1f,1f);
                for (int i = 0; i < 1; i++) {
                    SimpleScalingFlash sparksParticle = new SimpleScalingFlash(sprite, toPos, (int) (100*percent+100)); //20*1000);//
                    sparksParticle.angle = 0;
                    sparksParticle.scaleByDamage(
                            10,
                            500000,
                            damageInitial* (0.8f + 0.5f* (float)Math.random()),
                            1,
                            150
                    );
                    sparksParticle.setColors(new float[][] {
                            new float[]{
                                    0.6f * (float)Math.random(),
                                    baseColor.y,//+ 0.2f * (float)Math.random(),
                                    baseColor.z,//+ 0.2f * (float)Math.random(),
                                    1f,
                                    0.5f},
                            new float[]{
                                    0.6f * (float)Math.random(),
                                    baseColor.y,// + 0.2f * (float)Math.random(),
                                    baseColor.z,// + 0.2f * (float)Math.random(),
                                    0f,
                                    1f},
                    });
                    ModParticleUtil.playClientDirect(sparksParticle);
                    /*
                    //Flash particles, a bit randomized pos, smaller than sparks
                    //flash/burn particle
                    float randomX = (float) (-25 + Math.random() * 50) * percentSize;
                    float randomY = (float) (-25 + Math.random() * 50) * percentSize;
                    float randomZ = (float) (-25 + Math.random() * 50) * percentSize;

                    Vector3f pos = new Vector3f(toPos.x + randomX, toPos.y + randomY, toPos.z + randomZ);
                    SimpleScalingFlash flashParticle = new SimpleScalingFlash(SpriteList.SMOKEY_01.getSprite(), pos,(int) (Math.random() * 200 + 100));

                    flashParticle.scaleByDamage(
                            10,
                            1000000,
                            damageInitial * (0.8f + 0.5f* (float)Math.random()),
                            1,
                            90
                    );

                    flashParticle.setColors(new float[][] {
                            new float[]{
                                    0.6f * (float)Math.random(),
                                    baseColor.y,//+ 0.2f * (float)Math.random(),
                                    baseColor.z,//+ 0.2f * (float)Math.random(),
                                    1f,
                                    0.5f},
                            new float[]{
                                    0.6f * (float)Math.random(),
                                    baseColor.y,// + 0.2f * (float)Math.random(),
                                    baseColor.z,// + 0.2f * (float)Math.random(),
                                    0f,
                                    1f},
                    });

                    //    flashParticle.velocity.set(normal);
                    ModParticleUtil.playClientDirect(flashParticle);
                    */
                }
            }
        }, mod);

        StarLoader.registerListener(CannonProjectileAddEvent.class, new Listener<CannonProjectileAddEvent>() {
            @Override
            public void onEvent(CannonProjectileAddEvent event) {
                if (event.isServer()) {
                    return;
                }
                final Vector3f dir = new Vector3f();
                Vector3f pos = new Vector3f();
                int ownerId = event.getContainer().getOwnerId(event.getIndex());
                if (!(GameCommon.getGameObject(ownerId) instanceof SegmentController)) {
                    return;
                }
                event.getContainer().getPos(event.getIndex(), pos);
                event.getContainer().getVelocity(event.getIndex(), dir);

//                SegmentController shooter = (SegmentController) GameCommon.getGameObject(ownerId);
//                Vector3f velocity = shooter.getLinearVelocity(new Vector3f());
                Vector3f velocity = new Vector3f();
                event.getContainer().getVelocity(event.getIndex(), velocity); //mutate velocity, fill with values of shot.
                velocity.normalize(); velocity.scale(100);
                Transform world = new Transform();
                world.origin.set(pos);

                 Vector3f color = new Vector3f(
                        event.getContainer().getColor(event.getIndex(),new Vector4f()).x,
                        event.getContainer().getColor(event.getIndex(),new Vector4f()).y,
                        event.getContainer().getColor(event.getIndex(),new Vector4f()).z
                 );
                 if (color.equals(new Vector3f(1,1,1))) {
                     color.set(0.67f,1,0.48f); //turn white into light green
                 }
            //   GodParticle projectile = new GodParticle(SpriteList.GLOWBALL.getSprite(), pos,10000);
            //   projectile.velocity = velocity;
            //   projectile.setSizes(new Vector3f[]{new Vector3f(20,20,0)});
            //   ModParticleUtil.playClientDirect(projectile);

                float damage = event.getContainer().getDamage(event.getIndex());
                float scale = ExtraEffects.extrapolate(100,1000000,damage);
                float size = (ExtraEffects.interpolate(0.5f,30,scale));
                dir.normalize();
                dir.scale(size);
                int sprite = SpriteList.MULTISPARK_SMALL.getSprite();
                if (scale < 0.25) {
                    sprite = SpriteList.MULTISPARK_MEDIUM.getSprite();
                }
                if (scale < 0.1) {
                    sprite = SpriteList.MULTISPARK_BIG.getSprite();
                }
                if (scale < 0.05) {
                    sprite = SpriteList.MULTISPARK_SINGLE.getSprite();
                }
                float[][] colors = new float[][]{
                        new float[]{color.x, color.y, color.z, 0.2f, 0.5f},
                        new float[]{color.x, color.y, color.z, 0, 1}
                };
    //500
                for (int i = 0; i < 500; i++) {
                    pos.add(dir);
                    GodParticle particle = new GodParticle(sprite, pos, (int) (ExtraEffects.interpolate(500, 8000,scale) + Math.random() * ExtraEffects.interpolate(500,3000,scale)));

                    float baseSize = size * 0.75f;
                    baseSize = baseSize * ExtraEffects.extrapolate(0,500,500-i);
                    Vector3f[] sizes = new Vector3f[]{
                            new Vector3f(baseSize + (float) Math.random() * baseSize ,baseSize + (float) Math.random() *baseSize,0),
                            new Vector3f((float) (2*baseSize + Math.random() *baseSize),(float) (2*baseSize + Math.random() *baseSize),1f),
                    };
                    //TODO create delayed appearance throught color snaps
                    particle.setSizes(sizes);
                    particle.setColors(colors);
                    ModParticleUtil.playClientDirect(particle);
                }

               // ModParticleUtil.playClient(ExtraEffectsParticles.CANNON_SHOOT, pos, SpriteList.BIGSMOKE.getSprite(), new ModParticleUtil.Builder().setLifetime(4000).setAmount(10));
            }
        }, mod);
    }
}
