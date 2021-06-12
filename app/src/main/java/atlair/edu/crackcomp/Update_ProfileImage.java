package atlair.edu.crackcomp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.ncorti.slidetoact.SlideToActView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Update_ProfileImage extends AppCompatActivity {

    Toolbar tb;
    CircleImageView circle_img;
    ProgressBar pb;
    SlideToActView stv;

    Button choosebutton,cancel;
    ImageView img;

    Uri filepath;

    int PICK_IMAGE_REQUEST=234;


    StorageReference storageReference,downloadreference;
    DatabaseReference ref;
    FirebaseAuth mAuth;
    FirebaseUser fuser;

    ProgressDialog pd;

    String imgurl;

    Check_Internet_Status internet_status = new Check_Internet_Status();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__profile_image);

        tb = findViewById(R.id.updateprofileimage_toolbar);
        circle_img = findViewById(R.id.updateprofileimage_image);
        circle_img.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enterfrom_left));

        pb=findViewById(R.id.updateprofileimage_progressbar);
        stv=findViewById(R.id.updateprofileimage_slidingButton);
        choosebutton=findViewById(R.id.updateprofileimage_choosebutton);
        cancel=findViewById(R.id.updateprofileimage_cancel);
        img=findViewById(R.id.updateprofileimage_imageview);

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


        //    check Internet Connection  --------------------------

        internet_status.internet_status(Update_ProfileImage.this, pb);

        ref= FirebaseDatabase.getInstance().getReference();
        storageReference= FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        fuser = mAuth.getCurrentUser();

        if(fuser != null)
        {
            ref.child("User").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username=dataSnapshot.child("name").getValue().toString();
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

//----------------------------------------------------------------------------------------------------


        choosebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();
            }
        });

        stv.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {

                uploadFile();

                }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filepath=data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        choosebutton.setVisibility(View.GONE);
        img.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);

    }

    public void chooseImage()
    {
        Intent in=new Intent();
        in.setType("image/*");
        in.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(in,"image"),PICK_IMAGE_REQUEST);

    }

    public void uploadFile()
    {
        if (filepath != null)
        {
            pd=new ProgressDialog(this);
            pd.setTitle("Uploading");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(false);
            pd.show();

            downloadreference=storageReference.child("UserImage/"+mAuth.getCurrentUser().getUid());
            downloadreference.putFile(filepath)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            startActivity(new Intent(Update_ProfileImage.this,Account_Detail.class));
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            finish();
                            stv.resetSlider();
                            downloadreference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downUrl = uri;
                                    String final_url = downUrl.toString();


                                    ref.child("User").child(mAuth.getCurrentUser().getUid()).child("imgurl").setValue(final_url);



                                    ref.child("User").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            imgurl = dataSnapshot.child("imgurl").getValue().toString();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                     //            update question image   --------------------------------------------------------------------------------


                                                ref.child("Question").child(mAuth.getCurrentUser().getUid()).child("Competative Exam").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        for (DataSnapshot railway : dataSnapshot.getChildren())
                                                        {
                                                            if (railway != null)
                                                            {
                                                                for (DataSnapshot gk : railway.getChildren())
                                                                {

                                                                    if (gk.exists())
                                                                    {
                                                                        Iterable<DataSnapshot> it = gk.getChildren();
                                                                        for (DataSnapshot ds : it) {
                                                                      ds.getRef().child("imgurl").setValue(imgurl);

                                                                        }

                                                                    }
                                                                }
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                                ref.child("Question").child(mAuth.getCurrentUser().getUid()).child("Computer Language").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        for (DataSnapshot com_language : dataSnapshot.getChildren())
                                                        {
                                                            if (com_language.exists()) {

                                                                Iterable<DataSnapshot> it = com_language.getChildren();
                                                                for (DataSnapshot ds : it) {

                                                                    ds.getRef().child("imgurl").setValue(imgurl);

                                                                }


                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                                ref.child("Question").child(mAuth.getCurrentUser().getUid()).child("Other Question").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        Iterable<DataSnapshot> it = dataSnapshot.getChildren();
                                                        for (DataSnapshot ds : it) {
                                                            ds.getRef().child("imgurl").setValue(imgurl);

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                                ref.child("Question").child(mAuth.getCurrentUser().getUid()).child("Current Affairs").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                                        Iterable<DataSnapshot> it = dataSnapshot.getChildren();
                                                        for (DataSnapshot ds : it) {

                                                            ds.getRef().child("imgurl").setValue(imgurl);
                                                        }

                                                    }



                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });

                            Toast.makeText(getApplicationContext(), "Profile Photo Updated successfully", Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(final UploadTask.TaskSnapshot taskSnapshot) {

                    int progress=(int)(100*taskSnapshot.getBytesTransferred())/(int)taskSnapshot.getTotalByteCount();
                    pd.setProgress(progress);
                }
            });

        }
        else
        {
            Toast.makeText(this, "please choose your image", Toast.LENGTH_SHORT).show();
            stv.resetSlider();

        }

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(Update_ProfileImage.this, Account_Detail.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
         super.onBackPressed();
    }


}
