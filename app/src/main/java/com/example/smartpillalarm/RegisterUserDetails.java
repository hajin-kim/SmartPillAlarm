package com.example.smartpillalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class RegisterUserDetails extends AppCompatActivity {

    private EditText age;
    private RadioButton sex_male;
    private RadioButton sex_female;
    private RadioButton pregnancy_positive;
    private RadioButton pregnancy_negative;
    private RadioButton blood_pressure_positive;
    private RadioButton blood_pressure_negative;
    private RadioButton diabetes_positive;
    private RadioButton diabetes_negative;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_details);

        age = (EditText)findViewById(R.id.et_userdetails_age);
        sex_male = (RadioButton)findViewById(R.id.checkbox_userdetails_male);
        sex_female = (RadioButton)findViewById(R.id.checkbox_userdetails_female);
        pregnancy_positive = (RadioButton)findViewById(R.id.checkbox_userdetails_pregnancy_positive);
        pregnancy_negative = (RadioButton)findViewById(R.id.checkbox_userdetails_pregnancy_negative);
        blood_pressure_positive = (RadioButton)findViewById(R.id.checkbox_userdetails_blood_pressure_positive);
        blood_pressure_negative = (RadioButton)findViewById(R.id.checkbox_userdetails_blood_pressure_negative);
        diabetes_positive = (RadioButton)findViewById(R.id.checkbox_userdetails_diabetes_positive);
        diabetes_negative = (RadioButton)findViewById(R.id.checkbox_userdetails_diabetes_negative);
        register = (Button)findViewById(R.id.btn_userdetails_register);

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
                startActivity(new Intent(RegisterUserDetails.this, Login.class));
            }
        });

    }
}