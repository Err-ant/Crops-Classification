package com.example.cropsclassification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CommentItemAdapter extends FirebaseRecyclerAdapter<CommentModel, CommentViewHolder> {

    public CommentItemAdapter(@NonNull FirebaseRecyclerOptions<CommentModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull CommentModel model) {

        Glide.with(holder.commentUserImage.getContext()).load(model.getCommentUserImage()).into(holder.commentUserImage);
        holder.commentUserName.setText(model.getCommentUserName());
        holder.commentText.setText(model.getCommentText());
        holder.commentDateTime.setText("Date :"+model.getCommentDate()+" Time :"+model.getCommentTime());




    }

}
