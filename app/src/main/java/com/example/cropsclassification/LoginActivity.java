package com.example.cropsclassification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        activityLoginBinding.actionbarLogin.actionTitle.setText("Login");

        editTextLoginEmail = findViewById(R.id.edit_text_login_email);
        editTextLoginPassword =  findViewById(R.id.edit_text_login_password);

        progressBar = findViewById(R.id.progressBarLoginId);
        alreadyHaveAccount = findViewById(R.id.text_view_have_account);

        authUser = FirebaseAuth.getInstance();

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if(checkbox.equals("true")){
            Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(toMainActivity);
            finish();
        }


        activityLoginBinding.editTextLoginEmail.addTextChangedListener(new TextWatcher() {
            private final long DELAY = 100; // Delay time in milliseconds
            private Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    activityLoginBinding.emailErrorMessage.setText("");
                } else {
                    activityLoginBinding.emailErrorMessage.setText("Invalid email address");
                }

                handler.removeCallbacks(runnable); // Remove the previous runnable
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!s.toString().isEmpty()) { // Check if the email is not empty
                            // Check if email is already registered
                            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(s.toString())
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            List<String> signInMethods = task.getResult().getSignInMethods();
                                            if (signInMethods != null && signInMethods.size() > 0) {
                                                activityLoginBinding.emailErrorMessage.setText("");
                                            } else {
                                                activityLoginBinding.emailErrorMessage.setText("No registered user with this Email");
                                            }
                                        } else {
                                            // Handle errors
                                            activityLoginBinding.emailErrorMessage.setText(task.getException().getMessage());
                                        }
                                    });
                        } else {
                            activityLoginBinding.emailErrorMessage.setText("");
                        }
                    }
                };
                handler.postDelayed(runnable, DELAY); // Schedule the new runnable
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
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


    private void loginUser(String email, String password) {
        authUser.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logged in successfully.",
                            Toast.LENGTH_SHORT).show();


                    if(activityLoginBinding.rememberMeCheck.isChecked()){

                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "true");
                        editor.apply();

                    }
                    else if(!activityLoginBinding.rememberMeCheck.isChecked()){

                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "false");
                        editor.apply();

                    }

                    //Open MainPage after successfully Login.
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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