package me.jakev.extraeffects;

import java.util.ArrayList;

/**
 * Created by Jake on 12/13/2020.
 * <insert description here>
 */
public class Studying {
    public static void main(String[] args) {
        Item[] items = new Item[]{
                new Item(3,5),
                new Item(1,2),
                new Item(4,8),
                new Item(5,6),
                new Item(2,3),
                new Item(6,7),
                new Item(7,9),
        };
        ArrayList<ArrayList<Item>> allCombos = new ArrayList<>();
        int combos = 128;
        //Pick or not pick an item
        for (int i = 0; i < combos; i++) {
            ArrayList<Item> combo = new ArrayList<>();
            boolean pickA = (i & 0b00000001) != 0;
            boolean pickB = (i & 0b00000010) != 0;
            boolean pickC = (i & 0b00000100) != 0;
            boolean pickD = (i & 0b00001000) != 0;
            boolean pickE = (i & 0b00010000) != 0;
            boolean pickF = (i & 0b00100000) != 0;
            boolean pickG = (i & 0b01000000) != 0;
            if(pickA){
                combo.add(items[0]);
            }
            if(pickB){
                combo.add(items[1]);
            }
            if(pickC){
                combo.add(items[2]);
            }
            if(pickD){
                combo.add(items[3]);
            }
            if(pickE){
                combo.add(items[4]);
            }
            if(pickF){
                combo.add(items[5]);
            }
            if(pickG){
                combo.add(items[6]);
            }
            allCombos.add(combo);
        }
        int max = 0;
        ArrayList<Item> best = new ArrayList<>();
        for (ArrayList<Item> allCombo : allCombos) {
            int totalWeight = 0;
            int totalValue = 0;
            for (Item item : allCombo) {
                totalWeight += item.weight;
                totalValue += item.value;
            }
            if(totalWeight <= 9){
                if(max <= totalValue){
                    best = allCombo;
                    max = totalValue;
                }
            }
        }
        System.err.println(best.toString());
    }

    static class Item{
        int weight;
        int value;

        public Item(int weight, int value) {
            this.weight = weight;
            this.value = value;
        }
    }
    public static int lcs(char[] arrA, char[] arrB){
        int[][] m = new int[arrA.length+1][arrB.length+1];
        for (int x = 0; x < arrA.length; x++) {
            for (int y = 0; y < arrB.length; y++) {
                char a = arrA[x];
                char b = arrB[y];
                if(a == b){
                    //The diagonal before plus 1
                    m[x+1][y+1] = m[x][y] + 1;
                }else{
                    m[x+1][y+1] = Math.max(m[x][y+1], m[x+1][y]);
                }
            }
        }

        //backtrack
        int ptrX = arrA.length;
        int ptrY = arrB.length;
        StringBuilder lcsString = new StringBuilder();
        while (ptrX > 0 && ptrY > 0){
            int above = m[ptrX][ptrY-1];
            int left = m[ptrX-1][ptrY];
            if(above == left){
                //Must have incremented from the diagonal
                ptrX--;
                ptrY--;
                lcsString.append(arrA[ptrX]);
            }else if(above > left){
                ptrY--;
            }else{
                ptrX--;
            }
        }
        System.err.println(lcsString.reverse().toString());
        return m[arrA.length][arrB.length];
    }
    public int lengthOfLIS(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }



        int[] dp = new int[nums.length];
        dp[0] = 1;
        int maxans = 1;
        for (int i = 1; i < dp.length; i++) {
            int maxval = 0;
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) {
                    maxval = Math.max(maxval, dp[j]);
                }
            }
            dp[i] = maxval + 1;
            maxans = Math.max(maxans, dp[i]);
        }
        return maxans;
    }
}
/*
NP-Hard and NP-complete

Polynomial time = good, exponential time = bad

P    NP [Non-deterministic polynomial algorithm]
_______

 */