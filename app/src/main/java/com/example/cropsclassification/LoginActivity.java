package com.example.cropsclassification;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cropsclassification.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding activityLoginBinding;
    private EditText editTextLoginEmail, editTextLoginPassword;
    private ProgressBar progressBar;
    private FirebaseAuth authUser;
    private TextView alreadyHaveAccount;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    List<String> emailList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());
        getSupportActionBar().setTitle("Login");


        editTextLoginEmail = findViewById(R.id.edit_text_login_email);
        editTextLoginPassword =  findViewById(R.id.edit_text_login_password);

        progressBar = findViewById(R.id.progressBarLoginId);
        alreadyHaveAccount = findViewById(R.id.text_view_have_account);

        authUser = FirebaseAuth.getInstance();

        activityLoginBinding.editTextLoginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Retrieve up to 5 emails that match the input string
                Query query = mDatabase.orderByChild("Email")
                        .startAt(s.toString())
                        .endAt(s + "\uf8ff")
                        .limitToFirst(5);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Clear previous results
                        emailList.clear();

                        // Loop through the query results and add the emails to the list
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String email = snapshot.child("email").getValue(String.class);
                            emailList.add(email);
                        }

                        // Update the UI with the new list of emails
                        updateEmailListUI();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors here
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used in this example
            }
        });


        activityLoginBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textLoginEmail = editTextLoginEmail.getText().toString();
                String textLoginPassword = editTextLoginPassword.getText().toString();

                if(TextUtils.isEmpty(textLoginEmail)){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email name is required.");
                    editTextLoginEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textLoginEmail).matches()){
                    Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid email name is required.");
                    editTextLoginEmail.requestFocus();
                }
                else if(TextUtils.isEmpty(textLoginPassword)){
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextLoginPassword.setError("Password is required.");
                    editTextLoginPassword.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textLoginEmail, textLoginPassword);
                }
            }
        });


        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    private void updateEmailListUI() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_login, emailList);
        ListView emailListView = findViewById(R.id.email_list);
        emailListView.setAdapter(adapter);
        emailListView.setVisibility(emailList.isEmpty() ? View.GONE : View.VISIBLE);
    }


    private void loginUser(String email, String password) {
        authUser.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logged in successfully.",
                            Toast.LENGTH_SHORT).show();

                    //Open MainPage after successfully Login.


                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    finish();

                }else {
                    Toast.makeText(LoginActivity.this, "User log in failed. Please try again.",
                            Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });


    }


}