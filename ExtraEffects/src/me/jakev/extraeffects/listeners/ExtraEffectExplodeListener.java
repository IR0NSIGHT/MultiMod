package me.jakev.extraeffects.listeners;

import api.common.GameClient;
import api.common.GameCommon;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.entity.SegmentControllerOverheatEvent;
import api.listener.events.player.PlayerAcquireTargetEvent;
import api.listener.events.systems.ShieldHitEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.particle.ModParticleUtil;
import api.utils.sound.AudioUtils;
import com.bulletphysics.linearmath.Transform;
import me.jakev.extraeffects.ExtraEffectsParticles;
import me.jakev.extraeffects.SpriteList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.element.ElementKeyMap;

import javax.vecmath.Vector3f;
import java.util.HashMap;

/**
 * Created by Jake on 12/9/2020.
 * <insert description here>
 */
public class ExtraEffectExplodeListener {
    public static void init(StarMod inst) {
        StarLoader.registerListener(SegmentControllerOverheatEvent.class, new Listener<SegmentControllerOverheatEvent>() {
            @Override
            public void onEvent(SegmentControllerOverheatEvent event) {
                Vector3i sector = event.getEntity().getSector(new Vector3i());
                final Vector3f pos = event.getEntity().getWorldTransformCenterOfMass(new Transform()).origin;
                int sectorId = event.getEntity().getSectorId();
                int pSec = GameClient.getClientState().getCurrentSectorId();
                ModParticleUtil.playServer(sectorId,
                        ExtraEffectsParticles.EXPLOSION_TRIGGER, pos, SpriteList.NOTHING.getSprite(), new ModParticleUtil.Builder());
            }
        }, inst);
        StarLoader.registerListener(PlayerAcquireTargetEvent.class, new Listener<PlayerAcquireTargetEvent>() {
            @Override
            public void onEvent(PlayerAcquireTargetEvent event) {
                String p = event.getPlayer().getName();
                Integer oldId = lastTarget.get(p);
                if (oldId == null) oldId = 0;
                int newId = (event.getTarget() == null) ? 0 : event.getTarget().getId();
                if (newId != 0 && oldId == 0) {
                    AudioUtils.serverPlaySound("0022_spaceship user - locked on target successful beep", 1F, 1F, event.getPlayer());
                    lastTarget.put(p, newId);
                } else if (newId == 0) {
                    lastTarget.put(p, 0);
                }
            }
        }, inst);
        StarLoader.registerListener(ShieldHitEvent.class, new Listener<ShieldHitEvent>() {
            @Override
            public void onEvent(ShieldHitEvent event) {
                if (event.getShield().getPercentOne() < 0.1F) {
                    if (GameClient.getClientState() != null) {
                        if (((ManagedUsableSegmentController<?>) event.getHitController()).getAttachedPlayers().contains(GameClient.getClientPlayerState())) {
                            AudioUtils.clientPlaySound("0022_spaceship user - ship turbulence small", 1F, 1F);
                        }
                    }
                }
            }
        }, inst);
        StarLoader.registerListener(SegmentPieceActivateEvent.class, new Listener<SegmentPieceActivateEvent>() {
            @Override
            public void onEvent(SegmentPieceActivateEvent event) {
                if (GameClient.getClientState() != null) {
                    if (((ManagedUsableSegmentController<?>) event.getSegmentPiece().getSegmentController()).getAttachedPlayers().contains(GameClient.getClientPlayerState())) {
                        if (event.getSegmentPiece().getType() == ElementKeyMap.LOGIC_REMOTE_INNER) {
                            if (GameCommon.isOnSinglePlayer()) {
                                if (event.getSegmentPiece().isActive()) {
                                    AudioUtils.clientPlaySound("0022_item - forcefield powerdown", 1F, 1F);
                                } else {
                                    AudioUtils.clientPlaySound("0022_action - enter digits in digital keypad", 1F, 1F);
                                }
                            } else {
                                if (event.getSegmentPiece().isActive()) {
                                    AudioUtils.clientPlaySound("0022_item - forcefield powerdown", 1F, 1F);
                                } else {
                                    AudioUtils.clientPlaySound("0022_action - enter digits in digital keypad", 1F, 1F);
                                }

                            }
                        }
                    }
                }
            }
        }, inst);
    }

    private static HashMap<String, Integer> lastTarget = new HashMap<>();
}
