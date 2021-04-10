package me.jakev.extraeffects.listeners;

import api.ModPlayground;
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
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ShieldAddOn;
import org.schema.game.common.controller.elements.ShieldContainerInterface;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.SendableGameState;
import org.schema.schine.network.objects.Sendable;

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
                event.getParticles().getPos(event.getParticleIndex(), pos);
                dir.normalize();
                dir.scale(0.3F);

                //TODO hit particles isnt always visible, maybe random inworld rotation?


                float damageInitial = event.getShotHandler().initialDamage;
                GodParticle particle = new GodParticle(SpriteList.FLASH.getSprite(), pos, 100);
                float baseSize = ExtraEffects.interpolate(  //size range dependenent on damage
                        1f,
                        60,
                        ExtraEffects.extrapolate(50,1000000,damageInitial) //allowed damage range
                );
                float startSize = 0.5f * baseSize + (float) Math.random() * baseSize;
                float endSize = 2 * baseSize + (float)  Math.random() *baseSize;
                particle.setSizes(new Vector3f[]{
                        new Vector3f( startSize ,startSize,0),
                        new Vector3f(endSize,endSize,1f),
                });

                particle.setColors(new float[][] {
                        new float[]{1,1,0,1,0},
                        new float[]{1,0.6f,0.04f,0.5f,0.8f},
                        new float[]{0.5f,0.3f,0,0,1},
                });


                particle.playOnClient(false);
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
                SendableGameState sgs = GameClientState.instance.getGameState();
                Sendable object = (Sendable) sgs.getState().getLocalAndRemoteObjectContainer().getLocalObjects().get(ownerId);

                //Sendable gameObject = GameCommon.getGameObject(ownerId);
                if (!(object instanceof SegmentController)) {
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

                float damage = event.getContainer().getDamage(event.getIndex());

               if (damage < 100000) { //roughly a 9x9x9 cannon + 100% beam
                   return;
               }
                float scale = ExtraEffects.extrapolate(0,1000000,damage);
                float size = (ExtraEffects.interpolate(1,15,scale));
                dir.normalize();
                dir.scale(size);
                int sprite = SpriteList.MULTISPARK_SMALL.getSprite();
                if (scale < 0.75) {
                    sprite = SpriteList.MULTISPARK_MEDIUM.getSprite();
                }
                if (scale < 0.5) {
                    sprite = SpriteList.MULTISPARK_BIG.getSprite();
                }
                if (scale < 0.1) {
                //    sprite = SpriteList.MULTISPARK_SINGLE.getSprite();
                }
                float[][] colors = new float[][]{
                        new float[]{color.x, color.y, color.z, 0.5f, 0f},
                        new float[]{color.x, color.y, color.z, 0, 1}
                };
                int lifetime = 0;
                for (int i = 0; i < 500; i++) {
                    pos.add(dir);
                    lifetime = (int) ExtraEffects.interpolate(2000, 6000,scale);
                    lifetime = (int) (lifetime * (0.8f + Math.random() * 0.5));
                    GodParticle particle = new GodParticle(sprite, pos, lifetime);

                    float baseSize = size + size * (float) Math.random() * 0.3f;
                    baseSize = baseSize * ExtraEffects.extrapolate(0,500,500-i);
                    Vector3f[] sizes = new Vector3f[]{
                            new Vector3f(baseSize + (float) Math.random() * baseSize ,baseSize + (float) Math.random() *baseSize,0),
                            new Vector3f((float) (2*baseSize + Math.random() *baseSize),(float) (2*baseSize + Math.random() *baseSize),1f),
                    };
                    //TODO create delayed appearance throught color snaps
                    particle.setSizes(sizes);
                    particle.setColors(colors);
                    particle.playOnClient(false);
                }
            }
        }, mod);
    }
}
