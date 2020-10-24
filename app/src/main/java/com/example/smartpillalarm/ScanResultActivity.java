package com.example.smartpillalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScanResultActivity extends AppCompatActivity {

    Context thisContext;

    String prodCode;
    String drugName;
    String drugInfo;
    int numDrug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        thisContext = this;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        prodCode = extras.getString(getString(R.string.extra_key_prodCode));
        drugName = extras.getString(getString(R.string.extra_key_drugName));
        drugInfo = extras.getString(getString(R.string.extra_key_drugInfo));
        numDrug = extras.getInt(getString(R.string.extra_key_numDrug));

        TextView textViewDrugName = findViewById(R.id.scan_result_textview_drug_name);
        TextView textViewDrugInfo = findViewById(R.id.scan_result_textview_drug_info);

        textViewDrugName.setText(drugName);
        textViewDrugInfo.setText(drugInfo);

        Button buttonConfirm = findViewById(R.id.scan_result_button_confirm);
        Button buttonCancel = findViewById(R.id.scan_result_button_cancel);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent alarmGeneratorIntent = new Intent(thisContext, AlarmGeneratorActivity.class);
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_prodCode), prodCode);
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_drugName), drugName);
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_drugInfo), drugInfo); // TODO: 여기에 전달할 효능효과 등의 내용을 넣어주세요.
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_numDrug), numDrug);
                startActivity(alarmGeneratorIntent);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}