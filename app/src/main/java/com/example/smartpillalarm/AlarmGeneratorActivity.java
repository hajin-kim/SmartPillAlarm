package com.example.smartpillalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_generator);

        final TimePicker picker = findViewById(R.id.time_picker);
        picker.setIs24HourView(true);

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


        // load the time onto the nextNotifyTime
        Calendar nextNotifyTime = Calendar.getInstance();

        // show a message for the next alarm
        int num_of_alarm = alarmDB.num_of_alarm;

        // get the time of the alarm if exist
        // get the current local time if not
        long millis;
        if (num_of_alarm == 0) {
            Methods.generateDateToast(getApplicationContext(),
                    R.string.message_on_create_where_no_alarm);
            millis = nextNotifyTime.getTimeInMillis();
        }
        else {
            millis = alarmDB.array_alarm[0].time;
            nextNotifyTime.setTimeInMillis(millis);
            Methods.generateDateToast(getApplicationContext(),
                    R.string.message_on_create_where_with_next_alarm,
                    nextNotifyTime.getTime());
        }

        // set the time of the timePicker for the default(current)
        Date currentTime = nextNotifyTime.getTime();
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
                final SharedPreferences sharedPreferences = getSharedPreferences(AlarmDB.DB_NAME, MODE_PRIVATE);

                AlarmDB alarmDB = null;

                try {
                    alarmDB = AlarmDB.getInstance(sharedPreferences);
                } catch (Exception e) {
                    Methods.generateDateToast(getApplicationContext(),
                            R.string.message_on_throw_database_fault);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        deleteSharedPreferences(AlarmDB.DB_NAME);
                    }
                    return;
                }

                // when this button is clicked
                int num_of_alarm = alarmDB.num_of_alarm;
                if (num_of_alarm >= AlarmDB.MAX_ALARM) {
                    Methods.generateDateToast(getApplicationContext(),
                            R.string.warning_number_of_alarm_exceeds_limitation);
                    return;
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

                // insert
                // show a toast message for that the alarm is generated
                if (alarmDB.insertAlarm(calendar, "alarm", true, sharedPreferences)) {
                    Methods.generateDateToast(getApplicationContext(),
                            R.string.message_alarm_generated,
                            calendar.getTime());
                } else {
                    /*
                    should be implemented
                    H.K.
                     */
                    // DEV CODE
                    System.out.println("!!! 알람 생성 실패 !!!");
                    return;
                }

                diaryNotification();
//                diaryNotification(calendar.getTimeInMillis());
            }
        });
    }


//   public void diaryNotification(long time)
    public void diaryNotification()
    {
        final SharedPreferences sharedPreferences = getSharedPreferences(AlarmDB.DB_NAME, MODE_PRIVATE);

        AlarmDB alarmDB = null;

        try {
            alarmDB = AlarmDB.getInstance(sharedPreferences);
        } catch (Exception e) {
            Methods.generateDateToast(getApplicationContext(),
                    R.string.message_on_throw_database_fault);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                deleteSharedPreferences(AlarmDB.DB_NAME);
            }
            return;
        }

        PackageManager packageManager = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Methods.generateDateToast(this,
                    R.string.message_on_exception_null_alarm_manager);
            return;
        }


        // get earliest alarm
        Alarm alarm = alarmDB.getEarliestAlarm();

        // jf user activated the alarm
        if (alarm != null) {
            long millis = alarm.time;
            // DEV CODE
//            long millis = time;
            //

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    millis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        millis,
                        pendingIntent);
            }

            // turn on DeviceBootReceiver
            packageManager.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }

        else {
            // disable daily notification
            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null) {
                alarmManager.cancel(pendingIntent);
                Methods.generateDateToast(this,
                        R.string.message_on_disable_all_alarms);
            }

            // turn off DeviceBootReceiver
            packageManager.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }

        // DEV CODE
        AlarmDB.printAlarmDB(this);
        Methods.printNextAlarm(this);
    }
}