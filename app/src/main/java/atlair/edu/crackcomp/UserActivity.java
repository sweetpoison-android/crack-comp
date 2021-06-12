package atlair.edu.crackcomp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {

    CollapsingToolbarLayout ctl;
    Toolbar tb;
    CircleImageView civ;
    ProgressBar pb;
    RecyclerView rv;

    DatabaseReference ref;
    FirebaseAuth auth;
    FirebaseUser fuser;

    String userimgurl,username;
    String ques,option1,option2,option3,option4,ans,time;


    ArrayList<Main_activity_bean> ar1 = new ArrayList<>();
    ArrayList<Main_activity_bean> ar2 = new ArrayList<>();
    ArrayList<Main_activity_bean> ar3 = new ArrayList<>();
    ArrayList<Main_activity_bean> ar4 = new ArrayList<>();
    Main_activity_recyclerviewAdapter ardp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ctl=findViewById(R.id.user_collapsingtoolbar);
        tb=findViewById(R.id.user_toolbar);
        civ=findViewById(R.id.user_headerimage);
        pb = findViewById(R.id.user_progressbar);
        rv=findViewById(R.id.user_recyclerview);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        ref= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        fuser=auth.getCurrentUser();

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
               Intent in=new Intent(UserActivity.this,ImageShowInFullScreen.class);
               in.putExtra("userimage",userimgurl);
               ActivityOptions activityOptions= null;
               if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                   activityOptions = ActivityOptions.makeSceneTransitionAnimation(UserActivity.this, v, "image_transition");
               }
               startActivity(in, activityOptions.toBundle());

           }
       });

ref.child("Question").child(fuser.getUid()).child("Competative Exam").addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot railway : dataSnapshot.getChildren())
                {
                    if (railway != null)
                    {
                        for (DataSnapshot gk : railway.getChildren())
                        {

                            if (gk.exists()) {

                                Iterable<DataSnapshot> it = gk.getChildren();
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

                                    ar1.add(new Main_activity_bean(userimgurl,username,ques,option1,option2,option3,option4,ans,Long.parseLong(time)));
                                    pb.setVisibility(View.GONE);

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

ref.child("Question").child(fuser.getUid()).child("Computer Language").addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot com_language : dataSnapshot.getChildren())
                {
                    if (com_language.exists()) {

                        Iterable<DataSnapshot> it = com_language.getChildren();
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

                            ar2.add(new Main_activity_bean(userimgurl,username,ques,option1,option2,option3,option4,ans,Long.parseLong(time)));

                            pb.setVisibility(View.GONE);

                        }

                    }
                }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});

        ref.child("Question").child(fuser.getUid()).child("Current Affairs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

                            ar3.add(new Main_activity_bean(userimgurl,username,ques,option1,option2,option3,option4,ans,Long.parseLong(time)));
                            pb.setVisibility(View.GONE);

                        }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("Question").child(fuser.getUid()).child("Other Question").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


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

                            ar4.add(new Main_activity_bean(userimgurl,username,ques,option1,option2,option3,option4,ans,Long.parseLong(time)));
                            pb.setVisibility(View.GONE);

                        }


                setlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu,menu);

       MenuItem search = menu.findItem(R.id.search);
       SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search Question");

       searchView.setOnSearchClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               menu.findItem(R.id.upload).setVisible(false);
               civ.setAlpha(0.3f);

           }
       });

       searchView.setOnCloseListener(new SearchView.OnCloseListener() {
           @Override
           public boolean onClose() {
               menu.findItem(R.id.upload).setVisible(true);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.upload :
               new MainDialog().show(getSupportFragmentManager(),"xyz");

            case R.id.all_ques :
                setlist();
                break;

            case R.id.compexam_ques :
                setlist1();
                break;

            case R.id.complang_ques :
                setlist2();
                break;

            case R.id.current_ques :
                setlist3();
                break;

            case R.id.othe_ques :
                setlist4();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void setlist() {
        ArrayList<Main_activity_bean> ar = new ArrayList<>();

        ar.addAll(ar1);
        ar.addAll(ar2);
        ar.addAll(ar3);
        ar.addAll(ar4);

        if (ar.size() == 0) {
            Toast.makeText(this, "You have not any Question this type of", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ardp = new Main_activity_recyclerviewAdapter(this, ar);
            rv.setAdapter(ardp);
             Collections.sort(ar);
             GridLayoutManager mngr = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mngr);
        }
    }

    public void setlist1()
    {
        if (ar1.size() == 0)
        {
            Toast.makeText(this, "You have not any Question this type of", Toast.LENGTH_SHORT).show();
        }
        else {
            ardp = new Main_activity_recyclerviewAdapter(this, ar1);
            rv.setAdapter(ardp);
            Collections.sort(ar1);
            GridLayoutManager mngr = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mngr);
        }
    }
    public void setlist2()
    {

        if (ar2.size() == 0)
        {
            Toast.makeText(this, "You have not any Question this type of", Toast.LENGTH_SHORT).show();
        }
        else {
            ardp = new Main_activity_recyclerviewAdapter(this, ar2);
            rv.setAdapter(ardp);
             Collections.sort(ar2);
             GridLayoutManager mngr = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mngr);
        }
    }
    public void setlist3()
    {
        if (ar3.size() == 0)
        {
            Toast.makeText(this, "You have not any Question this type of", Toast.LENGTH_SHORT).show();
        }
        else
            {
            ardp = new Main_activity_recyclerviewAdapter(this, ar3);
            rv.setAdapter(ardp);
             Collections.sort(ar3);
            GridLayoutManager mngr = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mngr);
        }
    }
    public void setlist4() {
        if (ar4.size() == 0) {
            Toast.makeText(this, "You have not any Question this type of", Toast.LENGTH_SHORT).show();
        } else {
            ardp = new Main_activity_recyclerviewAdapter(this, ar4);
            rv.setAdapter(ardp);
            Collections.sort(ar4);
            GridLayoutManager mngr = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mngr);
        }
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(UserActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();

    }

}
