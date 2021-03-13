package me.jakev.starfm.cp;

import api.utils.StarRunnable;
import me.jakev.starfm.StarFM;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.server.data.GameServerState;

/**
 * Created by Jake on 3/9/2021.
 * <insert description here>
 */
public class ShipCPSweeper extends StarRunnable {
    public ShipCPSweeper(StarFM inst) {
        runTimer(inst, 25);
    }

    @Override
    public void run() {
        for (SegmentController seg : GameServerState.instance.getSegmentControllersByName().values()) {
            ShipCPTracker.updateControlPower(seg);
        }
    }
}
