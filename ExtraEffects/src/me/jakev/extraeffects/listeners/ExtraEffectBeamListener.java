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
import org.schema.schine.graphicsengine.camera.Camera;
import org.schema.schine.graphicsengine.core.Controller;

import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
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

                    //move a bit into cameras direction
                    Vector3f camDir = new Vector3f();
                    camDir.set(Controller.getCamera().getPos());
                    camDir.sub(to); camDir.normalize(); camDir.scale(5 + (float)Math.random()*5);
                    to.set(event.getBeamState().hitPoint);
                    to.add(camDir);

                    //flying sparks
                    SimpleScalingFlash sparksParticle = new SimpleScalingFlash(SpriteList.MULTISPARK_MANY.getSprite(), to, (int) (Math.random() * 300 + 50)); //20*1000);//
                    sparksParticle.scaleByDamage(
                            10,
                            200000,
                            damageInitial * (0.5f + 0.5f* (float)Math.random()),
                            1,
                            120
                    );
                    //TODO either make sparks bigger to be visible beyond 1km or dont draw to save performance
                    sparksParticle.setColors(new float[][] {
                            new float[]{1f,(float) (0.2f + 0.8f * Math.random()),(float) Math.random() * 0.3f,1,0.5f},
                            new float[]{1f,(float) (0.2f + 0.8f * Math.random()),(float) Math.random() * 0.3f,0.5f,1f},
                    });

                    //sparksParticle.velocity.set(normal);


                    Camera c = Controller.getCamera();
                    ModParticleUtil.playClientDirect(sparksParticle);
                    Quat4f particleRot = sparksParticle.rotation;
                //    sparksParticle.rotation = new Quat4f((float)Math.random(),(float)Math.random(),(float)Math.random(),(float)Math.random());
                    Vector3f toCamera =  c.getCachedForward();

                    //flash/burn particle
                    SimpleScalingFlash flashParticle = new SimpleScalingFlash(SpriteList.GLOWBALL.getSprite(), to, 100);
                    flashParticle.scaleByDamage(
                            10,
                            200000,
                            damageInitial * (0.8f + 0.5f* (float)Math.random()),
                            1,
                            90
                    );

                    flashParticle.setColors(new float[][] {
                            new float[]{1f,(float) (0.2f + 0.8f * Math.random()),(float) Math.random() * 0.3f,1,0.5f},
                    });

                //    flashParticle.velocity.set(normal);
                    ModParticleUtil.playClientDirect(flashParticle);                }

            }
        }, mod);
    }
}
