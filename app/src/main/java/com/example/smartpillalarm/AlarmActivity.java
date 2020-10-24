package com.example.smartpillalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AlarmActivity extends AppCompatActivity {

    private Button buttonTookPill;
    private Button buttonDidnotTakePill;
    private Button buttonShutdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        buttonTookPill = findViewById(R.id.button_alarm_took_pill);
        buttonDidnotTakePill = findViewById(R.id.button_alarm_didnot_take_pill);
        buttonShutdown = findViewById(R.id.button_alarm_shutdown);

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