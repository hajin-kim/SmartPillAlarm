package com.example.smartpillalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ScanResultActivity extends AppCompatActivity {

    private static final String TAG = "ScanResultActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    Context thisContext;

    String prodCode;
    String drugName;
    String drugInfo;
    int num_pill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        // get extra data
        thisContext = this;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        prodCode = extras.getString(getString(R.string.extra_key_prodCode));

        final TextView textViewDrugName = findViewById(R.id.scan_result_textview_drug_name);
        final TextView textViewDrugInfo = findViewById(R.id.scan_result_textview_drug_info);

        textViewDrugName.setText(drugName);
        textViewDrugInfo.setText(drugInfo);

        Button buttonConfirm = findViewById(R.id.scan_result_button_confirm);
        Button buttonCancel = findViewById(R.id.scan_result_button_cancel);

        firebaseAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "What is my ID:"+firebaseAuth.getUid());
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        DatabaseReference pillDataRef = myRef.child("PillData").child(prodCode);

        pillDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String wholeName;
                Integer paranthesis_idx;

                drugInfo = "";
                for(DataSnapshot ds:snapshot.child("item_efficacy").getChildren()){
                    drugInfo += ds.getValue().toString();
                    drugInfo += "\n";
                }

                // drugName formatting: () 존재하면 그 전까지만 나오도록 포매팅
                wholeName = snapshot.child("item_name").getValue().toString();
                paranthesis_idx = wholeName.indexOf("(");
                if (paranthesis_idx != -1){
                    wholeName = wholeName.substring(0,paranthesis_idx);
                }
                drugName = wholeName;
                num_pill = Integer.parseInt(snapshot.child("pack_unit").getValue().toString());
                textViewDrugName.setText(drugName);
                textViewDrugInfo.setText(drugInfo);
//                num_pill = extras.getInt(getString(R.string.extra_key_numDrug));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drugName == null) {
                    Toast.makeText(getApplicationContext(), "다시 눌러주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent alarmGeneratorIntent = new Intent(thisContext, AlarmGeneratorActivity.class);
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_prodCode), prodCode);
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_drugName), drugName);
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_drugInfo), drugInfo); // TODO: 여기에 전달할 효능효과 등의 내용을 넣어주세요.
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_numDrug), num_pill);
                startActivity(alarmGeneratorIntent);
                finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(thisContext, MainActivity.class));
                finish();
            }
        });
    }
}