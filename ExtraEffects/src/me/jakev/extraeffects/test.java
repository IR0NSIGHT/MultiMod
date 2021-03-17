package me.jakev.extraeffects;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 15.03.2021
 * TIME: 18:38
 */
public class test {
    public static void main (String[] args) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            ExtraEffects.extrapolate(0,100,Math.round(Math.random() * 100));
        };
        long endTime = System.currentTimeMillis();

        System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }
}
