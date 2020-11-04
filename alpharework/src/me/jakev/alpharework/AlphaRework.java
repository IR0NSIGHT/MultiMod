package me.jakev.alpharework;

import api.common.GameClient;
import api.common.GameServer;
import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.draw.CubeTexturePostLoadEvent;
import api.listener.events.player.PlayerAcquireTargetEvent;
import api.listener.events.register.RegisterAddonsEvent;
import api.listener.events.systems.ShieldHitEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.utils.game.SegmentControllerUtils;
import api.utils.registry.UniversalRegistry;
import api.utils.sound.AudioUtils;
import api.utils.textures.StarLoaderTexture;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.schine.graphicsengine.forms.Sprite;

import javax.imageio.ImageIO;
import java.io.IOException;

public class AlphaRework extends StarMod {
    public static void main(String[] args) {

    }

    public static FileConfiguration config;
    public static AlphaRework alphaRework;

    @Override
    public void onGameStart() {
        setModName("AlphaRework");
        setModVersion("0.2");
        setModDescription("Reworks High/Low damage chambers.");
        setModAuthor("JakeV");
        setModSMVersion("0.202.104");
        setSMDResourceId(8182);
    }

    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        ElementKeyMap.getInfo(1119).chamberMutuallyExclusive.clear();
        ElementKeyMap.getInfo(1035).chamberMutuallyExclusive.clear();
        ElementKeyMap.getInfo(34).chamberMutuallyExclusive.clear();
        ElementKeyMap.getInfo(35).chamberMutuallyExclusive.clear();
    }

    @Override
    public void onPreEnableServer() {
        UniversalRegistry.registerURV(UniversalRegistry.RegistryType.PLAYER_USABLE_ID, this, ShieldHardenAddOn.UID_NAME);
        UniversalRegistry.registerURV(UniversalRegistry.RegistryType.PLAYER_USABLE_ID, this, AlphaDriverAddOn.UID_NAME);
    }

    public static Sprite betterCrossHair;

    @Override
    public void onEnable() {
        alphaRework = this;
        StarLoader.registerListener(CubeTexturePostLoadEvent.class, new Listener<CubeTexturePostLoadEvent>() {
            @Override
            public void onEvent(CubeTexturePostLoadEvent event) {
                try {
                    betterCrossHair = StarLoaderTexture.newSprite(ImageIO.read(AlphaRework.class.getResourceAsStream("sprites/crosshair.png")), alphaRework, "mycrosshair");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, this);


        super.onEnable();
        config = getConfig("config");

        StarLoader.registerListener(RegisterAddonsEvent.class, new Listener<RegisterAddonsEvent>() {
            @Override
            public void onEvent(RegisterAddonsEvent event) {
                event.addModule(new ShieldHardenAddOn(event.getContainer(), alphaRework));
                event.addModule(new AlphaDriverAddOn(event.getContainer(), alphaRework));
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
                        event.setDamage(event.getDamage() * 0.20F);

                    }
                }
            }
        }, alphaRework);

        StarLoader.registerListener(PlayerAcquireTargetEvent.class, new Listener<PlayerAcquireTargetEvent>() {
            @Override
            public void onEvent(PlayerAcquireTargetEvent event) {
                if(GameClient.getClientState() != null){
                    AudioUtils.clientPlaySound("0022_spaceship user - locked on target successful beep", 1F, 1F);
                }
            }
        }, this);

    }

    @Override
    public void onDisable() {
        config.saveConfig();
    }
}
