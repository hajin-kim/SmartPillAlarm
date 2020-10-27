package com.example.smartpillalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AlarmActivity extends AppCompatActivity {

    private Button buttonTookPill;
    private Button buttonDidnotTakePill;
    private Button buttonShutdown;

    private String prodCode;
    private String drugName;
    private String drugInfo;
    private int num_pill;
    private boolean extras_loaded;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // get data from pending
        Bundle extras = getIntent().getExtras();

        buttonTookPill = findViewById(R.id.button_alarm_took_pill);
        buttonDidnotTakePill = findViewById(R.id.button_alarm_didnot_take_pill);
        buttonShutdown = findViewById(R.id.button_alarm_shutdown);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        assert extras != null;
        prodCode = extras.getString(getString(R.string.extra_key_prodCode));
        if (!prodCode.equals(getString(R.string.extra_key_NULL))) {
            drugName = extras.getString(getString(R.string.extra_key_drugName));
            drugInfo = extras.getString(getString(R.string.extra_key_drugInfo));
            num_pill = extras.getInt(getString(R.string.extra_key_numDrug));
            extras_loaded = true;
        }
        System.out.println("ALARMACTIVITY DATA GETTING TEST: "+prodCode);

        buttonTookPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlProdQuant(firebaseAuth, firebaseDatabase, prodCode, -1);
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

    public void controlProdQuant(FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase, String prodCode, Integer prodQuant){
        /*
         * 제품 수량 조절하는 함수: 주로 약 복용 체크시 개수 하나씩 까는 데 사용할 것
         * 유저의 firebaseAuth, firebaseDatabase를 제공받은 후 제품의 prodCode를 받아 수량을 조절함
         * ex) 알약 개수 두 개 차감하려면:
         *     >> controlProdQuant(firebaseAuth, firebaseDatabase, prodCode, -2);
         * */
        final Integer quantity = prodQuant;
        final DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        final DatabaseReference myQuant = myRef.child("PillData").child(prodCode).child("pack_unit");

        // onDataChange: due to asynchronous behavior, everything must be done inside it!
        myQuant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentQuant = Integer.parseInt(snapshot.getValue().toString());
                myQuant.setValue(String.valueOf(currentQuant+quantity));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}