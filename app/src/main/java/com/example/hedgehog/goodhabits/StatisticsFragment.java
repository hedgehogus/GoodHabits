package com.example.hedgehog.goodhabits;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hedgehog on 13.05.2016.
 */
public class StatisticsFragment extends Fragment {

    Activity activity;
    ListView rootListView;
    ArrayList<StatisticsItem> alItems = new ArrayList<>();
    int currentDateId;
    String user;


    ListStatisticsFragmentAdapter lfa;

    public void setDefaultArray() {
        alItems.clear();
        StatisticsItem temp = null;
        int tempId;
        int tempDay;
        int tempMonth;
        int tempYear;
        int tempProgress;
        boolean isToday;
        int tempDayOfWeek;
        Date tempDate = null;
        Calendar cal = Calendar.getInstance();
        user = MainActivity.getCurrentUser();
        currentDateId = MainActivity.getDateID();
        String select = "SELECT _id, _date, _rating FROM statistics WHERE _user = '" + user + "';";
        Cursor c = MainActivity.database.rawQuery(select, null);
        if (c.moveToLast()) {
            while (!c.isBeforeFirst()) {
                tempId = c.getInt(c.getColumnIndex("_id"));
                tempProgress = c.getInt(c.getColumnIndex("_rating"));
                tempDate = new Date(c.getLong(c.getColumnIndex("_date")));
                cal.setTime(tempDate);
                tempDay = cal.get(Calendar.DAY_OF_MONTH);
                tempMonth = cal.get(Calendar.MONTH);
                tempYear = cal.get(Calendar.YEAR);
                tempDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                isToday = tempId == currentDateId;
                temp = new StatisticsItem(tempId, tempDay, tempMonth, tempYear, tempProgress, isToday, tempDayOfWeek);
                alItems.add(temp);
                c.moveToPrevious();
            }
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, null);
        rootListView = (ListView) rootView;
        lfa = new ListStatisticsFragmentAdapter(activity, R.layout.item_layout, alItems);
        rootListView.setAdapter(lfa);

        return rootView;
    }



}
