package atlair.edu.crackcomp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.Collections;


import de.hdodenhof.circleimageview.CircleImageView;

public class ShowQuestionComp extends AppCompatActivity {

    CollapsingToolbarLayout ctl;
    Toolbar tb;
    ProgressBar pb;
    RecyclerView rv;
    MyRecyclerviewAdapter ardp;
    ArrayList<Main_activity_bean> ar=new ArrayList<Main_activity_bean>();
    CircleImageView civ;

    DatabaseReference ref;
    FirebaseAuth auth;
    FirebaseUser fuser;
    String userimgurl,username;

    SharedPreferences shp;

    String subject,compexam,compmain,language;

    String option1,option2,option3,option4,ques,ans,time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_question_comp);

        ctl=findViewById(R.id.showquestionomp_collapsingtoolbar);
        tb=findViewById(R.id.showquestioncomp_toolbar);
        pb=findViewById(R.id.showquestion_progressbar);
        civ=findViewById(R.id.showquestioncomp_headerimage);
        rv=findViewById(R.id.showquestioncomp_recyclerviw);

        setSupportActionBar(tb);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

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
                Intent in=new Intent(ShowQuestionComp.this,ImageShowInFullScreen.class);
                in.putExtra("userimage",userimgurl);
                ActivityOptions activityOptions= null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    activityOptions = ActivityOptions.makeSceneTransitionAnimation(ShowQuestionComp.this, v, "image_transition");
                }
                startActivity(in, activityOptions.toBundle());

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
           ss.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        Iterable<DataSnapshot> it = dataSnapshot.getChildren();
                        for (DataSnapshot ds : it) {

                            userimgurl = ds.child("imgurl").getValue().toString();
                            username = ds.child("name").getValue().toString();
                            option1 = ds.child("option1").getValue().toString();
                            option2 = ds.child("option2").getValue().toString();
                            option3 = ds.child("option3").getValue().toString();
                            option4 = ds.child("option4").getValue().toString();

                            ques = ds.child("question").getValue(String.class);
                            ans = ds.child("ans").getValue(String.class);
                           time = ds.child("time").getValue(String.class);
                            ar.add(new Main_activity_bean(userimgurl,username,ques,option1,option2,option3,option4,ans,Long.parseLong(time)));
                            Collections.sort(ar);

                            pb.setVisibility(View.GONE);

                        }

                    }
                    else {
                        Toast.makeText(ShowQuestionComp.this, "you have no question \n Please upload your Question", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }

                    setlist();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




    }

    //    for create option menu programmatically

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.showquestion_toolbar, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search Question");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                civ.setAlpha(0.3f);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                civ.setAlpha(1.0f);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ardp.getFilter().filter(newText);
                return false;
            }
        });

        MenuItem gotoactivity = menu.findItem(R.id.gotoactivity);
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

        new Activity_Dialog_CompExam().show(getSupportFragmentManager(),"xyz");
    }

    public void setlist()
    {
        ardp=new MyRecyclerviewAdapter(this,ar);
        rv.setAdapter(ardp);
        GridLayoutManager mngr=new GridLayoutManager(this,1);
        rv.setLayoutManager(mngr);

    }

}
