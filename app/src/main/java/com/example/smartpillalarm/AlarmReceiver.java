package com.example.smartpillalarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // decline strings
        String channelName = context.getString(R.string.channel_alarm_notification_name);
        String channelDescription = context.getString(R.string.channel_alarm_notification_description);
        String alarm_auto_channel_content_title = context.getResources().getString(R.string.notification_auto_channel_content_title);
        String alarm_auto_channel_content_text = context.getResources().getString(R.string.notification_auto_channel_content_text);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, AlarmGeneratorActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        // make a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");


        // OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Oreo 이상에서 mipmap 사용시 시스템 UI 에러남
            builder.setSmallIcon(R.drawable.ic_launcher_foreground);

            // 소리와 알림메시지를 같이 보여줌
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(channelDescription);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }
        // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
        else builder.setSmallIcon(R.mipmap.ic_launcher);

        // set the builder info
        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())

                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle(alarm_auto_channel_content_title)
                .setContentText(alarm_auto_channel_content_text)
                .setContentInfo("INFO")
                .setContentIntent(pendingI);

        // DO notify
        if (notificationManager != null)
            notificationManager.notify(1234, builder.build());

        /*
        we should change algorithm into:
            get current time by Calendar.getInstance()
            foreach alarm: DB,
                if alarm.time < current time
                    alarm.time.add(Calendar.DATE, 1)
                    // sort DB
            DB apply

        H.K.
         */

        SharedPreferences sharedPreferences = context.getSharedPreferences(AlarmDB.DB_NAME, MODE_PRIVATE);
        Calendar current_calendar = Calendar.getInstance();

        AlarmDB alarmDB = null;

        try {
            alarmDB = AlarmDB.getInstance(sharedPreferences);
        } catch (Exception e) {
            Methods.generateDateToast(context,
                    R.string.message_on_throw_database_fault);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.deleteSharedPreferences(AlarmDB.DB_NAME);
            }
            return;
        }

//        // same time of the next day
//        Calendar nextNotifyTime = Calendar.getInstance();
//        nextNotifyTime.setTimeInMillis(alarmDB.array_alarm[0].time);
//        nextNotifyTime.add(Calendar.DATE, 1);
//
//        alarmDB.array_alarm[0].time = nextNotifyTime.getTimeInMillis();

//        // show a toast message for the new alarm
//        Methods.generateDateToast(context, R.string.message_alarm_generated, nextNotifyTime.getTime());


        alarmDB.catchOldAlarm();
        alarmDB.sortAlarm();
        alarmDB.putAlarmOnPreferences(sharedPreferences);


        // diary notification
        PackageManager packageManager = context.getPackageManager();
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Methods.generateDateToast(context,
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
            if (PendingIntent.getBroadcast(context, 0, alarmIntent, 0) != null) {
                alarmManager.cancel(pendingIntent);
                Methods.generateDateToast(context,
                        R.string.message_on_disable_all_alarms);
            }

            // turn off DeviceBootReceiver
            packageManager.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }


//        for (int i = 0; i < AlarmDB.MAX_ALARM; ++i) {
//            long alarm_time = sharedPreferences.getLong(AlarmDB.ALARM_TIME[i], 0L);
//            String alarm_contents = sharedPreferences.getString(AlarmDB.ALARM_CONTENTS[i], null);
//            boolean alarm_activated = sharedPreferences.getBoolean(AlarmDB.ALARM_ACTIVATED[i], false);
//
//            if (alarm_time == 0L) continue;
//
//            Calendar nextNotifyTime = Calendar.getInstance();
//            nextNotifyTime.setTimeInMillis(alarm_time);
//
//            // same time of the next day
//            if (current_calendar.after(nextNotifyTime)) {
//                nextNotifyTime.add(Calendar.DATE, 1);
//
//                // show a toast message for the new alarm
//                Methods.generateDateToast(context, R.string.message_alarm_generated, nextNotifyTime.getTime());
//            }
//
//            // save onto the preference
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putLong(AlarmDB.ALARM_TIME[i],
//                    alarm_time);
//            editor.putString(AlarmDB.ALARM_CONTENTS[i],
//                    alarm_contents);
//            editor.putBoolean(AlarmDB.ALARM_ACTIVATED[i],
//                    alarm_activated);
//        }
//        for (int i = 0; i < num_of_alarms-1; ++i) {
//            editor.putLong(AlarmDB.ALARM_TIME[i],
//                    sharedPreferences.getLong(AlarmDB.ALARM_TIME[i+1], 0L));
//            editor.putString(AlarmDB.ALARM_CONTENTS[i],
//                    sharedPreferences.getString(AlarmDB.ALARM_CONTENTS[i+1], null));
//            editor.putBoolean(AlarmDB.ALARM_ACTIVATED[i],
//                    sharedPreferences.getBoolean(AlarmDB.ALARM_ACTIVATED[i+1], false));
//        }
//        editor.putLong(AlarmDB.ALARM_TIME[num_of_alarms-1], nextNotifyTime.getTimeInMillis());
//        editor.putString(AlarmDB.ALARM_CONTENTS[num_of_alarms-1], alarm_contents);
//        editor.putBoolean(AlarmDB.ALARM_ACTIVATED[num_of_alarms-1], alarm_activated);
//        editor.apply();

        // DEV CODE
        AlarmDB.printAlarmDB(context);
        Methods.printNextAlarm(context);
    }

}
