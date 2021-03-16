package me.jakev.extraeffects.listeners;

import api.listener.Listener;
import api.listener.events.weapon.BeamPostAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticleUtil;
import javassist.expr.Instanceof;
import me.jakev.extraeffects.ExtraEffectsParticles;
import me.jakev.extraeffects.SpriteList;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.SegmentControllerHpController;
import org.schema.game.common.controller.elements.ShieldAddOn;
import org.schema.game.common.controller.elements.ShieldContainerInterface;
import org.schema.game.common.controller.elements.beam.damageBeam.DamageBeamHandler;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.data.SegmentPiece;

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
                Vector3f hitPoint = event.getBeamState().hitPoint;
                double shields = 0;

                if (event.getBeamState().hitPoint == null) {
                    normal.set(event.getBeamState().to);
                    to.set(event.getBeamState().to);
                } else {

                    normal.set(event.getBeamState().hitPoint);
                    to.set(event.getBeamState().hitPoint);
                    //play hit effect

                    SegmentPiece block = event.getBeamState().currentHit;
                    SegmentController hitObject = block.getSegmentController();

                    if (hitObject instanceof ManagedUsableSegmentController) {
                        ShieldAddOn shield = ((ShieldContainerInterface) ((ManagedUsableSegmentController) hitObject).getManagerContainer()).getShieldAddOn();
                        if (shield.isUsingLocalShieldsAtLeastOneActive()) {
                            shields = shield.getShieldLocalAddOn().getActiveShields().get(0).shields;
                        };
                    };

                    //TODO refine this absolute mess
                }
                Vector3f color = new Vector3f (event.getBeamState().color.x, event.getBeamState().color.y ,event.getBeamState().color.z);
                if (ran % 15 == 0 && shields > 0) {
                    ModParticleUtil.playClient(ExtraEffectsParticles.BEAM_HIT, to, SpriteList.RING.getSprite(), new ModParticleUtil.Builder().setLifetime(500).setOffset(color));
                }

                normal.sub(event.getBeamState().from);
                float length = normal.length();
                normal.normalize();

                Vector3f inverseNormal = new Vector3f(normal);
                inverseNormal.scale(-1F);
                start.add(normal);
                length = Math.min(5000,length);  //cap at 5000m
                Vector3f timedOffset = new Vector3f(normal); //offset that depends on time -> movement over time
                timedOffset.scale(System.currentTimeMillis() % 100);
                start.add(timedOffset);
                //create a particle every 50*normal step, very short lifetime (~1 frame), offset based on time for simulated speed
                //better way would be to attach particles to the beam itself, instead of playing them 1 frame and respawn at new beampos.
                for (int i = 0; i < length; i += 1) {
                    start.add(normal);

                    if (i % 50 != 0) {
                        continue;
                    }

                    ModParticleUtil.playClient(
                            ExtraEffectsParticles.BEAM_HIT,
                            start,
                            SpriteList.RING.getSprite(),
                        new ModParticleUtil.Builder().setLifetime(50).setOffset(color)
                    );
                }


            }
        }, mod);
    }
}
