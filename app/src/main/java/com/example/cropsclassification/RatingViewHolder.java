package com.example.cropsclassification;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingViewHolder extends RecyclerView.ViewHolder{

    CircleImageView ratingUserImage;
    TextView ratingUserName,ratingText, ratingCnt;

    public RatingViewHolder(@NonNull View itemView) {
        super(itemView);

        ratingUserImage = itemView.findViewById(R.id.ratingUserImage);
        ratingUserName = itemView.findViewById(R.id.ratingUserName);
        ratingText = itemView.findViewById(R.id.userFeedback);
        ratingCnt = itemView.findViewById(R.id.userOwnRating);

    }

}
