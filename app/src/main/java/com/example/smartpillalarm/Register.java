package com.example.smartpillalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText register_ID, register_Email, register_PW, register_age;
    private Button register_button;
    private TextView register_already_registered;
    private FirebaseAuth firebaseAuth;
    private RadioGroup rgroup_gender;
    private RadioButton rbutton_male, rbutton_female;
    private CheckBox checkbox_pregnancy;
    private CheckBox checkbox_diabetes;
    private CheckBox checkbox_blood_pressure;
    private Context appContext;

    String id, pw, email, age;
    boolean gender = true, pregnancy = false, blood_pressure = false, diabetes = false;

    private void setup_UI_Views(){
        register_ID = (EditText)findViewById(R.id.et_register_ID);
        register_Email = (EditText)findViewById(R.id.et_register_Email);
        register_PW = (EditText)findViewById(R.id.et_register_PW);
        register_age = (EditText)findViewById(R.id.et_register_age);
        register_button = (Button)findViewById(R.id.btn_register_register);
        register_already_registered = (TextView)findViewById(R.id.tv_register_already_registered);
        rgroup_gender = (RadioGroup)findViewById(R.id.rgroup_register_gender);
        rbutton_male = (RadioButton)findViewById(R.id.rbutton_register_male);
        rbutton_female = (RadioButton)findViewById(R.id.rbutton_register_female);
        checkbox_blood_pressure = (CheckBox)findViewById(R.id.checkbox_register_blood_pressure);
        checkbox_diabetes = (CheckBox)findViewById(R.id.checkbox_register_diabetes);
        checkbox_pregnancy = (CheckBox)findViewById(R.id.checkbox_register_pregnancy);
        checkbox_pregnancy.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setup_UI_Views();    // UI View 설정

        firebaseAuth = FirebaseAuth.getInstance();

        // 임신, 고혈압, 당뇨 체크박스
        checkbox_pregnancy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(checkbox_pregnancy.isChecked() && gender == false){
                    pregnancy = true;
                }
                else{
                    pregnancy = false;
                }
            }
        });

        checkbox_blood_pressure.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(checkbox_blood_pressure.isChecked()){
                    blood_pressure = true;
                }
                else{
                    blood_pressure = false;
                }
            }
        });

        checkbox_diabetes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(checkbox_diabetes.isChecked()){
                    diabetes = true;
                }
                else{
                    diabetes = false;
                }
            }
        });

        // 회원가입 완료 버튼
        register_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(allValuesFilled()){
                    // Upload data to database
                    String reg_Email = register_Email.getText().toString().trim();
                    String reg_PW = register_PW.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(reg_Email,reg_PW).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                //sendEmailVerification();
                                sendUserData();
                                //firebaseAuth.signOut();
                                finish();
                                startActivity(new Intent(Register.this, Login.class));
                            }
                            else{
                                Methods.generateToast(appContext, R.string.tv_register_register_failed);
                            }
                        }
                    });
                }
            }
        });

        register_already_registered.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(Register.this, Login.class));
            }
        });

    }

    private void sendUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        UserDetails userDetails = new UserDetails(email, id,age,gender,pregnancy,blood_pressure,diabetes);
        myRef.setValue(userDetails);
        Toast.makeText(this, "데이터베이스 업로드 완료", Toast.LENGTH_SHORT).show();
    }

    public void checkButton(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbutton_register_male:
                if (checked)
                    gender = true;
                    pregnancy = false;
                    if(checkbox_pregnancy.isChecked()){
                        checkbox_pregnancy.toggle();
                    }
                    checkbox_pregnancy.setEnabled(false);
                    break;
            case R.id.rbutton_register_female:
                if (checked)
                    gender = false;
                    checkbox_pregnancy.setEnabled(true);
                    break;
        }
    }

    private boolean allValuesFilled(){
        id = register_ID.getText().toString();
        pw = register_PW.getText().toString();
        email = register_Email.getText().toString();
        age = register_age.getText().toString();


        // check if user filled id, pw and email
        if (id.isEmpty()) {
            Methods.generateToast(appContext, R.string.tv_common_id_is_empty);
            return false;
        }
        if (pw.isEmpty()) {
            Methods.generateToast(appContext, R.string.tv_common_pw_is_empty);
            return false;
        }
        if (email.isEmpty()){
            Methods.generateToast(appContext, R.string.tv_common_email_is_empty);
            return false;
        }
        if(age.isEmpty()){
            Methods.generateToast(appContext, R.string.tv_common_age_is_empty);
            return false;
        }

        return true;
    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        Methods.generateToast(appContext, R.string.tv_register_verify_email_sent);
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Register.this, Login.class));
                    }
                    else{
                        Methods.generateToast(appContext, R.string.tv_register_verify_email_sending_failed);
                    }
                }
            });
        }
    }

}