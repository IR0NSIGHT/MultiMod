package me.jakev.extraeffects.listeners;

import api.DebugFile;
import api.listener.Listener;
import api.listener.events.entity.ShipJumpEngageEvent;
import api.listener.events.player.PlayerChatEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import me.jakev.extraeffects.particles.advanced.JumpEffect;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.server.data.GameServerState;

import javax.vecmath.Vector3f;
import javax.xml.crypto.dsig.Transform;
import java.io.IOException;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 08.07.2021
 * TIME: 19:37
 */
public class ExtraEffectsJumpListener {
    public static void init(StarMod mod) {
        StarLoader.registerListener(ShipJumpEngageEvent.class, new Listener<ShipJumpEngageEvent>() {
            @Override
            public void onEvent(ShipJumpEngageEvent event) {
                if (event.isCanceled()) return;
                //if (event.isServer()) return;
                playJumpEffect(event.getController(),event.getOriginalSectorPos());
                playJumpEffect(event.getController(),event.getNewSector());
            }
        },mod);
    }

    private static void playJumpEffect(SegmentController ship, Vector3i sector) {
        int sectorID;
        try {
            sectorID = GameServerState.instance.getUniverse().getSector(sector).getId();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        Vector3f pos = ship.getWorldTransform().origin;
        JumpEffect p = new JumpEffect(pos,20000,sectorID);
        p.setAttributes((int) ship.getBoundingSphereTotal().radius,200, ship.getUniqueIdentifier());
        DebugFile.log("Jumpeffect: " + p.toString());
        new RemotePlay(p).broadcastToAll();
    }
}
