package com.example.smartpillalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appContext = getApplicationContext();

        login_ID = (EditText)findViewById(R.id.et_login_ID);
        login_PW = (EditText)findViewById(R.id.et_login_PW);
        login_countdown = (TextView)findViewById(R.id.tv_login_countdown);
        login_button = (Button)findViewById(R.id.btn_login_login);
        login_register = (TextView)findViewById(R.id.tv_login_register);

        login_countdown.setText(getString(R.string.tv_login_chance_left) + "5");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(Login.this, MainActivity.class));
        }

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String user_id = login_ID.getText().toString();
                String user_pw = login_PW.getText().toString();

                // check if user filled id and pw
                if (user_id.isEmpty()) {
                    Methods.generateToast(appContext, R.string.tv_common_id_is_empty);
                    return;
                }
                if (user_pw.isEmpty()) {
                    Methods.generateToast(appContext, R.string.tv_common_pw_is_empty);
                    return;
                }

                validate(user_id, user_pw);
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
        progressDialog.setMessage(getString(R.string.tv_login_waiting));
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(user_ID,user_PW).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    checkEmailVerification();
                }
                else{
                    progressDialog.dismiss();
                    Methods.generateToast(appContext, R.string.tv_login_failure);
                    counter--;
                    login_countdown.setText(getString(R.string.tv_login_chance_left) + counter);
                    if(counter == 0){
                        login_button.setEnabled(false);
                    }
                }
            }
        });
    }

    private void checkEmailVerification(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        boolean email_verified = firebaseUser.isEmailVerified();
        if(email_verified){
            finish();
            Methods.generateToast(appContext, R.string.tv_login_success);
            startActivity(new Intent(Login.this, MainActivity.class));
        }
        else{
            Methods.generateToast(appContext, R.string.tv_login_check_your_email);
            firebaseAuth.signOut();
        }
    }

}