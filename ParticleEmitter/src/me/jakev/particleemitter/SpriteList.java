package me.jakev.particleemitter;

import api.utils.textures.StarLoaderTexture;
import org.schema.schine.graphicsengine.forms.Sprite;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Created by Jake on 12/7/2020.
 * <insert description here>
 */
public enum SpriteList {
    SPARK,
    SMOKE,
    FLASH,
    ENERGY,
    BALL,
    CLOUD,
    FIRE,
    FLOWER,
    THEONE,
    RING,
    FIREFLASH,
    NOTHING,
    FIRESPARK,
    BIGSMOKE,

    ;

    private Sprite sprite;
    private String name;

    public static void init(ParticleEmitterMod mod) {
        StarLoaderTexture.runOnGraphicsThread(() -> {
            synchronized (SpriteList.class) {
                for (SpriteList value : SpriteList.values()) {
                    String name = value.name().toLowerCase();
                    value.name = name;
                    try {
                        value.sprite = StarLoaderTexture.newSprite(ImageIO.read(ParticleEmitterMod.class.getResourceAsStream("res/" + name + ".png")), mod, "particleemitter_" + name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public Sprite getSprite() {
        return sprite;
    }

    public String getName() {
        return "particleemitter_" + name;
    }
}
