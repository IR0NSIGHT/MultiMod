package me.jakev.smworldborder;

import api.common.GameServer;
import api.mod.StarMod;
import api.mod.config.FileConfiguration;
import api.utils.StarRunnable;
import api.utils.VarUtil;
import api.utils.game.PlayerUtils;
import api.utils.sound.AudioUtils;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.data.PlayerControllable;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.data.player.PlayerState;

/**
 * Created by Jake on 10/21/2020.
 * <insert description here>
 */
public class SMWorldBorder extends StarMod {
    public static void main(String[] args) { }
    public static boolean tooFar(SegmentController controller){
        Vector3i system = controller.getSystem(new Vector3i());
        float dist = system.lengthSquared();
        return dist > systemRadius*systemRadius;
    }
    public static int systemRadius = 2;
    @Override
    public void onGameStart() {
        setModName("SMWorldBorder");
        setModVersion("0.1");
        setModDescription("Limits area of play");
        setModAuthor("JakeV");
        setModSMVersion("0.202.101");
        setServerSide(true);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig("config");
        systemRadius = config.getConfigurableInt("AllowedRadius", 20);
        config.saveConfig();

        new StarRunnable(){
            @Override
            public void run() {
                if(GameServer.getServerState() != null){
                    for (final PlayerState player : GameServer.getServerState().getPlayerStatesByName().values()) {
                        PlayerControllable currentControl = PlayerUtils.getCurrentControl(player);
                        if(currentControl instanceof SegmentController){
                            if(tooFar((SegmentController) currentControl)){
                                int timesWarned = VarUtil.incrementAndGet(player, "timesWarned");
                                if(timesWarned > 15){

                                    VarUtil.assignField(player, "timesWarned", 0);
                                    PlayerUtils.sendMessage(player, "Well. I warned you. Goodbye");
                                    ((SegmentController) currentControl).startCoreOverheating(null);
                                    AudioUtils.serverPlaySound("0022_explosion_two", 1F,1F, player);
                                    player.damage(100000, null, player);
                                }else{
                                    AudioUtils.clientPlaySound("0022_gameplay - low fuel warning constant beeps (loop)", 1F,1F);
                                    new StarRunnable(){
                                        @Override
                                        public void run() {
                                            AudioUtils.serverPlaySound("0022_gameplay - low fuel warning constant beeps (loop)", 1F,1F);
                                            if(ticksRan > 190){
                                                cancel();
                                            }
                                        }
                                    }.runTimer(SMWorldBorder.this, 40);
                                    PlayerUtils.sendMessage(player, "!!! YOU HAVE LEFT THE WORLD BORDER !!!\n  >>> MOVE TOWARD 2,2,2 TO PREVENT BEING VAPORIZED");
                                }
                            }
                        }
                    }
                }
            }
        }.runTimer(this, 25*4);
    }
}
