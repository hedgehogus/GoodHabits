package com.example.hedgehog.goodhabits;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    Activity activity;
    EditText etName, etPass;
    ImageView ivSwirl;
    Button bEnter;
    View rootView;
    String name;
    String pass;
    boolean isNewUser;
    ChangeFragmentListener al;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login,null);
        etName = (EditText) rootView.findViewById(R.id.etName);
        etPass = (EditText) rootView.findViewById(R.id.etPass);
        ivSwirl = (ImageView) rootView.findViewById(R.id.ivSwirl);
        bEnter = (Button) rootView.findViewById(R.id.bEnter);
        bEnter.setOnClickListener(this);
        al = (ChangeFragmentListener) activity;



        return rootView;
    }


    @Override
    public void onClick(View v) {
        name = etName.getText().toString();
        pass = etPass.getText().toString();
        if (name.length() == 0){
            Toast.makeText(activity, R.string.you_must_enter, Toast.LENGTH_LONG).show();
        } else {
            String selectInformationFromUsers = "SELECT _pass FROM users WHERE _login = '" + name + "';";
            Cursor c = MainActivity.database.rawQuery(selectInformationFromUsers,null);
                    if (c.moveToFirst()){
                        isNewUser = false;

                        if ( pass.equals(c.getString(c.getColumnIndex("_pass")))){
                            al.changeFragment(name,pass,isNewUser);
                            startAnimation();
                        } else {
                            Toast.makeText(activity, R.string.incorrect_pass, Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        isNewUser = true;
                        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                                .setMessage(R.string.create_new_account)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        al.changeFragment(name,pass,isNewUser);
                                        startAnimation();

                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .create();
                        alertDialog.show();

                    }

        }



    }

    public void startAnimation() {
        AnimationListener1 animationListener1= new AnimationListener1();
        Animation swirlAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_swirl);
        swirlAnimation.setAnimationListener(animationListener1);
        ivSwirl.startAnimation(swirlAnimation);
        Animation buttonAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_button_hide);
        buttonAnimation.setFillAfter(true);
        bEnter.setClickable(false);
        bEnter.startAnimation(buttonAnimation);


    }



    private class AnimationListener1 implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            al.animationEnd();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {


        }
    }
}
