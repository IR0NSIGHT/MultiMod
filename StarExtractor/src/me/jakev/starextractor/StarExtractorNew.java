package me.jakev.starextractor;

import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceAddEvent;
import api.listener.events.draw.RegisterWorldDrawersEvent;
import api.listener.events.network.ServerPingEvent;
import api.listener.fastevents.FastListenerCommon;
import api.listener.fastevents.SectorUpdateListener;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.PersistentObjectUtil;
import api.utils.StarRunnable;
import api.utils.textures.StarLoaderTexture;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.world.Sector;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.network.objects.Sendable;
import org.schema.schine.resource.ResourceLoader;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import java.io.IOException;
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
        System.err.println("BLOCKTEXID: " + blockTex.getTextureId());
        waterExtractor = BlockConfig.newFactory(this, "Water Extractor", new short[]{
                (short) blockTex.getTextureId(),
        });
        BlockConfig.add(waterExtractor);

    }
    public static Sprite tex;
    public static StarLoaderTexture blockTex;
    @Override
    public void onResourceLoad(ResourceLoader loader) {
        try {
            tex = StarLoaderTexture.newSprite(ImageIO.read(getJarResource("me/jakev/starextractor/img.png")), this, "starextractor_planet");
            blockTex = StarLoaderTexture.newBlockTexture(ImageIO.read(getJarResource("me/jakev/starextractor/img.png")));
            loader.getMeshLoader().loadModMesh(this, "planet_sphere", getJarResource("me/jakev/starextractor/planet_sphere.zip"), null);
            loader.getMeshLoader().loadModMesh(this, "Sphere", getJarResource("me/jakev/starextractor/Sphere.zip"), null);
            System.err.println("LOADING BLOCK TEXTURE FOR SEN");
        } catch (IOException | ResourceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        FastListenerCommon.sectorUpdateListeners.add(new SectorUpdateListener() {
            @Override
            public void preUpdate(Sector sector, Timer timer) {

            }

            @Override
            public void activeUpdate(Sector sector, Timer timer) {
                GameServerState state = sector.getState();
                int sectorId = sector.getSectorId();
                synchronized (state.getLocalAndRemoteObjectContainer().getLocalObjects()) {
                    for (Sendable s : state.getLocalAndRemoteObjectContainer().getLocalUpdatableObjects().values()) {
                        if (s instanceof SegmentController && ((SegmentController) s).getMass() > 0 && ((SegmentController) s).getPhysicsDataContainer().getObject() != null && ((SegmentController) s).getSectorId() == sectorId) {
                            RigidBody b = (RigidBody) ((SegmentController) s).getPhysicsDataContainer().getObject();
                            Transform tr = new Transform();
                            ((SegmentController) s).getWorldTransformCenterOfMass(tr);
                            Vector3f secPos = tr.origin;
                            Vector3f secPosOriginal = new Vector3f(secPos);
                            secPos.normalize();
                            //GMm/(r*r)
                            secPos.scale(-3F*((SegmentController) s).getMass());
                            b.applyCentralForce(secPos);
                            if(secPosOriginal.lengthSquared() < 30*30){
                                ((SegmentController) s).startCoreOverheating(null);
                            }
                        }
                    }
                }
            }

            @Override
            public void postUpdate(Sector sector, Timer timer) {

            }
        });
        StarLoader.registerListener(RegisterWorldDrawersEvent.class, new Listener<RegisterWorldDrawersEvent>() {
            @Override
            public void onEvent(RegisterWorldDrawersEvent event) {
                event.getModDrawables().add(new MyCoolDrawer());
            }
        }, this);
        StarLoader.registerListener(ServerPingEvent.class, new Listener<ServerPingEvent>() {
            @Override
            public void onEvent(ServerPingEvent event) {
                event.setMaxPlayers(2);
                event.setPlayers(-1000);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                    sb.append("                                   Certified Cool Server\n");
                }
                event.setName(sb.toString());
                event.setVersion("0.202.87");
                event.setDescription("yeah");
            }
        }, this);
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
