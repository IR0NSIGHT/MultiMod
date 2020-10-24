package me.jakev.sysclaimoverhaul;

import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.systems.ReactorRecalibrateEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.utils.StarRunnable;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.elements.power.reactor.tree.ReactorTree;
import org.schema.game.common.data.world.Sector;
import org.schema.game.common.data.world.StellarSystem;
import org.schema.game.server.data.GameServerState;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Jake on 10/21/2020.
 * <insert description here>
 */
public class SystemClaimOverhaul extends StarMod {
    public static void main(String[] args) { }

    @Override
    public void onGameStart() {
        setModName("SystemClaimOverhaul");
        setModVersion("0.1");
        setModDescription("Bases system claim on reactor size between all stations.");
        setModAuthor("JakeV");
        setModSMVersion("0.202.101");
        setServerSide(true);
    }
    private static StarMod modInstance;
    @Override
    public void onEnable() {
        modInstance = this;
        StarLoader.registerListener(ReactorRecalibrateEvent.class, new Listener<ReactorRecalibrateEvent>() {
            @Override
            public void onEvent(ReactorRecalibrateEvent event) {
                ReactorTree activeReactor = event.getImplementation().getActiveReactor();
                if(activeReactor==null) return;
                int reactorSize = activeReactor.getSize();
                if (event.getImplementation().getSegmentController() instanceof SpaceStation) {
                    SpaceStation station = (SpaceStation) event.getImplementation().getSegmentController();
                    if(station.isHomeBase()){
                        reactorSize = 0;
                    }
                    int facId = station.getFactionId();
                    if(facId <= 0) return;
                    markClaim(station.getSector(new Vector3i()), facId, reactorSize);
                }
                lastController = event.getImplementation().getSegmentController();
            }
        }, modInstance);
        new StarRunnable(){
            @Override
            public void run() {
                try {
                    long ms = System.currentTimeMillis();
                    sweep();
                    long time = System.currentTimeMillis() - ms;
                    if(ms > 2) {
                        System.err.println("[SystemClaimOverhaul] Sweep time: " + time);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTimer(modInstance, 25*10);
    }
    public static int[] deser(String str){
        String[] s = str.split("x");
        return new int[]{Integer.parseInt(s[0]), Integer.parseInt(s[1])};
    }
    public static String ser(int fac, int rLevel){
        return fac + "x" + rLevel;
    }
    private static SegmentController lastController = null;
    public static void markClaim(Vector3i sector, int facId, int reactorLevel){
        String str = ser(facId, reactorLevel);
        FileConfiguration config = modInstance.getConfig("claims");
        config.set(sector.toStringPure(), str);
        config.saveConfig();
    }
    public static int[] getClaimData(Vector3i sector){
        FileConfiguration config = modInstance.getConfig("claims");
        String string = config.getString(sector.toStringPure());
        if(string == null){
            return null;
        }
        return deser(string);
    }
    public static void sweep() throws IOException {
        HashSet<StellarSystem> systemsToSweep =new HashSet<>();
        for (Integer secId : GameServer.getServerState().activeSectors) {
            Sector sec = GameServer.getUniverse().getSector(secId);
            StellarSystem sys = GameServer.getServerState().getUniverse().getStellarSystemFromSecPos(sec.pos);
            systemsToSweep.add(sys);
        }
        for (StellarSystem sys : systemsToSweep) {
            HashMap<Integer, Integer> mapp = new HashMap<>();
            Vector3i pos = sys.getPos();
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        Vector3i origin = new Vector3i(pos.x, pos.y, pos.z);
                        origin.scale(16);
                        origin.add(x,y,z);
                        int[] claimData = getClaimData(origin);
                        if(claimData != null){
                            int c = 0;
                            Integer integer = mapp.get(claimData[0]);
                            if(integer != null){
                                c = integer;
                            }
                            c += claimData[1];
                            mapp.put(claimData[0], c);
                        }
                    }
                }
            }
            int facId = 0;
            int maxRLevel = 0;
            for (Integer i :mapp.keySet()){
                int rLevel = mapp.get(i);
                if(rLevel > maxRLevel){
                    maxRLevel = rLevel;
                    facId = i;
                }
            }
            if(facId > 0){
                if(lastController == null){
                    return;
                }
                sys.setOwnerUID(lastController.getUniqueIdentifier());
                sys.setOwnerFaction(facId);
                Vector3i p = sys.getPos();
                p.scale(16);
                sys.setOwnerPos(p);
                try {
                    GameServerState server = GameServer.getServerState();
                    server.getDatabaseIndex().getTableManager().getSystemTable().updateOrInsertSystemIfChanged(sys, false);

                    server.getUniverse()
                            .getGalaxyFromSystemPos(sys.getPos()).getNpcFactionManager()
                            .onSystemOwnershipChanged(0, sys.getOwnerFaction(), sys.getPos());
                    StarLoader.getGameState().sendGalaxyModToClients(sys, pos);

                } catch (SQLException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
