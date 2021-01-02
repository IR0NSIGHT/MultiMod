package me.jakev.extraeffects.listeners;

import api.listener.Listener;
import api.listener.events.weapon.ExplosionEvent;
import api.listener.events.weapon.MissilePostAddEvent;
import api.listener.fastevents.FastListenerCommon;
import api.listener.fastevents.MissileUpdateListener;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.PacketSCPlayExtraEffect;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.MissileShootParticle;
import me.jakev.extraeffects.particles.SimpleFireParticle;
import org.schema.game.common.data.missile.Missile;
import org.schema.schine.graphicsengine.core.Timer;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 12/8/2020.
 * <insert description here>
 */
public class ExtraEffectMissileListener {
    public static void init(StarMod mod){
        FastListenerCommon.missileUpdateListeners.add(new MissileUpdateListener() {
            @Override
            public void updateServer(Missile missile, Timer timer) {

            }

            @Override
            public void updateClient(Missile missile, Timer timer) {
                ModParticleUtil.playClient(missile.getWorldTransform().origin, SpriteList.FIRE.getSprite(), 1, 1600, new Vector3f(0, 0, 0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new SimpleFireParticle(3F, 20);
                    }
                });
            }
        });
        StarLoader.registerListener(MissilePostAddEvent.class, new Listener<MissilePostAddEvent>() {
            @Override
            public void onEvent(MissilePostAddEvent event) {
                final Vector3f dir = new Vector3f();
                event.getMissile().getDirection(dir);
                dir.normalize();
                dir.scale(0.3F);
                Vector3f origin = event.getMissile().getWorldTransform().origin;
                ModParticleUtil.playClient(origin, SpriteList.BALL.getSprite(), 40, 1000,
                        new Vector3f(0,0,0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new MissileShootParticle(dir, 0.15F, 1F);
                    }
                });
            }
        }, mod);
        StarLoader.registerListener(ExplosionEvent.class, new Listener<ExplosionEvent>() {
            @Override
            public void onEvent(ExplosionEvent event) {
                System.err.println("Explosion Event");
                Vector3f toPos = event.getExplosion().fromPos;
                PacketSCPlayExtraEffect.executeMissileHit(event.getSector().pos, toPos);
            }
        }, mod);
    }
}
