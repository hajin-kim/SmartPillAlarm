package com.example.smartpillalarm;

import android.app.AlarmManager;
import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Methods {
    public static void generateDateToast(Context context, int id) {
        String date_text = context.getResources().getString(id);
        Toast.makeText(context.getApplicationContext(), date_text, Toast.LENGTH_SHORT).show();
    }

    public static void generateDateToast(Context context, int id, Date date) {
        String date_text = new SimpleDateFormat(context.getResources().getString(id), Locale.getDefault()).format(date);
        Toast.makeText(context.getApplicationContext(), date_text, Toast.LENGTH_SHORT).show();
    }

    public static void printNextAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Date date = new Date(alarmManager.getNextAlarmClock().getTriggerTime());
        System.out.println(new SimpleDateFormat(context.getResources().getString(R.string.message_on_create_where_with_next_alarm), Locale.getDefault()).format(date));
    }
}
