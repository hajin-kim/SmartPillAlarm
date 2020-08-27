package com.example.smartpillalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindPW extends AppCompatActivity {

    private EditText findpw_Email;
    private Button findpw_reset_button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_p_w);

        findpw_Email = (EditText)findViewById(R.id.et_findpw_email);
        findpw_reset_button = (Button)findViewById(R.id.btn_findpw_reset_pw);
        firebaseAuth = FirebaseAuth.getInstance();

        findpw_reset_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String Email = findpw_Email.getText().toString().trim();
                if(Email.equals("")){
                    Toast.makeText(FindPW.this, "등록한 이메일 주소를 입력해 주십시오.", Toast.LENGTH_SHORT).show();
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(FindPW.this, "비밀번호 재설정을 위한 메일이 발송되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(FindPW.this, Login.class));
                            }
                            else{
                                Toast.makeText(FindPW.this, "오류 발생. 인터넷 연결을 확인해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}