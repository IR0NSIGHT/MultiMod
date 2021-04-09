package me.jakev.extraeffects.listeners;

import api.ModPlayground;
import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.player.PlayerChatEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.GodParticle;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.data.player.PlayerState;

import javax.vecmath.Vector3f;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 09.04.2021
 * TIME: 22:16
 */
public class DebugListener {
    public static void AddChatListener(StarMod instance) {
        StarLoader.registerListener(PlayerChatEvent.class, new Listener<PlayerChatEvent>() {
            @Override
            public void onEvent(PlayerChatEvent event) {
                if (event.isServer()) {
                    return;
                }
                if (event.getText().contains("uwu")) {
                    Vector3f playerPos = GameClientState.instance.getPlayer().getFirstControlledTransformableWOExc().getWorldTransform().origin;
                    GodParticle gp = new GodParticle(SpriteList.MULTISPARK_SINGLE.getSprite(), playerPos,30000);
                    gp.playOnClient(false);
                    ModPlayground.broadcastMessage("playing particle, count: " + GodParticle.particleCountGlobalClient);
                }
            }
        },instance);
    }
}
