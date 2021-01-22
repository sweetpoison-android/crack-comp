package atlair.edu.crackcomp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;
import com.ncorti.slidetoact.SlideToActView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Update_MobileNumber extends AppCompatActivity {

    Toolbar tb;
    CircleImageView circle_img;
    ProgressBar pb;
    CountryCodePicker ccp;
    TextInputLayout til;
    SlideToActView stv;

    String country_Code,mobile_number;

    DatabaseReference ref;
    FirebaseAuth mAuth;
    FirebaseUser fuser;

    String imgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_mobilenumber);

        tb = findViewById(R.id.updatemobilenumber_toolbar);
        circle_img = findViewById(R.id.updatemobilenumber_image);
        circle_img.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enterfrom_left));


        pb=findViewById(R.id.updatemobilenumber_progressbar);
        ccp=findViewById(R.id.updatemobilenumber_countrycode);
        til=findViewById(R.id.updatemobilenumber_textinput1);
        stv=findViewById(R.id.updatemobilenumber_slidingButton);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //   check internet connection

        boolean connected=false;
        ConnectivityManager mngr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(mngr.getNetworkInfo(mngr.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
        {
            connected = true;
            pb.setVisibility(View.GONE);
        }
        else {
            connected = false;
            pb.setVisibility(View.VISIBLE);
            AlertDialog.Builder bld=new AlertDialog.Builder(Update_MobileNumber.this);
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


        ref= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();

        if(fuser != null)
        {
            ref.child("User").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    imgurl=dataSnapshot.child("imgurl").getValue().toString();

                    if(imgurl.equalsIgnoreCase("no image"))
                    {
                        circle_img.setImageResource(R.mipmap.adventure);
                    }
                    else
                    {
                        Picasso.get().load(imgurl).into(circle_img);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {

        }

        stv.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {

                country_Code=ccp.getSelectedCountryName();
                mobile_number=til.getEditText().getText().toString();

                if(!validateNumber())
                {
                    stv.resetSlider();
                }
                else
                {
                    ref.child("User").child(fuser.getUid()).child("country").setValue(country_Code);
                    ref.child("User").child(fuser.getUid()).child("mobile").setValue(mobile_number);
                    Toast.makeText(Update_MobileNumber.this, "Mobile number updated successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    stv.resetSlider();

                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(Update_MobileNumber.this, Account_Detail.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }

    public boolean validateNumber()
    {
        if(mobile_number.isEmpty())
        {
            til.setError("Please enter mobile number");
            return false;
        }
        else {
            til.setError(null);
            return true;
        }
    }
}
