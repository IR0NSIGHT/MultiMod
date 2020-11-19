package me.jakev.starextractor;

import org.schema.common.util.linAlg.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Fully serializable representation of the mod
 */
public class ExtractorContainer {
    public HashMap<Vector3i, SystemContainer> systemData = new HashMap<>();
}
 class SystemContainer {
    //Each system contains regenRate, amount collected, and a list of stations that have extractors
    public int regenRate = 1;
    public int water;
    public ArrayList<StationContainer> stations = new ArrayList<>();
}
class StationContainer{
    //The absolute block index of where extractors are
    public String entityUid;
    public int offlineCollectedResources;
    public ArrayList<Long> extractors = new ArrayList<>();
}
