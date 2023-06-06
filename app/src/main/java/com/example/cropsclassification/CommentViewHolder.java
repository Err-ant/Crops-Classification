package com.example.cropsclassification;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder{

    CircleImageView commentUserImage;
    TextView commentUserName,commentText, commentDateTime;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);


        commentUserImage = itemView.findViewById(R.id.commentUserProfileImage);
        commentUserName = itemView.findViewById(R.id.commentUserName);
        commentText = itemView.findViewById(R.id.commentText);
        commentDateTime = itemView.findViewById(R.id.commentDateTime);

    }
}
