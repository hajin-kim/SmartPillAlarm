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
import java.util.GregorianCalendar;
import java.util.Locale;


public class AlarmGeneratorActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_generator);

        final TimePicker picker = findViewById(R.id.time_picker);
        picker.setIs24HourView(true);

        final SharedPreferences sharedPreferences = getSharedPreferences(AlarmDB.DB_NAME, MODE_PRIVATE);

        // get the time of the alarm if exist
        // get the current local time if not
        long millis = sharedPreferences.getLong(AlarmDB.ALARM_TIME[0], Calendar.getInstance().getTimeInMillis());

        // load the time onto the nextNotifyTime
        Calendar nextNotifyTime = new GregorianCalendar();
        nextNotifyTime.setTimeInMillis(millis);

        // show a message for the next alarm
        int num_of_alarm = sharedPreferences.getInt(AlarmDB.NUM_OF_ALARM, 0);
        if (num_of_alarm == 0)
            Methods.generateDateToast(getApplicationContext(), R.string.message_on_create_no_alarm, nextNotifyTime.getTime());
        else
            Methods.generateDateToast(getApplicationContext(), R.string.message_on_create_next_alarm, nextNotifyTime.getTime());

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
                // when this button is clicked
                int num_of_alarm = sharedPreferences.getInt(AlarmDB.NUM_OF_ALARM, 0);
                if (num_of_alarm >= 20) {
                    Methods.generateDateToast(getApplicationContext(), R.string.warning_number_of_alarm_exceeds_limitation, null);
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

                // show a toast message for that the alarm is generated
                Methods.generateDateToast(getApplicationContext(), R.string.message_alarm_generated, calendar.getTime());

                // save the alarm to the preference
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(AlarmDB.ALARM_TIME[num_of_alarm], calendar.getTimeInMillis());
                editor.putBoolean(AlarmDB.ALARM_ACTIVATED[num_of_alarm], true);
                editor.putString(AlarmDB.ALARM_CONTENTS[num_of_alarm], "alarm " + num_of_alarm);
                editor.putInt(AlarmDB.NUM_OF_ALARM, ++num_of_alarm);
                editor.apply();

                diaryNotification();
            }
        });
    }


    void diaryNotification()
    {
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        final SharedPreferences sharedPreferences = getSharedPreferences(AlarmDB.DB_NAME, MODE_PRIVATE);

//        Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        int num_of_alarm = sharedPreferences.getInt(AlarmDB.NUM_OF_ALARM, 0);
        boolean dailyNotify; // 무조건 알람을 사용
        long millis;
//        Calendar calendar = Calendar.getInstance();

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        for (int i = 0; i < num_of_alarm; ++i) {
            // jf user activated the alarm
            dailyNotify = sharedPreferences.getBoolean(AlarmDB.ALARM_ACTIVATED[i], false);
            millis = sharedPreferences.getLong(AlarmDB.ALARM_TIME[0], 0L);
//            calendar.setTimeInMillis(millis);

            if (dailyNotify) {

                if (alarmManager != null) {

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis,
                            AlarmManager.INTERVAL_DAY, pendingIntent);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
                    }
                }

                // turn on DeviceBootReceiver
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

            }

//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
        }
    }
}