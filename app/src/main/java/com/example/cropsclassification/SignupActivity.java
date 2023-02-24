package com.example.cropsclassification;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextFullName, editTextEmail, editTextDob, editTextMobile, editTextPassword, editTextConPassword;

    private RadioGroup radioGroupGender;
    private RadioButton radioButtonGenderSelected;
    private DatePickerDialog picker;

    private static final String TAG = "SignupActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Registration");

        editTextFullName = findViewById(R.id.edit_text_full_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextDob = findViewById(R.id.edit_text_dob);
        editTextMobile = findViewById(R.id.edit_text_mobile);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextConPassword = findViewById(R.id.edit_text_confirm_password);

        radioGroupGender = findViewById(R.id.radio_group_register_gender);
        radioGroupGender.clearCheck();

        //Setting up Date picker on editText
        editTextDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year  =calendar.get(Calendar.YEAR);

                //DatePicker Dialog
                picker = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextDob.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });


        Button buttonRegister = findViewById(R.id.register_button);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
                radioButtonGenderSelected = findViewById(selectedGenderId);

                //obtain the entered data
                String textFullName = editTextFullName.getText().toString();
                String textEmail = editTextEmail.getText().toString();
                String textDob = editTextDob.getText().toString();
                String textMobile = editTextMobile.getText().toString();
                String textPassword = editTextPassword.getText().toString();
                String textConfirmPass = editTextConPassword.getText().toString();

                String textGender;

                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(SignupActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    editTextFullName.setError("Full name is required.");
                    editTextFullName.requestFocus();
                }
                else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(SignupActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Email name is required.");
                    editTextEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(SignupActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Valid email name is required.");
                    editTextEmail.requestFocus();
                }
                else if(TextUtils.isEmpty(textDob)){
                    Toast.makeText(SignupActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    editTextDob.setError("Date of birth is required.");
                    editTextDob.requestFocus();
                }
                else if(radioGroupGender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(SignupActivity.this, "Please select your Gender", Toast.LENGTH_SHORT).show();
                    radioButtonGenderSelected.setError("Gender is required.");
                    radioButtonGenderSelected.requestFocus();
                }
                else if(TextUtils.isEmpty(textMobile)){
                    Toast.makeText(SignupActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                    editTextMobile.setError("Mobile number is required.");
                    editTextMobile.requestFocus();
                }
                else if(textMobile.length() != 11){
                    Toast.makeText(SignupActivity.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
                    editTextMobile.setError("Mobile number should be 11 digits.");
                    editTextMobile.requestFocus();
                }
                else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(SignupActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Password is required.");
                    editTextPassword.requestFocus();
                }
                else if(textPassword.length() < 6){
                    Toast.makeText(SignupActivity.this, "Password should be at least 6 digits.", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Length of password is too short.");
                    editTextPassword.requestFocus();
                }
                else if(TextUtils.isEmpty(textConfirmPass)){
                    Toast.makeText(SignupActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    editTextConPassword.setError("Password confirmation is required.");
                    editTextConPassword.requestFocus();
                }
                else if(!textPassword.equals(textConfirmPass)){
                    Toast.makeText(SignupActivity.this, "Please enter same password", Toast.LENGTH_SHORT).show();
                    editTextConPassword.setError("Password confirmation is required.");
                    editTextConPassword.requestFocus();

                    //clear the entered password
                    editTextPassword.clearComposingText();
                    editTextConPassword.clearComposingText();
                }
                else{
                    textGender = radioButtonGenderSelected.getText().toString();
                    registerUser(textFullName, textEmail, textDob, textGender, textMobile, textPassword);
                }


            }
        });



    }


    //Register user using credentials given.
    private void registerUser(String textFullName, String textEmail, String textDob, String textGender, String textMobile, String textPassword) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //Create user profile
        firebaseAuth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(SignupActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            //Enter user data into the Firebase database
                            UserDetails userDetails = new UserDetails(textFullName, textEmail, textDob, textGender, textMobile);

                            //Extracting User reference from database for "Registered user"
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

                            databaseReference.child(firebaseUser.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "user registered successfully.",
                                                Toast.LENGTH_SHORT).show();
                                        //Email verification
                                        //firebaseUser.sendEmailVerification();

                                        //Open MainPage after successfully registered.


                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                | Intent.FLAG_ACTIVITY_NEW_TASK);

                                        startActivity(intent);
                                        finish();

                                    }else {
                                        Toast.makeText(SignupActivity.this, "User register failed. Please try again.",
                                                Toast.LENGTH_LONG).show();
                                    }


                                    //
                                }
                            });

                        }
                        else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                editTextPassword.setError("Your password is too weak.");
                                editTextPassword.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                editTextPassword.setError("Email is invalid.");
                                editTextPassword.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                editTextPassword.setError("User is already registered with this email.");
                                editTextPassword.requestFocus();
                            } catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(SignupActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

    }
}