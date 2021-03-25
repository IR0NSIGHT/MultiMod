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
import org.schema.game.common.controller.SegmentController;

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
        /*
                        ModParticleUtil.playClient(pos, SpriteList.RING.getSprite(), 1, 500, new Vector3f(0, 0F, 0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new CannonShotParticle();
                    }
                });
         */
        //TODO why is the hit particle so huge?
        StarLoader.registerListener(SegmentHitByProjectileEvent.class, new Listener<SegmentHitByProjectileEvent>() {
            @Override
            public void onEvent(SegmentHitByProjectileEvent event) {
                final Vector3f dir = new Vector3f();
                Vector3f pos = new Vector3f();
                pos.set(event.getShotHandler().posAfterUpdate);
//                event.getParticles().getVelocity(event.getParticleIndex(), dir);
                event.getParticles().getPos(event.getParticleIndex(), pos);
                dir.normalize();
                dir.scale(0.3F);

                //TODO hit particles isnt always visible, maybe random inworld rotation?
             //   ModParticleUtil.playClient(ExtraEffectsParticles.CANNON_HIT, pos, SpriteList.RING.getSprite(), new ModParticleUtil.Builder().setLifetime(700));
                float damageInitial = event.getShotHandler().initialDamage;
             //   ModParticleUtil.playClient(ExtraEffectsParticles.SIMPLE_FLASH, pos, SpriteList.FLASH.getSprite(), new ModParticleUtil.Builder().setLifetime(3000).setSize(new Vector2f(20,0)));
                GodParticle particle = new GodParticle(SpriteList.FLASH.getSprite(), pos, 200);
                float baseSize = ExtraEffects.interpolate(  //size range dependenent on damage
                        5,
                        60,
                        ExtraEffects.extrapolate(100,1000000,damageInitial) //allowed damage range
                );
                Vector3f[] sizes = new Vector3f[]{
                        new Vector3f(baseSize + (float) Math.random() * baseSize ,baseSize + (float) Math.random() *baseSize,0),
                        new Vector3f((float) (2*baseSize + Math.random() *baseSize),(float) (2*baseSize + Math.random() *baseSize),1f),
                };
                particle.setSizes(sizes);
                ModParticleUtil.playClientDirect(particle);

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
                        event.getContainer().getColor(event.getIndex(),new Vector4f()).z);
            //   GodParticle projectile = new GodParticle(SpriteList.GLOWBALL.getSprite(), pos,10000);
            //   projectile.velocity = velocity;
            //   projectile.setSizes(new Vector3f[]{new Vector3f(20,20,0)});
            //   ModParticleUtil.playClientDirect(projectile);

                float damage = event.getContainer().getDamage(event.getIndex());
                float scale = ExtraEffects.extrapolate(100,1000000,damage);
                float size = (ExtraEffects.interpolate(0.5f,15,scale));
                dir.normalize();
                dir.scale(size);
                int sprite = SpriteList.MULTISPARK_SMALL.getSprite();
                if (scale < 0.75) {
                    sprite = SpriteList.MULTISPARK_MEDIUM.getSprite();
                }
                if (scale < 0.5) {
                    sprite = SpriteList.MULTISPARK_BIG.getSprite();
                }
                if (scale < 0.25) {
                    sprite = SpriteList.FLASH.getSprite();
                }
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
                    ModParticleUtil.playClientDirect(particle);
                }

               // ModParticleUtil.playClient(ExtraEffectsParticles.CANNON_SHOOT, pos, SpriteList.BIGSMOKE.getSprite(), new ModParticleUtil.Builder().setLifetime(4000).setAmount(10));
            }
        }, mod);
    }
}
