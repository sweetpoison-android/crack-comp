package atlair.edu.crackcomp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main_activity_recyclerviewAdapter extends RecyclerView.Adapter<Main_activity_recyclerviewAdapter.Myinner> {

    Context con;
    ArrayList<Main_activity_bean> ar = new ArrayList<>();


    public Main_activity_recyclerviewAdapter(Context con, ArrayList<Main_activity_bean> ar) {
        this.con = con;
        this.ar = ar;
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

    }

    @Override
    public int getItemCount() {
        return ar.size();

    }


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

                    if (bt.getText().toString().equalsIgnoreCase("Show Answer"))
                    {
                        ans.setText(ar.get(getAdapterPosition()).getAns());
                        ans.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));
                        ans.setVisibility(View.VISIBLE);
                        bt.setText("Hide Answer");

                    }
                  else if (bt.getText().toString().equalsIgnoreCase("Hide Answer"))
                    {
                        ans.setVisibility(View.GONE);
                        bt.setText("Show Answer");
                    }
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