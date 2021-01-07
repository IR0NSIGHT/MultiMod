package me.jakev.lowmade;

import api.mod.StarMod;
import api.mod.config.FileConfiguration;

public class LowMade extends StarMod {
    public static LowMade inst;

    public static void main(String[] args) { }
    @Override
    public void onGameStart() {
        inst = this;
        setModName("LowMade");
        setModVersion("1.2");
        setModDescription("Loads far away textures in 1x1 res");
        setModAuthor("JakeV");
        setModSMVersion("0.202.108");
    }
    public static int resolution = 1;
    @Override
    public void onEnable() {
        new LowMadeListener();
        FileConfiguration config = getConfig("config.txt");
        resolution = config.getConfigurableInt("resolution", 1);
        config.saveConfig();
    }
}