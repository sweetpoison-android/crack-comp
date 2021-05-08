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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GiveTest_CompExam extends AppCompatActivity {

    Toolbar tb;
    ProgressBar pb;
    RelativeLayout rl;
    TextView tv1,tv2;
    Button bt1,bt2,bt3,bt4;

    SharedPreferences shp;

    DatabaseReference ref;
    FirebaseAuth auth;
    FirebaseUser fuser;

    String subject,compexam,compmain,language;
    int total=0;
    int correct=0;
    int wrong=0;
    int pre_randomnumber=0;
    int randomnumber=0;

    ArrayList<String> ar=new ArrayList<>();
    ArrayList<UploadQuestion_Bean> bean=new ArrayList<>();

    ArrayList<Integer> question_no = new ArrayList<Integer>();

    Check_Internet_Status internet_status = new Check_Internet_Status();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_test__comp_exam);

        tb=findViewById(R.id.givetest_compexam_toolbar);
        pb=findViewById(R.id.givetest_progressbar);
        rl=findViewById(R.id.givetest_compexam_relativelayout);
        tv1=findViewById(R.id.givetest_compexam_textview1);
        tv2=findViewById(R.id.givetest_compexam_textview2);
        bt1=findViewById(R.id.givetest_compexam_button1);
        bt2=findViewById(R.id.givetest_compexam_button2);
        bt3=findViewById(R.id.givetest_compexam_button3);
        bt4=findViewById(R.id.givetest_compexam_button4);

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


        //   internet_status.internet_status(GiveTest_CompExam.this, pb);  // generate question from offline so internet is not necessary

        ref= FirebaseDatabase.getInstance().getReference();
        auth= FirebaseAuth.getInstance();
        fuser=auth.getCurrentUser();


        shp=getSharedPreferences("abc",MODE_PRIVATE);
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




        if (compmain.equalsIgnoreCase("Competative Exam"))
        {
            tb.setTitle(compexam+"("+subject+")");
            ref.child("Question").child(fuser.getUid()).child(compmain).child(compexam).child(subject).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> it = dataSnapshot.getChildren();

                    for (DataSnapshot ds : it) {
                        ar.add(ds.getKey());

                    }


                    updateQuestion();


                }

                public void updateQuestion() {
                    if (ar.size() != 0) {

                        if (total == ar.size()) {
                            Intent in=new Intent(GiveTest_CompExam.this,Result.class);
                            in.putExtra("total",total);
                            in.putExtra("correct",correct);
                            in.putExtra("wrong",wrong);
                            startActivity(in);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }

                        else if (ar.size() == 1)
                        {
                            Toast.makeText(GiveTest_CompExam.this, "Please upload more Question", Toast.LENGTH_SHORT).show();
                        }


                        else
                        {
                            pb.setVisibility(View.VISIBLE);
                            internet_status.internet_status(GiveTest_CompExam.this, pb);

              //  ref.child("Question").child(fuser.getUid()).child(compmain).child(compexam).child(subject).child(ar.get(total)).addValueEventListener(new ValueEventListener() {
                            pre_randomnumber=randomnumber;

                            randomnumber=new Random().nextInt(ar.size());

                            while (randomnumber == pre_randomnumber)
                            {
                                randomnumber=new Random().nextInt(ar.size());

                            }

                            ref.child("Question").child(fuser.getUid()).child(compmain).child(compexam).child(subject).child(ar.get(randomnumber)).addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    final UploadQuestion_Bean bean = dataSnapshot.getValue(UploadQuestion_Bean.class);


                                    tv2.setText(bean.getQuestion());

                                    bt1.setText(bean.getOption1());
                                    bt2.setText(bean.getOption2());
                                    bt3.setText(bean.getOption3());
                                    bt4.setText(bean.getOption4());
                                    pb.setVisibility(View.GONE);
                                    rl.setVisibility(View.VISIBLE);

                                    tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                    bt1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (bt1.getText().toString().equals(bean.getAns())) {
                                                correct++;

                                                bt1.setBackgroundColor(Color.GREEN);
                                                bt1.setTextColor(Color.parseColor("#000000"));
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt1.setTextColor(Color.parseColor("#ffffff"));

                                                        tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));



                                                        updateQuestion();
                                                    }
                                                }, 1500);


                                            } else {
                                                wrong++;
                                                bt1.setBackgroundColor(Color.RED);
                                                bt1.setTextColor(Color.parseColor("#000000"));

                                                if (bt2.getText().toString().equals(bean.getAns())) {
                                                    bt2.setBackgroundColor(Color.GREEN);
                                                    bt2.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt3.getText().toString().equals(bean.getAns())) {
                                                    bt3.setBackgroundColor(Color.GREEN);
                                                    bt3.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt4.getText().toString().equals(bean.getAns())) {
                                                    bt4.setBackgroundColor(Color.GREEN);
                                                    bt4.setTextColor(Color.parseColor("#000000"));
                                                }

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt1.setTextColor(Color.parseColor("#ffffff"));
                                                        bt2.setTextColor(Color.parseColor("#ffffff"));
                                                        bt3.setTextColor(Color.parseColor("#ffffff"));
                                                        bt4.setTextColor(Color.parseColor("#ffffff"));
                                                        updateQuestion();
                                                    }
                                                }, 1500);

                                            }
                                        }
                                    });

                                    bt2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (bt2.getText().toString().equals(bean.getAns())) {
                                                correct++;
                                                bt2.setBackgroundColor(Color.GREEN);
                                                bt2.setTextColor(Color.parseColor("#000000"));
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt2.setTextColor(Color.parseColor("#ffffff"));
                                                        tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                                        updateQuestion();
                                                    }
                                                }, 1500);


                                            } else {
                                                wrong++;
                                                bt2.setBackgroundColor(Color.RED);

                                                if (bt1.getText().toString().equals(bean.getAns())) {
                                                    bt1.setBackgroundColor(Color.GREEN);
                                                    bt1.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt3.getText().toString().equals(bean.getAns())) {
                                                    bt3.setBackgroundColor(Color.GREEN);
                                                    bt3.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt4.getText().toString().equals(bean.getAns())) {
                                                    bt4.setBackgroundColor(Color.GREEN);
                                                    bt4.setTextColor(Color.parseColor("#000000"));
                                                }

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt1.setTextColor(Color.parseColor("#ffffff"));
                                                        bt2.setTextColor(Color.parseColor("#ffffff"));
                                                        bt3.setTextColor(Color.parseColor("#ffffff"));
                                                        bt4.setTextColor(Color.parseColor("#ffffff"));
                                                        updateQuestion();
                                                    }
                                                }, 1500);

                                            }
                                        }
                                    });

                                    bt3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (bt3.getText().toString().equals(bean.getAns())) {
                                                correct++;
                                                bt3.setBackgroundColor(Color.GREEN);
                                                bt3.setTextColor(Color.parseColor("#000000"));
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt3.setTextColor(Color.parseColor("#ffffff"));

                                                        tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                                        updateQuestion();
                                                    }
                                                }, 1500);


                                            }

                                            else
                                            {
                                                wrong++;
                                                bt3.setBackgroundColor(Color.RED);
                                                bt3.setTextColor(Color.parseColor("#000000"));

                                                if (bt1.getText().toString().equals(bean.getAns())) {
                                                    bt1.setBackgroundColor(Color.GREEN);
                                                    bt1.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt2.getText().toString().equals(bean.getAns())) {
                                                    bt2.setBackgroundColor(Color.GREEN);
                                                    bt2.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt4.getText().toString().equals(bean.getAns())) {
                                                    bt4.setBackgroundColor(Color.GREEN);
                                                    bt4.setTextColor(Color.parseColor("#000000"));
                                                }

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt1.setTextColor(Color.parseColor("#ffffff"));
                                                        bt2.setTextColor(Color.parseColor("#ffffff"));
                                                        bt3.setTextColor(Color.parseColor("#ffffff"));
                                                        bt4.setTextColor(Color.parseColor("#ffffff"));
                                                        updateQuestion();
                                                    }
                                                }, 1500);

                                            }
                                        }
                                    });

                                    bt4.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (bt4.getText().toString().equals(bean.getAns())) {
                                                correct++;
                                                bt4.setBackgroundColor(Color.GREEN);
                                                bt4.setTextColor(Color.parseColor("#000000"));
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt4.setTextColor(Color.parseColor("#ffffff"));

                                                        tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                                        updateQuestion();
                                                    }
                                                }, 1500);


                                            } else {
                                                wrong++;
                                                bt4.setBackgroundColor(Color.RED);
                                                bt4.setTextColor(Color.parseColor("#000000"));

                                                if (bt1.getText().toString().equals(bean.getAns())) {
                                                    bt1.setBackgroundColor(Color.GREEN);
                                                    bt1.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt2.getText().toString().equals(bean.getAns())) {
                                                    bt2.setBackgroundColor(Color.GREEN);
                                                    bt2.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt3.getText().toString().equals(bean.getAns())) {
                                                    bt3.setBackgroundColor(Color.GREEN);
                                                    bt3.setTextColor(Color.parseColor("#000000"));
                                                }

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt1.setTextColor(Color.parseColor("#ffffff"));
                                                        bt2.setTextColor(Color.parseColor("#ffffff"));
                                                        bt3.setTextColor(Color.parseColor("#ffffff"));
                                                        bt4.setTextColor(Color.parseColor("#ffffff"));
                                                        updateQuestion();
                                                    }
                                                }, 1500);

                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            total++;

                        }
                    }
                    else
                    {
                        Toast.makeText(GiveTest_CompExam.this, "You have no question \n please upload your question", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                        rl.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

else if (compmain.equalsIgnoreCase("Computer Language"))
{
    tb.setTitle(language);
    ref.child("Question").child(fuser.getUid()).child(compmain).child(language).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> it = dataSnapshot.getChildren();

            for (DataSnapshot ds : it) {
                ar.add(ds.getKey());

            }


            updateQuestion();


        }

        public void updateQuestion() {
            if (ar.size() != 0) {

                if (total == ar.size()) {
                    Intent in=new Intent(GiveTest_CompExam.this,Result.class);
                    in.putExtra("total",total);
                    in.putExtra("correct",correct);
                    in.putExtra("wrong",wrong);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }

                else if (ar.size() == 1)
                {
                    Toast.makeText(GiveTest_CompExam.this, "Please upload more Question", Toast.LENGTH_SHORT).show();
                }


                else
                {
                    pb.setVisibility(View.VISIBLE);

                    //   internet_status.internet_status(GiveTest_CompExam.this, pb);  // show question from offline so internet is not necessary

//                ref.child("Question").child(fuser.getUid()).child(compmain).child(compexam).child(subject).child(ar.get(total)).addValueEventListener(new ValueEventListener() {
                    pre_randomnumber=randomnumber;
                    randomnumber=new Random().nextInt(ar.size());
                    while (randomnumber == pre_randomnumber)
                    {
                        randomnumber=new Random().nextInt(ar.size());
                    }


                    ref.child("Question").child(fuser.getUid()).child(compmain).child(language).child(ar.get(randomnumber)).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            final UploadQuestion_Bean bean = dataSnapshot.getValue(UploadQuestion_Bean.class);


                            tv2.setText(bean.getQuestion());

                            bt1.setText(bean.getOption1());
                            bt2.setText(bean.getOption2());
                            bt3.setText(bean.getOption3());
                            bt4.setText(bean.getOption4());
                            pb.setVisibility(View.GONE);
                            rl.setVisibility(View.VISIBLE);

                            tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                            bt1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (bt1.getText().toString().equals(bean.getAns())) {
                                        correct++;

                                        bt1.setBackgroundColor(Color.GREEN);
                                        bt1.setTextColor(Color.parseColor("#000000"));
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt1.setTextColor(Color.parseColor("#ffffff"));

                                                tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));



                                                updateQuestion();
                                            }
                                        }, 1500);


                                    } else {
                                        wrong++;
                                        bt1.setBackgroundColor(Color.RED);
                                        bt1.setTextColor(Color.parseColor("#000000"));

                                        if (bt2.getText().toString().equals(bean.getAns())) {
                                            bt2.setBackgroundColor(Color.GREEN);
                                            bt2.setTextColor(Color.parseColor("#000000"));
                                        } else if (bt3.getText().toString().equals(bean.getAns())) {
                                            bt3.setBackgroundColor(Color.GREEN);
                                            bt3.setTextColor(Color.parseColor("#000000"));
                                        } else if (bt4.getText().toString().equals(bean.getAns())) {
                                            bt4.setBackgroundColor(Color.GREEN);
                                            bt4.setTextColor(Color.parseColor("#000000"));
                                        }

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt1.setTextColor(Color.parseColor("#ffffff"));
                                                bt2.setTextColor(Color.parseColor("#ffffff"));
                                                bt3.setTextColor(Color.parseColor("#ffffff"));
                                                bt4.setTextColor(Color.parseColor("#ffffff"));
                                                updateQuestion();
                                            }
                                        }, 1500);

                                    }
                                }
                            });

                            bt2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (bt2.getText().toString().equals(bean.getAns())) {
                                        correct++;
                                        bt2.setBackgroundColor(Color.GREEN);
                                        bt2.setTextColor(Color.parseColor("#000000"));
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt2.setTextColor(Color.parseColor("#ffffff"));
                                                tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                                updateQuestion();
                                            }
                                        }, 1500);


                                    } else {
                                        wrong++;
                                        bt2.setBackgroundColor(Color.RED);

                                        if (bt1.getText().toString().equals(bean.getAns())) {
                                            bt1.setBackgroundColor(Color.GREEN);
                                            bt1.setTextColor(Color.parseColor("#000000"));
                                        } else if (bt3.getText().toString().equals(bean.getAns())) {
                                            bt3.setBackgroundColor(Color.GREEN);
                                            bt3.setTextColor(Color.parseColor("#000000"));
                                        } else if (bt4.getText().toString().equals(bean.getAns())) {
                                            bt4.setBackgroundColor(Color.GREEN);
                                            bt4.setTextColor(Color.parseColor("#000000"));
                                        }

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt1.setTextColor(Color.parseColor("#ffffff"));
                                                bt2.setTextColor(Color.parseColor("#ffffff"));
                                                bt3.setTextColor(Color.parseColor("#ffffff"));
                                                bt4.setTextColor(Color.parseColor("#ffffff"));
                                                updateQuestion();
                                            }
                                        }, 1500);

                                    }
                                }
                            });

                            bt3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (bt3.getText().toString().equals(bean.getAns())) {
                                        correct++;
                                        bt3.setBackgroundColor(Color.GREEN);
                                        bt3.setTextColor(Color.parseColor("#000000"));
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt3.setTextColor(Color.parseColor("#ffffff"));

                                                tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                                updateQuestion();
                                            }
                                        }, 1500);


                                    }

                                    else
                                        {
                                        wrong++;
                                        bt3.setBackgroundColor(Color.RED);
                                        bt3.setTextColor(Color.parseColor("#000000"));

                                        if (bt1.getText().toString().equals(bean.getAns())) {
                                            bt1.setBackgroundColor(Color.GREEN);
                                            bt1.setTextColor(Color.parseColor("#000000"));
                                        } else if (bt2.getText().toString().equals(bean.getAns())) {
                                            bt2.setBackgroundColor(Color.GREEN);
                                            bt2.setTextColor(Color.parseColor("#000000"));
                                        } else if (bt4.getText().toString().equals(bean.getAns())) {
                                            bt4.setBackgroundColor(Color.GREEN);
                                            bt4.setTextColor(Color.parseColor("#000000"));
                                        }

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt1.setTextColor(Color.parseColor("#ffffff"));
                                                bt2.setTextColor(Color.parseColor("#ffffff"));
                                                bt3.setTextColor(Color.parseColor("#ffffff"));
                                                bt4.setTextColor(Color.parseColor("#ffffff"));
                                                updateQuestion();
                                            }
                                        }, 1500);

                                    }
                                }
                            });

                            bt4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (bt4.getText().toString().equals(bean.getAns())) {
                                        correct++;
                                        bt4.setBackgroundColor(Color.GREEN);
                                        bt4.setTextColor(Color.parseColor("#000000"));
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt4.setTextColor(Color.parseColor("#ffffff"));

                                                tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                                updateQuestion();
                                            }
                                        }, 1500);


                                    } else {
                                        wrong++;
                                        bt4.setBackgroundColor(Color.RED);
                                        bt4.setTextColor(Color.parseColor("#000000"));

                                        if (bt1.getText().toString().equals(bean.getAns())) {
                                            bt1.setBackgroundColor(Color.GREEN);
                                            bt1.setTextColor(Color.parseColor("#000000"));
                                        } else if (bt2.getText().toString().equals(bean.getAns())) {
                                            bt2.setBackgroundColor(Color.GREEN);
                                            bt2.setTextColor(Color.parseColor("#000000"));
                                        } else if (bt3.getText().toString().equals(bean.getAns())) {
                                            bt3.setBackgroundColor(Color.GREEN);
                                            bt3.setTextColor(Color.parseColor("#000000"));
                                        }

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                bt1.setTextColor(Color.parseColor("#ffffff"));
                                                bt2.setTextColor(Color.parseColor("#ffffff"));
                                                bt3.setTextColor(Color.parseColor("#ffffff"));
                                                bt4.setTextColor(Color.parseColor("#ffffff"));
                                                updateQuestion();
                                            }
                                        }, 1500);

                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    total++;

                }
            }
            else
            {
                Toast.makeText(GiveTest_CompExam.this, "You have no question \n please upload your question", Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
                rl.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

}

//        else if (compmain.equalsIgnoreCase("General Question") || compmain.equalsIgnoreCase("Current Affairs") )

       else
        {
            tb.setTitle(compmain);

            ref.child("Question").child(fuser.getUid()).child(compmain).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> it = dataSnapshot.getChildren();

                    for (DataSnapshot ds : it) {
                        ar.add(ds.getKey());

                    }


                    updateQuestion();


                }



                public void updateQuestion()
                {
                    if (ar.size() != 0)
                    {

                        if (total == ar.size()) {
                            Intent in=new Intent(GiveTest_CompExam.this,Result.class);
                            in.putExtra("total",total);
                            in.putExtra("correct",correct);
                            in.putExtra("wrong",wrong);
                            startActivity(in);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }

                        else if (ar.size() == 1)
                        {
                            Toast.makeText(GiveTest_CompExam.this, "Please upload more Question", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            pb.setVisibility(View.VISIBLE);

                         //   internet_status.internet_status(GiveTest_CompExam.this, pb);  // show question from offline so internet is not necessary

//                ref.child("Question").child(fuser.getUid()).child(compmain).child(compexam).child(subject).child(ar.get(total)).addValueEventListener(new ValueEventListener() {
                            pre_randomnumber=randomnumber;
                            randomnumber=new Random().nextInt(ar.size());
                            while (randomnumber == pre_randomnumber)
                            {
                                randomnumber=new Random().nextInt(ar.size());
                            }


                            ref.child("Question").child(fuser.getUid()).child(compmain).child(ar.get(randomnumber)).addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    final UploadQuestion_Bean bean = dataSnapshot.getValue(UploadQuestion_Bean.class);


                                    tv2.setText(bean.getQuestion());

                                    bt1.setText(bean.getOption1());
                                    bt2.setText(bean.getOption2());
                                    bt3.setText(bean.getOption3());
                                    bt4.setText(bean.getOption4());
                                    pb.setVisibility(View.GONE);
                                    rl.setVisibility(View.VISIBLE);

                                    tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                    bt1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (bt1.getText().toString().equals(bean.getAns())) {
                                                correct++;

                                                bt1.setBackgroundColor(Color.GREEN);
                                                bt1.setTextColor(Color.parseColor("#000000"));
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt1.setTextColor(Color.parseColor("#ffffff"));

                                                        tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));



                                                        updateQuestion();
                                                    }
                                                }, 1500);


                                            } else {
                                                wrong++;
                                                bt1.setBackgroundColor(Color.RED);
                                                bt1.setTextColor(Color.parseColor("#000000"));

                                                if (bt2.getText().toString().equals(bean.getAns())) {
                                                    bt2.setBackgroundColor(Color.GREEN);
                                                    bt2.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt3.getText().toString().equals(bean.getAns())) {
                                                    bt3.setBackgroundColor(Color.GREEN);
                                                    bt3.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt4.getText().toString().equals(bean.getAns())) {
                                                    bt4.setBackgroundColor(Color.GREEN);
                                                    bt4.setTextColor(Color.parseColor("#000000"));
                                                }

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt1.setTextColor(Color.parseColor("#ffffff"));
                                                        bt2.setTextColor(Color.parseColor("#ffffff"));
                                                        bt3.setTextColor(Color.parseColor("#ffffff"));
                                                        bt4.setTextColor(Color.parseColor("#ffffff"));
                                                        updateQuestion();
                                                    }
                                                }, 1500);

                                            }
                                        }
                                    });

                                    bt2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (bt2.getText().toString().equals(bean.getAns())) {
                                                correct++;
                                                bt2.setBackgroundColor(Color.GREEN);
                                                bt2.setTextColor(Color.parseColor("#000000"));
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt2.setTextColor(Color.parseColor("#ffffff"));
                                                        tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                                        updateQuestion();
                                                    }
                                                }, 1500);


                                            } else {
                                                wrong++;
                                                bt2.setBackgroundColor(Color.RED);

                                                if (bt1.getText().toString().equals(bean.getAns())) {
                                                    bt1.setBackgroundColor(Color.GREEN);
                                                    bt1.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt3.getText().toString().equals(bean.getAns())) {
                                                    bt3.setBackgroundColor(Color.GREEN);
                                                    bt3.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt4.getText().toString().equals(bean.getAns())) {
                                                    bt4.setBackgroundColor(Color.GREEN);
                                                    bt4.setTextColor(Color.parseColor("#000000"));
                                                }

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt1.setTextColor(Color.parseColor("#ffffff"));
                                                        bt2.setTextColor(Color.parseColor("#ffffff"));
                                                        bt3.setTextColor(Color.parseColor("#ffffff"));
                                                        bt4.setTextColor(Color.parseColor("#ffffff"));
                                                        updateQuestion();
                                                    }
                                                }, 1500);

                                            }
                                        }
                                    });

                                    bt3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (bt3.getText().toString().equals(bean.getAns())) {
                                                correct++;
                                                bt3.setBackgroundColor(Color.GREEN);
                                                bt3.setTextColor(Color.parseColor("#000000"));
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt3.setTextColor(Color.parseColor("#ffffff"));

                                                        tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                                        updateQuestion();
                                                    }
                                                }, 1500);


                                            }

                                            else
                                            {
                                                wrong++;
                                                bt3.setBackgroundColor(Color.RED);
                                                bt3.setTextColor(Color.parseColor("#000000"));

                                                if (bt1.getText().toString().equals(bean.getAns())) {
                                                    bt1.setBackgroundColor(Color.GREEN);
                                                    bt1.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt2.getText().toString().equals(bean.getAns())) {
                                                    bt2.setBackgroundColor(Color.GREEN);
                                                    bt2.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt4.getText().toString().equals(bean.getAns())) {
                                                    bt4.setBackgroundColor(Color.GREEN);
                                                    bt4.setTextColor(Color.parseColor("#000000"));
                                                }

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt1.setTextColor(Color.parseColor("#ffffff"));
                                                        bt2.setTextColor(Color.parseColor("#ffffff"));
                                                        bt3.setTextColor(Color.parseColor("#ffffff"));
                                                        bt4.setTextColor(Color.parseColor("#ffffff"));
                                                        updateQuestion();
                                                    }
                                                }, 1500);

                                            }
                                        }
                                    });

                                    bt4.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (bt4.getText().toString().equals(bean.getAns())) {
                                                correct++;
                                                bt4.setBackgroundColor(Color.GREEN);
                                                bt4.setTextColor(Color.parseColor("#000000"));
                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt4.setTextColor(Color.parseColor("#ffffff"));

                                                        tv1.setText(Integer.toString(correct) + "/" + Integer.toString(total));
                                                        updateQuestion();
                                                    }
                                                }, 1500);


                                            } else {
                                                wrong++;
                                                bt4.setBackgroundColor(Color.RED);
                                                bt4.setTextColor(Color.parseColor("#000000"));

                                                if (bt1.getText().toString().equals(bean.getAns())) {
                                                    bt1.setBackgroundColor(Color.GREEN);
                                                    bt1.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt2.getText().toString().equals(bean.getAns())) {
                                                    bt2.setBackgroundColor(Color.GREEN);
                                                    bt2.setTextColor(Color.parseColor("#000000"));
                                                } else if (bt3.getText().toString().equals(bean.getAns())) {
                                                    bt3.setBackgroundColor(Color.GREEN);
                                                    bt3.setTextColor(Color.parseColor("#000000"));
                                                }

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        bt1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                        bt1.setTextColor(Color.parseColor("#ffffff"));
                                                        bt2.setTextColor(Color.parseColor("#ffffff"));
                                                        bt3.setTextColor(Color.parseColor("#ffffff"));
                                                        bt4.setTextColor(Color.parseColor("#ffffff"));
                                                        updateQuestion();
                                                    }
                                                }, 1500);

                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            total++;

                        }
                    }
                    else
                    {
                        Toast.makeText(GiveTest_CompExam.this, "You have no question \n please upload your question", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                        rl.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        new Activity_Dialog_CompExam().show(getSupportFragmentManager(),"xyz");
    }

}