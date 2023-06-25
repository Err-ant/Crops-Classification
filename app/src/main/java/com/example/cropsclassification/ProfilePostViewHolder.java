package com.example.cropsclassification;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePostViewHolder extends RecyclerView.ViewHolder {

    CircleImageView postProfileImage;
    ImageView img, likeImg, commentImg, postAction;
    TextView userName, prRes, postLoc, numReact, postTimeStamp, commentCnt;
    DatabaseReference likeReference;

    public ProfilePostViewHolder(@NonNull View itemView) {
        super(itemView);

        postProfileImage = itemView.findViewById(R.id.postUserProfileImage);
        img = itemView.findViewById(R.id.postImage);
        userName = itemView.findViewById(R.id.postUserName);
        postTimeStamp = itemView.findViewById(R.id.postTimestamp);
        prRes = itemView.findViewById(R.id.postText);
        postLoc = itemView.findViewById(R.id.postLocationText);
        numReact = itemView.findViewById(R.id.likeCou);
        likeImg = itemView.findViewById(R.id.likecc);
        commentImg = itemView.findViewById(R.id.comment_button);
        commentCnt = itemView.findViewById(R.id.commentCount);
        postAction = itemView.findViewById(R.id.postAction);


    }


    public void getLikeButtonStatus(final String postkey, final String userid) {

        likeReference = FirebaseDatabase.getInstance().getReference("likes");
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postkey).hasChild(userid)) {
                    int likeCount = (int) snapshot.child(postkey).getChildrenCount();
                    numReact.setText(String.valueOf(likeCount));
                    likeImg.setImageResource(R.drawable.ic_baseline_favorite_2);

                } else {
                    int likeCount = (int) snapshot.child(postkey).getChildrenCount();
                    numReact.setText(String.valueOf(likeCount));
                    likeImg.setImageResource(R.drawable.ic_baseline_favorite);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}
