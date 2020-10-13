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

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
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
                //finish();
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

        // DEV CODE
//        try {
//            String productCode = "199903739";
//            System.out.println("Start getAPI");
//            Methods.getAPIResponse(getApplicationContext(), productCode);
//            System.out.println("Done getAPI");
//        } catch (IOException e) {
//            System.out.println("Tracing getAPI");
//            e.printStackTrace();
//            System.out.println("Error found");
//            Toast.makeText(appContext, "Error found", Toast.LENGTH_SHORT).show();
//        }

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
        String productCode = "NULL";

        if(result != null){
            if(result.getContents() != null){
                Toast.makeText(getApplicationContext(), result.getFormatName(), Toast.LENGTH_SHORT).show();

                String codeFound = result.getContents();
                // get barcode format
                switch (BarcodeFormat.valueOf(result.getFormatName())) {
                    case EAN_13:
//                        codeFound = result.getContents();
//                        Toast.makeText(appContext, BarcodeFormat.EAN_13 + " " + codeFound, Toast.LENGTH_SHORT).show();
                        break;

                    case DATA_MATRIX:
                        codeFound = codeFound.substring(4, 4+13);
//                        Toast.makeText(appContext, BarcodeFormat.DATA_MATRIX + " " + codeFound, Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(appContext, result.getFormatName(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(appContext, "인식할 수 없는 바코드입니다!", Toast.LENGTH_SHORT).show();

                }

                String response = "";
                try {
                    productCode = searchProdCode(codeFound);
                    System.out.println("Start getAPI");
                    response = Methods.getAPIResponse(getApplicationContext(), productCode);
                    System.out.println("Done getAPI");
                } catch (IOException | IllegalAccessException e) {
                    System.out.println("Tracing getAPI");
                    e.printStackTrace();
                    System.out.println("Error found");
                    Toast.makeText(appContext, "Error found", Toast.LENGTH_SHORT).show();

                }

//                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("제품코드: "+productCode);
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
//                Toast.makeText(appContext, codeFound, Toast.LENGTH_SHORT).show();
                System.out.println("BARCODE TEST "+codeFound);
            }
            else{
                Toast.makeText(this, "결과 없음", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private Integer getRawID(String title) throws IllegalAccessException {
        Integer RawID = 0;
        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            if(title.equals(fields[count].getName())){
                RawID = fields[count].getInt(fields[count]);
            }
        }
        return RawID;
    }

    private String searchProdCode(String barcode) throws IOException, IllegalAccessException {
        String prodCode = "NULL";  // returns corresponding product code
        String title = barcode.substring(3,7);
        String key = barcode.substring(7);
        Integer count = 0;

        InputStream inputStream = getResources().openRawResource(getRawID("c" + title));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

        String line;
        bufferedReader.readLine(); // skip first line
        while((line = bufferedReader.readLine()) != null) {
            count++;
            // Split by ","
            String[] tokens = line.split(",");
            String compCode = "880"+title+tokens[1].substring(1); // ex) 880+0500+000102
            if (compCode.equals(barcode)) {
                prodCode = tokens[2].substring(1);  // get rid of "'" ex) '1234 => 1234
                Toast.makeText(this, "Found: "+prodCode, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Finished: "+prodCode);
                break;
            }
            else{
                Log.d(TAG, "Iter: "+count+" Current: "+compCode+" Objective: "+barcode);
            }
        }
        return prodCode;
    }

}
