package com.example.cropsclassification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ListItemViewHolder> {


    Context context;
    String[] cropsName, cropsDesc;
    int[] images;

    public ListItemAdapter(Context context, String[] cropsName, String[] cropsDesc, int[] images) {
        this.context = context;
        this.cropsName = cropsName;
        this.cropsDesc = cropsDesc;
        this.images = images;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);

        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {

        holder.descTextView.setText(cropsDesc[position]);
        holder.cropsImageView.setImageResource(images[position]);

    }

    @Override
    public int getItemCount() {
        return cropsName.length;
    }




    class ListItemViewHolder extends RecyclerView.ViewHolder{

        TextView descTextView;
        ImageView cropsImageView;
        public ListItemViewHolder(@NonNull View itemView) {
            super(itemView);

            descTextView = itemView.findViewById(R.id.post_content);
            cropsImageView = itemView.findViewById(R.id.post_image);
        }
    }

}
