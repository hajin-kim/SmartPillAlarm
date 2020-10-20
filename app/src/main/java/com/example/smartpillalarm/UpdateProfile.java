package com.example.smartpillalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {

    private TextView update_Email;
    private EditText update_ID, update_age;
    private ImageView update_pic;
    private Button update_finish;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    String gender, pregnancy, blood_pressure, diabetes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        update_pic = findViewById(R.id.iv_update_pic);
        update_Email = findViewById(R.id.tv_update_Email);
        update_ID = findViewById(R.id.et_update_ID);
        update_age = findViewById(R.id.et_update_age);
        update_finish = findViewById(R.id.btn_update_finish);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                update_Email.setText(userDetails.getE_mail());
                update_ID.setText(userDetails.getId());
                update_age.setText(userDetails.getAge());
                gender = userDetails.getGender();
                pregnancy = userDetails.getPregnancy();
                blood_pressure = userDetails.getBlood_pressure();
                diabetes = userDetails.getDiabetes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        update_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = update_Email.getText().toString().trim();
                String ID = update_ID.getText().toString().trim();
                String age = update_age.getText().toString().trim();

                UserDetails userDetails = new UserDetails(Email,ID,age,gender,pregnancy,blood_pressure,diabetes);
                databaseReference.child("UserDetails").setValue(userDetails);
                finish();
                startActivity(new Intent(UpdateProfile.this, Profile.class));
            }
        });
    }
}