package me.jakev.starextractor;

import api.listener.Listener;
import api.listener.events.block.SegmentPieceAddEvent;
import api.listener.events.block.SegmentPieceRemoveEvent;
import api.listener.events.entity.SegmentControllerOverheatEvent;
import api.mod.StarLoader;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.element.ElementCollection;

/**
 * Created by Jake on 2/24/2021.
 * <insert description here>
 */
public class BlockPlaceLogic extends Listener<SegmentPieceAddEvent> {
    @Override
    public void onEvent(SegmentPieceAddEvent event) {
        if(event.getNewType() != StarExtractorNew.waterExtractor.id){
            return;
        }
        long absIndex = event.getAbsIndex();
        SegmentController controller = event.getSegmentController();
        Vector3i system = controller.getSystem(new Vector3i());
        //
        SystemContainer systemContainer = StarExtractorNew.container.systemData.get(system);
        if(systemContainer == null){
            SystemContainer value = new SystemContainer();
            StarExtractorNew.container.systemData.put(system, value);
            systemContainer = value;
        }

        //Remove self from array if it exists
        for (StationContainer station : systemContainer.stations) {
            if(station.entityUid.equals(event.getSegmentController().getUniqueIdentifier())){
                systemContainer.stations.remove(station);
                break;
            }
        }
        StationContainer station = new StationContainer();
        station.entityUid = controller.getUniqueIdentifier();
        station.extractor = absIndex;
        systemContainer.stations.add(station);
    }
    static void initRemoveListeners(){
        StarLoader.registerListener(SegmentPieceRemoveEvent.class, new Listener<SegmentPieceRemoveEvent>() {
            @Override
            public void onEvent(SegmentPieceRemoveEvent event) {
                if(event.getType() != StarExtractorNew.waterExtractor.id){
                    return;
                }
                long absIndex = ElementCollection.getIndex(event.getX(), event.getY(), event.getZ());
                String uid = event.getSegment().getSegmentController().getUniqueIdentifier();
                Vector3i systemPos = event.getSegment().getSegmentController().getSystem(new Vector3i());
                SystemContainer system = StarExtractorNew.container.systemData.get(systemPos);
                if(system != null){
                    for (StationContainer station : system.stations) {
                        if(station.entityUid.equals(uid)){
                            if(absIndex == station.extractor){
                                system.stations.remove(station);
                                break;
                            }
                        }
                    }
                }
            }
        }, StarExtractorNew.inst);
        StarLoader.registerListener(SegmentControllerOverheatEvent.class, new Listener<SegmentControllerOverheatEvent>() {
            @Override
            public void onEvent(SegmentControllerOverheatEvent event) {
                String uid = event.getEntity().getUniqueIdentifier();
                Vector3i systemPos = event.getEntity().getSystem(new Vector3i());
                SystemContainer system = StarExtractorNew.container.systemData.get(systemPos);
                if(system != null){
                    for (StationContainer station : system.stations) {
                        if(station.entityUid.equals(uid)) {
                            system.stations.remove(station);
                            break;
                        }
                    }
                }
            }
        }, StarExtractorNew.inst);
    }
}
