package com.example.smartpillalarm.codescanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpillalarm.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class QRScannerActivity extends AppCompatActivity {

    //view Objects
    private Button buttonScan;
    private TextView textViewName, textViewAddress, textViewResult;

    //qr code scanner object
    private IntentIntegrator qrScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        //View Objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewResult = (TextView)  findViewById(R.id.textViewResult);

        //intializing scan object
        qrScanner = new IntentIntegrator(this);

        //button onClick
        buttonScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //scan option
                qrScanner.setPrompt("Scanning...");
                qrScanner.setOrientationLocked(false);
                qrScanner.initiateScan();
            }
        });
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(getApplicationContext(), "취소!", Toast.LENGTH_SHORT).show();
            } else {
                //qrcode 결과가 있으면
                Toast.makeText(getApplicationContext(), "스캔완료!", Toast.LENGTH_SHORT).show();
                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    textViewName.setText(obj.getString("name"));
                    textViewAddress.setText(obj.getString("address"));
                } catch (JSONException e) {
//                    e.printStackTrace();
                    textViewResult.setText(result.getContents());
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
