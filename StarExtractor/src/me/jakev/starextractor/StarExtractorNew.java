package me.jakev.starextractor;

import api.config.BlockConfig;
import api.listener.events.block.SegmentPieceAddEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.PersistentObjectUtil;
import api.utils.StarRunnable;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.server.data.GameServerState;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Jake on 2/23/2021.
 * <insert description here>
 */
public class StarExtractorNew extends StarMod {
    public static StarExtractorNew inst;

    @Override
    public void onLoad() {
        inst = this;
    }
    public static Vector3i[] extractorSystems = new Vector3i[]{new Vector3i(0,0,0)};
    public static ExtractorContainer container;
    public static ElementInformation waterExtractor;
    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        waterExtractor = config.newFactory(this, "Water Extractor", new short[]{123});
        config.add(waterExtractor);


    }
    @Override
    public void onEnable() {
        BlockPlaceLogic.initRemoveListeners();
        StarLoader.registerListener(SegmentPieceAddEvent.class, new BlockPlaceLogic(), this);
        ArrayList<Object> objects = PersistentObjectUtil.getObjects(StarExtractorNew.inst.getSkeleton(), ExtractorContainer.class);

        if(objects.isEmpty()){
            PersistentObjectUtil.addObject(StarExtractorNew.inst.getSkeleton(), new ExtractorContainer());
        }
        container = ((ExtractorContainer) objects.get(0));

        new StarRunnable(){
            @Override
            public void run() {
                //Resource add pass
                for (Map.Entry<Vector3i, SystemContainer> entry : container.systemData.entrySet()) {
                    Vector3i systemPos = entry.getKey();
                    SystemContainer system = entry.getValue();
                    int resourcesPerStation = system.regenRate / system.stations.size();
                    for (StationContainer station : system.stations) {
                        station.collectedResources += resourcesPerStation;
                    }
                }

                //Resource give pass
                for (Map.Entry<Vector3i, SystemContainer> entry : container.systemData.entrySet()) {
                    Vector3i systemPos = entry.getKey();
                    SystemContainer system = entry.getValue();
                    for (StationContainer station : system.stations) {
                        SegmentController controller = GameServerState.instance.getSegmentControllersByName().get(station.entityUid);
                        if (controller != null) {
                            SegmentPiece piece = new SegmentPiece();
                            long extractor = station.extractor;
                            controller.getSegmentBuffer().getPointUnsave(extractor, piece);
                            short type = piece.getType();
                            if (type == waterExtractor.id) {
                                ManagerContainer<?> managerContainer = ((ManagedUsableSegmentController<?>) controller).getManagerContainer();
                                Inventory inventory = managerContainer.getInventory(extractor);
                                int pos = inventory.incExistingOrNextFreeSlotWithoutException(system.resourceTypeId, station.collectedResources);
                                station.collectedResources = 0;
                                inventory.sendInventoryModification(pos);
                            }
                        }//if else: dont care, it will keep accumulating resources unloaded.
                    }
                }
            }
        }.runTimer(this, 1);
        new StarRunnable(){
            @Override
            public void run() {
                PersistentObjectUtil.save(inst.getSkeleton());
            }
        }.runTimer(this, 10*60*5);
    }

    @Override
    public void onDisable() {
        PersistentObjectUtil.save(inst.getSkeleton());
    }
    /*
    > Logic:
      For every system:
        For every block:
          Add const/blockcount
      For every system:
        For every station/block:
          If its loaded, add `resources` to its inventory

    > Structure:
     [System Pos]:
         [Station + Pos of block] + resources

    > Place logic:
      On Special block place:
         - Remove station from array if it exists
         - Add [Station+Pos of block to array]
      On special block remove:
         - Remove station from the array
      On station overheat:
         - Remove station from array

    > Possible flaws:
     - Station is removed by some other means, could accumilate resources in the background.
       Possible fix: Check if a station still exists in the database every shutdown

     */
}
