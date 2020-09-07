package com.example.smartpillalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class DeviceBootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

//            // on device boot complete, reset the alarm
//            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//
//            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            AlarmDB alarmDB = AlarmDB.getInstance(context);
            Alarm alarm = alarmDB.getEarliestAlarm(true);

            // show a toast message for the next alarm
            Methods.generateDateToast(context, R.string.message_after_boot_next_alarm, alarm.getTime());

//            if (manager != null) {
//                manager.setRepeating(AlarmManager.RTC_WAKEUP,
//                        alarm.getTime(),
//                        AlarmManager.INTERVAL_DAY,
//                        pendingIntent);
//
//
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
////                            alarm.getTime(),
////                            pendingIntent);
////                }
//            }

            Methods.reserveNotification(context);
        }
    }

}
