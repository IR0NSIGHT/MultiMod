package me.jakev.lowmade;

import api.mod.StarMod;
import api.mod.config.FileConfiguration;

public class LowMade extends StarMod {
    public static LowMade inst;
    public static int resolution = 1;
    @Override
    public void onEnable() {
        new LowMadeListener();
        FileConfiguration config = getConfig("config.txt");
        resolution = config.getConfigurableInt("resolution", 1);
        config.saveConfig();
    }
}