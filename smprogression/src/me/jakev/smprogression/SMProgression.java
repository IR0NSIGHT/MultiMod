package me.jakev.smprogression;

import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.controller.asteroid.AsteroidGenerateEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.registry.UniversalRegistry;
import me.jakev.smprogression.asteroids.MetalAsteroidFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;

import java.awt.image.BufferedImage;

/**
 * Created by Jake on 11/17/2020.
 * <insert description here>
 */
public class SMProgression extends StarMod {
    public static void main(String[] args) {

    }
    @Override
    public void onGameStart() {
        setModName("SMProgression");
        setModVersion("0.0.001");
        setModAuthor("Jake");
        setModDescription("The mod");
    }
    ImmutablePair<ElementInformation, Integer> metalOre;
    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        //Register metal ore
        BufferedImage image = new BufferedImage(64,64, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().fillRect(0,0,10,30);
        metalOre = config.newOre(this, "MetalOre", (short) 188, image);
        config.add(metalOre.left);

        //Set terrain_rock_white to our own custom block
        ElementInformation info = ElementKeyMap.getInfo(ElementKeyMap.TERRAIN_ROCK_WHITE);
        info.name = "Space Rock";
        info.fullName = "Space Rock";



    }

    @Override
    public void onPreEnableServer() {
        long metal_ore = UniversalRegistry.registerURV(UniversalRegistry.RegistryType.ORE, this, "MetalOre");
        System.err.println("METAL ORE URV=" + metal_ore);
    }

    @Override
    public void onEnable() {
        StarLoader.registerListener(AsteroidGenerateEvent.class, new Listener<AsteroidGenerateEvent>() {
            @Override
            public void onEvent(AsteroidGenerateEvent event) {
                event.setWorldCreatorFloatingRockFactory(new MetalAsteroidFactory(1L, metalOre.left.id));
            }
        }, this);
    }
}
