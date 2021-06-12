package atlair.edu.crackcomp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class MyRecyclerviewAdapter extends RecyclerView.Adapter<MyRecyclerviewAdapter.Myinner> implements Filterable  {

    Context con;
    ArrayList<Main_activity_bean> ar = new ArrayList<>();
    List<Main_activity_bean> filter_ar;



    public MyRecyclerviewAdapter(Context con, ArrayList<Main_activity_bean> ar) {
        this.con = con;
        this.ar = ar;
        this.filter_ar = new ArrayList<>(ar);

    }

    @NonNull
    @Override
    public Myinner onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater lnf = LayoutInflater.from(con);
        View v = lnf.inflate(R.layout.show_question_item, null, true);

        return new MyRecyclerviewAdapter.Myinner(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Myinner holder, int position) {

        if (ar.get(position).getImgurl().equalsIgnoreCase("no image")) {
            holder.img.setImageResource(R.mipmap.adventure);
            holder.img.setAnimation(AnimationUtils.loadAnimation(con, R.anim.fade_scale_animation));

        } else {
            Picasso.get().load(ar.get(position).getImgurl()).into(holder.img);
            holder.img.setAnimation(AnimationUtils.loadAnimation(con, R.anim.fade_scale_animation));

        }


        holder.name.setText(ar.get(position).getName());
        holder.name.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy  hh:mm a");  // hh used for 12hour format and HH used for 24 hour format and a used for am and pm
        String ftime = sdf.format((ar.get(position).getTime()));
        holder.time.setText(ftime);
        holder.time.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));

        holder.ques.setText(Integer.toString(position + 1) + " :- " + ar.get(position).getQuestion());
        holder.ques.setAnimation(AnimationUtils.loadAnimation(con, R.anim.reyclerviewitem_slidefromright));

        holder.option1.setText(ar.get(position).getOption1());
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Main_activity_bean> lt = new ArrayList<>();
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
        ImageView optionimage;
        TextView name, time, ques,option1,option2,option3,option4,ans;
        Button bt;

        SharedPreferences shp;

        String subject,compexam,compmain,language;
        FirebaseUser fuser;
        FirebaseAuth auth;

        public Myinner(@NonNull final View itemView) {
            super(itemView);


            img = itemView.findViewById(R.id.main_activity_item_imageview);
            optionimage = itemView.findViewById(R.id.main_activity_item_optionimage);
            name = itemView.findViewById(R.id.main_activity_item_name);
            time = itemView.findViewById(R.id.main_activity_item_time);
            ques = itemView.findViewById(R.id.main_activity_item_ques);
            option1 = itemView.findViewById(R.id.main_activity_item_option1);
            option2 = itemView.findViewById(R.id.main_activity_item_option2);
            option3 = itemView.findViewById(R.id.main_activity_item_option3);
            option4 = itemView.findViewById(R.id.main_activity_item_option4);
            bt = itemView.findViewById(R.id.main_activity_item_showansbutton);
            ans = itemView.findViewById(R.id.main_activity_item_ans);


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
            auth= FirebaseAuth.getInstance();
            fuser=auth.getCurrentUser();

            shp=con.getSharedPreferences("abc",MODE_PRIVATE);


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


//            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//                @Override
//                public void onCreateContextMenu(ContextMenu contextMenu, final View view, ContextMenu.ContextMenuInfo menuInfo) {
//
//                    contextMenu.setHeaderTitle("Choose Option");
//
//                    contextMenu.add(0,view.getId(),0,"delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem menuItem) {
//
//                            deleteQuestion();
//                            return true;
//                        }
//                    });
//
//                    contextMenu.add(0,view.getId(),0,"Update").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//                            Intent in=new Intent(con,Update_Question.class);
//                          in.putExtra("question_position",Integer.toString(getAdapterPosition()));
//
//                            con.startActivity(in);
//
//                            return true;
//                        }
//                    });
//
//                    contextMenu.add(0,view.getId(),0,"Share").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem item) {
//
//                            Bitmap imgBitmap = BitmapFactory.decodeResource(con.getResources(),R.drawable.questionshare);
//                            String imgBitmapPath = MediaStore.Images.Media.insertImage(con.getContentResolver(),imgBitmap,"title"+ System.currentTimeMillis(),null);
//                            Uri imgBitmapUri = Uri.parse(imgBitmapPath);
//
//                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                //                shareIntent.setPackage("com.whatsapp");  // for only on whatsapp sharing
//                                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                shareIntent.putExtra(Intent.EXTRA_STREAM,imgBitmapUri);
//                                shareIntent.setType("image/*");
//                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                shareIntent.putExtra(Intent.EXTRA_TEXT," Q :- "+ ar.get(getAdapterPosition()).getQuestion()+"\n" +
//                                        "A. " + ar.get(getAdapterPosition()).getOption1() + "\n" +
//                                        "B. " + ar.get(getAdapterPosition()).getOption2() + "\n" +
//                                        "C. " + ar.get(getAdapterPosition()).getOption3() + "\n" +
//                                        "D. " + ar.get(getAdapterPosition()).getOption4() + "\n" +
//                                        " Ans :- "+ar.get(getAdapterPosition()).getAns());
//                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Crack Comp");
//                                con.startActivity(Intent.createChooser(shareIntent, "Share this"));
//
//                            return true;
//                        }
//                    });
//
//                }
//            });
//
//
            optionimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(con, v);
                    popupMenu.getMenuInflater().inflate(R.menu.showquestionitem_optionmenu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId())
                            {
                                case R.id.delete :
                                    deleteQuestion();
                                    break;

                                case R.id.update :
                                    Intent in=new Intent(con,Update_Question.class);
                          in.putExtra("question_position",Integer.toString(getAdapterPosition()));
                            con.startActivity(in);
                                    break;

                                case R.id.share :

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

                                    break;

                            }

                            return false;
                        }
                    });

                    popupMenu.show();
                }
            });

        }

        public void deleteQuestion()
        {

            AlertDialog.Builder bld=new AlertDialog.Builder(con);
            bld.setTitle("Delete Question");
            bld.setIcon(R.mipmap.adventure);
            bld.setMessage("Are you sure, you want to delete this Question ?");
            bld.setCancelable(false);
            bld.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
                    Query delete = null;

                    if(compmain.equalsIgnoreCase("Competative Exam")) {

                        delete = ref.child("Question").child(fuser.getUid()).child(compmain).child(compexam).child(subject).orderByChild("question").equalTo(ar.get(getAdapterPosition()).getQuestion());
                    }
                    else if(compmain.equalsIgnoreCase("Computer Language")) {

                        delete = ref.child("Question").child(fuser.getUid()).child(compmain).child(language).orderByChild("question").equalTo(ar.get(getAdapterPosition()).getQuestion());
                    }
                    if(compmain.equalsIgnoreCase("Current Affairs") || compmain.equalsIgnoreCase("Other Question")) {

                        delete = ref.child("Question").child(fuser.getUid()).child(compmain).orderByChild("question").equalTo(ar.get(getAdapterPosition()).getQuestion());
                    }
                    delete.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                ds.getRef().removeValue();
                                Toast.makeText(con,ar.get(getAdapterPosition()).getQuestion()+"\t deleted", Toast.LENGTH_SHORT).show();

                                ar.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            bld.setNeutralButton("Not now", new DialogInterface.OnClickListener() {
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
    }
}
