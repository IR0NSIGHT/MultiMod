package me.jakev.lodblockexample;

import api.config.BlockConfig;
import api.mod.StarMod;
import api.utils.StarRunnable;
import api.utils.textures.StarLoaderTexture;
import org.schema.game.client.view.GameResourceLoader;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.resource.ResourceLoader;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Created by Jake on 1/8/2021.
 * <insert description here>
 */
public class LodBlockExample extends StarMod {

    public static LodBlockExample mod;


    StarLoaderTexture icon;
    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        ElementInformation chair = BlockConfig.newElement(this, "le toilet funny", new short[]{12});
        BlockConfig.setBasicInfo(chair, "iFunny toilet", 100, 0.1F, true, true, icon.getTextureId());
        BlockConfig.assignLod(chair, this, "toilet", null);
        BlockConfig.add(chair);
        BlockConfig.addRecipe(chair, 1, 5, new FactoryResource(1, (short) 1));
        for (ElementInformation elem : ElementKeyMap.infoArray) {
            if(elem != null){
                elem.deprecated = false;
            }
        }
    }

    @Override
    public void onResourceLoad(ResourceLoader loader) {

    }

    @Override
    public void onEnable() {
        try {
            icon = StarLoaderTexture.newIconTexture(ImageIO.read(getJarResource("me/jakev/lodblockexample/icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("Called OnEnable of LodBlockExample");
        new StarRunnable(true){
            @Override
            public void run() {
                try {
                    System.err.println("Loading lodblockexam");
                    GameResourceLoader resLoader = (GameResourceLoader) Controller.getResLoader();
                    resLoader.getMeshLoader().loadModMesh(LodBlockExample.this, "toilet", getJarResource("me/jakev/lodblockexample/toilet.zip"), "convexhull");
                } catch (ResourceException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.runLater(this, 0);
    }
}