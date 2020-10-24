package me.jakev.shipcreditrecycler;

import api.listener.Listener;
import api.listener.events.player.PlayerChatEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import org.schema.game.client.data.PlayerControllable;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;

import static api.common.GameCommon.getPlayerFromName;

/**
 * Created by Jake on 10/21/2020.
 * <insert description here>
 */
public class ShipCreditRecycler extends StarMod {
    public static void main(String[] args) { }

    @Override
    public void onGameStart() {
        setModName("ShipCreditRecycler");
        setModVersion("0.1");
        setModDescription("Allows users to get blueprints when not near a shop");
        setModAuthor("JakeV");
        setModSMVersion("0.202.101");
        setServerSide(true);
    }

    @Override
    public void onEnable() {
        StarLoader.registerListener(PlayerChatEvent.class, new Listener<PlayerChatEvent>() {
            @Override
            public void onEvent(PlayerChatEvent event) {
                String txt = event.getText();
                PlayerState p = getPlayerFromName(event.getMessage().sender);
                if(event.isServer()) {
                    if (txt.toLowerCase().startsWith("!recycle")) {
                        PlayerControllable inside = PlayerUtils.getCurrentControl(p);
                        if (inside instanceof SegmentController) {
                            SegmentController root = ((SegmentController) inside).railController.getRoot();
                            int creds = (int) (root.railController.calculateRailMassIncludingSelf() * 1000);
                            root.setMarkedForDeletePermanentIncludingDocks(true);
                            p.modCreditsServer(creds);
                            PlayerUtils.sendMessage(p, "You have recieved: " + creds + " credits.");
                        } else {
                            PlayerUtils.sendMessage(p, "You are not in a ship.");
                        }
                    }
                }

                //!deposit
                //!balance
                //!withdraw
            }
        }, this);
    }
}
