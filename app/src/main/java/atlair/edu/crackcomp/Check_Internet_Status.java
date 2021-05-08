package atlair.edu.crackcomp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ProgressBar;

public class Check_Internet_Status {

    public static void internet_status(final Context con, ProgressBar pb)
    {
        boolean connected=false;

        ConnectivityManager mngr=(ConnectivityManager)con.getSystemService(con.CONNECTIVITY_SERVICE);
        if(mngr.getNetworkInfo(mngr.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || mngr.getNetworkInfo(mngr.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            connected = true;
            // Toast.makeText(this, " Internet connected ", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
        }
        else {
            connected = false;
            //Toast.makeText(this, " Please connect to Internet ", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.VISIBLE);
            AlertDialog.Builder bld=new AlertDialog.Builder(con);
            bld.setTitle("Internet Connection");
            bld.setIcon(R.drawable.ic_internet_black_24dp);
            bld.setMessage("Please connect with internet");
            bld.setCancelable(false);
            bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent in=new Intent(Intent.ACTION_MAIN);
                    in.setComponent(new ComponentName("com.android.settings" , "com.android.settings.Settings$DataUsageSummaryActivity"));
                    con.startActivity(in);

                }
            });

            bld.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            final AlertDialog dialog=bld.create();
            if (dialog.getWindow() != null)
            {
                dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
            }
            dialog.show();
        }


    }



}
