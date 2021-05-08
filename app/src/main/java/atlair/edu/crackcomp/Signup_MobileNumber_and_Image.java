package atlair.edu.crackcomp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.ncorti.slidetoact.SlideToActView;

import java.io.IOException;

public class Signup_MobileNumber_and_Image extends AppCompatActivity {

    ProgressBar pb;
    CountryCodePicker ccp;
    TextInputLayout til;
    SlideToActView stv;
    Button skip;
    Button choosebutton,cancel;
    ImageView img;
    Uri filepath;

    int PICK_IMAGE_REQUEST=234;

    String country_Code,mobile_number;

    StorageReference storageReference,downloadreference;
    DatabaseReference ref;
    FirebaseAuth mAuth;

    ProgressDialog pd;

    Check_Internet_Status internet_status = new Check_Internet_Status();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_mobile_number_and_image);

        pb=findViewById(R.id.MobileNumber_progressbar);
        ccp=findViewById(R.id.MobileNumber_countrycode);
        til=findViewById(R.id.MobileNumber_textinput1);
        stv=findViewById(R.id.MobileNumber_slidingButton);
        skip=findViewById(R.id.MobileNumber_skip);
        choosebutton=findViewById(R.id.MobileNumber_choosebutton);
        cancel=findViewById(R.id.MobileNumber_cancel);
        img=findViewById(R.id.MobileNumber_chooseimage);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        //    check internet status

        internet_status.internet_status(Signup_MobileNumber_and_Image.this, pb);

        ref= FirebaseDatabase.getInstance().getReference();
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



        storageReference= FirebaseStorage.getInstance().getReference();

        mAuth=FirebaseAuth.getInstance();

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
                    ref.child("User").child(mAuth.getCurrentUser().getUid()).child("country").setValue(country_Code);
                    ref.child("User").child(mAuth.getCurrentUser().getUid()).child("mobile").setValue(mobile_number);
                    uploadFile();

                }
    }
});

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("User").child(mAuth.getCurrentUser().getUid()).child("imgurl").setValue("no image");

                ref.child("User").child(mAuth.getCurrentUser().getUid()).child("country").setValue("no country code");
                ref.child("User").child(mAuth.getCurrentUser().getUid()).child("mobile").setValue("no mobile number");
                startActivity(new Intent(Signup_MobileNumber_and_Image.this,Login.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
                stv.resetSlider();
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
                            startActivity(new Intent(Signup_MobileNumber_and_Image.this,Login.class));
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            finish();
                            stv.resetSlider();
                            downloadreference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downUrl = uri;
                                    String final_url = downUrl.toString();


                                        ref.child("User").child(mAuth.getCurrentUser().getUid()).child("imgurl").setValue(final_url);


                                }
                            })  ;

                            Toast.makeText(getApplicationContext(), "file uploaded", Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, "you can't go back", Toast.LENGTH_SHORT).show();
        return;
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
