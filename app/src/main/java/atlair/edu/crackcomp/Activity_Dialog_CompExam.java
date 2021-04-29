package atlair.edu.crackcomp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static android.content.Context.MODE_PRIVATE;

public class Activity_Dialog_CompExam extends DialogFragment {

    String[] sar=new String[3];
    String s="";

    SharedPreferences shp;
    String compmain = new String();


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        shp=getActivity().getSharedPreferences("abc",MODE_PRIVATE);
        if (shp.getString("main",null) != null)
        {
            compmain=shp.getString("main",null);
        }

        final AlertDialog.Builder bld=new AlertDialog.Builder(getActivity());
        bld.setTitle("Choose Option");
        bld.setIcon(getResources().getDrawable(R.mipmap.adventure));
        setCancelable(false);

        sar[0]=new String("Upload Question");
        sar[1]=new String("Show Question");
        sar[2]=new String("Give Test");

        bld.setSingleChoiceItems(sar, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                s=sar[which];
          AlertDialog dialog1=(AlertDialog)getDialog();
          dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

            }
        });

        bld.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (s.equalsIgnoreCase("Upload Question"))
                {
                    startActivity(new Intent(getActivity().getApplicationContext(),Upload_question.class));

                }
                else if (s.equalsIgnoreCase("Show Question"))
                {
                    startActivity(new Intent(getActivity().getApplicationContext(),ShowQuestionComp.class));
                }
                else if (s.equalsIgnoreCase("Give Test"))
                {
                    startActivity(new Intent(getActivity().getApplicationContext(),GiveTest_CompExam.class));
                }
                else if (s.equalsIgnoreCase(""))
                {
                    Toast.makeText(((Dialog)dialog).getContext(), "please select your option", Toast.LENGTH_SHORT).show();

                }
            }
        });

        bld.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               startActivity(new Intent(getActivity().getApplicationContext(),UserActivity.class));
            }
        });

        bld.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(compmain.equalsIgnoreCase("Competative Exam"))
                {
                    new Subject_Dialog().show(getActivity().getSupportFragmentManager(), "xyz");
                }
                else if (compmain.equalsIgnoreCase("Computer Language"))
                {
                    new CompLanguage_Dialog().show(getActivity().getSupportFragmentManager(),"xyz");
                }
                else if (compmain.equalsIgnoreCase("Current Affairs") || compmain.equalsIgnoreCase("Other Question"))
                {
                    new MainDialog().show(getActivity().getSupportFragmentManager(),"xyz");
                }
            }
        });
        return bld.create();
    }

    @Override
    public void onStart() {
        super.onStart();


        View decorview=getDialog()
                .getWindow()
                .getDecorView();

        ObjectAnimator scale_In = ObjectAnimator.ofPropertyValuesHolder(decorview, PropertyValuesHolder.ofFloat("scaleX",0.0f,1.0f),
                PropertyValuesHolder.ofFloat("scaleY",0.0f,1.0f),
                PropertyValuesHolder.ofFloat("alpha",0.0f,1.0f));
        scale_In.setDuration(700);
        scale_In.start();

        AlertDialog dialog = (AlertDialog)getDialog();
        Button cancel = dialog.getButton(Dialog.BUTTON_NEUTRAL);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View decorview=getDialog()
                        .getWindow()
                        .getDecorView();

                ObjectAnimator scale_out = ObjectAnimator.ofPropertyValuesHolder(decorview, PropertyValuesHolder.ofFloat("scaleX",1.0f,0.0f),
                        PropertyValuesHolder.ofFloat("scaleY",1.0f,0.0f),
                        PropertyValuesHolder.ofFloat("alpha",1.0f,0.0f));
                scale_out.setDuration(700);
                scale_out.start();

                scale_out.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                     dismiss();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }
        });

           if(s.equalsIgnoreCase(""))
           {
               dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
           }
    }
}

