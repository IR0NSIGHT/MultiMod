package me.jakev.extraeffects.listeners;

import api.ModPlayground;
import api.listener.Listener;
import api.listener.events.gui.PlayerGUIDrawEvent;
import api.mod.StarLoader;
import me.jakev.extraeffects.ExtraEffects;

import java.util.HashSet;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 08.07.2021
 * TIME: 20:14
 * allows to subscribe to a "onEachFrame" ticker
 */
public class ExtraEffectsDrawUtil {
    public static void init() {
        StarLoader.registerListener(PlayerGUIDrawEvent.class, new Listener<PlayerGUIDrawEvent>() {
            @Override
            public void onEvent(PlayerGUIDrawEvent event) {
                for (Runnable o: subscribers) {
                    o.run();
                }
            }
        }, ExtraEffects.inst);
    }
    private static HashSet<Runnable> subscribers = new HashSet<>();
    public static void subscribe(Runnable r) {
        subscribers.add(r);
    }
    public static boolean unsubscribe(Runnable r) {
        return subscribers.remove(r);
    }
}
