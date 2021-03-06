package me.jakev.scalarmining;

import api.listener.fastevents.FastListenerCommon;
import api.listener.fastevents.SalvageBeamHitListener;
import api.mod.StarMod;
import org.schema.game.common.controller.BeamHandlerContainer;
import org.schema.game.common.controller.Salvager;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.BeamState;
import org.schema.game.common.controller.elements.beam.harvest.SalvageBeamHandler;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.world.Segment;
import org.schema.schine.graphicsengine.core.Timer;

import javax.vecmath.Vector3f;
import java.util.Collection;

/**
 * Created by Jake on 3/13/2021.
 * <insert description here>
 */
public class ScalarMining extends StarMod {
    /*
    Mining calculation:
    8 = radius 1
    16 = radius 2
    32 = radius 3
    64 = radius 4

     */
    SalvageBeamHitListener listener;
    @Override
    public void onEnable() {
        System.err.println("Scalar Mining enabled!!!!!!!!!!! woohoo");
        listener = new SalvageBeamHitListener() {
            @Override
            public void handle(
                    SalvageBeamHandler handler, BeamState hittingBeam, int hits, BeamHandlerContainer<SegmentController> container, SegmentPiece hitPiece, Vector3f from,
                    Vector3f to, Timer timer, Collection<Segment> updatedSegments) {
//                HashSet<Segment> segs = new HashSet<>(updatedSegments);
                int r = Math.min(50, (int) (hittingBeam.getPower()/8000F));
                int origX = hitPiece.getAbsolutePosX();
                int origY = hitPiece.getAbsolutePosY();
                int origZ = hitPiece.getAbsolutePosZ();
                for (int x = -r; x < r; x++) {
                    for (int y = -r; y < r; y++) {
                        for (int z = -r; z < r; z++) {
                            if(x*x + y*y + z*z < r*r) {
                                SegmentPiece pointUnsave = hitPiece.getSegmentController().getSegmentBuffer().getPointUnsave(origX + x, origY + y, origZ + z);
                                if(pointUnsave != null) {
//                                    segs.add(pointUnsave.getSegment());
                                    ((Salvager) handler.getBeamShooter()).handleSalvage(hittingBeam, hits, container, from, pointUnsave, timer, updatedSegments);
                                    pointUnsave.setType((short) 0);
                                }
                            }
                        }
                    }
                }
//                updatedSegments.clear();
//                updatedSegments.addAll(segs);

            }
        };
        FastListenerCommon.salvageBeamHitListeners.add(listener);
    }

    @Override
    public void onDisable() {
        FastListenerCommon.salvageBeamHitListeners.remove(listener);
    }
}
