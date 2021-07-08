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
import me.jakev.extraeffects.particles.advanced.BasicExplosion;
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
    public static void init(StarMod mod) {

        StarLoader.registerListener(SegmentHitByProjectileEvent.class, new Listener<SegmentHitByProjectileEvent>() {
            @Override
            public void onEvent(SegmentHitByProjectileEvent event) {
                int sectorID = event.getProjectileController().getSectorId();
                //spawn a bright flash particle that expands very fast
                Vector3f toPos = new Vector3f();
                toPos.set(event.getShotHandler().posAfterUpdate);
                float damageInitial = event.getShotHandler().initialDamage;
                float percent = ExtraEffects.extrapolate(0,500000,damageInitial);
                float absSize = ExtraEffects.interpolate(0,120,percent);
                int sprite = SpriteList.BRIGHT_FLASH_WIDE_01.getSprite();
                GodParticle p = new SimpleScalingFlash(sprite,toPos,10000,sectorID);
                p.setColors(new float[][]{new float[]{1,1,1,1,0},new float[]{1,1,1,0,1}});
                p.setSizes(new Vector3f[]{new Vector3f(0,0,0),new Vector3f(absSize,2*absSize,1)});
                p.angle = 0;
                ModParticleUtil.playClientDirect(p);
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

                 Vector3f color = new Vector3f(
                        event.getContainer().getColor(event.getIndex(),new Vector4f()).x,
                        event.getContainer().getColor(event.getIndex(),new Vector4f()).y,
                        event.getContainer().getColor(event.getIndex(),new Vector4f()).z
                 );
                 if (color.equals(new Vector3f(1,1,1))) {
                     color.set(0.67f,1,0.48f); //turn white into light green
                 }

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

                for (int i = 0; i < 500; i++) {
                    pos.add(dir);
                    GodParticle particle = new GodParticle(sprite, pos, (int) (ExtraEffects.interpolate(500, 8000,scale) + Math.random() * ExtraEffects.interpolate(500,3000,scale)),event.getController().getSectorId());

                    float baseSize = size * 0.75f;
                    baseSize = baseSize * ExtraEffects.extrapolate(0,500,500-i);
                    Vector3f[] sizes = new Vector3f[]{
                            new Vector3f(baseSize + (float) Math.random() * baseSize ,baseSize + (float) Math.random() *baseSize,0),
                            new Vector3f((float) (2*baseSize + Math.random() *baseSize),(float) (2*baseSize + Math.random() *baseSize),1f),
                    };
                    particle.setSizes(sizes);
                    particle.setColors(colors);
                    ModParticleUtil.playClientDirect(particle);
                }
            }
        }, mod);
    }
}
