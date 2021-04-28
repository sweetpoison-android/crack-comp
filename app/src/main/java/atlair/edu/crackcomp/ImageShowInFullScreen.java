package atlair.edu.crackcomp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageShowInFullScreen extends AppCompatActivity {


    ImageView img;
    String userimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show_in_full_screen);


        img=findViewById(R.id.Imageshowinfullscreen_imageview);

        userimg=getIntent().getStringExtra("userimage");

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
if (userimg.equalsIgnoreCase("no image"))
{
    img.setImageResource(R.mipmap.adventure);
}
else
{
    Picasso.get().load(userimg).into(img);
}

    }
}
