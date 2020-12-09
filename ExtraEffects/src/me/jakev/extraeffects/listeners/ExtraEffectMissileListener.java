package me.jakev.extraeffects.listeners;

import api.listener.Listener;
import api.listener.events.weapon.ExplosionEvent;
import api.listener.events.weapon.MissilePostAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.FadeParticle;
import me.jakev.extraeffects.particles.FlashParticle;
import me.jakev.extraeffects.particles.MissileShootParticle;
import me.jakev.extraeffects.particles.SimpleFireParticle;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 12/8/2020.
 * <insert description here>
 */
public class ExtraEffectMissileListener {
    public static void init(StarMod mod){
        StarLoader.registerListener(MissilePostAddEvent.class, new Listener<MissilePostAddEvent>() {
            @Override
            public void onEvent(MissilePostAddEvent event) {
                Vector3f dir = new Vector3f();
                event.getMissile().getDirection(dir);
                dir.normalize();
                dir.scale(0.3F);
                Vector3f origin = event.getMissile().getWorldTransform().origin;
                ModParticleUtil.playClient(origin, SpriteList.BALL.getSprite(), 40, 1000, new Vector3f(0,0,0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new MissileShootParticle(dir, 0.15F, 1F);
                    }
                });
                new StarRunnable(true){
                    @Override
                    public void run() {
                        if(event.getMissile().isAlive()){
                            if(ticksRan > 2) {
                                ModParticleUtil.playClient(origin, SpriteList.FIRE.getSprite(), 1, 1200, new Vector3f(0, 0, 0), new ModParticleFactory() {
                                    @Override
                                    public ModParticle newParticle() {
                                        return new SimpleFireParticle(3F, 20);
                                    }
                                });
                            }
                        }else{
                            cancel();
                        }
                    }
                }.runTimer(mod, 1);
            }
        }, mod);
        StarLoader.registerListener(ExplosionEvent.class, new Listener<ExplosionEvent>() {
            @Override
            public void onEvent(ExplosionEvent event) {
                Vector3f toPos = event.getExplosion().fromPos;
                System.err.println("ThePos: " + toPos.toString());
                ModParticleUtil.playClient(toPos, SpriteList.FLASH.getSprite(), 1, 5000, new Vector3f(0,0,0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new FlashParticle(10);
                    }
                });
                ModParticleUtil.playClient(toPos, SpriteList.SMOKE.getSprite(), 200, 500, 0.4F,0,0,0,new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new FadeParticle();
                    }
                });
            }
        }, mod);
    }
}
