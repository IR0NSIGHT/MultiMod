package me.jakev.holoprojector;

import api.listener.fastevents.TextBoxDrawListener;
import api.utils.textures.StarLoaderTexture;
import com.bulletphysics.linearmath.Transform;
import org.apache.commons.lang3.StringUtils;
import org.schema.game.client.view.SegmentDrawer;
import org.schema.game.client.view.textbox.AbstractTextBox;
import org.schema.schine.graphicsengine.core.Controller;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.graphicsengine.forms.TransformableSubSprite;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static me.jakev.holoprojector.HoloProjectorMod.monke;
import static me.jakev.holoprojector.HoloProjectorMod.nothing;


/**
 * Created by Jake on 11/20/2020.
 * <insert description here>
 */
public class TextDrawListener implements TextBoxDrawListener {
    private final static ConcurrentHashMap<String, Sprite> imgCache = new ConcurrentHashMap<>();
    private final static ConcurrentLinkedQueue<String> downloadingImages = new ConcurrentLinkedQueue<>();
    @Override
    public void draw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox box) {

        textBoxElement.text.setFont(FontLibrary.getBlenderProHeavy31());
        if(textBoxElement.realText.contains("monke")) {
            box.getBg().setSprite(monke);
        }else{
            box.getBg().setSprite(nothing);
        }
    }

    @Override
    public void preDrawBackground(SegmentDrawer.TextBoxSeg seg, AbstractTextBox abstractTextBox) {
        for (SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement : seg.v) {
            if(textBoxElement.rawText.contains("[img]")){
                String str = StringUtils.substringBetween(textBoxElement.rawText, "[img]", "[/img]");
                Sprite image = getImage(str);
                if(image != null) {
                    Sprite.draw3D(image, new ScalableImageSubSprite[]{new ScalableImageSubSprite(0.1F, textBoxElement.worldpos)}, 1, Controller.getCamera());
                }
            }
        }
    }

    @Override
    public void preDraw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox abstractTextBox) {
    }

    @Nullable
    private static Sprite getImage(String url){
        Sprite bufferedImage = imgCache.get(url);
        if(bufferedImage != null){
            return bufferedImage;
        }else{
            fetchImage(url);
            return null;
        }
    }

    private static void fetchImage(final String url) {
        if (!downloadingImages.contains(url)) {
            new Thread() {
                @Override
                public void run() {
                    downloadingImages.add(url);
                    final BufferedImage bufferedImage = fromURL(url);
                    StarLoaderTexture.runOnGraphicsThread(new Runnable() {
                        @Override
                        public void run() {
                            Sprite sprite = StarLoaderTexture.newSprite(bufferedImage, HoloProjectorMod.mod, "holoprojector" + System.currentTimeMillis());
                            imgCache.put(url, sprite);
                        }
                    });
                    downloadingImages.remove(url);
                }
            }.start();
        }
    }
    private static BufferedImage fromURL(String u){
        BufferedImage image = null;
        try {
            URL url = new URL(u);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "NING/1.0");
            InputStream stream = urlConnection.getInputStream();
            image = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
class ScalableImageSubSprite implements TransformableSubSprite {
    private float scale;
    private Transform transform;

    ScalableImageSubSprite(float scale, Transform transform){
        this.scale = scale;
        this.transform = transform;
    }

    @Override
    public float getScale(long l) {
        return scale;
    }

    @Override
    public int getSubSprite(Sprite sprite) {
        return 0;
    }

    @Override
    public boolean canDraw() {
        return true;
    }

    @Override
    public Transform getWorldTransform() {
        return transform;
    }
}