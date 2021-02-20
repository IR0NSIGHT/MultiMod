package me.jakev.lowmade;

import api.DebugFile;
import api.listener.Listener;
import api.listener.events.draw.CubeTexturePostLoadEvent;
import api.mod.StarLoader;
import api.utils.textures.TextureSwapper;
import org.schema.game.client.view.GameResourceLoader;
import org.schema.schine.graphicsengine.texture.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Jake on 10/10/2020.
 * <insert description here>
 */
public class LowMadeListener {
    public LowMadeListener(){
        StarLoader.registerListener(CubeTexturePostLoadEvent.class, new Listener<CubeTexturePostLoadEvent>() {
            @Override
            public void onEvent(CubeTexturePostLoadEvent event) {
                DebugFile.log("Fired cubetexturepostload", LowMade.inst);
                Texture[] texArray = event.getTexArray();
                //texArray == GameResourceLoader.cubeTextures ||
                if (texArray == GameResourceLoader.cubeTexturesLow) {
                    for (int i = 0; i < 8; i++) {
                        Image timg = TextureSwapper.getImageFromTexture(texArray[i]);
                        timg = timg.getScaledInstance(16,16, Image.SCALE_AREA_AVERAGING);
                        BufferedImage img = new BufferedImage(16,16, BufferedImage.TYPE_INT_ARGB);
                        Graphics g = img.getGraphics();
                        g.drawImage(timg, 0, 0, null);
                        Texture textureSheet = texArray[i];
                        if (textureSheet != null) {
                            String name = textureSheet.getName();
                            Texture tex = TextureSwapper.getTextureFromImage(img, name, false, false);
                            texArray[i] = tex;
                        } else {
                            texArray[i] = TextureSwapper.getTextureFromImage(img, "", false, false);
                        }
                    }
                }
                //texArray == GameResourceLoader.cubeNormalTextures
                if (texArray == GameResourceLoader.cubeNormalTexturesLow) {
                    for (int i = 0; i < 8; i++) {
                        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                        Graphics graphics = img.getGraphics();
                        graphics.setColor(new Color(128,128,255, 100));
                        graphics.fillRect(0, 0, 1, 1);
                        Texture textureSheet = texArray[i];
                        if (textureSheet != null) {
                            String name = textureSheet.getName();
                            Texture tex = TextureSwapper.getTextureFromImage(img, name, false, false);
                            texArray[i] = tex;
                        } else {
                            texArray[i] = TextureSwapper.getTextureFromImage(img, "", false, false);
                        }
                    }
                }
            }
        }, LowMade.inst);
    }
}
