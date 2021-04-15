package me.jakev.extraeffects.listeners;

import api.listener.Listener;
import api.listener.events.weapon.ExplosionEvent;
import api.listener.events.weapon.MissileHitByProjectileEvent;
import api.listener.events.weapon.MissilePostAddEvent;
import api.listener.fastevents.FastListenerCommon;
import api.listener.fastevents.MissileUpdateListener;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.ExtraEffects;
import me.jakev.extraeffects.ExtraEffectsParticles;
import me.jakev.extraeffects.SpriteList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.data.missile.Missile;
import org.schema.game.common.data.missile.updates.MissileSpawnUpdate;
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

                if (missile.getType().equals(MissileSpawnUpdate.MissileType.BOMB)) {
                    //no trail for bombs
                    return;
                }
//                for (Vector3f pos : interpolate(2, this.pos, missile.getWorldTransform().origin)) {
                    ModParticleUtil.playClient(missile.getSectorId(), ExtraEffectsParticles.MISSILE_FIRE_TRAIL, missile.getWorldTransform().origin, SpriteList.FIRE.getSprite(), new ModParticleUtil.Builder().setLifetime(700));
//                    ModParticleUtil.playClient(ExtraEffectsParticles.NORMAL_SMOKE, pos, SpriteList.BIGSMOKE.getSprite(), new ModParticleUtil.Builder().setLifetime(900).setType(ModParticleUtil.Builder.Type.EMISSION_BURST).setSpeed(0.2F));
//                }
            }

        });
        //Remove vanilla trails
//        try {
//            Field field = MissileTrailDrawer.class.getDeclaredField("MAX_TRAILS");
//            field.setAccessible(true);
//
//            Field modifiersField = Field.class.getDeclaredField("modifiers");
//            modifiersField.setAccessible(true);
//            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//
//            field.set(null, 0);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
        //
        StarLoader.registerListener(MissilePostAddEvent.class, new Listener<MissilePostAddEvent>() {
            @Override
            public void onEvent(final MissilePostAddEvent event) {
                final Vector3f dir = new Vector3f();
                event.getMissile().getDirection(dir);

                dir.normalize();
                dir.scale(0.3F);
                Vector3f origin = event.getMissile().getWorldTransform().origin;
                Sector sector = event.getMissile().getSector(event.getMissile().getSectorId());
                ModParticleUtil.playServer(sector.getSectorId(), ExtraEffectsParticles.MISSILE_SHOOT, origin, SpriteList.BALL.getSprite(), new ModParticleUtil.Builder().setLifetime(1000).setAmount(40));
                //Remove the vanilla missile trail
                new StarRunnable(){
                    @Override
                    public void run() {
//                        GameClientState.instance.getWorldDrawer().getTrailDrawer().endTrail(event.getMissile());
                    }
                }.runLater(ExtraEffects.inst, 3);
            }
        }, mod);

        StarLoader.registerListener(ExplosionEvent.class, new Listener<ExplosionEvent>() {
            @Override
            public void onEvent(ExplosionEvent event) {
                Vector3i sector = event.getSector().pos;
                Vector3f toPos = event.getExplosion().fromPos;
                float x = event.getExplosion().damageInitial;
                float percentSize = ExtraEffects.extrapolate(100,1000000,x);
                float absoluteSize = ExtraEffects.interpolate(10,400, percentSize) ;
                int sectorId = event.getSector().getSectorId();
                ModParticleUtil.playServer(sectorId, ExtraEffectsParticles.SIMPLE_FLASH_SCALABLE, toPos, SpriteList.FLASH.getSprite(), new ModParticleUtil.Builder().setLifetime(156).setOffset(absoluteSize,0,0));
                ModParticleUtil.playServer(sectorId, ExtraEffectsParticles.MINOR_SMOKE, toPos, SpriteList.FLASH.getSprite(),
                        new ModParticleUtil.Builder().setAmount(100).setLifetime(500).setSpeed(0.4F).setEmissionBurst(true));
            }
        }, mod);

        StarLoader.registerListener(MissileHitByProjectileEvent.class, new Listener<MissileHitByProjectileEvent>() {
            @Override
            public void onEvent(MissileHitByProjectileEvent event) {
                if(event.isServer()) {
                    int sectorId = event.getMissile().getSector(event.getMissile().getSectorId()).getSectorId();
                    Vector3f pos = event.getMissile().getWorldTransform().origin;
                    ModParticleUtil.playServer(sectorId, ExtraEffectsParticles.FLARE_EMITTER, pos, SpriteList.NOTHING.getSprite(),
                            new ModParticleUtil.Builder().setLifetime(1000).setRandomLife(100).setAmount(6).setSpeed(0.6F).setEmissionBurst(true));
                }
            }
        }, mod);
    }
}
