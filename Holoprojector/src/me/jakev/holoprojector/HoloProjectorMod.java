package me.jakev.holoprojector;

import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.draw.CubeTexturePostLoadEvent;
import api.listener.fastevents.FastListenerCommon;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.textures.StarLoaderTexture;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.schine.graphicsengine.forms.Sprite;

import javax.imageio.ImageIO;
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
    }
    public static Sprite monke;
    public static Sprite nothing;
    @Override
    public void onEnable() {
        mod = this;
        ListenerCommon.init(this);
        FastListenerCommon.getTextBoxListeners().add(new TextDrawListener());
        StarLoader.registerListener(CubeTexturePostLoadEvent.class, new Listener<CubeTexturePostLoadEvent>() {
            @Override
            public void onEvent(CubeTexturePostLoadEvent event) {
                try {
                    monke = StarLoaderTexture.newSprite(ImageIO.read(HoloProjectorMod.class.getResourceAsStream("sprites/monke.png")), HoloProjectorMod.this, "holoprojector");
                    nothing = StarLoaderTexture.newSprite(ImageIO.read(HoloProjectorMod.class.getResourceAsStream("sprites/nothing.png")), HoloProjectorMod.this, "nothing");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }
}
