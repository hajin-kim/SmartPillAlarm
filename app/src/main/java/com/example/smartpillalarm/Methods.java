package com.example.smartpillalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

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
        // get earliest alarm
        AlarmDB alarmDB = AlarmDB.getInstance(context);
        Alarm alarm = alarmDB.getEarliestAlarm(true);

        PackageManager packageManager = context.getPackageManager();
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        alarmIntent.putExtra(context.getString(R.string.extra_key_prodCode), alarm.getDrugProdCode());
        alarmIntent.putExtra(context.getString(R.string.extra_key_drugName), alarm.getDrugName());
        alarmIntent.putExtra(context.getString(R.string.extra_key_drugInfo), alarm.getDrugInfo());
        alarmIntent.putExtra(context.getString(R.string.extra_key_numDrug), alarm.getNum_pill());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Methods.generateToast(context,
                    R.string.message_on_exception_null_alarm_manager);
            return;
        }

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
