package me.jakev.alpharework;

import api.ModPlayground;
import api.common.GameClient;
import api.listener.events.systems.ReactorRecalibrateEvent;
import api.utils.addon.SimpleAddOn;
import api.utils.game.SegmentControllerUtils;
import api.utils.sound.AudioUtils;
import org.schema.common.util.linAlg.Vector4i;
import org.schema.game.client.view.gui.shiphud.newhud.FillableBar;
import org.schema.game.client.view.gui.shiphud.newhud.Hud;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.elements.power.reactor.tree.ReactorElement;
import org.schema.game.common.data.element.ElementKeyMap;

import javax.vecmath.Vector4f;
import java.lang.reflect.Field;

/**
 * Created by Jake on 10/23/2020.
 * <insert description here>
 */
public class ShieldHardenAddOn extends SimpleAddOn {
    public static final String UID_NAME = "ShieldHardenAddOn";
    public ShieldHardenAddOn(ManagerContainer<?> var1, AlphaRework mod) {
        super(var1, ElementKeyMap.SHIELD_CAP_ID, mod, UID_NAME);
        alphaDir1 = AlphaRework.config.getConfigurableInt("alpha_duration_1", 4);
        alphaDir2 = AlphaRework.config.getConfigurableInt("alpha_duration_2", 7);
        onReactorRecalibrate(null);
    }
    private boolean playerUsable = false;

    @Override
    public void onReactorRecalibrate(ReactorRecalibrateEvent event) {
        try {
            ReactorElement alpha1 = SegmentControllerUtils.getChamberFromElement(getManagerUsableSegmentController(), ElementKeyMap.getInfo(SHIELD_TYPE_ALPHA_1));
            ReactorElement alpha2 = SegmentControllerUtils.getChamberFromElement(getManagerUsableSegmentController(), ElementKeyMap.getInfo(SHIELD_TYPE_ALPHA_2));
            if (alpha1 != null && alpha1.isAllValid()) {
                playerUsable = true;
            } else if (alpha2 != null && alpha2.isAllValid()) {
                playerUsable = true;
            } else {
                playerUsable = false;
            }
        } catch (Exception ignored){

        }
    }

    @Override
    public float getChargeRateFull() {
        return AlphaRework.config.getConfigurableInt("harden_charge_time", 20);
    }

    @Override
    public double getPowerConsumedPerSecondResting() {
        return 0;
    }

    @Override
    public double getPowerConsumedPerSecondCharging() {
        return getSegmentController().getMass() * AlphaRework.config.getConfigurableFloat("harden_power_per_mass", 2F);
    }
    public static short SHIELD_TYPE_ALPHA_1 = 1035;
    public static short SHIELD_TYPE_ALPHA_2 = 34;
    int alphaDir1;
    int alphaDir2;

    @Override
    public boolean isPlayerUsable() {
        return playerUsable;
//        return true;
    }

    @Override
    public float getDuration() {
        ReactorElement element = SegmentControllerUtils.getChamberFromElement(getManagerUsableSegmentController(), ElementKeyMap.getInfo(SHIELD_TYPE_ALPHA_2));
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
        AudioUtils.serverPlaySound("0022_spaceship user - turbo boost large", 10F, 1F, getAttachedPlayers());
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
            try {
                Hud hud = GameClient.getClientState().getWorldDrawer().getGuiDrawer().getHud();
                Field f = Hud.class.getDeclaredField("shieldBarRight");
                f.setAccessible(true);
                FillableBar o = (FillableBar) f.get(hud);
                Vector4f color = o.getColor();
                Vector4i cc = o.getConfigColor();
                color.x = cc.x/256F;
                color.y = cc.y/255F;
                color.z = cc.z/255F;
                AudioUtils.clientPlaySound("0022_gameplay - power down big", 10F, 1F);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActive() {
        if(GameClient.getClientState() != null){
            Hud hud = GameClient.getClientState().getWorldDrawer().getGuiDrawer().getHud();
            try {
                Field f = Hud.class.getDeclaredField("shieldBarRight");
                f.setAccessible(true);
                FillableBar o = (FillableBar) f.get(hud);
                Vector4f color = o.getColor();
                color.x = 0;
                color.y = 0.4F;
                color.z = 1;
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
        return "Shield Hardener";
    }

}
