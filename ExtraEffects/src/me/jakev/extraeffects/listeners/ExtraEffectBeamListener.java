package me.jakev.extraeffects.listeners;

import api.listener.Listener;
import api.listener.events.weapon.BeamPostAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.godpresets.SimpleScalingFlash;
import org.schema.game.common.controller.elements.BeamState;
import org.schema.game.common.controller.elements.beam.damageBeam.DamageBeamHandler;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 12/8/2020.
 * <insert description here>
 */
public class ExtraEffectBeamListener {
    public static void init(StarMod mod) {
        StarLoader.registerListener(BeamPostAddEvent.class, new Listener<BeamPostAddEvent>() {
            int ran = 0;

            @Override
            public void onEvent(BeamPostAddEvent event) {
                ran++;

                if(!(event.getHandler() instanceof DamageBeamHandler)){
                   return;
                }

                Vector3f start = new Vector3f(event.getBeamState().from);
                Vector3f to = new Vector3f();
                Vector3f normal = new Vector3f();

                //play hit effect
                if (event.getBeamState().hitPoint == null) {
                    normal.set(event.getBeamState().to);
                    to.set(event.getBeamState().to);
                } else {

                    to.set(event.getBeamState().hitPoint);
                    normal.set(start); normal.sub(to); normal.normalize(); normal.scale(1);

                    BeamState bs = event.getBeamState();
                    float damageInitial = bs.getPower();

                    //flying sparks
                    SimpleScalingFlash sparksParticle = new SimpleScalingFlash(SpriteList.MULTISPARK_MANY.getSprite(), to, (int) (Math.random() * 200 + 100));
                    sparksParticle.scaleByDamage(
                            10,
                            100000,
                            damageInitial,
                            1,
                            60
                    );
                    //TODO either make sparks bigger to be visible beyond 1km or dont draw to save performance
                    sparksParticle.setColors(new float[][] {
                            new float[]{1f,(float) (0.2f + 0.8f * Math.random()),(float) Math.random() * 0.3f,1,0.5f},
                            new float[]{1f,(float) (0.2f + 0.8f * Math.random()),(float) Math.random() * 0.3f,0.5f,1f},
                    });

                    sparksParticle.velocity.set(normal);
                    ModParticleUtil.playClientDirect(sparksParticle);

                    //flash/burn particle
                    SimpleScalingFlash flashParticle = new SimpleScalingFlash(SpriteList.GLOWBALL.getSprite(), to, 100);
                    flashParticle.scaleByDamage(
                            10,
                            100000,
                            damageInitial,
                            1,
                            45
                    );

                    flashParticle.setColors(new float[][] {
                            new float[]{1f,(float) (0.2f + 0.8f * Math.random()),(float) Math.random() * 0.3f,1,0.5f},
                    });

                    flashParticle.velocity.set(normal);
                    ModParticleUtil.playClientDirect(flashParticle);                }

            }
        }, mod);
    }
}
