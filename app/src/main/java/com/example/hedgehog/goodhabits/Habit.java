package com.example.hedgehog.goodhabits;

/**
 * Created by hedgehog on 20.05.2016.
 */
public class Habit {

    int itemId;
    String name;
    int rating;
    boolean isAchieved;

    public Habit (String name, int rating, boolean isAchieved, int id){
        this.itemId = id;
        this.name = name;
        this.rating = rating;
        this.isAchieved = isAchieved;
    }
}
