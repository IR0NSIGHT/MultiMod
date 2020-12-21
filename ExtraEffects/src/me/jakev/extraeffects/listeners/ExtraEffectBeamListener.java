package me.jakev.extraeffects.listeners;

import api.listener.Listener;
import api.listener.events.weapon.BeamPostAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.FadeParticle;
import me.jakev.extraeffects.particles.RingHitParticle;
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

                Vector3f normal = new Vector3f();
                Vector3f to = new Vector3f();
                if (event.getBeamState().hitPoint == null) {
                    normal.set(event.getBeamState().to);
                    to.set(event.getBeamState().to);
                } else {
                    normal.set(event.getBeamState().hitPoint);
                    to.set(event.getBeamState().hitPoint);

                }
                normal.sub(event.getBeamState().from);
                float length = normal.length();
                normal.normalize();

                Vector3f inverseNormal = new Vector3f(normal);
                inverseNormal.scale(-1F);
                if (ran % 8 == 0) {
                    ModParticleUtil.playClient(to, SpriteList.RING.getSprite(), 1, 2000, inverseNormal, new ModParticleFactory() {
                        @Override
                        public ModParticle newParticle() {
                            return new RingHitParticle();
                        }
                    });
                }

                normal.scale(25);
                start.add(normal);
                start.add(normal);
                ModParticleUtil.playClient(start, SpriteList.RING.getSprite(), 1, 1300, normal, new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new FadeParticle(10);
                    }
                });

            }
        }, mod);
    }
}
