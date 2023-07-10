package com.example.cropsclassification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cropsclassification.databinding.ActivityProfileBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {


    ActivityProfileBinding activityProfileBinding;

    FirebaseUser firebaseUser;

    String fullName, email, userId;

    int currentPage = 1;

    ProfilePostAdapter profilePostAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activityProfileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        activityProfileBinding.actionbarProfile.actionTitle.setText("Profile");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        showUserProfileDetails();

        activityProfileBinding.profileDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEditProfile = new Intent(ProfileActivity.this, EditProfile.class);
                startActivity(intentEditProfile);
            }
        });

        activityProfileBinding.recyclerViewIdProfile.setLayoutManager(new LinearLayoutManager(this));


        // Set up the initial query for the first page of posts
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        Query query = postsRef.orderByChild("userID").equalTo(userId).limitToFirst(5);

        FirebaseRecyclerOptions<PostDetailsModel> options =
                new FirebaseRecyclerOptions.Builder<PostDetailsModel>()
                        .setQuery(query, PostDetailsModel.class)
                        .build();

        profilePostAdapter = new ProfilePostAdapter(options, ProfileActivity.this);
        activityProfileBinding.recyclerViewIdProfile.setAdapter(profilePostAdapter);


        activityProfileBinding.btnShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMore();
            }
        });

    }

    private void showMore() {
        currentPage++;
        int limit = currentPage * 5; // Calculate the new limit based on the current page

        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        Query query = postsRef.orderByChild("userID").equalTo(userId).limitToFirst(limit);

        FirebaseRecyclerOptions<PostDetailsModel> options =
                new FirebaseRecyclerOptions.Builder<PostDetailsModel>()
                        .setQuery(query, PostDetailsModel.class)
                        .build();
        profilePostAdapter.updateOptions(options);
        activityProfileBinding.btnShowMore.setText("Page " + String.valueOf(currentPage+1));

    }




    private void showUserProfileDetails() {

        String  userID = firebaseUser.getUid();

        //Extracting User reference from database for "Users"
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                if(userDetails != null){
                    fullName = userDetails.getUserName();
                    email = firebaseUser.getEmail();

                    activityProfileBinding.profileName.setText(fullName);
                    activityProfileBinding.profileEmail.setText(email);

                    Uri uri = firebaseUser.getPhotoUrl();
                    //Set user's current DP in imageView(If uploaded already).
                    Picasso.with(ProfileActivity.this).load(uri).into(activityProfileBinding.imageViewProfilePic);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }





    @Override
    protected void onStart() {
        super.onStart();
        profilePostAdapter.startListening();
    }



}

