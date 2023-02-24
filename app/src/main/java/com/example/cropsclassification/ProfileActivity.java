package com.example.cropsclassification;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cropsclassification.databinding.ActivityProfileBinding;
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

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding activityProfileBinding;

    FirebaseAuth firebaseAuth;
    StorageReference storageRef;
    FirebaseUser firebaseUser;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;
    String fullName, email, dob, gender, mobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activityProfileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());
        getSupportActionBar().setTitle("Profile");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        showUserProfileDetails();

        storageRef = FirebaseStorage.getInstance().getReference("ProfilePics");

        //choosing image to upload
        activityProfileBinding.imageViewProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        activityProfileBinding.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePicture();
            }
        });

        // Set user's profile picture after uploaded


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
                    fullName = userDetails.fullName;
                    email = firebaseUser.getEmail();
                    dob = userDetails.dob;
                    gender = userDetails.gender;
                    mobile =  userDetails.mobile;

                    activityProfileBinding.profileName.setText("Name: " + fullName);
                    activityProfileBinding.profileEmail.setText("Email: " + email);
                    activityProfileBinding.profileMobile.setText("Mobile: " + mobile);
                    activityProfileBinding.profileDob.setText("Date of Birth: " + dob);
                    activityProfileBinding.profileGender.setText("Gender: " + gender);

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
                        }
                    });
                    Toast.makeText(ProfileActivity.this, "Uploaded successful!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Upload failed, try again!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(ProfileActivity.this, "No profile picture was selected!", Toast.LENGTH_LONG).show();
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
            activityProfileBinding.imageViewProfilePic.setImageURI(uriImage);
        }
    }
}