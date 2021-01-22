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

public class CompLanguage_Dialog extends DialogFragment {

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
        bld.setTitle("Choose Language");
        bld.setIcon(getResources().getDrawable(R.mipmap.adventure));
        setCancelable(false);

        sar[0]=new String("HTML");
        sar[1]=new String("CSS");
        sar[2]=new String("PHP");
        sar[3]=new String("JAVA");

        bld.setSingleChoiceItems(sar, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           s=sar[which];
           AlertDialog dialog1 = (AlertDialog)getDialog();
           dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
           edt.putString("language",s);
           edt.commit();



            }
        });

        bld.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               new Activity_Dialog_CompExam().show(getActivity().getSupportFragmentManager(),"xyz");

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
        if (s.equalsIgnoreCase(""))
        {
            AlertDialog dialog = (AlertDialog)getDialog();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
