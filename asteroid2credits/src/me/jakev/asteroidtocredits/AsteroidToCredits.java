package me.jakev.asteroidtocredits;

import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceSalvageEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.utils.game.SegmentControllerUtils;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.world.StellarSystem;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jake on 10/21/2020.
 * <insert description here>
 */
public class AsteroidToCredits extends StarMod {
    public static void main(String[] args) { }

    @Override
    public void onGameStart() {
        setModName("AsteroidToCredits");
        setModVersion("0.1");
        setModDescription("Mine asteroids for credits, simple as");
        setModAuthor("JakeV");
        setModSMVersion("0.202.101");
        setServerSide(true);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig("config");
        final int creditsPerBlock = config.getConfigurableInt("CreditsPerBlock", 100);
        float creditMult = config.getConfigurableFloat("OwnedSystemMultipler", 4);
        final int creditsPerOwned = (int) (creditsPerBlock*creditMult);
        config.saveConfig();

        StarLoader.registerListener(SegmentPieceSalvageEvent.class, new Listener<SegmentPieceSalvageEvent>() {
            @Override
            public void onEvent(SegmentPieceSalvageEvent event) {
                SegmentController segmentController = event.getSegmentController();
                ArrayList<PlayerState> ps = SegmentControllerUtils.getAttachedPlayers(segmentController);
                for (PlayerState p : ps) {
                    try {
                        StellarSystem sys = GameServer.getUniverse().getStellarSystemFromStellarPos(segmentController.getSystem(new Vector3i()));
                        if (sys.getOwnerFaction() == p.getFactionId()) {
                            p.modCreditsServer(creditsPerOwned);
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    p.modCreditsServer(creditsPerBlock);
                }
            }
        }, this);
    }
}