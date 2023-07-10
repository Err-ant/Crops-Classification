package com.example.cropsclassification;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cropsclassification.databinding.ActivityRestApiBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestApi extends AppCompatActivity {

    ActivityRestApiBinding activityRestApiBinding;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRestApiBinding = ActivityRestApiBinding.inflate(getLayoutInflater());
        setContentView(activityRestApiBinding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        activityRestApiBinding.actionbarRestApi.actionTitle.setText("REST Api");


        apiInterface = RetrofitInstance.getRetrofit().create(ApiInterface.class);
        apiInterface.getPosts().enqueue(new Callback<List<ApiPostPojoModel>>() {
            @Override
            public void onResponse(Call<List<ApiPostPojoModel>> call, Response<List<ApiPostPojoModel>> response) {
                if(response.body().size() > 0){

                    List<ApiPostPojoModel> postList = response.body();
                    ApiPostAdapter apiPostAdapter = new ApiPostAdapter(postList,RestApi.this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RestApi.this);
                    activityRestApiBinding.recyclerViewApiPost.setLayoutManager(linearLayoutManager);
                    activityRestApiBinding.recyclerViewApiPost.setAdapter(apiPostAdapter);

                    Toast.makeText(RestApi.this, "List is not empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RestApi.this, "List is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ApiPostPojoModel>> call, Throwable t) {
                Toast.makeText(RestApi.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}