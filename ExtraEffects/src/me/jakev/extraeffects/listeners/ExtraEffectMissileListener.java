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
    public static void init(StarMod mod){
        FastListenerCommon.missileUpdateListeners.add(new MissileUpdateListener() {
            @Override
            public void updateServer(Missile missile, Timer timer) {

            }

            @Override
            public void updateClient(Missile missile, Timer timer) {
                ModParticleUtil.playClient(ExtraEffectsParticles.MISSILE_FIRE_TRAIL, missile.getWorldTransform().origin, SpriteList.FIRE.getSprite(), new ModParticleUtil.Builder().setLifetime(1600));
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
