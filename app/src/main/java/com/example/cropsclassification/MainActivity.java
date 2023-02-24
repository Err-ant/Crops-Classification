package com.example.cropsclassification;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cropsclassification.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;

    private FirebaseAuth authUser;

    int[] images = {R.drawable.rice, R.drawable.jute, R.drawable.maize, R.drawable.sugarcan, R.drawable.wheat};
    String[] cropsName, cropsDesc;

    ListItemAdapter listItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);
        getSupportActionBar().setTitle("Home Page");

        cropsName = getResources().getStringArray(R.array.crops_name);
        cropsDesc = getResources().getStringArray(R.array.crops_desc);

        listItemAdapter = new ListItemAdapter(this, cropsName, cropsDesc, images);

        activityMainBinding.recyclerViewID.setAdapter(listItemAdapter);
        activityMainBinding.recyclerViewID.setLayoutManager(new LinearLayoutManager(this));


        /*
        activityMainBinding.gotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        }); */



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gotoProfile:
                Intent intentGotoProfile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentGotoProfile);
                return true;

            case R.id.gotoClassify:
                Intent intent = new Intent(MainActivity.this, ClassifyActivity.class);
                startActivity(intent);
                return true;

            case R.id.changePass:
                Toast.makeText(this, "Change password is selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.deleteAccount:
                Toast.makeText(this, "Delete account is selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent intentGotoLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentGotoLogin);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

    }

}

    @Override
    protected void onStart() {
        super.onStart();
        authUser = FirebaseAuth.getInstance();
        if (authUser.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}