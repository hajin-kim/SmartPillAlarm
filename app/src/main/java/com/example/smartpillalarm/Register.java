package com.example.smartpillalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private EditText register_ID, register_Email, register_PW;
    private Button register_button;
    private TextView register_already_registered;
    private FirebaseAuth firebaseAuth;
    private Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setup_UI_Views();    // UI View 설정

        appContext = getApplicationContext();

        firebaseAuth = FirebaseAuth.getInstance();

        register_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(all_values_filled()){
                    // Upload data to database
                    String reg_Email = register_Email.getText().toString().trim();
                    String reg_PW = register_PW.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(reg_Email,reg_PW).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                send_email_verification();
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

    private void setup_UI_Views(){
        register_ID = (EditText)findViewById(R.id.et_register_ID);
        register_Email = (EditText)findViewById(R.id.et_register_Email);
        register_PW = (EditText)findViewById(R.id.et_register_PW);
        register_button = (Button)findViewById(R.id.btn_register_register);
        register_already_registered = (TextView)findViewById(R.id.tv_register_already_registered);
    }

    private Boolean all_values_filled(){
        Boolean result = false;
        String id = register_ID.getText().toString();
        String pw = register_PW.getText().toString();
        String email = register_Email.getText().toString();

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

        return true;
    }

    private void send_email_verification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
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