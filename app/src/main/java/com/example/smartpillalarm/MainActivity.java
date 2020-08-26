package com.example.smartpillalarm;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button logout_button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        final Context context = this;

        firebaseAuth = FirebaseAuth.getInstance();    // for login-logout via Firebase

        logout_button = (Button)findViewById(R.id.btn_main_logout);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // DEV CODE
//        SharedPreferences sharedPreferences = getSharedPreferences(AlarmDB.DB_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(AlarmDB.NUM_OF_ALARM, 0);
//        editor.apply();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            deleteSharedPreferences(AlarmDB.DB_NAME);
        }
        //

        // DEV CODE
        AlarmDB.printAlarmDB(this);
        Methods.printNextAlarm(this);

        AlarmDB alarmDB = null;

        try {
            alarmDB = AlarmDB.getInstance(getSharedPreferences(AlarmDB.DB_NAME, MODE_PRIVATE));
        } catch (Exception e) {
            Methods.generateDateToast(getApplicationContext(),
                    R.string.message_on_throw_database_fault);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                deleteSharedPreferences(AlarmDB.DB_NAME);
            }
            return;
        }

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
}
