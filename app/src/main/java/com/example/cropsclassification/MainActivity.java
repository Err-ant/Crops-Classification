package com.example.cropsclassification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cropsclassification.databinding.ActivityMainBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;

    ListItemAdapter listItemAdapter;

    // Declare variables for pagination
    private static final int PAGE_SIZE = 5; // Number of posts to fetch per page
    private int currentPage = 1; // Current page number

    ArrayList<PostDetailsModel> postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        //getSupportActionBar().setTitle("Home");
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        // Initialize ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, activityMainBinding.drawerLayout, R.string.open, R.string.close);
        activityMainBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setupNavigationMenu();

        activityMainBinding.actionbarLayout.actionbarLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the drawer when the logo is clicked
                activityMainBinding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        activityMainBinding.actionbarLayout.actionbarIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsMenu();
            }
        });

        activityMainBinding.actionbarLayout.actionbarIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGotoProfile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentGotoProfile);
            }
        });

        activityMainBinding.recyclerViewID.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions<PostDetailsModel> options =
                new FirebaseRecyclerOptions.Builder<PostDetailsModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Posts"), PostDetailsModel.class)
                        .build();


        listItemAdapter = new ListItemAdapter(options, MainActivity.this);

        activityMainBinding.recyclerViewID.setAdapter(listItemAdapter);

        activityMainBinding.currentPage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPagePosts(2);
            }
        });

    }

    private void currentPagePosts(int currPage) {

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }



    private void setupNavigationMenu() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation menu item clicks here
                switch (item.getItemId()) {
                    case R.id.toProfile:
                        // Handle 'Profile' item click
                        Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intentProfile);
                        break;

                    case R.id.toClassify:
                        // Handle 'Classify' item click
                        Intent intentClassify = new Intent(MainActivity.this, ClassifyActivity.class);
                        startActivity(intentClassify);
                        break;

                    case R.id.toMultiDrop:
                        // Handle 'Dropdown' item click
                        Intent intentMultiDrop = new Intent(MainActivity.this, MultipleDropdown.class);
                        startActivity(intentMultiDrop);
                        break;

                    case R.id.toYoutube:
                        // Handle 'See youtube video' item click
                        Intent intentYoutube = new Intent(MainActivity.this, EmbedVideo.class);
                        startActivity(intentYoutube);
                        break;

                    case R.id.toPdfGenerator:
                        // Handle 'See youtube video' item click
                        Intent intentPdfGen = new Intent(MainActivity.this, PdfGenerator.class);
                        startActivity(intentPdfGen);
                        break;

                    case R.id.toGsapSaaS:
                        // Handle 'See youtube video' item click
                        Intent intentGsap = new Intent(MainActivity.this, GsapActivity.class);
                        startActivity(intentGsap);
                        break;

                    case R.id.toSetting:
                        // Handle 'Setting' item click
                        Toast.makeText(MainActivity.this, "Setting is selected", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.toRate:
                        // Handle 'Rate Us' item click
                        Intent intentRate = new Intent(MainActivity.this, RatingActivity.class);
                        startActivity(intentRate);
                        break;

                    case R.id.toLogout:

                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "false");
                        editor.apply();

                        // Handle 'Logout' item click
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                        Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intentLogout);
                        finish();
                        break;

                }

                // Close the navigation drawer
                activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void showOptionsMenu() {
        PopupMenu popupMenu = new PopupMenu(this, activityMainBinding.actionbarLayout.actionbarIcon1);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.settings_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item clicks here
                switch (item.getItemId()) {

                    case R.id.gotoClassify:
                        Intent intent = new Intent(MainActivity.this, ClassifyActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.gotoMultiDrop:
                        Intent intentMultiDrop = new Intent(MainActivity.this, MultipleDropdown.class);
                        startActivity(intentMultiDrop);
                        return true;

                    case R.id.gotoVideoPlayer:
                        Intent intentEmbedVideo = new Intent(MainActivity.this, EmbedVideo.class);
                        startActivity(intentEmbedVideo);
                        return true;


                    case R.id.changePass:
                        Toast.makeText(MainActivity.this, "Change password is selected", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.deleteAccount:
                        Toast.makeText(MainActivity.this, "Delete account is selected", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.gotoRating:
                        Intent intentRating = new Intent(MainActivity.this, RatingActivity.class);
                        startActivity(intentRating);
                        return true;

                    case R.id.logout:

                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "false");
                        editor.apply();

                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                        Intent intentGotoLogin = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intentGotoLogin);
                        finish();
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        listItemAdapter.startListening();
    }



}