package atlair.edu.crackcomp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tv;
    AppBarLayout apb;
    Toolbar tb;
   ProgressBar pb;
    DrawerLayout dl;
    NavigationView nv;
    RecyclerView rv;
    Main_activity_recyclerviewAdapter ardp;
    ArrayList<Main_activity_bean> ar1 = new ArrayList<>();
    ArrayList<Main_activity_bean> ar2 = new ArrayList<>();
    ArrayList<Main_activity_bean> ar3 = new ArrayList<>();
    ArrayList<Main_activity_bean> ar4 = new ArrayList<>();

    ArrayList<String> ars = new ArrayList<>();

    TextView navheader_user;
    CircleImageView navheader_img;

    FirebaseAuth fauth;
    FirebaseUser fuser;
    DatabaseReference ref;

   String imgurl,sname;
   String ques_imgurl,ques_username,ques,option1,option2,option3,option4,ans;
   String time = "12345678996";

    MenuItem register;

    SharedPreferences shp;
    SharedPreferences.Editor edt;

   public static Utils ut = new Utils();

   AppUpdateManager appUpdateManager;
   int request_code = 1;

   Check_Internet_Status check_internet = new Check_Internet_Status();

   String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dl=findViewById(R.id.main_drawerlayout);
        nv=findViewById(R.id.main_navigationview);
        apb = findViewById(R.id.main_appbarlayout);
        tb=findViewById(R.id.main_toolbar);
        tv = findViewById(R.id.mainactivity_textview);
        rv = findViewById(R.id.main_recyclerveiw);
        pb=findViewById(R.id.main_progressbar);
        View v=nv.getHeaderView(0);
        navheader_user=v.findViewById(R.id.nav_header_textview1);
        navheader_img=v.findViewById(R.id.nav_header_imageview);

        In_app_update();

        shp=getSharedPreferences("abc",MODE_PRIVATE);
        if (shp.getString("token",null) != null)
        {
            token=shp.getString("token",null);
        }

        if (token == null)
        {
            check_internet.internet_status(MainActivity.this,pb);
        }



        ut.getoffline();



        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        setSupportActionBar(tb);


        ref= FirebaseDatabase.getInstance().getReference();
        fauth=FirebaseAuth.getInstance();
        fuser=fauth.getCurrentUser();

        navheader_img.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
               if(fuser != null && fauth.getCurrentUser().isEmailVerified())
               {

                   startActivity(new Intent(MainActivity.this,UserActivity.class));
                   overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                   finish();
               }
               else return;
            }
        });

        Menu menu=nv.getMenu();
        register=menu.findItem(R.id.register);

        if(fauth.getCurrentUser()!= null && fauth.getCurrentUser().isEmailVerified())
        {
            register.setTitle("Logout");
        }
        else
        {
            register.setTitle("Login");
        }

        if(fuser != null && fauth.getCurrentUser().isEmailVerified()) {

            ref.child("User").child(fauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     sname=dataSnapshot.child("name").getValue().toString();
                     imgurl=dataSnapshot.child("imgurl").getValue().toString();

                     shp=getSharedPreferences("abc",MODE_PRIVATE);
                     edt=shp.edit();
                     edt.putString("imgurl",imgurl);
                     edt.commit();

                    if(imgurl.equalsIgnoreCase("no image"))
                    {
                    navheader_img.setImageResource(R.mipmap.adventure);
                    }
                    else
                    {
                        Picasso.get().load(imgurl).into(navheader_img);
                    }

                    navheader_user.setText(sname);
                    navheader_user.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/DancingScript-Regular.otf"));
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            navheader_user.setText("Crack Comp");
        }


        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,dl,tb,R.string.open_navigation_drawer,R.string.close_navigation_drawer);
        if(dl != null)
        {
            dl.setDrawerListener(toggle);
        }
        toggle.syncState();

        if(nv != null)
        {
            nv.setNavigationItemSelectedListener(this);
        }


       ref.child("User").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               Iterable<DataSnapshot> it = dataSnapshot.getChildren();
               for (DataSnapshot ds : it)
               {
                   ars.add(ds.getKey());
               }

               for (int i=0; i<ars.size(); i++)
               {
                   ref.child("Question").child(ars.get(i)).child("Competative Exam").addListenerForSingleValueEvent(new ValueEventListener() {
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

                                               ques_imgurl=ds.child("imgurl").getValue().toString();
                                               ques_username=ds.child("name").getValue().toString();
                                               ques = ds.child("question").getValue(String.class);
                                               option1 = ds.child("option1").getValue().toString();
                                               option2 = ds.child("option2").getValue().toString();
                                               option3 = ds.child("option3").getValue().toString();
                                               option4 = ds.child("option4").getValue().toString();
                                               ans = ds.child("ans").getValue(String.class);
                                               time = ds.child("time").getValue(String.class);

                                               ar1.add(new Main_activity_bean(ques_imgurl,ques_username,ques,option1,option2,option3,option4,ans,Long.parseLong(time)));
                                               Collections.sort(ar1);
                                               pb.setVisibility(View.GONE);
                                               ref.keepSynced(true);

                                           }

                                       }
                                   }
                               }
                           }
                     //  setlist1();
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });


                   ref.child("Question").child(ars.get(i)).child("Computer Language").addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           for (DataSnapshot com_language : dataSnapshot.getChildren())
                           {
                               if (com_language.exists()) {

                                   Iterable<DataSnapshot> it = com_language.getChildren();
                                   for (DataSnapshot ds : it) {

                                       ques_imgurl=ds.child("imgurl").getValue().toString();
                                       ques_username=ds.child("name").getValue().toString();
                                       ques = ds.child("question").getValue(String.class);
                                       option1 = ds.child("option1").getValue().toString();
                                       option2 = ds.child("option2").getValue().toString();
                                       option3 = ds.child("option3").getValue().toString();
                                       option4 = ds.child("option4").getValue().toString();
                                       ans = ds.child("ans").getValue(String.class);
                                       time = ds.child("time").getValue(String.class);

                                       ar2.add(new Main_activity_bean(ques_imgurl,ques_username,ques,option1,option2,option3,option4,ans,Long.parseLong(time)));
                                      Collections.sort(ar2);
                                       pb.setVisibility(View.GONE);
                                       ref.keepSynced(true);

                                   }


                               }
                           }
                     // setlist2();
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });

                   ref.child("Question").child(ars.get(i)).child("Current Affairs").addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           Iterable<DataSnapshot> it = dataSnapshot.getChildren();
                           for (DataSnapshot ds : it) {

                               ques_imgurl=ds.child("imgurl").getValue().toString();
                               ques_username=ds.child("name").getValue().toString();
                               ques = ds.child("question").getValue(String.class);
                               option1 = ds.child("option1").getValue().toString();
                               option2 = ds.child("option2").getValue().toString();
                               option3 = ds.child("option3").getValue().toString();
                               option4 = ds.child("option4").getValue().toString();
                               ans = ds.child("ans").getValue(String.class);
                               time = ds.child("time").getValue(String.class);

                               ar3.add(new Main_activity_bean(ques_imgurl,ques_username,ques,option1,option2,option3,option4,ans,Long.parseLong(time)));
                               Collections.sort(ar3);
                               pb.setVisibility(View.GONE);
                               ref.keepSynced(true);

                           }
                       //  setlist3();
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });

                   ref.child("Question").child(ars.get(i)).child("Other Question").addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                           Iterable<DataSnapshot> it = dataSnapshot.getChildren();
                           for (DataSnapshot ds : it) {

                               ques_imgurl=ds.child("imgurl").getValue().toString();
                               ques_username=ds.child("name").getValue().toString();
                               ques = ds.child("question").getValue(String.class);
                               option1 = ds.child("option1").getValue().toString();
                               option2 = ds.child("option2").getValue().toString();
                               option3 = ds.child("option3").getValue().toString();
                               option4 = ds.child("option4").getValue().toString();
                               ans = ds.child("ans").getValue(String.class);
                               time = ds.child("time").getValue(String.class);

                               ar4.add(new Main_activity_bean(ques_imgurl,ques_username,ques,option1,option2,option3,option4,ans,Long.parseLong(time)));
                               Collections.sort(ar4);
                               pb.setVisibility(View.GONE);
                               ref.keepSynced(true);
                           }
                          setlist();
                           tv.setText("All Ques");
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });


               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        ref.keepSynced(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.share :

                Bitmap imgBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.questionshare);
                String imgBitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),imgBitmap,"title"+ System.currentTimeMillis(),null);
                Uri imgBitmapUri = Uri.parse(imgBitmapPath);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
               // shareIntent.setPackage("com.whatsapp");      // for only on whatsapp sharing .....
             //   shareIntent.setPackage("com.google.android.gm");    // for only on gmail sharing  .....
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_STREAM,imgBitmapUri);
                    shareIntent.setType("image/*");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Crack Comp(An App for students)");
                    String shareUrl = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Click on link to download this App :\n" + shareUrl);
                    startActivity(Intent.createChooser(shareIntent, "Share this"));

                dl.closeDrawer(GravityCompat.START);

                break;

            case R.id.profile :

                 if (fuser != null) {
                     Intent in = new Intent(MainActivity.this, Account_Detail.class);
                     startActivity(in);
                     overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                 }
                 else
                 {
                     dl.closeDrawer(GravityCompat.START);
                    login_AlertDialog();

                 }

                break;

            case R.id.register :

                if(register.getTitle().toString().equalsIgnoreCase("Login"))
                {
                    startActivity(new Intent(MainActivity.this,Login.class));
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    finish();
                }
                else if(register.getTitle().toString().equalsIgnoreCase("Logout"))
                {
                     custom_Dialog();

                }
                else
                {
                    startActivity(new Intent(MainActivity.this,Signup.class));
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    finish();
                }
        }
        return false;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        final MenuItem upload=menu.add(0,0,1,"Upload");
//        upload.setIcon(R.drawable.ic_cloud_upload_black_24dp);
//        upload.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//
//        if (fuser != null)
//        {
//            upload.setVisible(false);
//        }
//        else
//        {
//            upload.setVisible(true);
//        }
//
//        upload.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                login_AlertDialog();
//                return true;
//            }
//        });
//
//
//
//
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
//       MenuItem search = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) search.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                upload.setVisible(false);
//                ardp.getFilter().filter(newText);
//                return false;
//            }
//        });
//
//
//        MenuItem allques = menu.add(0,0,1,"All Question");
//        allques.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//
//        allques.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                setlist();
//                tv.setText("All Question");
//                return true;
//            }
//        });
//
//        MenuItem compexam = menu.add(0,0,1,"Competative Exam Ques");
//        compexam.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//
//        compexam.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                setlist1();
//                tv.setText("Competative Exam Question");
//                return true;
//            }
//        });
//
//        MenuItem compLang = menu.add(0,0,1,"Computer Language Ques");
//        compLang.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//
//        compLang.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                setlist2();
//                tv.setText("Computer Language Question");
//                return true;
//            }
//        });
//
//
//        MenuItem currentaffais = menu.add(0,0,1,"Current Affairs Ques");
//        currentaffais.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//
//        currentaffais.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                setlist3();
//                tv.setText("Current Affairs Question");
//                return true;
//            }
//        });
//
//        MenuItem otherques = menu.add(0,0,1,"Other Ques");
//        otherques.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//
//        otherques.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                setlist4();
//                tv.setText("Other Question");
//                return true;
//            }
//        });
//


//        return super.onCreateOptionsMenu(menu);
//    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        final MenuItem upload = menu.findItem(R.id.upload);

        if (fuser != null)
        {
            upload.setVisible(false);
        }
        else
        {
            upload.setVisible(true);
        }

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search Question");


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload.setVisible(false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (fuser != null)
                {
                    upload.setVisible(false);
                }
                else
                {
                    upload.setVisible(true);
                }
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
         login_AlertDialog();
                break;

            case R.id.all_ques :
                setlist();
                tv.setText("All Question");

                break;

            case R.id.compexam_ques :
                setlist1();
                tv.setText("Competative Exam Ques");
                break;

            case R.id.complang_ques :
                setlist2();
                tv.setText("Computer Language Ques");
                break;

            case R.id.current_ques :
                setlist3();
                tv.setText("Current Affairs Ques");
                break;

            case R.id.othe_ques :
                setlist4();
                tv.setText("Other Question");
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
        Collections.sort(ar);
            ardp = new Main_activity_recyclerviewAdapter(this, ar);
            rv.setAdapter(ardp);
            GridLayoutManager mngr = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mngr);

    }

    public void setlist1()
    {


            ardp = new Main_activity_recyclerviewAdapter(this, ar1);
            rv.setAdapter(ardp);
            GridLayoutManager mngr = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mngr);

    }
    public void setlist2()
    {

            ardp = new Main_activity_recyclerviewAdapter(this, ar2);
            rv.setAdapter(ardp);
            GridLayoutManager mngr = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mngr);

    }
    public void setlist3()
    {
            ardp = new Main_activity_recyclerviewAdapter(this, ar3);
            rv.setAdapter(ardp);
            GridLayoutManager mngr = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mngr);
        }

    public void setlist4() {

            ardp = new Main_activity_recyclerviewAdapter(this, ar4);
            rv.setAdapter(ardp);
            GridLayoutManager mngr = new GridLayoutManager(this, 1);
            rv.setLayoutManager(mngr);
        }

    public void login_AlertDialog()
    {

        AlertDialog.Builder bld=new AlertDialog.Builder(MainActivity.this);
        bld.setTitle("Login");
        bld.setIcon(R.drawable.boy);

        bld.setMessage("Please Login with CRACKCOMP Account");
        bld.setCancelable(false);
        bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();

            }
        });

        bld.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog=bld.create();
        if (dialog.getWindow() != null)
        {
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        }
        dialog.show();

    }
        //    login and logout Dialog ...............
    public void custom_Dialog()
    {
        final Dialog dialog=new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.alert_dialog);
        CircleImageView dialog_img=dialog.findViewById(R.id.custom_dialog_circleimageview);

        if (imgurl.equalsIgnoreCase("no image"))
        {
            dialog_img.setImageResource(R.mipmap.adventure);
        }
        else
        {
            Picasso.get().load(imgurl).into(dialog_img);
        }

        Button ok=dialog.findViewById(R.id.custom_dialog_okbutton);
        Button cancel=dialog.findViewById(R.id.custom_dialog_canclebutton);
        dialog.setCancelable(false);
        ok.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ok.setBackground(getResources().getDrawable(R.drawable.border));
        cancel.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cancel.setBackground(getResources().getDrawable(R.drawable.border));


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fauth.signOut();
                startActivity(new Intent(MainActivity.this,Login.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view=dialog.getWindow().getDecorView();

                ObjectAnimator scaledown=ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat("scaleX",1.0f,0.0f),PropertyValuesHolder.ofFloat("scaleY",1.0f,0.0f),PropertyValuesHolder.ofFloat("alpha",1.0f,0.0f));
                scaledown.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }

                });
                scaledown.setDuration(700);
                scaledown.start();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        View view=dialog.getWindow().getDecorView();
        ObjectAnimator scaleup=ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat("scaleX",0.0f,1.0f),PropertyValuesHolder.ofFloat("scaleY",0.0f,1.0f),PropertyValuesHolder.ofFloat("alpha",0.0f,1.0f));
        scaleup.setDuration(700);
        scaleup.start();
//                    ((ViewGroup)dialog.getWindow().getDecorView()).getChildAt(0).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.enterfrom_left));

    }


    public void In_app_update()
    {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if ((result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                && result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
                {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.FLEXIBLE, MainActivity.this, request_code);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
       appUpdateManager.registerListener(installStateUpdatedListener);
    }

       InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED)
            {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "App is ready to install", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Install App", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appUpdateManager.completeUpdate();
                        Toast.makeText(MainActivity.this, "CRACKCOMP updated successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                snackbar.setTextColor(Color.WHITE);
                snackbar.setActionTextColor(Color.WHITE);

                snackbar.show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code && resultCode != RESULT_OK)
        {
                Toast.makeText(this, "update failed"+resultCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (appUpdateManager != null)
        {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // for intermediate update

//        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
//            @Override
//            public void onSuccess(AppUpdateInfo result) {
//           if (result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
//           {
//               try {
//                   appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.FLEXIBLE, MainActivity.this, request_code);
//               } catch (IntentSender.SendIntentException e) {
//                   e.printStackTrace();
//               }
//           }
//            }
//        });
    }

    @Override
    public void onBackPressed() {

        if(dl!=null)
        {
            if(dl.isDrawerOpen( GravityCompat.START ))
            {
                dl.closeDrawer( GravityCompat.START );
            }
            else
            {

                super.onBackPressed();
            }
        }

    }

    @Override
    protected void onStart() {
        In_app_update();
        super.onStart();
    }
}



class Utils {

    private static FirebaseDatabase dboffline;

    public static FirebaseDatabase getoffline() {


        if (dboffline == null) {
            dboffline = FirebaseDatabase.getInstance();
            dboffline.setPersistenceEnabled(true);
        }


        return dboffline;
    }

//    public static FirebaseDatabase getonline() {
//
//        if(dboffline == null)
//        {
//            dboffline=FirebaseDatabase.getInstance();
//            dboffline.setPersistenceEnabled(false);
//        }
//
//        return dboffline;
//
//    }
}
