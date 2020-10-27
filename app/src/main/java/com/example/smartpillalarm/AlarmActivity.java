package com.example.smartpillalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AlarmActivity extends AppCompatActivity {

    private Button buttonTookPill;
    private Button buttonDidnotTakePill;
    private Button buttonShutdown;

    private FirebaseAuth firebaseAuth;
    private String prodCode;
    private String drugName;
    private String drugInfo;
    private int num_pill;
    private boolean extras_loaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // get data from pending
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        prodCode = extras.getString(getString(R.string.extra_key_prodCode));
        if (!prodCode.equals(getString(R.string.extra_key_NULL))) {
            drugName = extras.getString(getString(R.string.extra_key_drugName));
            drugInfo = extras.getString(getString(R.string.extra_key_drugInfo));
            num_pill = extras.getInt(getString(R.string.extra_key_numDrug));
            extras_loaded = true;
        }
        System.out.println("ALARMACTIVITY DATA GETTING TEST: "+prodCode);

        buttonTookPill = findViewById(R.id.button_alarm_took_pill);
        buttonDidnotTakePill = findViewById(R.id.button_alarm_didnot_take_pill);
        buttonShutdown = findViewById(R.id.button_alarm_shutdown);

        // TODO: controlProdQuant 사용해서 알약 개수 조절하기
        // ex) 알약 개수 두 개 차감하려면:
        //     >> controlProdQuant(firebaseAuth, firebaseDatabase, prodCode, -2);
//        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                controlProdQuant(firebaseAuth, firebaseDatabase, "199303108", -2);
//            }
//        });

        buttonTookPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        buttonDidnotTakePill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonShutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}