package com.example.cropsclassification;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cropsclassification.databinding.ActivityCommentBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding activityCommentBinding;

    DatabaseReference userRef, commentRef;
    String postKey;

    CommentItemAdapter commentItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCommentBinding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(activityCommentBinding.getRoot());
        getSupportActionBar().setTitle("Comment");

        postKey=getIntent().getStringExtra("postKey");

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        commentRef=FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("comments");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUserId = firebaseUser.getUid();

        activityCommentBinding.commentRecyclerviewId.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(commentRef, CommentModel.class)
                        .build();

        commentItemAdapter = new CommentItemAdapter(options);

        activityCommentBinding.commentRecyclerviewId.setAdapter(commentItemAdapter);


        activityCommentBinding.commentSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String commentUserName = snapshot.child("userName").getValue().toString();

                            String commentUserImage;
                            if (firebaseUser.getPhotoUrl() != null) {
                                commentUserImage = firebaseUser.getPhotoUrl().toString();
                            } else {
                                // Set the profile image from a drawable resource
                                commentUserImage = "android.resources://" + getPackageName() + "/" + R.drawable.ic_person_24;
                            }

                            processComment(commentUserName,commentUserImage, currentUserId);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }

    private void processComment(String commentUserName, String commentUserImage, String currentUserId) {

        String commentText = activityCommentBinding.editTextComment.getText().toString();
        String randomPostKey = currentUserId +""+new Random().nextInt(1000);

        Calendar dateValue = Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-mm-yy");
        String currDate=dateFormat.format(dateValue.getTime());

        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
        String currTime=timeFormat.format(dateValue.getTime());

        HashMap commentDetails=new HashMap();
        commentDetails.put("commentUserid",currentUserId);
        commentDetails.put("commentUserName",commentUserName);
        commentDetails.put("commentUserImage",commentUserImage);
        commentDetails.put("commentText",commentText);
        commentDetails.put("commentDate",currDate);
        commentDetails.put("commentTime",currTime);

        commentRef.child(randomPostKey).updateChildren(commentDetails)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Comment Added", Toast.LENGTH_LONG).show();
                            activityCommentBinding.editTextComment.setText("");
                        }
                        else {
                            Toast.makeText(getApplicationContext(), task.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();
        commentItemAdapter.startListening();
    }
}