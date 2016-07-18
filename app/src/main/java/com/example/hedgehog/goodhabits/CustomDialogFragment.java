package com.example.hedgehog.goodhabits;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * Created by hedgehog on 14.05.2016.
 */
public class CustomDialogFragment extends DialogFragment implements View.OnClickListener, DialogInterface.OnClickListener{

    Activity activity;
    EditText etHabit;
    NumberPicker numberPicker;
    Button bOk;
    Button bCancel;
    Button bDel;
    String nameOfHabit;
    int ratingOfHabit;
    boolean showDelButton;
    int position;
    FragmentManager fragmentManager;
    ChangeItemListener changeItemListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        nameOfHabit = args.getString("name");
        ratingOfHabit = args.getInt("rating");
        showDelButton = args.getBoolean("canDelete");
        position = args.getInt("position");

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog, null);
        etHabit = (EditText) rootView.findViewById(R.id.etHabit);
        numberPicker = (NumberPicker) rootView.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setWrapSelectorWheel(false);
        bOk = (Button) rootView.findViewById(R.id.bOk);
        bCancel = (Button) rootView.findViewById(R.id.bCancel);
        bDel = (Button) rootView.findViewById(R.id.bDel);
        bOk.setOnClickListener(this);
        bCancel.setOnClickListener(this);
        bDel.setOnClickListener(this);
        etHabit.setText(nameOfHabit);
        numberPicker.setValue(ratingOfHabit);
        if (showDelButton){
            bDel.setVisibility(View.VISIBLE);
        }

        fragmentManager = getFragmentManager();
        changeItemListener = (ChangeItemListener) fragmentManager.findFragmentByTag("toDoListFragment");


        return rootView;
    }


    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        nameOfHabit = etHabit.getText().toString();
        ratingOfHabit = numberPicker.getValue();
        bundle.putString("name", nameOfHabit);
        bundle.putInt("rating", ratingOfHabit);
        switch (v.getId()){
            case R.id.bOk:
                if (nameOfHabit.length() > 0){
                    if (position == ToDoListFragment.NEW_ITEM){
                        bundle.putInt("mainKey", ToDoListFragment.NEW_ITEM);

                    } else {
                        bundle.putInt("mainKey", ToDoListFragment.CHANGE_ITEM);
                    }
                    changeItemListener.changeFragment(bundle);
                    dismiss();
                } else {
                    Toast.makeText(activity, R.string.toast_enter_the_name_of_habit, Toast.LENGTH_SHORT ).show();
                }

                break;
            case R.id.bCancel:

                dismiss();
                break;

            case R.id.bDel:
                AlertDialog alertDialog = new AlertDialog.Builder(activity)
                        .setMessage(R.string.delete_question)
                        .setPositiveButton(R.string.ok, this)
                        .setNegativeButton(R.string.cancel, this)
                        .create();
                alertDialog.show();
                break;
        }



    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putInt("mainKey", ToDoListFragment.DELETE_ITEM);
                changeItemListener.changeFragment(bundle);
                dismiss();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }

    }


}
