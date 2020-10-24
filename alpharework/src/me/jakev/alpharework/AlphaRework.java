package me.jakev.alpharework;

import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.register.RegisterAddonsEvent;
import api.listener.events.systems.ShieldHitEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.utils.game.SegmentControllerUtils;
import api.utils.registry.UniversalRegistry;
import api.utils.sound.AudioUtils;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;

public class AlphaRework extends StarMod {
    public static void main(String[] args) {

    }

    public static FileConfiguration config;
    public static AlphaRework alphaRework;

    @Override
    public void onGameStart() {
        setModName("AlphaRework");
        setModVersion("0.1");
        setModDescription("Reworks High/Low damage chambers.");
        setModAuthor("JakeV");
        setModSMVersion("0.202.104");
    }


    @Override
    public void onPreEnableServer() {
        UniversalRegistry.registerURV(UniversalRegistry.RegistryType.PLAYER_USABLE_ID, this, ShieldHardenAddOn.UID_NAME);
    }

    @Override
    public void onEnable() {
        alphaRework = this;
        super.onEnable();
        config = getConfig("config");

        StarLoader.registerListener(RegisterAddonsEvent.class, new Listener<RegisterAddonsEvent>() {
            @Override
            public void onEvent(RegisterAddonsEvent event) {
                event.addModule(new ShieldHardenAddOn(event.getContainer(), alphaRework));
            }
        }, alphaRework);
        StarLoader.registerListener(ShieldHitEvent.class, new Listener<ShieldHitEvent>() {
            @Override
            public void onEvent(ShieldHitEvent event) {
                //Disable HD/LD
                event.setHighDamage(false);
                event.setLowDamage(false);
                //

                SegmentController controller = event.getHitController();
                if (controller instanceof ManagedUsableSegmentController) {
                    ManagedUsableSegmentController<?> ms = (ManagedUsableSegmentController<?>) controller;
                    ShieldHardenAddOn addOn = SegmentControllerUtils.getAddon(ms, ShieldHardenAddOn.class);
                    if (addOn != null && addOn.isActive()) {
                        if (GameServer.getServerState() != null) {
                            AudioUtils.serverPlaySound("0022_spaceship user - collision with metal small", 1F, 1F, SegmentControllerUtils.getAttachedPlayers(controller));
                        }
                        event.setDamage(event.getDamage() * 0.25F);

                    }
                }
            }
        }, alphaRework);
    }

    @Override
    public void onDisable() {
        config.saveConfig();
    }
}
