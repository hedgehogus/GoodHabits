package com.example.hedgehog.goodhabits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by hedgehog on 20.05.2016.
 */
public class ListFragmentAdapter extends ArrayAdapter<Habit>  {

    ArrayList<Habit> arrayList;

    Context context;

    public ListFragmentAdapter(Context context, int resource, ArrayList<Habit> objects) {
        super(context, resource, objects);
        arrayList = objects;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = inflater.inflate(R.layout.item_layout, parent, false);
        } else{
            rootView = convertView;
        }
        LinearLayout ll = (LinearLayout) rootView;
        ll.setClipChildren(false);
        TextView tvAction = (TextView) rootView.findViewById(R.id.tvAction);
        TextView tvRating = (TextView) rootView.findViewById(R.id.tvRating);
        ImageView ivTick = (ImageView) rootView.findViewById(R.id.ivTick);
        
        tvAction.setText(arrayList.get(position).name);
        tvRating.setText(String.valueOf(arrayList.get(position).rating));
        if (arrayList.get(position).isAchieved){
            rootView.setBackgroundResource(R.drawable.all_buttons_pressed);
            ivTick.setVisibility(View.VISIBLE);
        } else {
            rootView.setBackgroundResource(R.drawable.all_buttons);
            ivTick.setVisibility(View.INVISIBLE);
        }


        return rootView;
    }
}

