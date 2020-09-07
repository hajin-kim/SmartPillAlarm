package com.example.smartpillalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Methods {
    public static void generateToast(Context context, int id) {
        String text = context.getResources().getString(id);
        Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void generateDateToast(Context context, int id, Date date) {
        String date_text = new SimpleDateFormat(context.getResources().getString(id), Locale.getDefault()).format(date);
        Toast.makeText(context.getApplicationContext(), date_text, Toast.LENGTH_SHORT).show();
    }

    public static void generateDateToast(Context context, int id, long date) {
        String date_text = new SimpleDateFormat(context.getResources().getString(id), Locale.getDefault()).format(date);
        Toast.makeText(context.getApplicationContext(), date_text, Toast.LENGTH_SHORT).show();
    }


    //   public void diaryNotification(long time)
    public static void reserveNotification(Context context)
    {
        PackageManager packageManager = context.getPackageManager();
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Methods.generateToast(context,
                    R.string.message_on_exception_null_alarm_manager);
            return;
        }

        // get earliest alarm
        AlarmDB alarmDB = AlarmDB.getInstance(context);
        Alarm alarm = alarmDB.getEarliestAlarm(true);

        // jf user activated the alarm
        if (alarm != null) {
            /*
            the critical problem of alarmManager is, that we can set only one alarm at one time.
            we should solve this problem.
            please refer https://hoyi327.tistory.com/33
            H.K.
             */
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    alarm.getTime(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        alarm.getTime(),
                        pendingIntent);
            }

            // turn on DeviceBootReceiver
            packageManager.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
        else {
            // disable daily notification
            if (PendingIntent.getBroadcast(context, 0, alarmIntent, 0) != null) {
                alarmManager.cancel(pendingIntent);
                Methods.generateToast(context,
                        R.string.message_on_disable_all_alarms);
            }

            // turn off DeviceBootReceiver
            packageManager.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }

        // DEV CODE
        AlarmDB.printAlarmDB(context);
//        Methods.printNextAlarm(context);
    }


    // DEV CODE
//    public static void printNextAlarm(Context context) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Date date = new Date(alarmManager.getNextAlarmClock().getTriggerTime());
//        System.out.println(new SimpleDateFormat(context.getResources().getString(R.string.message_on_create_where_with_next_alarm), Locale.getDefault()).format(date));
//    }

}
