package me.jakev.extraeffects.listeners;

import api.DebugFile;
import api.listener.Listener;
import api.listener.events.weapon.ExplosionEvent;
import api.listener.events.weapon.MissileHitByProjectileEvent;
import api.listener.events.weapon.MissilePostAddEvent;
import api.listener.fastevents.FastListenerCommon;
import api.listener.fastevents.MissileUpdateListener;
import api.mod.StarLoader;
import api.mod.StarMod;
import me.jakev.extraeffects.particles.advanced.BasicExplosion;
import org.schema.game.common.data.missile.Missile;
import org.schema.game.common.data.missile.updates.MissileSpawnUpdate;
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
            public void updateServerPost(Missile missile, Timer timer) {

            }
            Vector3f pos = new Vector3f();
            @Override
            public void updateClient(Missile missile, Timer timer) {
                pos.set(missile.getWorldTransform().origin);
            }

            @Override
            public void updateClientPost(Missile missile, Timer timer) {

                if (missile.getType().equals(MissileSpawnUpdate.MissileType.BOMB)) {
                    //no trail for bombs
                    return;
                }

                //JAKES UGLY ORANGE TRAIL FROM 0.1
//                for (Vector3f pos : interpolate(2, this.pos, missile.getWorldTransform().origin)) {
                //    ModParticleUtil.playClient(missile.getSectorId(), ExtraEffectsParticles.MISSILE_FIRE_TRAIL, missile.getWorldTransform().origin, SpriteList.FIRE.getSprite(), new ModParticleUtil.Builder().setLifetime(700));
//                    ModParticleUtil.playClient(ExtraEffectsParticles.NORMAL_SMOKE, pos, SpriteList.BIGSMOKE.getSprite(), new ModParticleUtil.Builder().setLifetime(900).setType(ModParticleUtil.Builder.Type.EMISSION_BURST).setSpeed(0.2F));
//                }
            }

        });

        StarLoader.registerListener(MissilePostAddEvent.class, new Listener<MissilePostAddEvent>() {
            @Override
            public void onEvent(final MissilePostAddEvent event) {

            }
        }, mod);

        StarLoader.registerListener(ExplosionEvent.class, new Listener<ExplosionEvent>() {
            @Override
            public void onEvent(ExplosionEvent event) {
                if (!event.isServer()) return; //should never happen, hook is 100% serverside
        //        DebugFile.log("################################## MISSILE EXPLOSION");
                int sectorID = event.getSector().getSectorId();
                Vector3f toPos = event.getExplosion().centerOfExplosion.origin;
                float x = event.getExplosion().damageInitial;
                //get shooting weapon + its color new Vector3f(0,0.85f,1)
                new RemotePlay(new BasicExplosion(
                        toPos,
                        1000,
                        (int) x,
                        0.3f,
                        new Vector3f(0,0.85f,1),
                        sectorID
                )).broadcastToAll();

            }
        }, mod);

        StarLoader.registerListener(MissileHitByProjectileEvent.class, new Listener<MissileHitByProjectileEvent>() {
            @Override //why you prokem TODO
            public void onEvent(MissileHitByProjectileEvent event) {
                if(event.isServer()) {
                    if (event.getDamage() < event.getMissile().getHp()) return;
                    int sectorId = event.getMissile().getSector(event.getMissile().getSectorId()).getSectorId();
                    Vector3f pos = event.getMissile().getWorldTransform().origin;

                    //get shooting weapon + its color new Vector3f(0,0.85f,1)
                    new RemotePlay(new BasicExplosion(
                            pos,
                            1000,
                            (int) event.getMissile().getDamage(),
                            0.3f,
                            new Vector3f(0,0.85f,1),
                            sectorId
                    )).broadcastToAll();                }
            }
        }, mod);
    }
}
