package com.example.cropsclassification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApiPostAdapter extends RecyclerView.Adapter<ApiPostAdapter.ApiPostViewHolder> {

    private List<ApiPostPojoModel> dataList;
    private Context context;

    public ApiPostAdapter(List<ApiPostPojoModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ApiPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.api_post_item, parent, false);
        return new ApiPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApiPostViewHolder holder, int position) {
        holder.apiPostTle.setText(dataList.get(position).getTitle());
        holder.apiPostDesc.setText(dataList.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ApiPostViewHolder extends RecyclerView.ViewHolder {
        TextView apiPostTle, apiPostDesc;

        ApiPostViewHolder(View itemView) {
            super(itemView);
            apiPostTle = itemView.findViewById(R.id.apiPostTitle);
            apiPostDesc = itemView.findViewById(R.id.apiPostBody);
        }
    }
}
