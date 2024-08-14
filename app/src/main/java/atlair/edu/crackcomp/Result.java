package atlair.edu.crackcomp;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.IPieInfo;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

public class Result extends AppCompatActivity {

    AnimatedPieView animatedPieView;
    Toolbar tb;
    TextView tv1,tv2,tv3;

    ReviewManager reviewManager;
    ReviewInfo reviewInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tb=findViewById(R.id.result_toolbar);
        animatedPieView = findViewById(R.id.result_piechart);
        tv1=findViewById(R.id.result_textview1);
        tv2=findViewById(R.id.result_textview2);
        tv3=findViewById(R.id.result_textview3);

        setSupportActionBar(tb);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent in=getIntent();
        int a= in.getIntExtra("total",0);
        int b=in.getIntExtra("correct",0);
        int c=in.getIntExtra("wrong",0);
        tv1.setText("Total Question : "+Integer.toString(a));
        tv2.setText("Correct : "+Integer.toString(b));
        tv3.setText("Wrong : "+Integer.toString(c));

        AnimatedPieViewConfig config = new AnimatedPieViewConfig();
        config.addData(new SimplePieInfo(b, Color.parseColor("#AA26F426"), "Correct"));
        config.addData(new SimplePieInfo(c, Color.parseColor("#F44336"), "Wrong"));
        config.duration(1000);
        config.drawText(true);
        config.textSize(50f);
        config.strokeMode(false);

        animatedPieView.applyConfig(config);
        animatedPieView.start();

        config.selectListener(new OnPieSelectListener<IPieInfo>() {
            @Override
            public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isFloatUp) {
                Toast.makeText(Result.this, pieInfo.getDesc() + " Question : " + Integer.toString((int)pieInfo.getValue()), Toast.LENGTH_SHORT).show();
            }
        });


        Give_inAppreview();
    }


    public void Give_inAppreview()
    {

        reviewManager = ReviewManagerFactory.create(Result.this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful())
                {
                    reviewInfo = task.getResult();
                    Task<Void> flow = reviewManager.launchReviewFlow(Result.this, reviewInfo);
                    flow.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(Result.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem gotoactivity=menu.add(0,0,1,"Goto Activity");
        gotoactivity.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        gotoactivity.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Result.this,GiveTest_CompExam.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
