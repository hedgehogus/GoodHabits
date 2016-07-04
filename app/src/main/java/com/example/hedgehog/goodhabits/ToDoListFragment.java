package com.example.hedgehog.goodhabits;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class ToDoListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ChangeItemListener {

    Activity activity;
    Button bAddNew;
    ListView listView;

    String user;
    static int currentOverallProgress;

    ArrayList<Habit> alHabit = new ArrayList<>() ;

    ListFragmentAdapter lfa;

    static final int NEW_ITEM = -101;
    static final int CHANGE_ITEM = -102;
    static final int DELETE_ITEM = -103;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

    }
    public void setDefaultArray(){
        alHabit.clear();
        Habit temp = null;
        String tempName;
        int tempRating;
        int tempId;
        boolean  tempIsAchieved;
        user = MainActivity.getCurrentUser();
        String select = "SELECT _id, _name, _rating, _is_achieved FROM habits WHERE _user = '" + user + "';";
        Cursor c = MainActivity.database.rawQuery(select, null);

        if(c.moveToLast()){
            while (!c.isBeforeFirst()){
                tempId = c.getInt(c.getColumnIndex("_id"));
                tempName = c.getString(c.getColumnIndex("_name"));
                tempRating = c.getInt(c.getColumnIndex("_rating"));
                tempIsAchieved = (c.getInt(c.getColumnIndex("_is_achieved"))==1? true : false);
                temp = new Habit(tempName,tempRating,tempIsAchieved,tempId);
                alHabit.add(temp);
                c.moveToPrevious();
            }
        }
    }

    public void notif(){
        lfa.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_to_do_list, null);
        bAddNew = (Button) rootView.findViewById(R.id.bAddNew);
        bAddNew.setOnClickListener(this);
        listView = (ListView) rootView.findViewById(R.id.lvActions);
        lfa = new ListFragmentAdapter(activity, R.layout.item_layout, alHabit);
        listView.setClipChildren(false);
        listView.setAdapter(lfa);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        //changeCurrentOverallProgress();
        return rootView;
    }



    @Override
    public void onClick(View v) {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
        customDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle b = new Bundle();
        b.putString("name", "");
        b.putInt("rating", 5);
        b.putBoolean("canDelete", false);
        b.putInt("position", NEW_ITEM);
        customDialogFragment.setArguments(b);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        customDialogFragment.show(ft, "dialog_fragment");


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean b = alHabit.get(position).isAchieved;
        String updateStatus;
        int currentId = alHabit.get(position).itemId;
        if (b == false) {
            b = true;
            updateStatus = "UPDATE habits SET _is_achieved = 1 WHERE _id = " + currentId + ";";
        } else {
            b = false;
            updateStatus = "UPDATE habits SET _is_achieved = 0 WHERE _id = " + currentId + ";";
        }
        MainActivity.database.execSQL(updateStatus);
        ImageView tick = (ImageView) view.findViewById(R.id.ivTick);
        Animation tickAnimation = null;
        if (b) {
            tickAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_tick);
        } else {
            tickAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_tick_fade);
        }
        tick.startAnimation(tickAnimation);
        alHabit.get(position).isAchieved = b;
        changeCurrentOverallProgress();
        lfa.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
        customDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle b = new Bundle();
        b.putString("name", alHabit.get(position).name);
        b.putInt("rating", alHabit.get(position).rating);
        b.putBoolean("canDelete", true);
        b.putInt("position", position);
        customDialogFragment.setArguments(b);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        customDialogFragment.show(ft, "dialog_fragment");
        return true;
    }

    @Override
    public void changeFragment(Bundle bundle) {
        int position = bundle.getInt("position");
        String name = bundle.getString("name");
        int rating = bundle.getInt("rating");
        switch (bundle.getInt("mainKey")){
            case NEW_ITEM:
                String insertNewItem = "INSERT INTO habits (_user, _name, _rating, _is_achieved) VALUES ('" + user + "', '" + name  + "', " + rating +" , " +"0" + ");";
                MainActivity.database.execSQL(insertNewItem);
                String select = "SELECT _id FROM habits WHERE _name = '" + name + "';";
                Cursor c = MainActivity.database.rawQuery(select, null);
                int currentId=0;
                if(c.moveToFirst()){
                    while (!c.isAfterLast()){
                        currentId = c.getInt(c.getColumnIndex("_id"));
                        c.moveToNext();
                    }
                }

                alHabit.add(0, new Habit(name, rating, false, currentId));
                break;
            case DELETE_ITEM:
                String deleteItem = "DELETE FROM habits WHERE _id = " + alHabit.get(position).itemId + ";";
                MainActivity.database.execSQL(deleteItem);
                alHabit.remove(position);
                break;
            case CHANGE_ITEM:
                Habit h = alHabit.get(position);
                int itemId = h.itemId;
                h.name = name;
                h.rating = rating;
                String updateHabits = "UPDATE habits SET " +
                        "_name = '"   + name +   "', " +
                        "_rating = "  + rating +
                        " WHERE _id = "+ itemId + ";";
                MainActivity.database.execSQL(updateHabits);
                break;

        }
        changeCurrentOverallProgress();
        lfa.notifyDataSetChanged();

    }

    public void changeCurrentOverallProgress() {
        int overallProgress = 0;
        int currentProgress = 0;

        for (Habit habit: alHabit ) {
            overallProgress += habit.rating;
            if (habit.isAchieved){
                currentProgress += habit.rating;
            }

        }
        if (overallProgress == 0){
            currentOverallProgress = 0;
        } else {
            currentOverallProgress = (currentProgress * 100) / (overallProgress);
        }

        String updateCurrentStatistics = "UPDATE statistics SET _rating = " + currentOverallProgress +
                " WHERE _id = " + MainActivity.getDateID() + ";";
        MainActivity.database.execSQL(updateCurrentStatistics);
    }
}
