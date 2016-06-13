package com.example.hedgehog.goodhabits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hedgehog on 08.06.2016.
 */
public class ListStatisticsFragmentAdapter  extends ArrayAdapter<StatisticsItem> {

    Context context;
    ArrayList<StatisticsItem> arrayList;

    public ListStatisticsFragmentAdapter(Context context, int resource, List<StatisticsItem> objects) {
        super(context, resource, objects);
        arrayList = (ArrayList<StatisticsItem>) objects;
        this.context = context;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.item_statistics, parent, false);
        TextView tvDate = (TextView) rootView.findViewById(R.id.tvDate);
        TextView tvDayOfWeek = (TextView) rootView.findViewById(R.id.tvDayOfWeek);
        TextView tvProgress = (TextView) rootView.findViewById(R.id.tvProgress);
        StatisticsItem temp = arrayList.get(position);
        String dayOfWeek = getDayOfWeek(temp.dayOfWeek);
        ProgressBar pbProgress = (ProgressBar) rootView.findViewById(R.id.pbProgress);
        tvDate.setText("" + temp.day + "." + temp.month + "." + temp.year);
        tvDayOfWeek.setText(dayOfWeek);
        tvProgress.setText(" " + temp.progress + " %");
        pbProgress.setProgress(temp.progress);
        return rootView;
    }

    private String getDayOfWeek (int day){
        String dayStr = null;
        switch (day){
            case Calendar.SUNDAY:
                dayStr = context.getResources().getString(R.string.sun);
                break;
            case Calendar.MONDAY:
                dayStr = context.getResources().getString(R.string.mon);
                break;
            case Calendar.TUESDAY:
                dayStr = context.getResources().getString(R.string.tue);
                break;
            case Calendar.WEDNESDAY:
                dayStr = context.getResources().getString(R.string.wed);
                break;
            case Calendar.THURSDAY:
                dayStr = context.getResources().getString(R.string.thu);
                break;
            case Calendar.FRIDAY:
                dayStr = context.getResources().getString(R.string.fri);
                break;
            case Calendar.SATURDAY:
                dayStr = context.getResources().getString(R.string.sat);
                break;
        }
        return dayStr;
    }
}