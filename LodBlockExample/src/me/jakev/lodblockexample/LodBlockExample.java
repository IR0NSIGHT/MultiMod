package me.jakev.lodblockexample;

import api.config.BlockConfig;
import api.mod.StarMod;
import api.utils.StarRunnable;
import org.schema.game.client.view.GameResourceLoader;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.ResourceException;

import java.io.IOException;

/**
 * Created by Jake on 1/8/2021.
 * <insert description here>
 */
public class LodBlockExample extends StarMod {

    public static LodBlockExample mod;


    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        ElementInformation chair = BlockConfig.newElement(this, "MyEpicNewBlock", new short[]{43});
        BlockConfig.setBasicInfo(chair, "iFunny spike", 100, 0.1F, true, true, 91);
        BlockConfig.assignLod(chair, this, "Spike", null);
        BlockConfig.add(chair);
        BlockConfig.addRefineryRecipe(ElementKeyMap.capsuleRecipe,
                new FactoryResource[]{new FactoryResource(1, ElementKeyMap.SHIPYARD_COMPUTER)},
                new FactoryResource[]{new FactoryResource(2, ElementKeyMap.COCKPIT_ID)}
                );
    }


    @Override
    public void onEnable() {
        System.err.println("Called OnEnable of LodBlockExample");
        new StarRunnable(true){
            @Override
            public void run() {
                try {
                    System.err.println("Loading lodblockexam");
                    GameResourceLoader resLoader = (GameResourceLoader) Controller.getResLoader();
                    resLoader.getMeshLoader().loadModMesh(LodBlockExample.this, "Spike", getJarResource("me/jakev/lodblockexample/Spike.zip"), "Console");
                } catch (ResourceException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.runLater(this, 0);
    }
}