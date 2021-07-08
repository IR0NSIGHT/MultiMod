package me.jakev.extraeffects.listeners;

import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.player.PlayerChatEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import me.jakev.extraeffects.particles.advanced.PulsingExplosion;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;

import javax.vecmath.Vector3f;
import javax.xml.crypto.dsig.Transform;

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
                int sectorID = obj.getSectorId();
                Vector3f pos = obj.getClientTransform().origin;
                PulsingExplosion p = new PulsingExplosion(pos,10000,-1,0, new Vector3f(),sectorID);
                p.setAttributes(50,200);
                p.play();
            }
        },mod);
    }
}
