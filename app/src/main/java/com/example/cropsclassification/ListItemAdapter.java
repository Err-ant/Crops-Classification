package com.example.cropsclassification;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListItemAdapter extends FirebaseRecyclerAdapter<PostDetailsModel, myViewHolder> {

    FirebaseUser firebaseUser;
    DatabaseReference likeRef;
    Boolean testClick = false;

    private Context mContext;

    public ListItemAdapter(@NonNull FirebaseRecyclerOptions<PostDetailsModel> options, Context context) {
        super(options);
        mContext = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new myViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PostDetailsModel model) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        String postKey = getRef(position).getKey();

        holder.getLikeButtonStatus(postKey, userId);

        if(model.getProfileImageUrl()!=null){
            Glide.with(holder.postProfileImage.getContext()).load(model.getProfileImageUrl()).into(holder.postProfileImage);
        }

        holder.userName.setText(model.getUserName());
        holder.postTimeStamp.setText(model.getCurrDateTime());
        holder.prRes.setText(model.getPredictionResult());
        holder.postLoc.setText(model.getUploadLocation());
        holder.numReact.setText(String.valueOf(model.getNumberOfReact()));

        Glide.with(holder.img.getContext()).load(model.getImageURL()).into(holder.img);

        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("comments");
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int commCount = (int)snapshot.getChildrenCount();
                holder.commentCnt.setText(String.valueOf(commCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        likeRef = FirebaseDatabase.getInstance().getReference("likes");

        // Set the click listener for the likee view
        holder.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                testClick = true;
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(testClick == true){

                            if(snapshot.child(postKey).hasChild(userId)){
                                likeRef.child(postKey).child(userId).removeValue();
                                testClick = false;
                            }
                            else{
                                likeRef.child(postKey).child(userId).setValue(true);
                                testClick = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        holder.commentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext,CommentActivity.class);
                intent.putExtra("postKey",postKey);
                mContext.startActivity(intent);
            }
        });
    }


}
