package com.example.hedgehog.goodhabits;

/**
 * Created by hedgehog on 08.06.2016.
 */
public class StatisticsItem {

    int id;
    int day;
    int month;
    int year;
    int progress;
    boolean isToday;
    int dayOfWeek;

    public StatisticsItem (int id, int day, int month, int year, int progress, boolean isToday, int dayOfWeek){
        this.id = id;
        this.day = day;
        this.month = month;
        this.year = year;
        this.progress = progress;
        this.isToday = isToday;
        this.dayOfWeek = dayOfWeek;
    }

}
