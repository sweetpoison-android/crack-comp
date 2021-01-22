package atlair.edu.crackcomp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MainDialog extends DialogFragment {

    String[] sar=new String[4];
    String s=new String();


    SharedPreferences shp;
    SharedPreferences.Editor edt;

    String compmain=new String();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


       shp=getActivity().getSharedPreferences("abc", Context.MODE_PRIVATE);
       edt=shp.edit();

        final AlertDialog.Builder bld=new AlertDialog.Builder(getActivity());
        bld.setTitle("Choose Topic");
        bld.setIcon(getResources().getDrawable(R.mipmap.adventure));
        setCancelable(false);

        sar[0]=new String("Competative Exam");
        sar[1]=new String("Computer Language");
        sar[2]=new String("Current Affairs");
        sar[3]=new String("Other Question");

        bld.setSingleChoiceItems(sar, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              s=sar[which];

              AlertDialog dialog1 = (AlertDialog)getDialog();
              dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
              edt.putString("main",s);
              edt.commit();
            }
        });

        bld.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           if(s.equalsIgnoreCase("Competative Exam"))
           {
               if (shp.getString("main",null) != null)
               {
                   compmain=shp.getString("main",null);
                   if(!s.equalsIgnoreCase(compmain))
                   {
                       shp.edit().remove("main").commit();
                   }
               }
               shp.edit().remove("language").commit();
               new ComExam_Dialog().show(getActivity().getSupportFragmentManager(),"abc");
           }
          else if(s.equalsIgnoreCase("Computer Language"))
           {
               shp.edit().remove("subject").commit();
               shp.edit().remove("compexam").commit();
               if (shp.getString("main",null) != null)
               {
                   compmain=shp.getString("main",null);
                   if(!s.equalsIgnoreCase(compmain))
                   {
                       shp.edit().remove("main").commit();
                   }
               }
               new CompLanguage_Dialog().show(getActivity().getSupportFragmentManager(),"abc");
           }
           else if(s.equalsIgnoreCase("Current Affairs"))
           {
               shp.edit().remove("subject").commit();
               shp.edit().remove("compexam").commit();
               if (shp.getString("main",null) != null)
               {
                   compmain=shp.getString("main",null);
                   if(!s.equalsIgnoreCase(compmain))
                   {
                       shp.edit().remove("main").commit();
                   }
               }
               shp.edit().remove("language").commit();

                new Activity_Dialog_CompExam().show(getActivity().getSupportFragmentManager(),"abc");
           }
           else if(s.equalsIgnoreCase("Other Question"))
           {
               shp.edit().remove("subject").commit();
               shp.edit().remove("compexam").commit();
               if (shp.getString("main",null) != null)
               {
                   compmain=shp.getString("main",null);
                   if(!s.equalsIgnoreCase(compmain))
                   {
                       shp.edit().remove("main").commit();
                   }
               }
               shp.edit().remove("language").commit();
               new Activity_Dialog_CompExam().show(getActivity().getSupportFragmentManager(),"abc");
           }
            }
        });

        bld.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             startActivity(new Intent(getActivity(), UserActivity.class));
            }
        });

        bld.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
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
