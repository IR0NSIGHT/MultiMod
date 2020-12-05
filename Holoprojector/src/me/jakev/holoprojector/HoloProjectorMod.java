package me.jakev.holoprojector;

import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.draw.CubeTexturePostLoadEvent;
import api.listener.fastevents.FastListenerCommon;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.textures.StarLoaderTexture;
import org.schema.game.client.view.GameResourceLoader;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.forms.Sprite;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import java.io.IOException;

/**
 * Created by Jake on 11/20/2020.
 * <insert description here>
 */
public class HoloProjectorMod extends StarMod {
    public static void main(String[] args) {

    }

    public static HoloProjectorMod mod;

    @Override
    public void onGameStart() {
        setModName("HoloProjector");
        setModVersion("1.0");
        setModAuthor("JakeV");
        setModDescription("Invisible display module block");
    }

    public static ElementInformation holoProjector;

    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        holoProjector = config.newElement(this, "Holoprojector", new short[]{194});
        holoProjector.blended = true;
        holoProjector.drawOnlyInBuildMode = true;
        BlockConfig.setBasicInfo(holoProjector, "Invisible display module", 100, 0.1F, true, true, 76);
        config.add(holoProjector);

        ElementInformation chair = config.newElement(this, "le chair", new short[]{195});
        BlockConfig.setBasicInfo(chair, "le chair", 100, 0.1F, true, true, 78);
        config.assignLod(chair, this, "displayscreen", null);
        config.add(chair);
    }

    public static Sprite monke;
    public static Sprite nothing;

    @Override
    public void onEnable() {
        GameResourceLoader resLoader = (GameResourceLoader) Controller.getResLoader();
        StarLoaderTexture.runOnGraphicsThread(new Runnable() {
            @Override
            public void run() {
                try {
//                resLoader.getMeshLoader().loadModMesh(HoloProjectorMod.this, "DisplayScreen", HoloProjectorMod.class.getResourceAsStream("DisplayScreen.zip"), null);
                    resLoader.getMeshLoader().loadModMesh(HoloProjectorMod.this, "displayscreen", HoloProjectorMod.class.getResourceAsStream("displayscreen.zip"), null);
                    Mesh displayscreen = resLoader.getMeshLoader().getModMesh(HoloProjectorMod.this, "displayscreen");
                    displayscreen.setInitialScale(new Vector3f(0.4F,0.4F,0.4F));
//                    displayscreen.setRot(new Vector3f(1,1,0)); // Correct scale
                    displayscreen.setRot(new Vector3f(0,0,0));
                    displayscreen.setFirstDraw(true);

                } catch (ResourceException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mod = this;
        ListenerCommon.init(this);
        FastListenerCommon.textBoxListeners.add(new TextDrawListener());
        StarLoader.registerListener(CubeTexturePostLoadEvent.class, new Listener<CubeTexturePostLoadEvent>() {
            @Override
            public void onEvent(CubeTexturePostLoadEvent event) {
                try {
                    monke = StarLoaderTexture.newSprite(ImageIO.read(HoloProjectorMod.class.getResourceAsStream("sprites/monke.png")), HoloProjectorMod.this, "holoprojector");
                    nothing = StarLoaderTexture.newSprite(ImageIO.read(HoloProjectorMod.class.getResourceAsStream("sprites/nothing.png")), HoloProjectorMod.this, "nothing");
                    StarLoaderTexture.newSprite(ImageIO.read(HoloProjectorMod.class.getResourceAsStream("sprites/spark.png")), HoloProjectorMod.this, "hpspark");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }
}
