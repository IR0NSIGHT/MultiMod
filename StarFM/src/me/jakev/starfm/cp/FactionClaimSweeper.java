package me.jakev.starfm.cp;

import api.common.GameServer;
import api.utils.StarRunnable;
import me.jakev.starfm.StarFM;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.world.StellarSystem;
import org.schema.game.server.data.GameServerState;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jake on 3/8/2021.
 * <insert description here>
 */
public class FactionClaimSweeper extends StarRunnable {
    public FactionClaimSweeper(StarFM inst) {
        runTimer(inst, 1);
    }
    int rad = 16;
    final Vector3i systemTmp = new Vector3i();
    private Vector3i getNextSystem(){
        Vector3i v = systemTmp;
        if(v.x++ >= rad){
            v.x = -rad;
            if(v.y++ >= rad){
                v.y = -rad;
                if(v.z++ >= rad){
                    v.z = -rad;
                }
            }
        }
        return systemTmp;
    }
    @Override
    public void run() {
        System.err.println("hi");
        //TODO Only update active sectors
        Vector3i system = getNextSystem();//new Vector3i(10,-10,10);
        ArrayList<CPInfo> infos = SystemCPTracker.getSystemControlPower(system);
        if (infos != null && infos.size() > 0) {
            claimSystem(system, infos.get(0).factionId, "ENTITY_SHIP_JAKE");
            printClaimInfo(system, infos);
        }
    }
    public static void printClaimInfo(Vector3i system, Collection<CPInfo> infos){
        System.err.println("==== [ System Claim Data ] =====");
        System.err.println("System: " + system.toString());
        for (CPInfo info : infos) {
            System.err.println("Faction: " + info.factionId + " has CP: " + info.controlPower);
        }
    }

    private static SegmentController controllerCache = null;
//    private static String getDummyUid(){
//        if(controllerCache != null && controllerCache.isFullyLoaded()){
//            return controllerCache.getUniqueIdentifier();
//        }
//        for (Sendable entry : GameCommon.getGameState().getState().getLocalAndRemoteObjectContainer().getLocalObjects().values()) {
//            if(entry instanceof SegmentController){
//                controllerCache = (SegmentController) entry;
//                break;
//            }
//        }
//        return null;
//    }

    public static void claimSystem(Vector3i system, int facId, String dummyUid) {
        try {
            system = new Vector3i(system);
            system.scale(16);
            //Actually gets it from SECTOR POS, classic starmade move
            StellarSystem sys = GameServerState.instance.getUniverse().getStellarSystemFromStellarPos(system);
//            sys.setOwnerUID(getDummyUid());
            sys.setOwnerUID(dummyUid);
            sys.setOwnerFaction(facId);
            Vector3i p = new Vector3i(sys.getPos());
            p.scale(16);
            sys.setOwnerPos(p);
            GameServerState server = GameServer.getServerState();
            server.getDatabaseIndex().getTableManager().getSystemTable().updateOrInsertSystemIfChanged(sys, false);

            server.getUniverse()
                    .getGalaxyFromSystemPos(sys.getPos()).getNpcFactionManager()
                    .onSystemOwnershipChanged(0, sys.getOwnerFaction(), sys.getPos());
            GameServerState.instance.getGameState().sendGalaxyModToClients(sys, system);
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
