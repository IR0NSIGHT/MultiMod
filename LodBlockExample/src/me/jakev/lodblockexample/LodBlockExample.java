package me.jakev.lodblockexample;

import api.config.BlockConfig;
import api.mod.StarMod;
import api.utils.textures.StarLoaderTexture;
import org.schema.game.client.view.GameResourceLoader;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Mesh;

import java.io.IOException;

/**
 * Created by Jake on 1/8/2021.
 * <insert description here>
 */
public class LodBlockExample extends StarMod {
    public static void main(String[] args) {

    }

    public static LodBlockExample mod;

    @Override
    public void onGameStart() {
        setModName("LodBlockExample");
        setModVersion("1.0");
        setModAuthor("JakeV");
        setModDescription("Invisible display module block");
    }

    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        ElementInformation chair = config.newElement(this, "le chair", new short[]{195});
        BlockConfig.setBasicInfo(chair, "le chair", 100, 0.1F, true, true, 78);
        config.assignLod(chair, this, "cube", null);
        config.add(chair);
    }


    @Override
    public void onEnable() {
        StarLoaderTexture.runOnGraphicsThread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.err.println("Loading lodblockexam");
                    GameResourceLoader resLoader = (GameResourceLoader) Controller.getResLoader();
                    resLoader.getMeshLoader().loadModMesh(LodBlockExample.this, "cube", LodBlockExample.class.getResourceAsStream("cube.zip"), null);
                    Mesh displayscreen = resLoader.getMeshLoader().getModMesh(LodBlockExample.this, "cube");
                } catch (ResourceException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mod = this;
    }
}