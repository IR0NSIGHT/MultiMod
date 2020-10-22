package me.jakev.corecommands;

import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.player.PlayerChatEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.game.PlayerUtils;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.server.data.PlayerNotFountException;

public class CoreCommands extends StarMod {
    public static void main(String[] args) { }

    @Override
    public void onGameStart() {
        setModName("CoreCommands");
        setModVersion("0.1");
        setModDescription("!core + !balance, !withdraw, !deposit, commands");
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
                if(txt.toLowerCase().startsWith("!core")) {
                    p.sendInventoryModification(p.getInventory().incExistingOrNextFreeSlot(ElementKeyMap.CORE_ID, 1), -9223372036854775808L);
                    p.sendInventoryModification(p.getInventory().incExistingOrNextFreeSlot(ElementKeyMap.REACTOR_MAIN, 1), -9223372036854775808L);
                    p.sendInventoryModification(p.getInventory().incExistingOrNextFreeSlot(ElementKeyMap.THRUSTER_ID, 1), -9223372036854775808L);
                    PlayerUtils.sendMessage(p, "There you go");
                }
                //!deposit
                //!balance
                //!withdraw
            }
        }, this);
    }
    public static PlayerState getPlayerFromName(String name){
        try {
            return GameServer.getServerState().getPlayerFromNameIgnoreCase(name);
        } catch (PlayerNotFountException e) {
            e.printStackTrace();
        }
        return null;
    }
}
