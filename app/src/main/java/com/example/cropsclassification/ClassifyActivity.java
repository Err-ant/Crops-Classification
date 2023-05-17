package com.example.cropsclassification;

import static java.lang.Math.ceil;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.cropsclassification.databinding.ActivityClassifyBinding;
import com.example.cropsclassification.ml.Model;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ClassifyActivity extends AppCompatActivity {

    ActivityClassifyBinding activityClassifyBinding;

    private String userID, userName, predictionResult, uploadLocation;
    int numberOfReact, rating;
    private ProgressBar progressBar;

    Bitmap bitmap;
    private Uri postUriImage;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    private static final String TAG = "ClassifyActivity";
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityClassifyBinding = ActivityClassifyBinding.inflate(getLayoutInflater());
        setContentView(activityClassifyBinding.getRoot());
        getSupportActionBar().setTitle("Classify Crops");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ClassifyActivity.this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        storageReference = FirebaseStorage.getInstance().getReference("PostImages");
        databaseReference = FirebaseDatabase.getInstance().getReference("PostImages");

        activityClassifyBinding.selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        activityClassifyBinding.captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);

            }
        });

        activityClassifyBinding.sharePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UploadImage();
            }
        });


    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if(locationResult == null){
                return;
            }
            for(Location location: locationResult.getLocations()){
                Geocoder geocoder = new Geocoder(ClassifyActivity.this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);
                    activityClassifyBinding.textViewLatLong.setText("Latitude : " + addressList.get(0).getLatitude()
                            + "\nLongitude : " + addressList.get(0).getLongitude());

                    activityClassifyBinding.textViewLocationAddress.setText(addressList.get(0).getAddressLine(0));
                    stopLocationUpdates();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            if (data != null) {
                Uri uri = data.getData();
                postUriImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    activityClassifyBinding.imageView.setImageBitmap(bitmap);
                    predict();
                    checkSettingsAndStartLocationUpdates();
                    //stopLocationUpdates();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 12) {
            if (data != null) {

                bitmap = (Bitmap) data.getExtras().get("data");
                postUriImage = getImageUri(bitmap);

                activityClassifyBinding.imageView.setImageBitmap(bitmap);
                predict();
                checkSettingsAndStartLocationUpdates();

                //stopLocationUpdates();

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void predict() {
        try {

            Model model = Model.newInstance(ClassifyActivity.this);

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(bitmap);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            probability.sort(Comparator.comparing(Category::getScore, Comparator.reverseOrder()));
            int score = (int) ceil(probability.get(0).getScore() * 100);

            predictionResult = "Prediction :" + probability.get(0).getLabel() + "(" + score +"%)";
            activityClassifyBinding.result.setText(probability.get(0).getLabel() + ": " + score +"%");

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(ClassifyActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //checkSettingsAndStartLocationUpdates();
        }
        if (ActivityCompat.checkSelfPermission(ClassifyActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
        }
        if (ActivityCompat.checkSelfPermission(ClassifyActivity.this, Manifest.permission
                .CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askCameraPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopLocationUpdates();
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(ClassifyActivity.this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Settings of device are satisfied and we can start location updates
                startLocationUpdates();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(ClassifyActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }



    void askCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ClassifyActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 11);
        }
    }

    private void askLocationPermission(){

        if (ActivityCompat.checkSelfPermission(ClassifyActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ClassifyActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 10001);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 10001){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission granted
                //checkSettingsAndStartLocationUpdates();
            }
            else{
                //Permission not granted
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private String getFileExtension(Uri uriImage) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uriImage));
    }

    public void UploadImage() {

        if (postUriImage != null) {

            activityClassifyBinding.progressBarPostBtn.setVisibility(View.VISIBLE);

            userID = firebaseUser.getUid();
            //Extracting User reference from database for "Users"
            DatabaseReference databaseReferenceCurr = FirebaseDatabase.getInstance().getReference("Users");
            databaseReferenceCurr.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserDetails userDetails = snapshot.getValue(UserDetails.class);
                    if(userDetails != null){
                        userName = userDetails.fullName;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ClassifyActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            });

            //Upload post data
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(postUriImage));
            storageReference2.putFile(postUriImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String TempImageName = activityClassifyBinding.result.getText().toString().trim();
                            String imageURL = taskSnapshot.getUploadSessionUri().toString();

                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            PostDetailsModel imageUploadInfo = new PostDetailsModel(TempImageName, imageURL, userID, userName, uploadLocation, 5, 2);

                            String ImageUploadId = databaseReference.push().getKey();
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                            activityClassifyBinding.progressBarPostBtn.setVisibility(View.GONE);
                        }
                    });



        }
        else {
            Toast.makeText(ClassifyActivity.this, "Handle URI image error!", Toast.LENGTH_LONG).show();
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }



}