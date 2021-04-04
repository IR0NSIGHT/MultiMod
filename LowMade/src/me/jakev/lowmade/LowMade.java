package me.jakev.lowmade;

import api.listener.Listener;
import api.listener.events.input.KeyPressEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import org.schema.game.client.data.GameClientState;

public class LowMade extends StarMod {
    public static LowMade inst;
    public static int resolution = 1;
    @Override
    public void onEnable() {
        System.err.println(" === Enabling LowMade ===");
        new LowMadeListener();
        FileConfiguration config = getConfig("config.txt");
        resolution = config.getConfigurableInt("resolution", 1);
        config.saveConfig();
        StarLoader.registerListener(KeyPressEvent.class, new Listener<KeyPressEvent>() {
            @Override
            public void onEvent(KeyPressEvent event) {
                if(event.getChar() == 'j'){
                    GameClientState.instance.getGameState().getNetworkObject().serverShutdown.set(1F, true);
                    System.err.println("PRESSED J");
                }
            }
        }, this);
    }
}