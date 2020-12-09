package me.jakev.extraeffects.listeners;

import api.listener.Listener;
import api.listener.events.weapon.BeamPostAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import com.bulletphysics.linearmath.Transform;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.EnergyParticle;
import me.jakev.extraeffects.particles.FadeParticle;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 12/8/2020.
 * <insert description here>
 */
public class ExtraEffectBeamListener {
    public static void init(StarMod mod) {
        StarLoader.registerListener(BeamPostAddEvent.class, new Listener<BeamPostAddEvent>() {
            @Override
            public void onEvent(BeamPostAddEvent event) {

                Vector3f start = new Vector3f(event.getBeamState().from);


                Vector3f normal = new Vector3f();
                if (event.getBeamState().hitPoint == null) {
                    normal.set(event.getBeamState().to);
                } else {
                    normal.set(event.getBeamState().hitPoint);
                    ModParticleUtil.playClient(new Vector3f(event.getBeamState().hitPoint), SpriteList.ENERGY.getSprite(), 3, 700, 0.6F, 0, 0, 0, new ModParticleFactory() {
                        @Override
                        public ModParticle newParticle() {
                            return new EnergyParticle();
                        }
                    });
                }
                normal.sub(event.getBeamState().from);
                float length = normal.length();
                normal.normalize();

                float offset = 6F;
                Vector3f normalNormal = new Vector3f(normal);
                normal.scale(offset);

                for (float i = 0; i < length; i += offset) {
                    start.add(normal);
//                    if (i > 900) {
                    if (i > 100) {
                        return;
                    }
                    if (i < 6) {
                        continue;
                    }

                    Transform transform = new Transform();
                    transform.origin.set(start);
                    Vector3f orthA = new Vector3f(10,0,0);
                    orthA.cross(orthA, normalNormal);
                    Vector3f orthB = new Vector3f();
                    orthB.cross(orthA, normalNormal);
                    transform.basis.setColumn(2, normalNormal);
                    transform.basis.setColumn(1, orthA);
                    transform.basis.setColumn(0, orthB);

                    for (float r = 0; r < Math.PI*2; r+=0.1F) {
                        Vector3f n = new Vector3f((float)Math.sin(r)*5F,(float)Math.cos(r)*5F,0);
                        transform.transform(n);
                        ModParticleUtil.playClient(n, SpriteList.BALL.getSprite(), 1, 400, new Vector3f(0, 0, 0), new ModParticleFactory() {
                            @Override
                            public ModParticle newParticle() {
                                return new FadeParticle();
                            }
                        });
                    }
                }
            }
        }, mod);
    }
}
