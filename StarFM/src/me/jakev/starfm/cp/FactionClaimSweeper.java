package me.jakev.starfm.cp;

import api.utils.StarRunnable;
import me.jakev.starfm.StarFM;
import org.schema.common.util.linAlg.Vector3i;

import java.util.ArrayList;

/**
 * Created by Jake on 3/8/2021.
 * <insert description here>
 */
public class FactionClaimSweeper extends StarRunnable {
    public FactionClaimSweeper(StarFM inst) {
        runTimer(inst, 1);
    }

    @Override
    public void run() {
        ArrayList<CPInfo> infos = SystemCPTracker.getSystemControlPower(new Vector3i(0, 0, 0));
        if(infos != null && infos.size() > 0){

        }
    }
}
