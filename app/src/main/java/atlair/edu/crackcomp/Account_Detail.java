package atlair.edu.crackcomp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account_Detail extends AppCompatActivity {

    CollapsingToolbarLayout ctl;
    Toolbar tb;
    ProgressBar pb;
    CircleImageView civ;
    ImageView cam_img;

    DatabaseReference ref;
    FirebaseAuth auth;
    FirebaseUser fuser;
    String userimgurl;

    TextView name,email,mobile,signuptime;
    String sname,semail,smobile,ssignuptime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__detail);


        ctl=findViewById(R.id.accountdetail_collapsingtoolbar);
        tb=findViewById(R.id.accountdetail_toolbar);
        pb=findViewById(R.id.accountdetail_progressbar);
        civ=findViewById(R.id.accountdetail_headerimage);
        cam_img = findViewById(R.id.accountdetail_camera);

        name = findViewById(R.id.accountdetail_name);
        email = findViewById(R.id.accountdetail_email);
        mobile = findViewById(R.id.accountdetail_mobile);
        signuptime = findViewById(R.id.accountdetail_signuptime);


        setSupportActionBar(tb);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        ref= FirebaseDatabase.getInstance().getReference();
        auth= FirebaseAuth.getInstance();
        fuser=auth.getCurrentUser();

        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(fuser != null)
        {
            ref.child("User").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username=dataSnapshot.child("name").getValue().toString();
                    userimgurl=dataSnapshot.child("imgurl").getValue().toString();

                    if(userimgurl.equalsIgnoreCase("no image"))
                    {
                        civ.setImageResource(R.mipmap.adventure);
                    }
                    else
                    {
                        Picasso.get().load(userimgurl).into(civ);
                    }
                    ctl.setTitle(username);
                    ctl.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(),"fonts/DancingScript-Regular.otf"));
                    ctl.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(),"fonts/DancingScript-Regular.otf"));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {

        }

        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Account_Detail.this,ImageShowInFullScreen.class);
                in.putExtra("userimage",userimgurl);
                ActivityOptions activityOptions= null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(Account_Detail.this, v, "image_transition");
                }
                startActivity(in, activityOptions.toBundle());

            }
        });

        ref.child("User").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    sname =  dataSnapshot.child("name").getValue().toString();
                    name.setText(sname);
                    name.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/DancingScript-Regular.otf"));

                semail = dataSnapshot.child("email").getValue().toString();
                email.setText(semail);
                email.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/DancingScript-Regular.otf"));

                smobile = dataSnapshot.child("mobile").getValue().toString();
                mobile.setText(smobile);
                mobile.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/DancingScript-Regular.otf"));


                ssignuptime = dataSnapshot.child("time").getValue().toString();

                    Long ltime = Long.parseLong(ssignuptime);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String ftime = sdf.format(ltime);

                signuptime.setText("You are active from "+ftime);
                signuptime.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/DancingScript-Regular.otf"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (mobile.getRight() - mobile.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                            startActivity(new Intent(Account_Detail.this, Update_MobileNumber.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                    }
                }
                return true;
            }
        });


        cam_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(Account_Detail.this, Update_ProfileImage.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

        @Override
    public void onBackPressed() {

        startActivity(new Intent(Account_Detail.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        super.onBackPressed();
    }
}
