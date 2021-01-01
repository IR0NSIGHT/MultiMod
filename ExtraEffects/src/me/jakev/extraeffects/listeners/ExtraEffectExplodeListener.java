package me.jakev.extraeffects.listeners;

import api.common.GameClient;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.entity.SegmentControllerOverheatEvent;
import api.listener.events.player.PlayerAcquireTargetEvent;
import api.listener.events.systems.ShieldHitEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import api.utils.sound.AudioUtils;
import com.bulletphysics.linearmath.Transform;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particles.FadeParticle;
import me.jakev.extraeffects.particles.shipexplode.ColorFlashParticle;
import me.jakev.extraeffects.particles.shipexplode.DebrisFlairParticle;
import me.jakev.extraeffects.particles.shipexplode.InvisibleEmitterParticle;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.element.ElementKeyMap;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.HashMap;

/**
 * Created by Jake on 12/9/2020.
 * <insert description here>
 */
public class ExtraEffectExplodeListener {
    public static void init(StarMod inst){
        StarLoader.registerListener(SegmentControllerOverheatEvent.class, new Listener<SegmentControllerOverheatEvent>() {
            @Override
            public void onEvent(SegmentControllerOverheatEvent event) {
                Vector3f pos = event.getEntity().getWorldTransformCenterOfMass(new Transform()).origin;
                new StarRunnable(){

                    @Override
                    public void run() {
                        ModParticleUtil.playClient(pos, SpriteList.BIGSMOKE.getSprite(), 70, 3000, 0.3F,false, new ModParticleFactory() {
                            @Override
                            public ModParticle newParticle() {
                                return new FadeParticle(10, 500);
                            }
                        });
                    }
                }.runLater(inst, 2);
                //130
                ModParticleUtil.playClient(pos, SpriteList.FLASH.getSprite(), 1, 180, new Vector3f(0, 0, 0), new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {//15
                        return new ColorFlashParticle(25, new Vector4f(1,1,0,1), new Vector4f(1F,0,0,1F));
                    }
                });

                //30, 1.6
                ModParticleUtil.playClient(pos, SpriteList.NOTHING.getSprite(), 40, 5000, 1.8F,0,0,0, new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new InvisibleEmitterParticle(SpriteList.FIREFLASH.getSprite(), 1, 5000, new Vector3f(0, 0, 0), 1000, new ModParticleFactory() {
                            @Override
                            public ModParticle newParticle() {
                                return new DebrisFlairParticle();
                            }
                        });
                    }
                });
            }
        }, inst);
        StarLoader.registerListener(PlayerAcquireTargetEvent.class, new Listener<PlayerAcquireTargetEvent>() {
            @Override
            public void onEvent(PlayerAcquireTargetEvent event) {
                if(GameClient.getClientState() != null){
                    String p = event.getPlayer().getName();
                    Integer oldId = lastTarget.get(p);
                    if(oldId == null) oldId = 0;
                    int newId = (event.getTarget() == null)?0:event.getTarget().getId();
                    if(newId != 0 && oldId == 0){
                        AudioUtils.clientPlaySound("0022_spaceship user - locked on target successful beep", 1F, 1F);
                        lastTarget.put(p, newId);
                    }else if(newId == 0){
                        lastTarget.put(p, 0);
                    }
                }
            }
        }, inst);
        StarLoader.registerListener(ShieldHitEvent.class, new Listener<ShieldHitEvent>() {
            @Override
            public void onEvent(ShieldHitEvent event) {
                if(event.getShield().getPercentOne() < 0.1F){
                    if(GameClient.getClientState() != null){
                        if(((ManagedUsableSegmentController<?>) event.getHitController()).getAttachedPlayers().contains(GameClient.getClientPlayerState())) {
                            AudioUtils.clientPlaySound("0022_spaceship user - ship turbulence small", 1F, 1F);
                        }
                    }
                }
            }
        }, inst);
        StarLoader.registerListener(SegmentPieceActivateEvent.class, new Listener<SegmentPieceActivateEvent>() {
            @Override
            public void onEvent(SegmentPieceActivateEvent event) {
                if(GameClient.getClientState() != null) {
                    if(((ManagedUsableSegmentController<?>) event.getSegmentPiece().getSegmentController()).getAttachedPlayers().contains(GameClient.getClientPlayerState())) {
                        if (event.getSegmentPiece().getType() == ElementKeyMap.LOGIC_REMOTE_INNER) {
                            if(event.getSegmentPiece().isActive()){
                                AudioUtils.clientPlaySound("0022_item - forcefield powerdown", 1F, 1F);
                            }else {
                                AudioUtils.clientPlaySound("0022_action - enter digits in digital keypad", 1F, 1F);
                            }
                        }
                    }
                }
            }
        }, inst);
    }
    private static HashMap<String, Integer> lastTarget = new HashMap<>();
}
