package me.jakev.starfm.cp;

import api.mod.ModSkeleton;
import api.mod.config.PersistentObjectUtil;
import me.jakev.starfm.StarFM;
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
        //SPECIAL CONDITION
        //If we have a pirate station, set its CP to 1000
        if(i == null) i = nullInfo;
        if(i.factionId == 0 && i.controlPower == 0) {
            if (uid.startsWith("ENTITY_SPACESTATION_ENTITY_SPACESTATION_Station_Piratestation")) {
                i = new CPInfo(-1, 1000);
            }
        }
        ///
        return i;
    }

    public static void updateControlPower(SegmentController seg) {
        boolean isStation = seg instanceof SpaceStation;
        boolean isHomebase = seg.isHomeBase();
        int controlPower = 0;
        int faction = seg.getFactionId();

        //TODO: Temporary Control Power formula
        controlPower += seg.getMass();
        if (isStation) controlPower *= 2;
        if(isHomebase) controlPower *= -1;
        //END

        String uidFull = seg.getUniqueIdentifierFull();
        storage.cpMap.put(uidFull, new CPInfo(faction, controlPower));
    }

    private static int cpFormula(int reactor, int shields, int manuPower){
        int reactorConst = 1;
        int shieldsConst = 1;
        return 1;
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
