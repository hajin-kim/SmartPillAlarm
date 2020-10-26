package com.example.smartpillalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AlarmGeneratorActivity extends AppCompatActivity {

    private Context appContext;
    private Context thisContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_generator);

        appContext = getApplicationContext();
        thisContext = this;

        final TimePicker picker = findViewById(R.id.time_picker);
        picker.setIs24HourView(true);

        final AlarmDB alarmDB = AlarmDB.getInstance(appContext);
        Alarm alarm = alarmDB.getEarliestAlarm(true);

        // show a message for the next alarm
        if (alarm == null) {
            Methods.generateToast(appContext,
                    R.string.message_on_create_where_no_alarm);
        }
        else {
            Methods.generateDateToast(appContext,
                    R.string.message_on_create_where_with_next_alarm,
                    alarm.getTime());
        }

        // get the current local time if not
        Date currentTime = Calendar.getInstance().getTime();

        // set the time of the timePicker for the default(current)
        SimpleDateFormat HourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat MinuteFormat = new SimpleDateFormat("mm", Locale.getDefault());

        int pre_hour = Integer.parseInt(HourFormat.format(currentTime));
        int pre_minute = Integer.parseInt(MinuteFormat.format(currentTime));

        if (Build.VERSION.SDK_INT >= 23){
            picker.setHour(pre_hour);
            picker.setMinute(pre_minute);
        }
        else {
            picker.setCurrentHour(pre_hour);
            picker.setCurrentMinute(pre_minute);
        }


        // generate the button_alarm_generator
        final Button button_alarm_generator = findViewById(R.id.button_alarm_generator);
        button_alarm_generator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // when this button is clicked
                int num_of_alarm = alarmDB.getNum_of_alarm();
                if (num_of_alarm >= AlarmDB.MAX_ALARM) {
                    Methods.generateToast(appContext, R.string.warning_number_of_alarm_exceeds_limitation);
                    return;
                }

                // load intent extra data
                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                String prodCode = "";
                String drugName = "";
                String drugInfo = "";
                int num_pill = 0;
                boolean extras_loaded = false;
                if (extras != null) {
                    prodCode = extras.getString(getString(R.string.extra_key_prodCode));
                    if (!prodCode.equals(getString(R.string.extra_key_NULL))) {
                        drugName = extras.getString(getString(R.string.extra_key_drugName));
                        drugInfo = extras.getString(getString(R.string.extra_key_drugInfo));
                        num_pill = extras.getInt(getString(R.string.extra_key_numDrug));
                        extras_loaded = true;
                    }
                }

                // get selected time from the timePicker
                int hour_24, minute;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour_24 = picker.getHour();
                    minute = picker.getMinute();
                } else {
                    hour_24 = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }

                // make a Calendar instance
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour_24);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                // if the time exceeds 1 day then add 1 additional day
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }

                // insert alarm into DB
                // show a toast message for that the alarm is generated
                boolean alarm_gen_succeed = false;
                if (extras_loaded) {
                    if (alarmDB.insertAlarm(calendar, drugInfo, true, drugName, prodCode, num_pill)) {
                        Methods.generateDateToast(appContext,
                                R.string.message_alarm_generated,
                                calendar.getTime());
                        alarm_gen_succeed = true;
                    }
                } else {
                    if (alarmDB.insertAlarm(calendar, "alarm", true)) {
                        Methods.generateDateToast(appContext,
                                R.string.message_alarm_generated,
                                calendar.getTime());
                        alarm_gen_succeed = true;
                    }
                }
                if (!alarm_gen_succeed) {
                    /*
                    should be implemented
                    H.K.
                     */
                    // DEV CODE
                    System.out.println("!!! 알람 생성 실패 !!!");
                    return;
                }

                // reserve notification
                Methods.reserveNotification(appContext);
                if (extras_loaded)
                    startActivity(new Intent(thisContext, MainActivity.class));
                finish();
            }
        });
    }
}
