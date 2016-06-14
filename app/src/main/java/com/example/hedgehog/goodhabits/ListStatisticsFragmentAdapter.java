package com.example.hedgehog.goodhabits;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
        ProgressBar pbProgress = (ProgressBar) rootView.findViewById(R.id.pbProgress);

        StatisticsItem temp = arrayList.get(position);
        String dayOfWeek = getDayOfWeek(temp.dayOfWeek);
        tvDate.setText("" + temp.day + "." + temp.month + "." + temp.year);
        tvDayOfWeek.setText(dayOfWeek);
        tvProgress.setText(" " + temp.progress + " %");
        pbProgress.setProgress(temp.progress);

        if (temp.isToday){
            tvDayOfWeek.setTypeface(null, Typeface.BOLD_ITALIC);
            tvDate.setTypeface(tvDate.getTypeface(), Typeface.BOLD);
            tvProgress.setTypeface(null, Typeface.BOLD_ITALIC);
            tvDate.setTextColor(context.getResources().getColor(R.color.highlightTextColor));
            tvDayOfWeek.setTextColor(context.getResources().getColor(R.color.highlightTextColor));
            tvProgress.setTextColor(context.getResources().getColor(R.color.highlightTextColor));
            tvDayOfWeek.setText(dayOfWeek + "  " + context.getResources().getString(R.string.today));
        }

        switch (temp.month){
            case Calendar.JANUARY:
                rootView.setBackgroundResource(R.drawable.month_jan);
                break;
            case Calendar.FEBRUARY:
                rootView.setBackgroundResource(R.drawable.month_feb);
                break;
            case Calendar.MARCH:
                rootView.setBackgroundResource(R.drawable.month_mar);
                break;
            case Calendar.APRIL:
                rootView.setBackgroundResource(R.drawable.month_apr);
                break;
            case Calendar.MAY:
                rootView.setBackgroundResource(R.drawable.month_may);
                break;
            case Calendar.JUNE:
                rootView.setBackgroundResource(R.drawable.month_jun);
                break;
            case Calendar.JULY:
                rootView.setBackgroundResource(R.drawable.month_jul);
                break;
            case Calendar.AUGUST:
                rootView.setBackgroundResource(R.drawable.month_aug);
                break;
            case Calendar.SEPTEMBER:
                rootView.setBackgroundResource(R.drawable.month_sep);
                break;
            case Calendar.OCTOBER:
                rootView.setBackgroundResource(R.drawable.month_oct);
                break;
            case Calendar.NOVEMBER:
                rootView.setBackgroundResource(R.drawable.month_now);
                break;
            case Calendar.DECEMBER:
                rootView.setBackgroundResource(R.drawable.month_dec);
                break;
        }
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