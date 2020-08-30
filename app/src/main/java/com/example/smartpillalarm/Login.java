package com.example.smartpillalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

public class Login extends AppCompatActivity {

    private EditText login_ID;
    private EditText login_PW;
    private TextView login_countdown;
    private Button login_button;
    private Integer counter = 5;
    private TextView login_register;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_ID = (EditText)findViewById(R.id.et_login_ID);
        login_PW = (EditText)findViewById(R.id.et_login_PW);
        login_countdown = (TextView)findViewById(R.id.tv_login_countdown);
        login_button = (Button)findViewById(R.id.btn_login_login);
        login_register = (TextView)findViewById(R.id.tv_login_register);

        login_countdown.setText("남은 로그인 횟수: 5");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(Login.this, MainActivity.class));
        }

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                validate(login_ID.getText().toString(), login_PW.getText().toString());
            }
        });

        login_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    private void validate(String user_ID, String user_PW){
        progressDialog.setMessage("로그인 중. 잠시만 기다려 주십시오.");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(user_ID,user_PW).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(Login.this,"로그인 성공",Toast.LENGTH_SHORT).show();
                    check_email_verification();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(Login.this,"로그인에 실패했습니다",Toast.LENGTH_SHORT).show();
                    counter--;
                    login_countdown.setText("남은 로그인 횟수: " + counter);
                    if(counter == 0){
                        login_button.setEnabled(false);
                    }
                }
            }
        });
    }

    private void check_email_verification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean email_verified = firebaseUser.isEmailVerified();
        if(email_verified){
            finish();
            startActivity(new Intent(Login.this, MainActivity.class));
        }
        else{
            Toast.makeText(this, "이메일을 확인해 주시기 바랍니다", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

}