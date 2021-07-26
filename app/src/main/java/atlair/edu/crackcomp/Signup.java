package atlair.edu.crackcomp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ncorti.slidetoact.SlideToActView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Signup extends AppCompatActivity {

    CircleImageView img;
    TextView tv;
    TextInputLayout name,email,password;
    ProgressBar pb;
    Toolbar tb;

    SlideToActView register;

    String sname,semail,spassword,stime;


    String Loginpassword;

    DatabaseReference ref;
    FirebaseAuth mauth;
    boolean b=false;
    String uid;
    SharedPreferences shp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        img=findViewById(R.id.signup_image);
        tv=findViewById(R.id.signup_textview1);
        name=findViewById(R.id.signup_textinput1);
        email=findViewById(R.id.signup_textinput2);
        password=findViewById(R.id.signup_textinput3);
        register=findViewById(R.id.signup_slidingButton);
        pb=findViewById(R.id.signup_progressbar);
        tb=findViewById(R.id.signup_toolbar);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        shp = getSharedPreferences("abc", Context.MODE_PRIVATE);
        if(shp.getString("id",null)!=null)
        {
            Loginpassword = shp.getString("id",null);
        }

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        img.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.enterfrom_left));

        ref= FirebaseDatabase.getInstance().getReference();
        mauth=FirebaseAuth.getInstance();

        Calendar c=Calendar.getInstance();
        SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy E hh:mm a");
        stime=df.format(c.getTime());


        register.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(final SlideToActView slideToActView) {
                pb.setVisibility(View.VISIBLE);
             sname=name.getEditText().getText().toString();
             semail=email.getEditText().getText().toString();

             spassword=password.getEditText().getText().toString();

              String key=ref.push().getKey();

                if(!validateName() | !validateemail() | !validatepassword())
                {
                   tv.setText("Please enter details");
                    register.resetSlider();
                    pb.setVisibility(View.GONE);
                }
                else
                {
                 ///   ref.child("User").child(substring).setValue(new Signup_bean(sname,semail,spassword,sphone,stime));

                    mauth.createUserWithEmailAndPassword(semail,spassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    pb.setVisibility(View.GONE);
                               if(task.isSuccessful())
                               {
                                  mauth.getCurrentUser().sendEmailVerification()
                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                             if(task.isSuccessful())
                                             {

//                                                 ref.child("User").child(mauth.getCurrentUser().getUid()).child("imgurl").setValue("no image");
//
//                                                 ref.child("User").child(mauth.getCurrentUser().getUid()).child("country").setValue("no country code");
//                                                 ref.child("User").child(mauth.getCurrentUser().getUid()).child("mobile").setValue("no mobile number");

                                                 HashMap<String ,Object>data = new HashMap<>();
                                                 data.put("name",sname);
                                                 data.put("email",semail);
                                                 data.put("password",spassword);
                                                 data.put("imgurl", "no image");
                                                 data.put("country", "no country code");
                                                 data.put("mobile", "no mobile number");
                                                  data.put("time",Long.toString(new Date().getTime()));

                                                  ref.child("User").child(mauth.getCurrentUser().getUid()).setValue(data);

                                                 Toast.makeText(Signup.this, "Registered Successfully \n Please check your email for verification", Toast.LENGTH_SHORT).show();
                                                 startActivity(new Intent(Signup.this, Login.class));
                                                 overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                                 finish();
                                                 register.resetSlider();

                                             }
                                             else
                                             {
                                                 Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                             }
                                              }
                                          });
                               }
                               else
                               {
                                   Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                   slideToActView.resetSlider();
                               }
                                }
                            });


                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Signup.this,Login.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        super.onBackPressed();
    }

    public boolean validateName()
    {
        if(sname.trim().isEmpty())
        {
            name.setError("Please enter name");
            return false;
        }
        else {
            name.setError(null);
            return true;
        }
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
