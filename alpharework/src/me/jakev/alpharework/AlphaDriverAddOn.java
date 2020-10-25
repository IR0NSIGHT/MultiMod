package me.jakev.alpharework;

import api.ModPlayground;
import api.common.GameClient;
import api.listener.events.systems.ReactorRecalibrateEvent;
import api.utils.addon.SimpleAddOn;
import api.utils.game.SegmentControllerUtils;
import api.utils.sound.AudioUtils;
import org.schema.common.util.linAlg.Vector4i;
import org.schema.game.client.view.gui.shiphud.newhud.Hud;
import org.schema.game.client.view.gui.shiphud.newhud.PowerConsumptionBar;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import org.schema.game.common.controller.elements.FireingUnit;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.elements.power.reactor.tree.ReactorElement;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.forms.gui.GUIOverlay;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * -Create class
 * -register class
 * -do not modify class schema
 * -varutil
 */
public class AlphaDriverAddOn extends SimpleAddOn {
    public static final String UID_NAME = "AlphaDriverAddOn";
    Vector4i origConfigColor = new Vector4i();
    Vector4i origConfigWarnColor = new Vector4i();
    public AlphaDriverAddOn(ManagerContainer<?> var1, AlphaRework mod) {
        super(var1, ElementKeyMap.EFFECT_OVERDRIVE_COMPUTER, mod, UID_NAME);
        alphaDir1 = AlphaRework.config.getConfigurableInt("overdrive_duration_1", 3);
        alphaDir2 = AlphaRework.config.getConfigurableInt("overdrive_duration_2", 12);
        removedPerSecond = 0.2F;
        onReactorRecalibrate(null);
        origConfigColor.set(PowerConsumptionBar.COLOR);
        origConfigWarnColor.set(PowerConsumptionBar.COLOR_WARN);
    }
    private boolean playerUsable = false;

    @Override
    public void onReactorRecalibrate(ReactorRecalibrateEvent event) {
        try {
            ReactorElement dps1 = SegmentControllerUtils.getChamberFromElement(getManagerUsableSegmentController(), ElementKeyMap.getInfo(SHIELD_TYPE_DPS_1));
            ReactorElement dps2 = SegmentControllerUtils.getChamberFromElement(getManagerUsableSegmentController(), ElementKeyMap.getInfo(SHIELD_TYPE_DPS_2));
            if (dps1 != null && dps1.isAllValid()) {
                playerUsable = true;
            } else if (dps2 != null && dps2.isAllValid()) {
                playerUsable = true;
            } else {
                playerUsable = false;
            }
        } catch (Exception ignored){

        }
    }

    @Override
    public float getChargeRateFull() {
        return 5;
    }

    @Override
    public double getPowerConsumedPerSecondResting() {
        return 0;
    }

    @Override
    public double getPowerConsumedPerSecondCharging() {
        return getSegmentController().getMass() * 2;
    }
    public static short SHIELD_TYPE_DPS_1 = 1119;
    public static short SHIELD_TYPE_DPS_2 = 35;
    int alphaDir1;
    int alphaDir2;
    double removedPerSecond;

    @Override
    public boolean isPlayerUsable() {
        return playerUsable;
    }

    @Override
    public float getDuration() {
        ReactorElement element = SegmentControllerUtils.getChamberFromElement(getManagerUsableSegmentController(), ElementKeyMap.getInfo(SHIELD_TYPE_DPS_2));
        boolean lvl2 = element != null && element.isAllValid();
        return (lvl2) ? alphaDir2 : alphaDir1;
    }

    @Override
    public boolean executeModule() {
        ModPlayground.broadcastMessage("Executing module, onServer=" + isOnServer() + ", charge=" + this.getCharge());
        return super.executeModule();
    }

    @Override
    public boolean onExecuteServer() {
        AudioUtils.serverPlaySound("0022_item - forcefield activate", 100F, 0.5F, getAttachedPlayers());
        AudioUtils.serverPlaySound("0022_gameplay - prompt 3", 100F, 1F, getAttachedPlayers());
        return true;
    }

    @Override
    public boolean onExecuteClient() {
        return true;
    }

    @Override
    public void onDeactivateFromTime() {
        super.onDeactivateFromTime();
        if(GameClient.getClientState() != null) {
            Hud hud = GameClient.getClientState().getWorldDrawer().getGuiDrawer().getHud();
            try {
                Field f = Hud.class.getDeclaredField("backgroundCrosshairHUD");
                f.setAccessible(true);
                GUIOverlay o = (GUIOverlay) f.get(hud);
                o.setSprite(Controller.getResLoader().getSprite("crosshair-c-gui-"));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            try {
                Field f = Hud.class.getDeclaredField("powerConsumptionBar");
                f.setAccessible(true);
                PowerConsumptionBar o = (PowerConsumptionBar) f.get(hud);
                PowerConsumptionBar.COLOR.set(origConfigColor);
                PowerConsumptionBar.COLOR_WARN.set(origConfigWarnColor);
                o.onInit();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            AudioUtils.clientPlaySound("0022_item - forcefield powerdown", 10F, 1F);
        }
    }
    @SuppressWarnings("rawtypes")
    @Override
    public void onActive() {
        if(GameClient.getClientState() != null){
            ManagedUsableSegmentController<?> controller = getManagerUsableSegmentController();
            ArrayList<SegmentController> allDocks = new ArrayList<>();
            controller.railController.getDockedRecusive(allDocks);
            for (SegmentController dock : allDocks) {
                if(dock instanceof ManagedUsableSegmentController) {
                    for (ElementCollectionManager manager : SegmentControllerUtils.getAllCollectionManagers((ManagedUsableSegmentController<?>) dock)) {
                        for (Object ec : manager.getElementCollections()) {
                            if (ec instanceof FireingUnit) {
                                FireingUnit f = ((FireingUnit) ec);
                                f.setReactorReloadNeeded(f.getReactorReloadNeeded() - removedPerSecond);
                            }
                        }
                    }
                }
            }
            Hud hud = GameClient.getClientState().getWorldDrawer().getGuiDrawer().getHud();
            try {
                Field f = Hud.class.getDeclaredField("backgroundCrosshairHUD");
                f.setAccessible(true);
                GUIOverlay o = (GUIOverlay) f.get(hud);
                o.setSprite(AlphaRework.betterCrossHair);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            try {
                Field f = Hud.class.getDeclaredField("powerConsumptionBar");
                f.setAccessible(true);
                PowerConsumptionBar o = (PowerConsumptionBar) f.get(hud);
                PowerConsumptionBar.COLOR.set(0,255,120,255);
                PowerConsumptionBar.COLOR_WARN.set(0,255,255,255);
                o.onInit();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInactive() {

    }

    @Override
    public String getName() {
        return "Alpha Driver";
    }
}
