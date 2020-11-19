package me.jakev.starextractor;

import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceAddEvent;
import api.listener.events.entity.SegmentControllerFullyLoadedEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.PersistentObjectUtil;
import api.utils.StarRunnable;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;

import java.util.ArrayList;

/**
 * Created by Jake on 11/19/2020.
 * <insert description here>
 */
public class StarExtractor extends StarMod {
    @Override
    public void onGameStart() {
        setModName("StarExtractor");
        setModVersion("0.1");
        setModAuthor("JakeV");
        setModDescription("fortntie battle royale simulator in roblox");
    }

    @Override
    public void onEnable() {
        ArrayList<Object> objects = PersistentObjectUtil.getObjects(this, ExtractorContainer.class);
        if(objects.isEmpty()){
            objects.add(new ExtractorContainer());
        }
        final Vector3i[] extractorSystems = new Vector3i[]{new Vector3i(0,0,0)};
        final ExtractorContainer container = ((ExtractorContainer) objects.get(0));
        new StarRunnable(){
            @Override
            public void run() {
                for (Vector3i extractorSystem : extractorSystems) {
                    //Load system container if not present
                    SystemContainer systemContainer = container.systemData.get(extractorSystem);
                    if(systemContainer == null){
                        SystemContainer value = new SystemContainer();
                        container.systemData.put(extractorSystem, value);
                        systemContainer = value;
                    }

                    //Add water based on regen rate
                    systemContainer.water += systemContainer.regenRate;
                    ArrayList<StationContainer> removeQueue = new ArrayList<>();
                    for (StationContainer station : systemContainer.stations) {
//                        GameServer.getServerState().getLocalAndRemoteObjectContainer().getUidObjectMap();
                        boolean stationExists = GameServer.getServerState().existsEntity(SimpleTransformableSendableObject.EntityType.SPACE_STATION, station.entityUid);
                        if(!stationExists){
                            removeQueue.add(station);
                        }
                        SegmentController controller = null;
                        for (SegmentController value : GameServer.getServerState().getSegmentControllersByName().values()) {
                            if(value.getUniqueIdentifier().equals(station.entityUid)){
                                controller = value;
                                break;
                            }
                        }
                        int maxTakenWater = Math.min(station.extractors.size(), systemContainer.water);
                        systemContainer.water -= maxTakenWater;
                        if(controller != null){
                            SegmentPiece piece = new SegmentPiece();
                            ArrayList<Long> rQueue = new ArrayList<>();
                            for (Long extractor : station.extractors) {
                                controller.getSegmentBuffer().getPointUnsave(extractor, piece);
                                short type = piece.getType();
                                if(type == /*TODO*/ 123){
                                    ManagerContainer<?> managerContainer = ((ManagedUsableSegmentController<?>) controller).getManagerContainer();
                                    Inventory inventory = managerContainer.getInventory(extractor);
                                    int pos = inventory.incExistingOrNextFreeSlotWithoutException((short) 1, 10);
                                    inventory.sendInventoryModification(pos);
                                }else{
                                    rQueue.add(extractor);
                                }
                                maxTakenWater--;
                                if(maxTakenWater < 1){
                                    break;
                                }
                            }
                            for (Long aLong : rQueue) {
                                station.extractors.remove(aLong);
                            }
                        }else{
                            station.offlineCollectedResources += maxTakenWater;
                        }

                    }
                    for (StationContainer remove : removeQueue) {
                        systemContainer.stations.remove(remove);
                    }


                }
            }
        }.runTimer(this, 20);

        StarLoader.registerListener(SegmentPieceAddEvent.class, new Listener<SegmentPieceAddEvent>() {
            @Override
            public void onEvent(SegmentPieceAddEvent event) {
                long absIndex = event.getAbsIndex();
                SegmentController controller = event.getSegmentController();
                Vector3i system = controller.getSystem(new Vector3i());
                SystemContainer systemContainer = container.systemData.get(system);
                if(systemContainer == null){
                    SystemContainer value = new SystemContainer();
                    container.systemData.put(system, value);
                    systemContainer = value;
                }
                StationContainer cStation = null;
                for (StationContainer station : systemContainer.stations) {
                    if(station.entityUid.equals(event.getSegmentController().getUniqueIdentifier())){
                        cStation = station;
                        break;
                    }
                }
                if(cStation == null){
                    StationContainer cont = new StationContainer();
                    systemContainer.stations.add(cont);
                    cStation = cont;
                }

                cStation.extractors.add(absIndex);
            }
        }, this);

        StarLoader.registerListener(SegmentControllerFullyLoadedEvent.class, new Listener<SegmentControllerFullyLoadedEvent>() {
            @Override
            public void onEvent(SegmentControllerFullyLoadedEvent event) {
                Vector3i system = event.getController().getSystem(new Vector3i());
                SystemContainer systemContainer = container.systemData.get(system);
                if(systemContainer != null){
                    for (StationContainer station : systemContainer.stations) {
                        if(station.entityUid.equals(event.getController().getUniqueIdentifier())){
                            int resourcePerExtractor = station.offlineCollectedResources/station.extractors.size();
                            station.offlineCollectedResources = 0;
                            for (Long extractor : station.extractors) {
                                Inventory inventory = ((ManagedUsableSegmentController<?>) event.getController()).getManagerContainer().getInventory(extractor);
                                inventory.incExistingOrNextFreeSlotWithoutException(((short) 1), resourcePerExtractor);

                            }
                            break;
                        }
                    }
                }
            }
        }, this);
    }
    /*
    Logic:
     for system in [all systems]:
       water += regenRate
       for station in [system]:
       if station.doesNotExist(): Remove
            for extractors:
                if extractor.does not exists: delete it
                if station.isLoaded():
                     extractor.addWater(min(--water, 1))
                else:
                     extractor.fakeAddWater


    Events:
        on custom block place:
            lazy load everything up to point:
            extractors.add(index)

     */
}
