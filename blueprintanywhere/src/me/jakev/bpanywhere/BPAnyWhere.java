package me.jakev.bpanywhere;

import api.listener.Listener;
import api.listener.events.player.PlayerHandleMetaBlueprintGiveEvent;
import api.mod.StarLoader;
import api.mod.StarMod;

/**
 * Created by Jake on 10/21/2020.
 * <insert description here>
 */
public class BPAnyWhere extends StarMod {
    public static void main(String[] args) { }

    @Override
    public void onGameStart() {
        setModName("BYAnyWhere");
        setModVersion("0.1");
        setModDescription("Allows users to get blueprints when not near a shop");
        setModAuthor("JakeV");
        setModSMVersion("0.202.101");
    }

    @Override
    public void onEnable() {
        StarLoader.registerListener(PlayerHandleMetaBlueprintGiveEvent.class, new Listener<PlayerHandleMetaBlueprintGiveEvent>() {
            @Override
            public void onEvent(PlayerHandleMetaBlueprintGiveEvent event) {
                event.setAllowed(true);
            }
        }, this);
    }
}
