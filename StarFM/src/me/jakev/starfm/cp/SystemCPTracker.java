package me.jakev.starfm.cp;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.database.DatabaseEntry;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.network.objects.Sendable;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Jake on 3/8/2021.
 * <insert description here>
 */
public class SystemCPTracker {
    public static ArrayList<CPInfo> getSystemControlPower(Vector3i system) {
        Vector3i secOrigin = new Vector3i(system);
        secOrigin.scale(16);
        Vector3i secEnd = new Vector3i(secOrigin);
        secEnd.add(16,16,16);
        try {
            List<DatabaseEntry> entities = GameServerState.instance.getDatabaseIndex().getTableManager().getEntityTable().getBySectorRange(secOrigin, secEnd, null);
            HashMap<String, CPInfo> infoMap = new HashMap<>();
            //Iterate through offline ships and put in infoMap
            for (DatabaseEntry entity : entities) {
                String uid = entity.getFullUid();
                infoMap.put(uid, ShipCPTracker.getControlPower(uid));
            }

            //Iterate through loaded ships and put in infoMap
            Object2ObjectOpenHashMap<String, Sendable> uidObjectMap = GameServerState.instance.getLocalAndRemoteObjectContainer().getUidObjectMap();
            Vector3i systemTmp = new Vector3i();
            for (Map.Entry<String, Sendable> entry : uidObjectMap.entrySet()) {
                Sendable ship = entry.getValue();
                if(ship instanceof SegmentController){
                    if(((SegmentController) ship).getSystem(systemTmp).equals(system)){
                        String fullUid = ((SegmentController) ship).getUniqueIdentifierFull();
                        infoMap.put(fullUid, ShipCPTracker.getControlPower(fullUid));
                    }
                }
            }

            //Now that we have all of the ships in the system, and their control power + faction id, put that into a list.
            HashMap<Integer, CPInfo> factionCPMap = new HashMap<>();
            for (CPInfo val : infoMap.values()) {
                //Skip unfactioned entities
                if(val.factionId == 0) continue;
                CPInfo cpInfo = factionCPMap.get(val.factionId);
                if(cpInfo == null){
                    CPInfo nInfo = new CPInfo();
                    factionCPMap.put(val.factionId, nInfo);
                    cpInfo = nInfo;
                    cpInfo.factionId = val.factionId;
                }
                cpInfo.controlPower += val.controlPower;
            }

            //Put the highest faction CP at the top
            ArrayList<CPInfo> list = new ArrayList<>(factionCPMap.values());
            Collections.sort(list, new Comparator<CPInfo>() {
                @Override
                public int compare(CPInfo o1, CPInfo o2) {
                    return Integer.compare(o1.controlPower, o2.controlPower);
                }
            });
            return list;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
