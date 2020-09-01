package com.example.smartpillalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private ImageView profile_pic;
    private TextView profile_Email, profile_ID, profile_age;
    private Button profile_update;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_pic = findViewById(R.id.iv_profile_pic);
        profile_Email = findViewById(R.id.tv_profile_Email);
        profile_ID = findViewById(R.id.tv_profile_ID);
        profile_age = findViewById(R.id.tv_profile_age);
        profile_update = findViewById(R.id.btn_profile_update);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                profile_Email.setText("Email: "+userDetails.getE_mail());
                profile_ID.setText("ID: "+userDetails.getId());
                profile_age.setText("나이: "+userDetails.getAge());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(Profile.this, UpdateProfile.class));
            }
        });
    }
}