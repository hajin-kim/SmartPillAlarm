package com.example.smartpillalarm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button logout_button;
    private Button profile_button;
    private Button scan_button;
    private Context appContext;
    private FirebaseAuth firebaseAuth;
    private StringBuilder requestResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout_button = findViewById(R.id.btn_main_logout);
        profile_button = findViewById(R.id.btn_main_profile);
        scan_button = findViewById(R.id.btn_main_scan);
        appContext = getApplicationContext();

        final Context context = this;

        firebaseAuth = FirebaseAuth.getInstance();    // for login-logout via Firebase

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        profile_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //finish();
                startActivity(new Intent(MainActivity.this, Profile.class));
            }
        });

        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        // DEV CODE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            deleteSharedPreferences(AlarmDB.DB_NAME);
        }

        // DEV CODE
        AlarmDB.printAlarmDB(appContext);
//        Methods.printNextAlarm(appContext);

         //DEV CODE
//        try {
//            String productCode = "199903739";
//            System.out.println("Start getAPI");
//            Methods.getAPIResponse(getApplicationContext(), productCode);
//            System.out.println("Done getAPI");
//        } catch (IOException e) {
//            System.out.println("Tracing getAPI");
//            e.printStackTrace();
//            System.out.println("Error found");
//            Toast.makeText(appContext, "Error found", Toast.LENGTH_LONG).show();
//        }

        // renew
//        AlarmDB.getInstance(appContext);

        final Button button_start = findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AlarmGeneratorActivity.class));
//                finish();
            }
        });
    }

    private void logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(MainActivity.this, Login.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_main_logout:{
                logout();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void scanCode(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(CaptureAct.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("코드 스캔");
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        String productCode = "NULL";

        if(result != null){
            if(result.getContents() != null){
                Toast.makeText(getApplicationContext(), result.getFormatName(), Toast.LENGTH_SHORT).show();

                String foundBarcodeDigits = result.getContents();
                // get barcode format
                switch (BarcodeFormat.valueOf(result.getFormatName())) {
                    case EAN_13:
//                        foundBarcodeDigits = result.getContents();
//                        Toast.makeText(appContext, BarcodeFormat.EAN_13 + " " + foundBarcodeDigits, Toast.LENGTH_SHORT).show();
                        break;

                    case DATA_MATRIX:
                        foundBarcodeDigits = foundBarcodeDigits.substring(4, 4+13);
//                        Toast.makeText(appContext, BarcodeFormat.DATA_MATRIX + " " + foundBarcodeDigits, Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(appContext, result.getFormatName(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(appContext, "인식할 수 없는 바코드입니다!", Toast.LENGTH_SHORT).show();

                }

                try {
                    productCode = searchProdCode(foundBarcodeDigits);
                    System.out.println(productCode);
                    System.out.println("Start getAPI");
                    getAPIResponse(productCode);
                    System.out.println("Done getAPI");
                } catch (IOException | IllegalAccessException e) {
                    System.out.println("Tracing getAPI");
                    e.printStackTrace();
                    System.out.println("Error found");
                    Toast.makeText(appContext, "Error found", Toast.LENGTH_SHORT).show();

                }
            }
            else{
                Toast.makeText(this, "Product code 결과 없음", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    // XML string parser: gets XML as input in string
    public void parseXml(String xmlString) throws IOException {
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput( new StringReader(xmlString) ); // pass input whatever xml you have
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d(TAG,"Start document");
                } else if(eventType == XmlPullParser.START_TAG) {
                    Log.d(TAG,"Start tag "+xpp.getName());
                } else if(eventType == XmlPullParser.END_TAG) {
                    Log.d(TAG,"End tag "+xpp.getName());
                } else if(eventType == XmlPullParser.TEXT) {
                    Log.d(TAG,"Text "+xpp.getText()); // here you get the text from xml
                }
                eventType = xpp.next();
            }
            Log.d(TAG,"End document");

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private int getRawID(String title) throws IllegalAccessException {
        int RawID = 0;
        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            if(title.equals(fields[count].getName())){
                RawID = fields[count].getInt(fields[count]);
            }
        }
        return RawID;
    }

    private String searchProdCode(String barcode) throws IOException, IllegalAccessException {
        String prodCode = "NULL";  // returns corresponding product code
        String title = barcode.substring(3,7);
        String key = barcode.substring(7);
        int count = 0;

        InputStream inputStream = getResources().openRawResource(getRawID("c" + title));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

        String line;
        bufferedReader.readLine(); // skip first line
        while((line = bufferedReader.readLine()) != null) {
            count++;
            // Split by ","
            String[] tokens = line.split(",");
            String compCode = "880"+title+tokens[1].substring(1); // ex) 880+0500+000102
            if (compCode.equals(barcode)) {
                prodCode = tokens[2].substring(1);  // get rid of "'" ex) '1234 => 1234
                Toast.makeText(this, "Found: "+prodCode, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Finished: "+prodCode);
                break;
            }
            else{
                Log.d(TAG, "Iter: "+count+" Current: "+compCode+" Objective: "+barcode);
            }
        }
        return prodCode;
    }

    public void getAPIResponse(final String productCode) throws IOException {
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlBuilder.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestResult = new StringBuilder();
                        requestResult.append( /*URLEncoder.encode(response, "EUC-KR")*/ response);
//                        Toast.makeText(appContext, "Response is: "+ requestResult.toString(), Toast.LENGTH_SHORT).show();
//                        System.out.println(requestResult.toString());
                        succeedAPIRequest(productCode);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(appContext, "That didn't work!", Toast.LENGTH_SHORT).show();
                System.out.println("That didn't work!");
            }
        })
        {
            @Override //response를 UTF8로 변경해주는 소스코드
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8String = new String(response.data, "UTF-8");
                    return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
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

//        Log.d(TAG, "HelloWorld! "+requestResult.toString());
    }

    public void succeedAPIRequest(String productCode) {
        System.out.println("TTT " + requestResult.length());

        Log.d(TAG, "This is actually Printing SOMETHING:"+requestResult);
        //parseXml(response);

//      Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("제품코드: "+productCode);
        builder.setTitle("스캔 결과");
        builder.setPositiveButton("재시도", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                scanCode();
            }
        }).setNegativeButton("완료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
//      Toast.makeText(appContext, codeFound, Toast.LENGTH_SHORT).show();
        System.out.println("BARCODE TEST "+productCode);
    }
}
