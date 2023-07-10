package com.example.cropsclassification;

import static java.lang.Math.ceil;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ClassifyActivity extends AppCompatActivity {

    ActivityClassifyBinding activityClassifyBinding;

    private String userID, userName, predictionResult, uploadLocation, predictionResult1;

    //Following three lines for PDF
    String[] infoArr = new String[] {"User", "Prediction", "Location", "Date", "Time"};
    String[] userInfo = new String[5];
    String fullName;

    Bitmap bitmap;
    private Uri postUriImage;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityClassifyBinding = ActivityClassifyBinding.inflate(getLayoutInflater());
        setContentView(activityClassifyBinding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        activityClassifyBinding.actionbarClassification.actionTitle.setText("Classify");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ClassifyActivity.this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        storageReference = FirebaseStorage.getInstance().getReference("PostImages");
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

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

        activityClassifyBinding.GenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReport();
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

                    /*
                    activityClassifyBinding.textViewLatLong.setText("Latitude : " + addressList.get(0).getLatitude()
                            + "\nLongitude : " + addressList.get(0).getLongitude());
                    */

                    uploadLocation = addressList.get(0).getAddressLine(0);
                    activityClassifyBinding.textViewLocationAddress.setText(addressList.get(0).getAddressLine(0));
                    activityClassifyBinding.sharePostBtn.setEnabled(true);

                    stopLocationUpdates();
                    enablePdfButton();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void enablePdfButton(){

        String userID = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                if(userDetails != null){
                    fullName = userDetails.getUserName();
                    userInfo[0] = fullName;
                    activityClassifyBinding.GenerateBtn.setEnabled(true);
                };
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ClassifyActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void createReport() {

        activityClassifyBinding.progressBarPostBtn.setVisibility(View.VISIBLE);
        // PDF generator start
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        // Create page
        float pageWidthInInches = 8.27f; // A4 page width in inches
        float pageHeightInInches = 11.69f; // A4 page height in inches
        int pageWidth = (int) (pageWidthInInches * 72); // Convert inches to points (1 inch = 72 points)
        int pageHeight = (int) (pageHeightInInches * 72); // Convert inches to points (1 inch = 72 points)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page1 = pdfDocument.startPage(pageInfo);
        Canvas canvas = page1.getCanvas();

        //Get current Date time
        Calendar dateValue = Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yy");
        String currDate=dateFormat.format(dateValue.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
        String currTime=timeFormat.format(dateValue.getTime());

        userInfo[1] = predictionResult1;
        userInfo[2] = uploadLocation;
        userInfo[3] = currDate;
        userInfo[4] = currTime;





        // Draw text and lines
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(22f);
        paint.setColor(Color.rgb(22, 126, 30));
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("Crops Classification", pageInfo.getPageWidth() / 2, 72, paint);


        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(16f);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.SANS_SERIF);

        int startXPos = 72, startYPos = 90, endXPos = pageInfo.getPageWidth() - 72;
        startYPos += 25;
        for (int i = 0; i < 5; i++) {
            startYPos += 10;
            canvas.drawText(infoArr[i], startXPos + 5, startYPos, paint);
            canvas.drawText(": ", startXPos + 80, startYPos, paint);

            // Handle multiline text using StaticLayout
            String userInfoText = userInfo[i];
            int textWidth = pageInfo.getPageWidth() - (startXPos + 100); // Adjust the text width as needed

            // Create a TextPaint object for text rendering
            TextPaint textPaint = new TextPaint();
            textPaint.set(paint);

            // Create a StaticLayout instance for the multiline text
            StaticLayout staticLayout = new StaticLayout(userInfoText, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            int textHeight = staticLayout.getHeight();
            canvas.save();
            canvas.translate(startXPos + 90, startYPos - 15); // Adjust the starting position as needed
            staticLayout.draw(canvas);
            canvas.restore();

            // Update the startYPos to account for the total height of the multiline text
            startYPos += textHeight;

            startYPos += 5;
        }


        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(18f);
        paint.setColor(Color.rgb(22, 126, 30));
        startYPos += 20;
        canvas.drawText("Captured/Uploaded Image", pageInfo.getPageWidth()/2, startYPos, paint);

        // Draw image
        Rect imageRect = new Rect(100, startYPos + 10, pageInfo.getPageWidth() - 100, 650); // Define the position and size of the image
        canvas.drawBitmap(bitmap, null, imageRect, null);

        canvas.drawLine(startXPos, pageInfo.getPageHeight() -72, pageInfo.getPageWidth()-72, pageInfo.getPageHeight() -72, paint);

        startYPos = pageInfo.getPageHeight() - 72;

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(10f);
        paint.setColor(Color.BLACK);

        startYPos += 15;
        canvas.drawText("Mohammed Yunus,", startXPos, startYPos, paint);
        canvas.drawText("Computer Science & Engineering, University of Chittagong", startXPos, startYPos + 15, paint);

        // Finish page
        pdfDocument.finishPage(page1);

        // Save the PDF document
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Crops.pdf");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            pdfDocument.writeTo(outputStream);
            pdfDocument.close();
            outputStream.close();
            Toast.makeText(ClassifyActivity.this, "PDF saved successfully", Toast.LENGTH_SHORT).show();
            activityClassifyBinding.progressBarPostBtn.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ClassifyActivity.this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
            activityClassifyBinding.progressBarPostBtn.setVisibility(View.GONE);
        }

    }





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

            predictionResult = "Prediction: " + probability.get(0).getLabel() + "(" + score +"%)";
            predictionResult1 = probability.get(0).getLabel() + " (" + score +"%)";
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
                        userName = userDetails.getUserName();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ClassifyActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            });


            // Get the current date and time
            String currDateTime = getCurrentDateTime();

            // Upload post data
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(postUriImage));
            storageReference2.putFile(postUriImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Get the download URL of the uploaded image
                            storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String postProfileImageUrl;
                                    if (firebaseUser.getPhotoUrl() != null) {
                                        postProfileImageUrl = firebaseUser.getPhotoUrl().toString();
                                    } else {
                                        // Set the profile image from a drawable resource
                                        postProfileImageUrl = "android.resources://" + getPackageName() + "/" + R.drawable.ic_person_24;
                                    }

                                    String imageURL = uri.toString();

                                    String ImageUploadId = databaseReference.push().getKey();
                                    PostDetailsModel imageUploadInfo = new PostDetailsModel(postProfileImageUrl, imageURL, userID, userName, currDateTime, predictionResult, uploadLocation);

                                    databaseReference.child(ImageUploadId).setValue(imageUploadInfo);

                                    Toast.makeText(getApplicationContext(), "Post Updated", Toast.LENGTH_LONG).show();

                                    activityClassifyBinding.progressBarPostBtn.setVisibility(View.GONE);
                                    activityClassifyBinding.result.setText("");
                                    activityClassifyBinding.sharePostBtn.setEnabled(false);
                                    activityClassifyBinding.textViewLocationAddress.setText("");
                                    activityClassifyBinding.imageView.setImageResource(R.drawable.ic_image_24);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors that occurred while retrieving the download URL
                                    Toast.makeText(ClassifyActivity.this, "Failed to retrieve image download URL", Toast.LENGTH_SHORT).show();
                                    activityClassifyBinding.progressBarPostBtn.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors that occurred while uploading the image
                            Toast.makeText(ClassifyActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
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

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }



}