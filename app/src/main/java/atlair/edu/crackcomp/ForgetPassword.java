package atlair.edu.crackcomp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.ncorti.slidetoact.SlideToActView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForgetPassword extends AppCompatActivity {
    CircleImageView img;

    TextInputLayout til;
    SlideToActView stv;
    Toolbar tb;

    String semail;

    ProgressBar pb;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        img = findViewById(R.id.forgetpassword_image);
        til = findViewById(R.id.forgetpassword_textinput1);
        stv = findViewById(R.id.forgetpassword_slidingButton);
        pb = findViewById(R.id.forgetpassword_progressbar);
        tb = findViewById(R.id.forgetpassword_toolbar);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        img.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.enterfrom_left));

        //       Internet connection .................................

        boolean connected=false;
        ConnectivityManager mngr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(mngr.getNetworkInfo(mngr.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
        {
            connected = true;
            // Toast.makeText(this, " Internet connected ", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
        }
        else {
            connected = false;
            //Toast.makeText(this, " Please connect to Internet ", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.VISIBLE);
            AlertDialog.Builder bld=new AlertDialog.Builder(ForgetPassword.this);
            bld.setTitle("Internet Connection");
            bld.setIcon(R.drawable.ic_internet_black_24dp);
            bld.setMessage("Please connect with internet");
            bld.setCancelable(false);
            bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent in=new Intent(Intent.ACTION_MAIN);
                    in.setComponent(new ComponentName("com.android.settings" , "com.android.settings.Settings$DataUsageSummaryActivity"));
                    startActivity(in);

                }
            });

            bld.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            final AlertDialog dialog=bld.create();
            dialog.show();
        }


        auth = FirebaseAuth.getInstance();


        stv.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                pb.setVisibility(View.VISIBLE);
                semail = til.getEditText().getText().toString();


                if(!validateemail())
                {

                    slideToActView.resetSlider();
                    pb.setVisibility(View.GONE);
                }
               else
                {
                    auth.sendPasswordResetEmail(semail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pb.setVisibility(View.GONE);
                                Toast.makeText(ForgetPassword.this, "Password sent to your email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgetPassword.this, Login.class));
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                stv.resetSlider();
                            } else {
                                Toast.makeText(ForgetPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                stv.resetSlider();
                                pb.setVisibility(View.GONE);
                            }
                        }
                    });
                }



            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgetPassword.this,Login.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        super.onBackPressed();
    }

    public boolean validateemail() {
        if (semail.isEmpty()) {
            til.setError("Please enter email address ");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(semail).matches()) {
            til.setError("Enter valid email address");
            return false;
        } else {
            til.setError(null);
            return true;
        }
    }
}