package atlair.edu.crackcomp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Result extends AppCompatActivity {

    Toolbar tb;
    TextView tv1,tv2,tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tb=findViewById(R.id.result_toolbar);
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Result.this,GiveTest_CompExam.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
