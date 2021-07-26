package atlair.edu.crackcomp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main_activity_recyclerviewAdapter extends RecyclerView.Adapter<Main_activity_recyclerviewAdapter.Myinner> implements Filterable {

    Context con;
    ArrayList<Main_activity_bean> ar = new ArrayList<>();
    List<Main_activity_bean> filter_ar;  // search recyclerview item



    public Main_activity_recyclerviewAdapter(Context con, ArrayList<Main_activity_bean> ar) {
        this.con = con;
        this.ar = ar;
        this.filter_ar = new ArrayList<>(ar);   // search recyclerview item
    }

    @NonNull
    @Override
    public Myinner onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater lnf = LayoutInflater.from(con);
        View v = lnf.inflate(R.layout.main_activity_item, null, true);

        return new Main_activity_recyclerviewAdapter.Myinner(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myinner holder, final int position) {

        if(ar.get(position).getImgurl().equalsIgnoreCase("no image"))
        {
            holder.img.setImageResource(R.mipmap.adventure);
            holder.img .setAnimation(AnimationUtils.loadAnimation(con, R.anim.fade_scale_animation));

        }
        else
        {
            Picasso.get().load(ar.get(position).getImgurl()).into(holder.img);
            holder.img.setAnimation(AnimationUtils.loadAnimation(con, R.anim.fade_scale_animation));

        }


        holder.name.setText( ar.get(position).getName());
        holder.name.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy  hh:mm a");  // hh used for 12hour format and HH used for 24 hour format and a used for am and pm
        String ftime = sdf.format((ar.get(position).getTime()));
        holder.time.setText(ftime);
        holder.time.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));

        holder.ques.setText(Integer.toString(position + 1) + " :- " + ar.get(position).getQuestion());
        holder.ques.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));

        holder.option1.setText( ar.get(position).getOption1());
        holder.option1.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));

        holder.option2.setText(ar.get(position).getOption2());
        holder.option2.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));

        holder.option3.setText(ar.get(position).getOption3());
        holder.option3.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));

        holder.option4.setText(ar.get(position).getOption4());
        holder.option4.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));

        holder. option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ar.get(position).getOption1().equalsIgnoreCase(ar.get(position).getAns()))
                {
                    holder.option1.setBackgroundResource(R.drawable.green_background);
                    holder.option1.setTextColor(Color.WHITE);
                    MediaPlayer.create(con, R.raw.applause_8).start();
                    Toast.makeText(con, "WOW...\n Right Answer", Toast.LENGTH_SHORT).show();


                }
                else {
                    holder.option1.setBackgroundResource(R.drawable.red_background);
                    holder.option1.setTextColor(Color.WHITE);
                    MediaPlayer.create(con, R.raw.wrong_clakson_sound_effect).start();
                    Toast.makeText(con, "Sorry... \n Wrong Answer", Toast.LENGTH_SHORT).show();

                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption1()))
                    {
                        holder.option1.setBackgroundResource(R.drawable.green_background);
                        holder.option1.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption2()))
                    {
                        holder.option2.setBackgroundResource(R.drawable.green_background);
                        holder.option2.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption3()))
                    {
                        holder.option3.setBackgroundResource(R.drawable.green_background);
                        holder.option3.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption4()))
                    {
                        holder.option4.setBackgroundResource(R.drawable.green_background);
                        holder.option4.setTextColor(Color.WHITE);
                    }

                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                          holder.option1.setBackgroundResource(0);
                          holder.option1.setTextColor(Color.BLACK);
                        holder.option2.setBackgroundResource(0);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundResource(0);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundResource(0);
                        holder.option4.setTextColor(Color.BLACK);

                    }
                },3000);
            }
        });

        holder. option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ar.get(position).getOption2().equalsIgnoreCase(ar.get(position).getAns()))
                {
                    holder.option2.setBackgroundResource(R.drawable.green_background);
                    holder.option2.setTextColor(Color.WHITE);
                    MediaPlayer.create(con, R.raw.applause_8).start();
                    Toast.makeText(con, "WOW... \n Right Answer", Toast.LENGTH_SHORT).show();

                }
                else {
                    holder.option2.setBackgroundResource(R.drawable.red_background);
                    holder.option2.setTextColor(Color.WHITE);
                    MediaPlayer.create(con, R.raw.wrong_clakson_sound_effect).start();
                    Toast.makeText(con, "Sorry... \n Wrong Answer", Toast.LENGTH_SHORT).show();


                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption1())) {
                        holder.option1.setBackgroundResource(R.drawable.green_background);
                        holder.option1.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption2())) {
                        holder.option2.setBackgroundResource(R.drawable.green_background);
                        holder.option2.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption3())) {
                        holder.option3.setBackgroundResource(R.drawable.green_background);
                        holder.option3.setTextColor(Color.WHITE);}
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption4())) {
                        holder.option4.setBackgroundResource(R.drawable.green_background);
                        holder.option4.setTextColor(Color.WHITE);
                    }
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.option1.setBackgroundResource(0);
                        holder.option1.setTextColor(Color.BLACK);
                        holder.option2.setBackgroundResource(0);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundResource(0);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundResource(0);
                        holder.option4.setTextColor(Color.BLACK);

                    }
                },3000);
            }
        });
        holder. option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ar.get(position).getOption3().equalsIgnoreCase(ar.get(position).getAns()))
                {
                    holder.option3.setBackgroundResource(R.drawable.green_background);
                    holder.option3.setTextColor(Color.WHITE);
                    MediaPlayer.create(con, R.raw.applause_8).start();
                    Toast.makeText(con, "WOW...\n Right Answer", Toast.LENGTH_SHORT).show();

                }
                else {
                    holder.option3.setBackgroundResource(R.drawable.red_background);
                    holder.option3.setTextColor(Color.WHITE);
                    MediaPlayer.create(con, R.raw.wrong_clakson_sound_effect).start();
                    Toast.makeText(con, "Sorry... \n Wrong Answer", Toast.LENGTH_SHORT).show();


                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption1())) {
                        holder.option1.setBackgroundResource(R.drawable.green_background);
                        holder.option1.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption2())) {
                        holder.option2.setBackgroundResource(R.drawable.green_background);
                        holder.option2.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption3())) {
                        holder.option3.setBackgroundResource(R.drawable.green_background);
                        holder.option3.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption4())) {
                        holder.option4.setBackgroundResource(R.drawable.green_background);
                        holder.option4.setTextColor(Color.WHITE);
                    }
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.option1.setBackgroundResource(0);
                        holder.option1.setTextColor(Color.BLACK);
                        holder.option2.setBackgroundResource(0);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundResource(0);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundResource(0);
                        holder.option4.setTextColor(Color.BLACK);

                    }
                },3000);
            }
        });
        holder. option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ar.get(position).getOption4().equalsIgnoreCase(ar.get(position).getAns()) )
                {
                    holder.option4.setBackgroundResource(R.drawable.green_background);
                    holder.option4.setTextColor(Color.WHITE);
                    MediaPlayer.create(con, R.raw.applause_8).start();
                    Toast.makeText(con, "WOW...\n Right Answer", Toast.LENGTH_SHORT).show();

                }
                else {
                    holder.option4.setBackgroundResource(R.drawable.red_background);
                    holder.option4.setTextColor(Color.WHITE);
                    MediaPlayer.create(con, R.raw.wrong_clakson_sound_effect).start();
                    Toast.makeText(con, "Sorry... \n Wrong Answer", Toast.LENGTH_SHORT).show();

                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption1())) {
                        holder.option1.setBackgroundResource(R.drawable.green_background);
                        holder.option1.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption2())) {
                        holder.option2.setBackgroundResource(R.drawable.green_background);
                        holder.option2.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption3())) {
                        holder.option3.setBackgroundResource(R.drawable.green_background);
                        holder.option3.setTextColor(Color.WHITE);
                    }
                    if (ar.get(position).getAns().equalsIgnoreCase(ar.get(position).getOption4())) {
                        holder.option4.setBackgroundResource(R.drawable.green_background);
                        holder.option4.setTextColor(Color.WHITE);
                    }
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.option1.setBackgroundResource(0);
                        holder.option1.setTextColor(Color.BLACK);
                        holder.option2.setBackgroundResource(0);
                        holder.option2.setTextColor(Color.BLACK);
                        holder.option3.setBackgroundResource(0);
                        holder.option3.setTextColor(Color.BLACK);
                        holder.option4.setBackgroundResource(0);
                        holder.option4.setTextColor(Color.BLACK);

                    }
                },3000);

            }
        });


    }

    @Override
    public int getItemCount() {
        return ar.size();

    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Main_activity_bean> lt = new ArrayList<>();
            if (constraint.toString().isEmpty())
            {
                lt.addAll(filter_ar);
            }else {
                for (Main_activity_bean ques : filter_ar)
                {
                  if (ques.getQuestion().toLowerCase().contains(constraint.toString().toLowerCase()))
                  {
                      lt.add(ques);
                  }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = lt;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
          ar.clear();
          ar.addAll((Collection<? extends Main_activity_bean>) results.values);
          notifyDataSetChanged();
        }
    };


    public class Myinner extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView name, time, ques,option1,option2,option3,option4,ans;
        Button bt;

        FirebaseAuth fauth;
        FirebaseUser fuser;
        DatabaseReference ref;


        public Myinner(@NonNull final View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.main_activity_item_imageview);
            name = itemView.findViewById(R.id.main_activity_item_name);
            time = itemView.findViewById(R.id.main_activity_item_time);
            ques = itemView.findViewById(R.id.main_activity_item_ques);
            option1 = itemView.findViewById(R.id.main_activity_item_option1);
            option2 = itemView.findViewById(R.id.main_activity_item_option2);
            option3 = itemView.findViewById(R.id.main_activity_item_option3);
            option4 = itemView.findViewById(R.id.main_activity_item_option4);
            bt = itemView.findViewById(R.id.main_activity_item_showansbutton);
            ans = itemView.findViewById(R.id.main_activity_item_ans);

            ref= FirebaseDatabase.getInstance().getReference("User");
            fauth=FirebaseAuth.getInstance();
            fuser=fauth.getCurrentUser();


         bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bitmap imgBitmap = BitmapFactory.decodeResource(con.getResources(),R.drawable.questionshare);
                    String imgBitmapPath = MediaStore.Images.Media.insertImage(con.getContentResolver(),imgBitmap,"title"+ System.currentTimeMillis(),null);
                    Uri imgBitmapUri = Uri.parse(imgBitmapPath);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    //                shareIntent.setPackage("com.whatsapp");  // for only on whatsapp sharing
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_STREAM,imgBitmapUri);
                    shareIntent.setType("image/*");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_TEXT," Q :- "+ ar.get(getAdapterPosition()).getQuestion()+"\n" +
                            "A. " + ar.get(getAdapterPosition()).getOption1() + "\n" +
                            "B. " + ar.get(getAdapterPosition()).getOption2() + "\n" +
                            "C. " + ar.get(getAdapterPosition()).getOption3() + "\n" +
                            "D. " + ar.get(getAdapterPosition()).getOption4() + "\n" +
                            " Ans :- "+ar.get(getAdapterPosition()).getAns());
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Crack Comp");
                    con.startActivity(Intent.createChooser(shareIntent, "Share this"));

                }
            });

         img.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Query query = ref.orderByChild("imgurl").equalTo(ar.get(getAdapterPosition()).getImgurl());
                 query.addChildEventListener(new ChildEventListener() {
                     @Override
                     public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String child_parent = dataSnapshot.getKey();
                         final Intent in = new Intent(con, AllUser_Activity.class);
                        in.putExtra("child_parent",child_parent);
                            con.startActivity(in);
                         ((Activity)con).finish();

                     }

                     @Override
                     public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                     }

                     @Override
                     public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                     }

                     @Override
                     public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });


             }
         });




        }


    }
}