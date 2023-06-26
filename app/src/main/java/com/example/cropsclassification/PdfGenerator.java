package com.example.cropsclassification;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.cropsclassification.databinding.ActivityPdfGeneratorBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStream;

public class PdfGenerator extends AppCompatActivity {

    ActivityPdfGeneratorBinding activityPdfGeneratorBinding;
    String[] infoArr = new String[] {"Name", "Email", "Mobile", "DateOfBirth", "Gender"};
    String[] userInfo = new String[5];
    String fullName, email, dob, gender, mobile;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPdfGeneratorBinding = ActivityPdfGeneratorBinding.inflate(getLayoutInflater());
        setContentView(activityPdfGeneratorBinding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        activityPdfGeneratorBinding.actionbarPdf.actionTitle.setText("Crystal Report");

        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Extracting User reference from database for "Current user"
        String  userID = firebaseUser.getUid();
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

                    activityPdfGeneratorBinding.editPdfName.setText(fullName);
                    activityPdfGeneratorBinding.editPdfEmail.setText(email);
                    activityPdfGeneratorBinding.editPdfMobile.setText(mobile);
                    activityPdfGeneratorBinding.editPdfDob.setText(dob);
                    activityPdfGeneratorBinding.editPdfGender.setText(gender);

                    userInfo[0] = fullName;
                    userInfo[1] = email;
                    userInfo[2] = mobile;
                    userInfo[3] = dob;
                    userInfo[4] = gender;

                    activityPdfGeneratorBinding.btnPdfGen.setEnabled(true);
                };
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PdfGenerator.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });

        activityPdfGeneratorBinding.btnPdfGen.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                createReport();
            }
        });

    }

    private void createReport() {


        //PDF generator start
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
        PdfDocument.Page page1 = pdfDocument.startPage(pageInfo);
        Canvas canvas = page1.getCanvas();

        // Draw icon
        Drawable iconDrawable = getResources().getDrawable(R.drawable.ic_baseline_menu_24);
        float scale = getResources().getDisplayMetrics().density;
        int iconWidthDP = 15; // Desired width in dp
        int iconWidthPixels = (int) (iconWidthDP * scale + 0.5f);
        int iconHeightDP = 20; // Desired height in dp
        int iconHeightPixels = (int) (iconHeightDP * scale + 0.5f);
        iconDrawable.setBounds(0, 0, iconWidthPixels, iconHeightPixels);
        iconDrawable.draw(canvas);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20f);
        paint.setColor(Color.rgb(22, 126, 30));
        canvas.drawText("Crops Classification", pageInfo.getPageWidth()/2 , 50, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15f);
        paint.setColor(Color.rgb(22, 126, 30));
        canvas.drawText("Personal Information", 20 , 80, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(12f);
        paint.setColor(Color.BLACK);

        int startXPos = 20, startYPos = 100, endXPos = pageInfo.getPageWidth() - 20;
        canvas.drawLine(startXPos, startYPos + 5, endXPos, startYPos + 5, paint);
        startYPos += 15;
        for(int i=0;i<5;i++){
            startYPos += 10;
            canvas.drawText(infoArr[i], startXPos + 5, startYPos, paint);
            canvas.drawText(userInfo[i], 105, startYPos, paint);
            startYPos += 10;
            canvas.drawLine(startXPos, startYPos + 5,endXPos, startYPos + 5, paint);
            startYPos += 15;
        }

        canvas.drawLine(20, 105, 20, 280, paint);
        canvas.drawLine(100, 105, 100, 280, paint);
        canvas.drawLine(pageInfo.getPageWidth() - 20, 105, pageInfo.getPageWidth() - 20, 280, paint);

        pdfDocument.finishPage(page1);

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
            Toast.makeText(PdfGenerator.this, "PDF saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(PdfGenerator.this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
        }

    }



}