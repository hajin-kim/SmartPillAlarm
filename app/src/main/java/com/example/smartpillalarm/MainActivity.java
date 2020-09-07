package com.example.smartpillalarm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button logout_button;
    private Button profile_button;
    private Button scan_button;
    private Context appContext;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout_button = findViewById(R.id.btn_main_logout);
        profile_button = findViewById(R.id.btn_main_profile);
        scan_button = findViewById(R.id.btn_main_scan);
        appContext = getApplicationContext();

        final Context context = this;

        firebaseAuth = FirebaseAuth.getInstance();    // for login-logout via Firebase

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        profile_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
                startActivity(new Intent(MainActivity.this, Profile.class));
            }
        });

        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        // DEV CODE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            deleteSharedPreferences(AlarmDB.DB_NAME);
        }

        // DEV CODE
        AlarmDB.printAlarmDB(appContext);
//        Methods.printNextAlarm(appContext);

        // renew
//        AlarmDB.getInstance(appContext);

        final Button button_start = findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AlarmGeneratorActivity.class));
//                finish();
            }
        });
    }

    private void logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(MainActivity.this, Login.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_main_logout:{
                logout();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void scanCode(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(CaptureAct.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("코드 스캔");
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() != null){
//                Toast.makeText(getApplicationContext(), result.getFormatName(), Toast.LENGTH_SHORT).show();

                // get barcode format
                switch (BarcodeFormat.valueOf(result.getFormatName())) {
                    case EAN_13:
                        Toast.makeText(this, BarcodeFormat.EAN_13 + result.getContents(), Toast.LENGTH_SHORT).show();

                    case DATA_MATRIX:
//                        Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                        break;

                    default:
//                        Toast.makeText(this, "인식할 수 없는 바코드입니다!", Toast.LENGTH_SHORT);
                        Toast.makeText(this, result.getFormatName(), Toast.LENGTH_SHORT).show();

                }

//                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("스캔 결과");
                builder.setPositiveButton("재시도", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanCode();
                    }
                }).setNegativeButton("완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                Toast.makeText(this, "결과 없음", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
}
