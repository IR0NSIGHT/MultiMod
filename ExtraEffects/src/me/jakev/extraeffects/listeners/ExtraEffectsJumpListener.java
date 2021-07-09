package me.jakev.extraeffects.listeners;

import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.entity.ShipJumpEngageEvent;
import api.listener.events.player.PlayerChatEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import me.jakev.extraeffects.particles.advanced.PulsingExplosion;
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
        //debug
        StarLoader.registerListener(PlayerChatEvent.class, new Listener<PlayerChatEvent>() {
            @Override
            public void onEvent(PlayerChatEvent event) {
                if (event.isServer()) return;
                if(!event.getText().contains("jump")) return;
                Transform t;
                SimpleTransformableSendableObject obj = GameClientState.instance.getCurrentPlayerObject();
                if (!(obj instanceof SegmentController)) return;
                SegmentController sc = (SegmentController) obj;
                playJumpEffect(sc,sc.getSector(new Vector3i()));


            }
        },mod);
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
        PulsingExplosion p = new PulsingExplosion(pos,20000,0,0, new Vector3f(),sectorID);
        p.setAttributes((int) ship.getBoundingSphereTotal().radius,200);
        p.setSC(ship);
        p.play();
    }
}
