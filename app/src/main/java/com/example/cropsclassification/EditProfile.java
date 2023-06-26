package com.example.cropsclassification;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cropsclassification.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class EditProfile extends AppCompatActivity {

    ActivityEditProfileBinding activityEditProfileBinding;

    FirebaseAuth firebaseAuth;
    StorageReference storageRef;
    FirebaseUser firebaseUser;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;
    String fullName, email, dob, gender, mobile;
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditProfileBinding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(activityEditProfileBinding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        activityEditProfileBinding.actionbarEditProfile.actionTitle.setText("Edit Profile");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        showUserProfileDetails();

        storageRef = FirebaseStorage.getInstance().getReference("ProfilePicture");

        //choosing image to upload
        activityEditProfileBinding.addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        activityEditProfileBinding.uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityEditProfileBinding.uploadProgressBar.setVisibility(View.VISIBLE);
                activityEditProfileBinding.addPhoto.setVisibility(View.GONE);
                uploadProfilePicture();

            }
        });

        activityEditProfileBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityEditProfileBinding.uploadProgressBar.setVisibility(View.VISIBLE);
                activityEditProfileBinding.saveBtn.setVisibility(View.GONE);
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        // Get the user inputs from the EditText fields
        String fullName = activityEditProfileBinding.editName.getText().toString().trim();
        String email = activityEditProfileBinding.editEmail.getText().toString().trim();
        String mobile = activityEditProfileBinding.editMobile.getText().toString().trim();
        String dob = activityEditProfileBinding.editDob.getText().toString().trim();

        // Get the selected gender from the RadioGroup
        String gender = "";
        int selectedGenderId = activityEditProfileBinding.editRadioGroup.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.editRadioMale) {
            gender = "Male";
        } else if (selectedGenderId == R.id.editRadioFemale) {
            gender = "Female";
        }

        // Update the user details in the database
        String userID = firebaseUser.getUid();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        userReference.child("userName").setValue(fullName);
        userReference.child("email").setValue(email);
        userReference.child("mobile").setValue(mobile);
        userReference.child("dob").setValue(dob);
        userReference.child("gender").setValue(gender);

        Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
        activityEditProfileBinding.uploadProgressBar.setVisibility(View.GONE);
        activityEditProfileBinding.saveBtn.setVisibility(View.VISIBLE);
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
                    mobile = userDetails.getMobile();
                    dob = userDetails.getDob();
                    gender = userDetails.getGender();

                    activityEditProfileBinding.editName.setText(fullName);
                    activityEditProfileBinding.editEmail.setText(email);
                    activityEditProfileBinding.editMobile.setText(mobile);
                    activityEditProfileBinding.editDob.setText(dob);

                    if (gender.equalsIgnoreCase("Male")) {
                        activityEditProfileBinding.editRadioMale.setChecked(true);
                    } else if (gender.equalsIgnoreCase("Female")) {
                        activityEditProfileBinding.editRadioFemale.setChecked(true);
                    }


                    Uri uri = firebaseUser.getPhotoUrl();
                    //Set user's current DP in imageView(If uploaded already).
                    Picasso.with(EditProfile.this).load(uri).into(activityEditProfileBinding.editProfileImage);


                    //Setting up Date picker on editText
                    activityEditProfileBinding.editDob.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Calendar calendar = Calendar.getInstance();
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            int month = calendar.get(Calendar.MONTH);
                            int year  =calendar.get(Calendar.YEAR);

                            //DatePicker Dialog
                            picker = new DatePickerDialog(EditProfile.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                    activityEditProfileBinding.editDob.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                                }
                            }, year, month, day);
                            picker.show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadProfilePicture() {
        if(uriImage != null){

            //save the image with uid of the currently logged user
            StorageReference fileReference = storageRef.child(firebaseAuth.getCurrentUser().getUid() + "."
                    + getFileExtension(uriImage));

            //Upload image to storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;

                            firebaseUser = firebaseAuth.getCurrentUser();

                            // Finally set the display image of the user after upload
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(userProfileChangeRequest);

                            Toast.makeText(EditProfile.this, "Uploaded successful!", Toast.LENGTH_SHORT).show();
                            activityEditProfileBinding.uploadProgressBar.setVisibility(View.GONE);
                            activityEditProfileBinding.addPhoto.setVisibility(View.VISIBLE);

                            String userProfileImageUrl = downloadUri.toString();
                            updateProfileImageUrlForPostAndComments(userProfileImageUrl);
                        }
                    });




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, "Upload failed, try again!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(EditProfile.this, "No profile picture was selected!", Toast.LENGTH_LONG).show();
            activityEditProfileBinding.uploadProgressBar.setVisibility(View.GONE);
            activityEditProfileBinding.addPhoto.setVisibility(View.VISIBLE);
        }
    }



    private String getFileExtension(Uri uriImage) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uriImage));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() != null){
            uriImage = data.getData();
            activityEditProfileBinding.editProfileImage.setImageURI(uriImage);
        }
    }

    private void updateProfileImageUrlForPostAndComments(String userProfileImageUrl) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = firebaseUser.getUid();

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String postId = postSnapshot.getKey();
                        String postUserID = postSnapshot.child("userID").getValue().toString();

                        DatabaseReference commentsRef = postRef.child(postId).child("comments");
                        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                                        String commentId = commentSnapshot.getKey();
                                        String commentUserID = commentSnapshot.child("commentUserid").getValue().toString();

                                        if (commentUserID.equals(userID)) {
                                            commentsRef.child(commentId).child("commentUserImage").setValue(userProfileImageUrl);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle any errors that may occur
                            }
                        });

                        if (postUserID.equals(userID)) {
                            // Update the profile image URL for the current user's posts
                            postRef.child(postId).child("profileImageUrl").setValue(userProfileImageUrl);

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Toast.makeText(EditProfile.this, "Post and comments images updated successfully!", Toast.LENGTH_LONG).show();
    }
}