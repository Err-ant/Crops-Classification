package com.example.cropsclassification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RatingListAdapter extends FirebaseRecyclerAdapter<RatingModel, RatingViewHolder> {

    public RatingListAdapter(@NonNull FirebaseRecyclerOptions<RatingModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_rating, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RatingViewHolder holder, int position, @NonNull RatingModel model) {

        Glide.with(holder.ratingUserImage.getContext()).load(model.getRatingUserImage()).placeholder(R.drawable.ic_person_24).into(holder.ratingUserImage);
        holder.ratingUserName.setText(model.getRatingUserName());
        holder.ratingText.setText(model.getRatingText());
        holder.ratingCnt.setText(String.valueOf(model.getGivenRating()));
    }
}
