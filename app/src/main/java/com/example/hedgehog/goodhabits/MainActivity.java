package com.example.hedgehog.goodhabits;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ChangeFragmentListener,View.OnClickListener {
    FrameLayout fragmentContainer;
    Button bToDoList, bStatistics;
    LoginFragment loginFragment;
    ToDoListFragment toDoListFragment;
    static FragmentManager fragmentManager;
    LinearLayout llButtons;
    StatisticsFragment statisticsFragment;
    TextView tvTopText;

    static String name, pass;

    boolean isLoginNow = false;

    static SQLiteDatabase database;

    long lastVisitOfCurrentUser;

    static private final int MENU_ITEM_LOGOUT_ID = 1;
    static private final int MENU_CLEAN_ALL_ID = 2;
    static private final int MENU_SHOW_USER_ID = 3;
    static private final int MENU_SHOW_ADD_ID = 4;
    static private final int TO_DO_LIST_FRAGMENT = 201;
    static private final int STATISTICS_FRAGMENT = 202;
    private int currentFragment;

    static Date date;

    private static int dateID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
        bToDoList = (Button) findViewById(R.id.bToDoList);
        bStatistics = (Button) findViewById(R.id.bStatistics);
        bToDoList.setOnClickListener(this);
        bStatistics.setOnClickListener(this);
        llButtons = (LinearLayout) findViewById(R.id.llButtons);
        llButtons.setVisibility(View.GONE);
        tvTopText = (TextView) findViewById(R.id.tvTopText);

        database = DBHelper.getInstance(this).getWritableDatabase();
        createTables();
        selectCurrentUser();

        fragmentManager = getFragmentManager();

        loginFragment = new LoginFragment();
        toDoListFragment = new ToDoListFragment();
        toDoListFragment.setDefaultArray();
        statisticsFragment = new StatisticsFragment();
        statisticsFragment.setDefaultArray();
        date = new Date();


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!isLoginNow) {
            fragmentTransaction.add(R.id.fragmentContainer, loginFragment, "loginFragment");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        } else {
            toDoListFragment.setDefaultArray();
            insertIntoStatisticsTable();
            fragmentTransaction.replace(R.id.fragmentContainer, toDoListFragment, "toDoListFragment");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            fragmentTransaction.commit();
            currentFragment = TO_DO_LIST_FRAGMENT;
            Animation animButtonShow = AnimationUtils.loadAnimation(this, R.anim.anim_button_show);
            llButtons.setVisibility(View.VISIBLE);
            llButtons.startAnimation(animButtonShow);
        }

    }

    public static Date getDate() {
        return date;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("fragment", currentFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFragment = savedInstanceState.getInt("fragment");
        switch (currentFragment){
            case TO_DO_LIST_FRAGMENT:
                break;
            case STATISTICS_FRAGMENT:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, statisticsFragment, "statisticsFragment");
                fragmentTransaction.commit();
                break;
        }

    }

    public static int getDateID() {
        return dateID;
    }

    public static String getCurrentUser(){
        return name;
    }

    private void createTables() {
        String createTableUsers = "CREATE TABLE IF NOT EXISTS users " +
                "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "_login TEXT NOT NULL, " +
                "_pass TEXT NOT NULL, " +
                "_last_visit INTEGER NOT NULL," +
                "_is_login_now INTEGER NOT NULL," +
                "_is_visible_button INTEGER NOT NULL" +
                ");";
        String createTableHabits = "CREATE TABLE IF NOT EXISTS habits " +
                "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "_user TEXT NOT NULL," +
                "_name TEXT NOT NULL, " +
                "_rating INTEGER NOT NULL, " +
                "_is_achieved INTEGER NOT NULL" +
                ");";
        String createTableStatistics = "CREATE TABLE IF NOT EXISTS statistics " +
                "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "_user TEXT NOT NULL," +
                "_date INTEGER NOT NULL," +
                "_rating INTEGER NOT NULL" +
                ");";

        database.execSQL(createTableUsers);
        database.execSQL(createTableHabits);
        database.execSQL(createTableStatistics);

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, MENU_ITEM_LOGOUT_ID, 0, R.string.menu_item_logout);
        menu.add(0, MENU_CLEAN_ALL_ID, 0, R.string.clean_all_information);
        menu.add(0, MENU_SHOW_USER_ID, 0, R.string.show_current_user);
        menu.add(0, MENU_SHOW_ADD_ID, 0, R.string.show_button);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case MENU_ITEM_LOGOUT_ID:
                if (isLoginNow) {
                    Animation animButtonShow = AnimationUtils.loadAnimation(this, R.anim.anim_button_hide);
                    llButtons.startAnimation(animButtonShow);
                    logOut();

                }
                break;
            case MENU_CLEAN_ALL_ID:

                AlertDialog warningDialog = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.warn)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.really)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String dropTableUsers = "DROP TABLE IF EXISTS users";
                                String dropTableHabits = "DROP TABLE IF EXISTS habits";
                                String dropTableStatistics = "DROP TABLE IF EXISTS statistics";
                                database.execSQL(dropTableUsers);
                                database.execSQL(dropTableHabits);
                                database.execSQL(dropTableStatistics);
                                createTables();
                                logOut();
                            }
                        }).create();
                warningDialog.show();
                break;
            case MENU_SHOW_USER_ID:
                if (isLoginNow){
                    tvTopText.setText(getResources().getString(R.string.now_login) + " " + getCurrentUser());
                    if (tvTopText.getVisibility() == View.GONE){
                        tvTopText.setVisibility(View.VISIBLE);
                        tvTopText.setTypeface(null, Typeface.BOLD);
                    }else{
                        tvTopText.setVisibility(View.GONE);
                    }
                }
               break;
            case MENU_SHOW_ADD_ID:
                toDoListFragment.hideButton();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        insertIntoStatisticsTable();
        toDoListFragment.notif();
        statisticsFragment.notif();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.HOUR_OF_DAY)>= 23 && cal.get(Calendar.MINUTE)>=55){
            AsyncTask<Integer,Void,Void> at = new MyAsyncTask();
            int sec = cal.get(Calendar.SECOND)+ cal.get(Calendar.MINUTE)*60 + cal.get(Calendar.HOUR_OF_DAY)*60*60;
            Integer integer = new Integer(86400-sec);
            at.execute(integer);
        }
    }

    private void logOut() {
        String updateNotLoginNow = "UPDATE users SET " +
                "_is_login_now = '0' WHERE _login = '" + name + "';";
        database.execSQL(updateNotLoginNow);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, loginFragment, "loginFragment");
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        llButtons.setVisibility(View.INVISIBLE);
        tvTopText.setVisibility(View.GONE);

        isLoginNow = false;
    }

    @Override
    public void changeFragment(String firstString, String secondString, boolean isNewUser) {

        name = firstString;
        pass = secondString;
        isLoginNow = true;

        String defaultHabit = getResources().getString(R.string.default_habit);
        lastVisitOfCurrentUser = date.getTime();
        if (isNewUser) {
            String insertIntoUsers = "INSERT INTO users (_login, _pass, _last_visit, _is_login_now, _is_visible_button) VALUES ('" +
                    name + "', '" + pass + "', " + Long.toString(lastVisitOfCurrentUser) + ", " + (isLoginNow ? "1" : "0") + ",1);";
            database.execSQL(insertIntoUsers);
            String insertFirstHabit = "INSERT INTO habits (_user, _name, _rating, _is_achieved) VALUES ('" + name +
                    "', '" + defaultHabit + "', 10 , 0);";
            database.execSQL(insertFirstHabit);
        } else {
            String updateCurrentUser = "UPDATE users SET _is_login_now = '1', _last_visit = " + Long.toString(lastVisitOfCurrentUser) +
                    " WHERE _login = '" + name + "';";
            database.execSQL(updateCurrentUser);
        }
        toDoListFragment.setDefaultArray();
        insertIntoStatisticsTable();

    }

    @Override
    public void animationEnd() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, toDoListFragment, "toDoListFragment");
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.commit();

        Animation animButtonShow = AnimationUtils.loadAnimation(this, R.anim.anim_button_show);
        llButtons.setVisibility(View.VISIBLE);
        llButtons.startAnimation(animButtonShow);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()){
            case R.id.bToDoList:
                fragmentTransaction.replace(R.id.fragmentContainer, toDoListFragment, "toDoListFragment");
                currentFragment = TO_DO_LIST_FRAGMENT;
                break;
            case R.id.bStatistics:
                statisticsFragment.setDefaultArray();
                fragmentTransaction.replace(R.id.fragmentContainer, statisticsFragment, "statisticsFragment");
                currentFragment = STATISTICS_FRAGMENT;
                break;
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();


    }

    public static class DBHelper extends SQLiteOpenHelper{

        public static final String DB_NAME = "mdb.db";

        private static DBHelper dbHelper;

        public static DBHelper getInstance (Context context){
            if (dbHelper == null){
                dbHelper = new DBHelper(context);
            }
            return dbHelper;
        }


        private DBHelper(Context context) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private void selectCurrentUser() {
        String selectStr = "SELECT * FROM users WHERE _is_login_now = '1'";

        Cursor c = database.rawQuery(selectStr, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                isLoginNow = true;
                name = c.getString(c.getColumnIndex("_login"));
                pass = c.getString(c.getColumnIndex("_pass"));
                lastVisitOfCurrentUser = c.getLong(c.getColumnIndex("_last_visit"));
                c.moveToNext();
            }
        }
    }

    private void insertIntoStatisticsTable() {
        String select = "SELECT _date FROM statistics WHERE _user = '" + name + "';";
        Cursor c = database.rawQuery(select, null);
        Date tempDate = null;
        date = new Date();
        if(c.moveToLast()){
            long l = c.getLong(c.getColumnIndex("_date"));
            tempDate = new Date(l);
        }
        String insetIntoStatistics = "INSERT INTO statistics (_user, _date, _rating) VALUES ('" + name +
                "', " + date.getTime() + ", " + 0 + ");";
        if (tempDate == null){
            database.execSQL(insetIntoStatistics);
        } else {
            Calendar cal = Calendar.getInstance();
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(date);
            cal.setTime(tempDate);
            int year = cal.get(Calendar.YEAR);
            int day = cal.get (Calendar.DAY_OF_YEAR);
            int currentYear = currentCal.get(Calendar.YEAR);
            int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);

            if (year == currentYear){
                if (day < currentDay){
                    for (int i = day; i < currentDay; i++){
                        cal.add (Calendar.DATE, 1);
                        String insetIntoStat = "INSERT INTO statistics (_user, _date, _rating) VALUES ('" + name +
                                "', " + (cal.getTimeInMillis()) + ", " + 0 + ");";
                        database.execSQL(insetIntoStat);
                        clearHabitChecked();
                    }
                }
            } else if (year < currentYear){
                while (year<=currentYear && day < currentDay ){
                    cal.add (Calendar.DATE, 1);
                    year = cal.get(Calendar.YEAR);
                    day = cal.get (Calendar.DAY_OF_YEAR);
                    String insetIntoStat = "INSERT INTO statistics (_user, _date, _rating) VALUES ('" + name +
                            "', " + currentCal.getTimeInMillis() + ", " + 0 + ");";
                    database.execSQL(insetIntoStat);
                    clearHabitChecked();
                }

            }
        }
        findDateID();
    }

    private void clearHabitChecked(){
        String updateHabitTable = "UPDATE habits SET _is_achieved = '0' WHERE _user = '" + name + "';";
        database.execSQL(updateHabitTable);
        toDoListFragment.setDefaultArray();

    }

    private void findDateID (){
        String select = "SELECT _date, _id, _user FROM statistics" +
                " WHERE _user = '" + name + "';";
        Cursor c = database.rawQuery(select, null);
        Date tempDate = null;
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(date);
        if(c.moveToLast()) {
            while (!c.isBeforeFirst()) {
                long l = c.getLong(c.getColumnIndex("_date"));
                tempDate = new Date(l);
                Calendar cal = Calendar.getInstance();
                cal.setTime(tempDate);
                int year = cal.get(Calendar.YEAR);
                int day = cal.get (Calendar.DAY_OF_YEAR);
                int currentYear = currentCal.get(Calendar.YEAR);
                int currentDay = currentCal.get(Calendar.DAY_OF_YEAR);
                if (year == currentYear && day == currentDay){
                    dateID = c.getInt(c.getColumnIndex("_id"));
                    break;
                }
                c.moveToPrevious();
            }
        }

    }
    private void testStatisticsTable (){
        String select = "SELECT * FROM statistics;" ;
        Cursor c = database.rawQuery(select, null);
        Date tempDate = null;
        if(c.moveToLast()) {
            while (!c.isBeforeFirst()) {
                long l = c.getLong(c.getColumnIndex("_date"));
                tempDate = new Date(l);
                Calendar cal = Calendar.getInstance();
                cal.setTime(tempDate);
                Log.d ("asdf", " " + c.getInt(c.getColumnIndex("_id")) + "     " + c.getInt(c.getColumnIndex("_rating")) + "     " + c.getString(c.getColumnIndex("_user")) );
                c.moveToPrevious();
            }
        }

    }

    private class MyAsyncTask extends AsyncTask<Integer,Void,Void>{
        @Override
        protected Void doInBackground(Integer... params) {
            int time = params[0]+1;
            while (time > 0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                time--;

            }

            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            insertIntoStatisticsTable();
                toDoListFragment.notif();
                statisticsFragment.notif();

        }


    }


}

