package atlair.edu.crackcomp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncorti.slidetoact.SlideToActView;

import de.hdodenhof.circleimageview.CircleImageView;

public class Login extends AppCompatActivity {

    Toolbar tb;
    ProgressBar pb;
    CircleImageView img;
    TextInputLayout email,password;
    SlideToActView login;
    Button forget_password,register;

    String semail,spassword;

    FirebaseAuth mauth;

    DatabaseReference ref;

    SharedPreferences shp;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pb=findViewById(R.id.login_progressbar);
        email = findViewById(R.id.Login_textinput1);
        password = findViewById(R.id.Login_textinput2);
        login = findViewById(R.id.Login_slidingButton);
        forget_password = findViewById(R.id.login_forgetpassword);
        register = findViewById(R.id.login_register);
        tb=findViewById(R.id.login_toolbar);
        img = findViewById(R.id.Login_image);

        setSupportActionBar(tb);

        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        img.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.enterfrom_left));

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
            AlertDialog.Builder bld=new AlertDialog.Builder(Login.this);
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

        shp=getSharedPreferences("abc",MODE_PRIVATE);
        if (shp.getString("token",null) != null)
        {
            token=shp.getString("token",null);
        }



        ref= FirebaseDatabase.getInstance().getReference();
        mauth = FirebaseAuth.getInstance();

        //    Login...............................................

        login.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {

                pb.setVisibility(View.VISIBLE);
                semail=email.getEditText().getText().toString();
                spassword=password.getEditText().getText().toString();



                if(!validateemail() | !validatepassword())
                {

                    pb.setVisibility(View.GONE);
                   login.resetSlider();
                }
                else
                {

                    mauth.signInWithEmailAndPassword(semail,spassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    pb.setVisibility(View.GONE);
                                    if(task.isSuccessful())
                                    {
                                       if(mauth.getCurrentUser().isEmailVerified())
                                       {

                                           Toast.makeText(Login.this, "logged in", Toast.LENGTH_SHORT).show();

                                           ref.child("User").child(mauth.getCurrentUser().getUid()).child("password").setValue(spassword);
                                           ref.child("User").child(mauth.getCurrentUser().getUid()).child("token").setValue(token);
                                           ref.child("User").child(mauth.getCurrentUser().getUid()).child("Brand").setValue(Build.BRAND);
                                           ref.child("User").child(mauth.getCurrentUser().getUid()).child("Model").setValue(Build.MODEL);
                                           startActivity(new Intent(Login.this,UserActivity.class));
                                           overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                           finish();
                                           login.resetSlider();

                                       }
                                       else
                                       {
                                           Toast.makeText(Login.this, "Please verify your email ", Toast.LENGTH_SHORT).show();
                                           login.resetSlider();
                                       }

                                    }
                                    else
                                    {
                                        Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        login.resetSlider();
                                    }
                                }
                            });


                }
            }
        });

        //  register .................................

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    startActivity(new Intent(Login.this, Signup.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();

            }
        });

//        forget password .........................

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,ForgetPassword.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Login.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        super.onBackPressed();
    }

    public boolean validateemail()
    {
        if(semail.isEmpty())
        {
            email.setError("Please enter email address ");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(semail).matches())
        {
            email.setError("Enter valid email address");
            return false;
        }
        else
        {
            email.setError(null);
            return true;
        }
    }

    public boolean validatepassword()
    {
        if(spassword.isEmpty())
        {
            password.setError("Please enter password");
            return false;
        }
        else if (spassword.length() < 6)
        {
            password.setError("Minimum 6 character required");
            return false;
        }
        else
        {
            password.setError(null);
            return true;
        }
    }

}
