package com.example.smartpillalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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


    public static StringBuilder getAPIResponse(String productCode) throws IOException {
//        String serviceKey = "r1uAyYmY3oR5pYyYwVAuUv%2FYqWVfqRmNNFWGEcsTqkABJmUp1CdLyPOWB5PLTnTQPGRduGwrjvr2Dxwp59mMYA%3D%3D".replace("%2F", "/").replace("%3D", "=");
//        String serviceKey = "r1uAyYmY3oR5pYyYwVAuUv/YqWVfqRmNNFWGEcsTqkABJmUp1CdLyPOWB5PLTnTQPGRduGwrjvr2Dxwp59mMYA==";

        // to get API service key from firebase database
        final String[] serviceKey = new String[1];
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("apiServiceKey");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                serviceKey[0] = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1471057/MdcinPrductPrmisnInfoService/getMdcinPrductItem"); /*URL*/
//        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=서비스키"); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode(serviceKey, "UTF-8")); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + serviceKey[0]); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("item_name","UTF-8") + "=" + URLEncoder.encode("종근당염산에페드린정", "UTF-8")); /*품목명*/
//        urlBuilder.append("&" + URLEncoder.encode("entp_name","UTF-8") + "=" + URLEncoder.encode("(주)종근당", "UTF-8")); /*업체명*/
//        urlBuilder.append("&" + URLEncoder.encode("item_permit_date","UTF-8") + "=" + URLEncoder.encode("19550117", "UTF-8")); /*허가일자*/
//        urlBuilder.append("&" + URLEncoder.encode("entp_no","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /*업체허가번호*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("3", "UTF-8")); /*한 페이지 결과 수*/
//        urlBuilder.append("&" + URLEncoder.encode("bar_code","UTF-8") + "=" + URLEncoder.encode("8806433032005", "UTF-8")); /*표준코드*/
        urlBuilder.append("&" + URLEncoder.encode("item_seq","UTF-8") + "=" + URLEncoder.encode(productCode, "UTF-8")); /*품목기준코드*/
//        urlBuilder.append("&" + URLEncoder.encode("start_change_date","UTF-8") + "=" + URLEncoder.encode("20151216", "UTF-8")); /*변경일자시작일*/
//        urlBuilder.append("&" + URLEncoder.encode("end_change_date","UTF-8") + "=" + URLEncoder.encode("20160101", "UTF-8")); /*변경일자종료일*/

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            responseBuilder.append(line);
        }

        rd.close();
        conn.disconnect();

        System.out.println(responseBuilder.toString());

        return responseBuilder;
    }



    // DEV CODE
//    public static void printNextAlarm(Context context) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Date date = new Date(alarmManager.getNextAlarmClock().getTriggerTime());
//        System.out.println(new SimpleDateFormat(context.getResources().getString(R.string.message_on_create_where_with_next_alarm), Locale.getDefault()).format(date));
//    }

}
