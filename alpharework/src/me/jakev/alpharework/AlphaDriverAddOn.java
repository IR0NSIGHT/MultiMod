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
        alphaDir2 = AlphaRework.config.getConfigurableInt("overdrive_duration_2", 6);
        removedPerSecond = AlphaRework.config.getConfigurableFloat("driver_speed", 0.11F);
        onReactorRecalibrate(null);
        origConfigColor.set(PowerConsumptionBar.COLOR);
        origConfigWarnColor.set(PowerConsumptionBar.COLOR_WARN);
    }

    private boolean playerUsable = false;

    @Override
    public PowerConsumerCategory getPowerConsumerCategory() {
        return super.getPowerConsumerCategory();
    }

    @Override
    public void onReactorRecalibrate(ReactorRecalibrateEvent event) {
        try {
            if(!(getSegmentController() instanceof ManagedUsableSegmentController)){
                return;
            }
            System.err.println("FIRED RECALIBRATE for: " + getSegmentController().getName());
            ReactorElement dps1 = SegmentControllerUtils.getChamberFromElement(getManagerUsableSegmentController(), ElementKeyMap.getInfo(SHIELD_TYPE_DPS_1));
            System.err.println("DPS1: " + dps1);
            ReactorElement dps2 = SegmentControllerUtils.getChamberFromElement(getManagerUsableSegmentController(), ElementKeyMap.getInfo(SHIELD_TYPE_DPS_2));
            System.err.println("DPS2: " + dps2);
            if (dps1 != null && dps1.isAllValid()) {
                playerUsable = true;
            } else if (dps2 != null && dps2.isAllValid()) {
                playerUsable = true;
            } else {
                playerUsable = false;
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    @Override
    public float getChargeRateFull() {
        return AlphaRework.config.getConfigurableInt("alpha_charge_time", 20);
    }

    @Override
    public double getPowerConsumedPerSecondResting() {
        return 1;
    }

    @Override
    public double getPowerConsumedPerSecondCharging() {
        return getSegmentController().getMass() * AlphaRework.config.getConfigurableFloat("alpha_power_cons_per_mass", 2F);
    }

    public static short SHIELD_TYPE_DPS_1 = 1119;
    public static short SHIELD_TYPE_DPS_2 = 35;
    int alphaDir1;
    int alphaDir2;
    double removedPerSecond;

    @Override
    public boolean isPlayerUsable() {
//        return true;
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
        return super.executeModule();
    }

    @Override
    public boolean onExecuteServer() {
        ModPlayground.broadcastMessage("EXE ON SERVER");
        AudioUtils.serverPlaySound("0022_item - forcefield activate", 100F, 0.5F, getAttachedPlayers());
        AudioUtils.serverPlaySound("0022_gameplay - prompt 3", 100F, 1F, getAttachedPlayers());
        return true;
    }

    @Override
    public boolean onExecuteClient() {
        ModPlayground.broadcastMessage("EXE ON CLIENT");
        return true;
    }

    @Override
    public void onDeactivateFromTime() {
        super.onDeactivateFromTime();
        if (GameClient.getClientState() != null) {
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
        if (GameClient.getClientState() != null) {
            ManagedUsableSegmentController<?> controller = getManagerUsableSegmentController();
            ArrayList<SegmentController> allDocks = new ArrayList<>();
            controller.railController.getDockedRecusive(allDocks);
            for (SegmentController dock : allDocks) {
                if (dock instanceof ManagedUsableSegmentController) {
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
                PowerConsumptionBar.COLOR.set(0, 255, 120, 255);
                PowerConsumptionBar.COLOR_WARN.set(0, 255, 255, 255);
                o.onInit();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    int ticks = 0;
    @Override
    public void onInactive() {
        //Recalibrate playerUsable on occasion
        if(ticks++%100 == 0){
            onReactorRecalibrate(null);
        }
    }

    @Override
    public String getName() {
        return "Alpha Driver";
    }
}
