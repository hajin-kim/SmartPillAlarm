package com.example.smartpillalarm;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Methods {
    public static void generateDateToast(Context context, int id, Date date) {
        String date_text;
        if (date == null)
            date_text = context.getResources().getString(id);
        else
            date_text = new SimpleDateFormat(context.getResources().getString(id), Locale.getDefault()).format(date);
        Toast.makeText(context.getApplicationContext(), date_text, Toast.LENGTH_SHORT).show();
    }
}
