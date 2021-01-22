package atlair.edu.crackcomp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ComExam_Dialog extends DialogFragment {

    String[] sar=new String[4];
    String s="";

    SharedPreferences shp;
    SharedPreferences.Editor edt;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        shp=getActivity().getSharedPreferences("abc", Context.MODE_PRIVATE);
        edt=shp.edit();

        AlertDialog.Builder bld=new AlertDialog.Builder(getActivity());
        bld.setTitle("Choose Comp Exam");
        bld.setIcon(getResources().getDrawable(R.mipmap.adventure));
        setCancelable(false);
        sar[0]=new String("Railway");
        sar[1]=new String("SSC");
        sar[2]=new String("Navy");
        sar[3]=new String("BSF");

       bld.setSingleChoiceItems(sar, -1, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
             s=sar[which];
             AlertDialog dialog1 = (AlertDialog)getDialog();
             dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
             edt.putString("compexam",s);
             edt.commit();
           }
       });

       bld.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               new Subject_Dialog().show(getActivity().getSupportFragmentManager(),"xyz");
           }
       });

        bld.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        bld.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new MainDialog().show(getActivity().getSupportFragmentManager(),"xyz");
            }
        });
       return bld.create();

    }

    @Override
    public void onStart() {
        super.onStart();
        if(s.equalsIgnoreCase(""))
        {
            AlertDialog dialog = (AlertDialog)getDialog();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
