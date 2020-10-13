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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

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


    public static String getAPIResponse(final Context appContext, String productCode) throws IOException {
//        String[] serviceKey = {"r1uAyYmY3oR5pYyYwVAuUv%2FYqWVfqRmNNFWGEcsTqkABJmUp1CdLyPOWB5PLTnTQPGRduGwrjvr2Dxwp59mMYA%3D%3D", ""};
//        String[] serviceKey = "r1uAyYmY3oR5pYyYwVAuUv%2FYqWVfqRmNNFWGEcsTqkABJmUp1CdLyPOWB5PLTnTQPGRduGwrjvr2Dxwp59mMYA%3D%3D".replace("%2F", "/").replace("%3D", "=");
        String[] serviceKey = {"r1uAyYmY3oR5pYyYwVAuUv/YqWVfqRmNNFWGEcsTqkABJmUp1CdLyPOWB5PLTnTQPGRduGwrjvr2Dxwp59mMYA==", ""};

        // TODO: it doesn't work
        // to get API service key from firebase database
//        final String[] serviceKey = new String[1];
//        DatabaseReference databaseReference;
//        databaseReference = FirebaseDatabase.getInstance().getReference("apiServiceKey");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                serviceKey[0] = snapshot.getValue().toString();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        System.out.println(serviceKey[0]);

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1471057/MdcinPrductPrmisnInfoService/getMdcinPrductItem"); /*URL*/
//        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=서비스키"); /*Service Key*/
//        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode(serviceKey, "UTF-8")); /*Service Key*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey[0]); /*Service Key*/
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


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(appContext);

        // Request a string response from the provided URL.
        final String[] result = new String[1];
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlBuilder.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        result[0] = /*URLEncoder.encode(response, "EUC-KR")*/response;
                        Toast.makeText(appContext, "Response is: "+ result[0], Toast.LENGTH_SHORT).show();
                        System.out.println(result[0]);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(appContext, "That didn't work!", Toast.LENGTH_SHORT).show();
                System.out.println("That didn't work!");
            }
        }){
            @Override //response를 UTF8로 변경해주는 소스코드
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8String = new String(response.data, "UTF-8");
                    return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    // log error
                    return Response.error(new ParseError(e));
                } catch (Exception e) {
                    // log error
                    return Response.error(new ParseError(e));
                }
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        stringRequest.setShouldCache(false);

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


//        URL url = new URL(urlBuilder.toString());
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/json");
//        System.out.println("Response code: " + conn.getResponseCode());

//        BufferedReader rd;
//        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        } else {
//            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//        }
//
//        StringBuilder responseBuilder = new StringBuilder();
//        String line;
//        while ((line = rd.readLine()) != null) {
//            responseBuilder.append(line);
//        }

//        rd.close();
//        conn.disconnect();
//
//        System.out.println(responseBuilder.toString());

//        return responseBuilder;
        return result[0];
    }



    // DEV CODE
//    public static void printNextAlarm(Context context) {
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Date date = new Date(alarmManager.getNextAlarmClock().getTriggerTime());
//        System.out.println(new SimpleDateFormat(context.getResources().getString(R.string.message_on_create_where_with_next_alarm), Locale.getDefault()).format(date));
//    }

}
