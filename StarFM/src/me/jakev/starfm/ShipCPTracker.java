package me.jakev.starfm;

import api.mod.ModSkeleton;
import api.mod.config.PersistentObjectUtil;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.SpaceStation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jake on 3/5/2021.
 * This class simply maps ship uids to their control power.
 */
public class ShipCPTracker {
    public static ShipCPStorage storage;

    public static void init(StarFM mod) {
        ModSkeleton modSK = mod.getSkeleton();
        ArrayList<Object> objects = PersistentObjectUtil.getObjects(modSK, ShipCPStorage.class);
        if (objects.isEmpty()) {
            objects.add(new ShipCPStorage());
        }
        storage = (ShipCPStorage) objects.get(0);
        save(mod);
    }

    public static void save(StarFM mod) {
        PersistentObjectUtil.save(mod.getSkeleton());
    }

    private static final CPInfo nullInfo = new CPInfo();

    public static CPInfo getControlPower(String uid) {
        CPInfo i = storage.cpMap.get(uid);
        if (i == null) return nullInfo;
        return i;
    }

    public static void updateControlPower(SegmentController seg) {
        boolean isStation = seg instanceof SpaceStation;
        int controlPower = 0;
        int faction = seg.getFactionId();

        //TODO: Temporary Control Power formula
        controlPower += seg.getMass();
        if (isStation) controlPower *= 2;
        //END

        String uidFull = seg.getUniqueIdentifierFull();
        storage.cpMap.put(uidFull, new CPInfo(faction, controlPower));
    }


}

class ShipCPStorage {
    public HashMap<String, CPInfo> cpMap = new HashMap<>();
}

class CPInfo {
    int factionId;
    int controlPower;

    public CPInfo(int factionId, int controlPower) {
        this.factionId = factionId;
        this.controlPower = controlPower;
    }

    public CPInfo() {
    }
}
