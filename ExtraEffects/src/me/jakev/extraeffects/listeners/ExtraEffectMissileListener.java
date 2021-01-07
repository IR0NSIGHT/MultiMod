package me.jakev.extraeffects.listeners;

import api.listener.Listener;
import api.listener.events.weapon.ExplosionEvent;
import api.listener.events.weapon.MissileHitByProjectileEvent;
import api.listener.events.weapon.MissilePostAddEvent;
import api.listener.fastevents.FastListenerCommon;
import api.listener.fastevents.MissileUpdateListener;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.ExtraEffectsParticles;
import me.jakev.extraeffects.SpriteList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.missile.Missile;
import org.schema.game.common.data.world.Sector;
import org.schema.schine.graphicsengine.core.Timer;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 12/8/2020.
 * <insert description here>
 */
public class ExtraEffectMissileListener {
    private static Vector3f[] interpolate(int times, Vector3f start, Vector3f end){
        Vector3f segment = new Vector3f(end);
        segment.sub(start);
        segment.scale(1F/ times);
        Vector3f[] arr = new Vector3f[times + 1];
        arr[0] = start;
        for (int i = 1; i < times + 1; i++) {
            arr[i] = new Vector3f(arr[i - 1]);
            arr[i].add(segment);
        }
        return arr;
    }
    public static void init(StarMod mod){
        FastListenerCommon.missileUpdateListeners.add(new MissileUpdateListener() {
            @Override
            public void updateServer(Missile missile, Timer timer) {

            }

            @Override
            public void updateServerPost(Missile missile, Timer timer) {

            }
            Vector3f pos = new Vector3f();
            @Override
            public void updateClient(Missile missile, Timer timer) {
                pos.set(missile.getWorldTransform().origin);
            }

            @Override
            public void updateClientPost(Missile missile, Timer timer) {

                for (Vector3f pos : interpolate(2, this.pos, missile.getWorldTransform().origin)) {
                    ModParticleUtil.playClient(ExtraEffectsParticles.MISSILE_FIRE_TRAIL, pos, SpriteList.FIRE.getSprite(), new ModParticleUtil.Builder().setLifetime(700));
//                    ModParticleUtil.playClient(ExtraEffectsParticles.NORMAL_SMOKE, pos, SpriteList.BIGSMOKE.getSprite(), new ModParticleUtil.Builder().setLifetime(900).setType(ModParticleUtil.Builder.Type.EMISSION_BURST).setSpeed(0.2F));
                }
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
                Sector sector = event.getMissile().getSector(event.getMissile().getSectorId());
                ModParticleUtil.playServer(sector.pos, ExtraEffectsParticles.MISSILE_SHOOT, origin, SpriteList.BALL.getSprite(), new ModParticleUtil.Builder().setLifetime(1000).setAmount(40));
            }
        }, mod);
        StarLoader.registerListener(ExplosionEvent.class, new Listener<ExplosionEvent>() {
            @Override
            public void onEvent(ExplosionEvent event) {
                Vector3i sec = event.getSector().pos;
                Vector3f toPos = event.getExplosion().fromPos;
                ModParticleUtil.playServer(sec, ExtraEffectsParticles.SIMPLE_FLASH, toPos, SpriteList.FLASH.getSprite(), new ModParticleUtil.Builder());
                ModParticleUtil.playServer(sec, ExtraEffectsParticles.MINOR_SMOKE, toPos, SpriteList.FLASH.getSprite(),
                        new ModParticleUtil.Builder().setAmount(100).setLifetime(500).setSpeed(0.4F).setType(ModParticleUtil.Builder.Type.USE_OFFSET_AS_VELOCITY));
            }
        }, mod);
        StarLoader.registerListener(MissileHitByProjectileEvent.class, new Listener<MissileHitByProjectileEvent>() {
            @Override
            public void onEvent(MissileHitByProjectileEvent event) {
                if(event.isServer()) {
                    Vector3i sector = event.getMissile().getSector(event.getMissile().getSectorId()).pos;
                    Vector3f pos = event.getMissile().getWorldTransform().origin;
                    ModParticleUtil.playServer(sector, ExtraEffectsParticles.FLARE_EMITTER, pos, SpriteList.NOTHING.getSprite(),
                            new ModParticleUtil.Builder().setLifetime(1000).setRandomLife(100).setAmount(6).setSpeed(0.6F).setUniformCircle(false).setType(ModParticleUtil.Builder.Type.EMISSION_BURST)
                    );
                }
            }
        }, mod);
    }
}
