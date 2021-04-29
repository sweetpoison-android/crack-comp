package atlair.edu.crackcomp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ncorti.slidetoact.SlideToActView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Update_Question extends AppCompatActivity {

    Toolbar tb;
    TextInputLayout til1,til2,til3,til4,til5,til6;
    AutoCompleteTextView actv;
    SlideToActView stv;
    Button cancel;

    String question,option1,option2,option3,option4,ans;

    DatabaseReference ref;
    FirebaseAuth auth;
    FirebaseUser fuser;

    SharedPreferences shp,shp1;

    String subject;
    String compexam;
    String compmain;
    String language;
    String question_position;

    ArrayList<String> ar=new ArrayList<>();

    int reverse;

    UploadQuestion_Bean bean = new UploadQuestion_Bean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_question);

        tb=findViewById(R.id.uploadquestion_toolbar);
        til1=findViewById(R.id.uploadquestion_textinput1);
        til2=findViewById(R.id.uploadquestion_textinput2);
        til3=findViewById(R.id.uploadquestion_textinput3);
        til4=findViewById(R.id.uploadquestion_textinput4);
        til5=findViewById(R.id.uploadquestion_textinput5);
        til6=findViewById(R.id.uploadquestion_textinput6);
        actv=findViewById(R.id.uploadquestion_autocomplete);
        stv=findViewById(R.id.uploadquestion_slidingButton);
        cancel=findViewById(R.id.uploadquestion_cancel);
        setSupportActionBar(tb);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        stv.setText("Slide for Update");

        subject=new String();
        compexam=new String();
        compmain=new String();
        language=new String();
        question_position=new String();


        ref= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        fuser=auth.getCurrentUser();

        shp=getSharedPreferences("abc",MODE_PRIVATE);
        shp1=getSharedPreferences("xyz",MODE_PRIVATE);
        if (shp.getString("subject",null) != null)
        {
            subject=shp.getString("subject",null);
        }

        if (shp.getString("compexam",null) != null)
        {
            compexam=shp.getString("compexam",null);
        }

        if (shp.getString("main",null) != null)
        {
            compmain=shp.getString("main",null);
        }

        if (shp.getString("language",null) != null)
        {
            language=shp.getString("language",null);
        }

        tb.setTitle("Update Question");

        tb.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        til6.getEditText().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                String[] location={til2.getEditText().getText().toString(),til3.getEditText().getText().toString(),til4.getEditText().getText().toString(),til5.getEditText().getText().toString()};
                ArrayAdapter<String> ardp=new ArrayAdapter<String>(Update_Question.this,android.R.layout.simple_list_item_1,location);
                actv.setThreshold(1);
                actv.setAdapter(ardp);

                return false;
            }
        });

       DatabaseReference ss = null;
        if(compmain.equalsIgnoreCase("Competative Exam")) {
            ss = ref.child("Question").child(fuser.getUid()).child(compmain).child(compexam).child(subject);
        }
        else if(compmain.equalsIgnoreCase("Computer Language")) {
            ss = ref.child("Question").child(fuser.getUid()).child(compmain).child(language);
        }
        else if(compmain.equalsIgnoreCase("Current Affairs") || compmain.equalsIgnoreCase("Other Question")) {
            ss = ref.child("Question").child(fuser.getUid()).child(compmain);
        }
        final DatabaseReference finalSs = ss;
        ss.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Iterable<DataSnapshot> it = dataSnapshot.getChildren();

               for (DataSnapshot ds : it) {
                   ar.add(ds.getKey());

               }
               Intent in=getIntent();
              String question_position1=in.getStringExtra("question_position");
               reverse = ar.size()-(Integer.parseInt(question_position1))-1;
               finalSs.child(ar.get(reverse)).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       bean = dataSnapshot.getValue(UploadQuestion_Bean.class);
                       if (dataSnapshot.exists()) {
                           til1.getEditText().setText(bean.getQuestion());
                           til2.getEditText().setText(bean.getOption1());
                           til3.getEditText().setText(bean.getOption2());
                           til4.getEditText().setText(bean.getOption3());
                           til5.getEditText().setText(bean.getOption4());
                           til6.getEditText().setText(bean.getAns());
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });





        stv.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {


                question=til1.getEditText().getText().toString();
                option1=til2.getEditText().getText().toString();
                option2=til3.getEditText().getText().toString();
                option3=til4.getEditText().getText().toString();
                option4=til5.getEditText().getText().toString();
                ans=actv.getText().toString();
                String time=Long.toString(new Date().getTime());

                if(!validateQuestion() | !validateOption1() | !validateOption2() | !validateOption3() | !validateOption4() | !validateAns())
                {

                    stv.resetSlider();
                }
                else
                {
                    ref.child("Question").child(fuser.getUid()).child(compmain).child(compexam).child(subject).child(language).child(ar.get(reverse)).setValue(new UploadQuestion_Bean(question, option1, option2, option3, option4, ans, bean.getTime(), bean.getName(), bean.getImgurl()));

                    stv.resetSlider();
                    reset();

                     onBackPressed();

                    Toast.makeText(Update_Question.this, "Question updated successfully", Toast.LENGTH_SHORT).show();

                }

            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void reset()
    {
        til1.getEditText().setText("");
        til2.getEditText().setText("");
        til3.getEditText().setText("");
        til4.getEditText().setText("");
        til5.getEditText().setText("");
        actv.setText("");
    }

    public boolean validateQuestion()
    {
        if(question.isEmpty())
        {
            til1.setError("Please enter Question");
            return false;
        }
        else {
            til1.setError(null);
            return true;
        }
    }
    public boolean validateOption1()
    {
        if(option1.isEmpty())
        {
            til2.setError("Please enter option A");
            return false;
        }
        else {
            til2.setError(null);
            return true;
        }
    }
    public boolean validateOption2()
    {
        if(option2.isEmpty())
        {
            til3.setError("Please enter option B");
            return false;
        }
        else {
            til3.setError(null);
            return true;
        }
    }
    public boolean validateOption3()
    {
        if(option3.isEmpty())
        {
            til4.setError("Please enter option C");
            return false;
        }
        else {
            til4.setError(null);
            return true;
        }
    }
    public boolean validateOption4()
    {
        if(option4.isEmpty())
        {
            til5.setError("Please enter option D");
            return false;
        }
        else {
            til5.setError(null);
            return true;
        }
    }
    public boolean validateAns()
    {
        if(ans.isEmpty())
        {
            til6.setError("Please enter Ans");
            return false;
        }
        else {
            til6.setError(null);
            return true;
        }
    }


    //    for create option menu programmatically

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem search=menu.add(0,0,1,"Goto Activity");
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

       startActivity(new Intent(getApplicationContext(),ShowQuestionComp.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        super.onBackPressed();
    }
}
