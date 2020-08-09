package com.example.smartpillalarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


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

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); // Oreo 이상에서 mipmap 사용시 시스템 UI 에러남

            int importance = NotificationManager.IMPORTANCE_HIGH; // 소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(channelDescription);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }
        else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        // set the builder info
        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())

                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle(alarm_auto_channel_content_title)
                .setContentText(alarm_auto_channel_content_text)
                .setContentInfo("INFO")
                .setContentIntent(pendingI);

        if (notificationManager != null) {

            // 노티피케이션 동작시킴
            notificationManager.notify(1234, builder.build());

            Calendar nextNotifyTime = Calendar.getInstance();

            // 내일 같은 시간으로 알람시간 결정
            nextNotifyTime.add(Calendar.DATE, 1);

            // Preference에 설정한 값 저장
            SharedPreferences.Editor editor = context.getSharedPreferences(AlarmDB.DB_NAME, MODE_PRIVATE).edit();
            editor.putLong(AlarmDB.ALARM_TIME[0], nextNotifyTime.getTimeInMillis());
            editor.apply();

            // show a toast message for the new alarm
            Methods.generateDateToast(context, R.string.message_alarm_generated, nextNotifyTime.getTime());
        }
    }

}
