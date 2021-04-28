package atlair.edu.crackcomp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class Token_generator extends FirebaseInstanceIdService {

    SharedPreferences shp;
    SharedPreferences.Editor edt;

    String token;
    @Override
    public void onTokenRefresh() {
       token = FirebaseInstanceId.getInstance().getToken();
        Log.d("token", token);

            bigtextstyle_notification("Thank You for Installing Crack Comp");

           shp = getSharedPreferences("abc",MODE_PRIVATE);
           edt= shp.edit();
           edt.putString("token",token);
           edt.commit();

        super.onTokenRefresh();
    }

    public void bigtextstyle_notification(String token) {
        NotificationManager mngr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder bld = new NotificationCompat.Builder(this);
        bld.setSmallIcon(R.mipmap.adventure);
        bld.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.adventure));
        bld.setContentTitle("Welcome");
        bld.setStyle(new NotificationCompat.BigTextStyle().bigText(token));
        bld.setContentText("Expand for Token");
        bld.setAutoCancel(true);
        bld.setDefaults(NotificationCompat.DEFAULT_ALL);
        bld.setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_1", "abc", NotificationManager.IMPORTANCE_HIGH);

            if (mngr != null) {
                mngr.createNotificationChannel(channel);
                bld.setChannelId("channel_1");
            }
        }
        mngr.notify(1, bld.build());
    }

}

