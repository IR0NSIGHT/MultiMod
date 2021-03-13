package me.jakev.starfm;

import api.listener.events.controller.ServerInitializeEvent;
import api.mod.StarMod;
import me.jakev.starfm.cp.FactionClaimSweeper;
import me.jakev.starfm.cp.ShipCPSweeper;
import me.jakev.starfm.cp.ShipCPTracker;

/**
 * Created by Jake on 3/4/2021.
 */
public class StarFM extends StarMod {

    @Override
    public void onEnable() {
    }

    @Override
    public void onServerCreated(ServerInitializeEvent event) {
        //=== Initialize Control Point Sytsems ===
        ShipCPTracker.init(this);
        FactionClaimSweeper factionClaimSweeper = new FactionClaimSweeper(this);
        ShipCPSweeper shipCPSweeper = new ShipCPSweeper(this);

        //=== Initialize Invulnerability Systems ===
    }
}
