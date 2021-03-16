package me.jakev.extraeffects;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 15.03.2021
 * TIME: 18:38
 */
public class test {
    public static void main (String[] args) {
       System.out.println(ExtraEffects.extrapolate(20,120,20));
        System.out.println(ExtraEffects.extrapolate(20,120,-20));
        System.out.println(ExtraEffects.extrapolate(20,120,220));
        System.out.println(ExtraEffects.extrapolate(20,120,95));

        System.out.println(ExtraEffects.interpolate(0,10,0.3f));
        System.out.println(ExtraEffects.interpolate(2,12,0.3f));
    }
}
