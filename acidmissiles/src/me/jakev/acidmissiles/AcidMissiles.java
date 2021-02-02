package me.jakev.acidmissiles;

import api.common.GameCommon;
import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.weapon.MissileHitEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.StarRunnable;
import org.schema.common.util.linAlg.Vector3b;
import org.schema.game.common.controller.EditableSendableSegmentController;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.damage.DamageDealerType;
import org.schema.game.common.controller.damage.Damager;
import org.schema.game.common.controller.damage.HitType;
import org.schema.game.common.controller.elements.ShieldAddOn;
import org.schema.game.common.controller.elements.ShieldContainerInterface;
import org.schema.game.common.data.world.SectorNotFoundException;

import javax.vecmath.Vector3f;

/**
 * Created by Jake on 1/6/2021.
 * <insert description here>
 */
public class AcidMissiles extends StarMod {
    public static void main(String[] args) {

    }

    @Override
    public void onEnable() {
        if(GameCommon.isOnSinglePlayer() || GameCommon.isDedicatedServer()) {
            new StarRunnable() {
                @Override
                public void run() {
                    GameServer.getServerState().getExplosionOrdersQueued().clear();
                    GameServer.getServerState().getTheadPoolExplosions().getQueue().clear();
                }
            }.runTimer(this, 1);
        }
        StarLoader.registerListener(MissileHitEvent.class, new Listener<MissileHitEvent>() {
            @Override
            public void onEvent(MissileHitEvent event) {
                if(!event.isServer()){
                    return;
                }
                event.setCanceled(true);
                SegmentController sc = event.getRaycast().getSegment().getSegmentController();
                Vector3b cubePos = event.getRaycast().getCubePos();
                long index = event.getRaycast().getSegment().getAbsoluteIndex(cubePos.x, cubePos.y, cubePos.z);

                Damager damager = event.getMissile().getOwner();
                if (!sc.checkAttack(damager, true, false)) {
                    return;
                }

                float dam = 0;
                if(sc.isUsingLocalShields()){
                    ShieldAddOn shieldAddOn = ((ShieldContainerInterface) ((ManagedUsableSegmentController<?>) sc).getManagerContainer()).getShieldAddOn();
                    System.err.println("Has local shields");
                    if(shieldAddOn.isUsingLocalShieldsAtLeastOneActive() || sc.railController.isDockedAndExecuted()){
                        try {
                            System.err.println("Hitting with: " + event.getMissile().getDamage());
                            //sc.getEffectContainer().get(HitReceiverType.SHIELD)
                            dam = (float) shieldAddOn.handleShieldHit(
                                    damager,
                                    null,
                                    event.getMissile().getWorldTransform().origin,
                                    event.getMissile().getSectorId(),
                                    DamageDealerType.MISSILE,
                                    HitType.WEAPON,
                                    event.getMissile().getDamage(),
                                    event.getMissile().getWeaponId());
                        } catch (SectorNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (dam <= 0) {
                            sc.sendHitConfirmToDamager(damager, true);
                        }
                    }
                }
                System.err.println("DAMAGE ON SHIELD::: " + dam);

                EditableSendableSegmentController ship = (EditableSendableSegmentController) sc;
                ship.getAcidDamageManagerServer().inputDamage(
                        index,
                        event.getMissile().getDirection(new Vector3f()),
                        event.getMissile().getDamage()*10,
                        32,
                        damager,
                        event.getMissile().getWeaponId(),
                        false, false
                );
                event.getMissile().setAlive(false);
            }
        }, this);
    }
}
