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
import org.schema.game.common.controller.SegmentController;

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

                ModParticleUtil.playClient(ExtraEffectsParticles.CANNON_HIT, pos, SpriteList.RING.getSprite(), new ModParticleUtil.Builder().setLifetime(700));
                ModParticleUtil.playClient(ExtraEffectsParticles.ORANGE_FLASH, pos, SpriteList.FLASH.getSprite(), new ModParticleUtil.Builder().setLifetime(300));
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
                //velocity.normalize(); velocity.scale(1);
                /* Transform
                Origin: [x, y, z]
                Basis:
                [rightX, upX, forwardX]
                [rightY, upY, forwardY]
                [rightZ, upZ, forwardZ]
                 */
                Transform world = new Transform();
                world.origin.set(pos);
                dir.normalize();
                dir.scale(5);
                Vector3f color = new Vector3f(
                        event.getContainer().getColor(event.getIndex(),new Vector4f()).x,
                        event.getContainer().getColor(event.getIndex(),new Vector4f()).y,
                        event.getContainer().getColor(event.getIndex(),new Vector4f()).z);
             //   for (int i = 0; i < 10; i++) {
                 //
                    //pos.add(dir);

            //    }

                ModParticleUtil.playClient(
                        ExtraEffectsParticles.GOD_PARTICLE,
                        new Vector3f(pos), SpriteList.SPARK.getSprite(),
                        new ModParticleUtil.Builder().setLifetime(250)
                                .setOffset(new Vector3f(velocity))
                );
                for (int i = 0; i < 500; i++) {
                    pos.add(dir);
                    ModParticleUtil.playClient(
                            ExtraEffectsParticles.GOD_PARTICLE,
                            new Vector3f(pos), SpriteList.MULTISPARK.getSprite(),
                            new ModParticleUtil.Builder().setLifetime((int) (Math.random() * 2000) + 4000 + i)
                                    //.setOffset(new Vector3f(velocity))
                    );
                }

               // ModParticleUtil.playClient(ExtraEffectsParticles.CANNON_SHOOT, pos, SpriteList.BIGSMOKE.getSprite(), new ModParticleUtil.Builder().setLifetime(4000).setAmount(10));
            }
        }, mod);
    }
}
