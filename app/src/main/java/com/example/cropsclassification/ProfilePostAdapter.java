package com.example.cropsclassification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class ProfilePostAdapter extends FirebaseRecyclerAdapter<PostDetailsModel, ProfilePostViewHolder> {

    FirebaseUser firebaseUser;
    DatabaseReference likeRef;
    Boolean testClick = false;

    private Context mContext;


    // Constructor
    public ProfilePostAdapter(@NonNull FirebaseRecyclerOptions<PostDetailsModel> options, Context context) {
        super(options);
        mContext = context;
    }

    @NonNull
    @Override
    public ProfilePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ProfilePostViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProfilePostViewHolder holder, int position, @NonNull PostDetailsModel model) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        String postKey = getRef(position).getKey();

        holder.getLikeButtonStatus(postKey, userId);

        Glide.with(holder.postProfileImage.getContext()).load(model.getProfileImageUrl()).placeholder(R.drawable.ic_person_24).into(holder.postProfileImage);

        holder.userName.setText(model.getUserName());
        holder.postTimeStamp.setText(model.getCurrDateTime());
        holder.prRes.setText(model.getPredictionResult());
        holder.postLoc.setText(model.getUploadLocation());

        Glide.with(holder.img.getContext()).load(model.getImageURL()).into(holder.img);

        likeRef = FirebaseDatabase.getInstance().getReference("likes");

        // Set the click listener for the like button
        holder.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testClick = true;
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (testClick) {
                            if(snapshot.child(postKey).hasChild(userId)) {
                                likeRef.child(postKey).child(userId).removeValue();
                                testClick = false;
                            } else {
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


        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("comments");
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int commCount = (int) snapshot.getChildrenCount();
                holder.commentCnt.setText(String.valueOf(commCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.commentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postKey", postKey);
                mContext.startActivity(intent);
            }
        });

        holder.postAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Post action is on Way!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateOptions(FirebaseRecyclerOptions<PostDetailsModel> options) {
        super.updateOptions(options);
        notifyDataSetChanged();
    }


}
