package com.example.cropsclassification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ListItemAdapter extends FirebaseRecyclerAdapter<PostDetailsModel, ListItemAdapter.myViewHolder> {

    public ListItemAdapter(@NonNull FirebaseRecyclerOptions<PostDetailsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull PostDetailsModel model) {

        holder.userName.setText(model.getUserName());
        holder.prRes.setText(model.getPredictionResult());
        holder.postLoc.setText(model.getUploadLocation());
        holder.numReact.setText(String.valueOf(model.getNumberOfReact()));
        holder.numRating.setText(String.valueOf(model.getRating()));

        Glide.with(holder.img.getContext()).load(model.getImageURL()).into(holder.img);

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        ImageView img;
        TextView userName, prRes, postLoc, numReact, numRating;
        LinearLayout likee;
        public myViewHolder(@NonNull View itemView) {

            super(itemView);
            img = itemView.findViewById(R.id.postImage);
            userName = itemView.findViewById(R.id.postUserName);
            prRes = itemView.findViewById(R.id.postText);
            postLoc = itemView.findViewById(R.id.postLocationText);
            numReact = itemView.findViewById(R.id.likeCount);
            numRating = itemView.findViewById(R.id.ratingCount);
            likee = itemView.findViewById(R.id.likecc);

            likee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle the click event for numReact TextView
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Retrieve the PostDetailsModel at the clicked position
                        PostDetailsModel model = getItem(position);

                        // Perform any desired action with the model
                        // For example, you can show a Toast with the number of reacts
                        Toast.makeText(itemView.getContext(), "Number of reacts: " + model.getNumberOfReact(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


}
