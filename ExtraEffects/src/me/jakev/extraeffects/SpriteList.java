package me.jakev.extraeffects;

import api.utils.particle.ModParticleUtil;

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
    MULTISPARK
    ;

    private int sprite;
    private String name;

    public static void init(ExtraEffects mod, ModParticleUtil.LoadEvent event) {
        for (SpriteList value : SpriteList.values()) {
            String name = value.name().toLowerCase();
            value.name = name;
            try {
                value.sprite = event.addParticleSprite(ImageIO.read(mod.getJarResource("me/jakev/extraeffects/res/" + name + ".png")), mod);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getSprite() {
        return sprite;
    }

    public String getName() {
        return "extraeffects_" + name;
    }
}
