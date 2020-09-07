package com.example.smartpillalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class AlarmDB implements DB {
    public final static String DB_NAME = "NAME_ALARM_DB";

    public final static int MAX_ALARM = 20;

    public final static String NUM_OF_ALARM = "KEY_NUM_OF_ALARM";

    public final static String ALARM_TIME[] =
            {
                    "KEY_ALARM1_TIME",
                    "KEY_ALARM2_TIME",
                    "KEY_ALARM3_TIME",
                    "KEY_ALARM4_TIME",
                    "KEY_ALARM5_TIME",
                    "KEY_ALARM6_TIME",
                    "KEY_ALARM7_TIME",
                    "KEY_ALARM8_TIME",
                    "KEY_ALARM9_TIME",
                    "KEY_ALARM10_TIME",
                    "KEY_ALARM11_TIME",
                    "KEY_ALARM12_TIME",
                    "KEY_ALARM13_TIME",
                    "KEY_ALARM14_TIME",
                    "KEY_ALARM15_TIME",
                    "KEY_ALARM16_TIME",
                    "KEY_ALARM17_TIME",
                    "KEY_ALARM18_TIME",
                    "KEY_ALARM19_TIME",
                    "KEY_ALARM20_TIME"
            };
    public final static String ALARM_ACTIVATED[] =
            {
                    "KEY_ALARM1_ACTIVATED",
                    "KEY_ALARM2_ACTIVATED",
                    "KEY_ALARM3_ACTIVATED",
                    "KEY_ALARM4_ACTIVATED",
                    "KEY_ALARM5_ACTIVATED",
                    "KEY_ALARM6_ACTIVATED",
                    "KEY_ALARM7_ACTIVATED",
                    "KEY_ALARM8_ACTIVATED",
                    "KEY_ALARM9_ACTIVATED",
                    "KEY_ALARM10_ACTIVATED",
                    "KEY_ALARM11_ACTIVATED",
                    "KEY_ALARM12_ACTIVATED",
                    "KEY_ALARM13_ACTIVATED",
                    "KEY_ALARM14_ACTIVATED",
                    "KEY_ALARM15_ACTIVATED",
                    "KEY_ALARM16_ACTIVATED",
                    "KEY_ALARM17_ACTIVATED",
                    "KEY_ALARM18_ACTIVATED",
                    "KEY_ALARM19_ACTIVATED",
                    "KEY_ALARM20_ACTIVATED"
            };
    public final static String ALARM_CONTENTS[] =
            {
                    "KEY_ALARM1_CONTENTS",
                    "KEY_ALARM2_CONTENTS",
                    "KEY_ALARM3_CONTENTS",
                    "KEY_ALARM4_CONTENTS",
                    "KEY_ALARM5_CONTENTS",
                    "KEY_ALARM6_CONTENTS",
                    "KEY_ALARM7_CONTENTS",
                    "KEY_ALARM8_CONTENTS",
                    "KEY_ALARM9_CONTENTS",
                    "KEY_ALARM10_CONTENTS",
                    "KEY_ALARM11_CONTENTS",
                    "KEY_ALARM12_CONTENTS",
                    "KEY_ALARM13_CONTENTS",
                    "KEY_ALARM14_CONTENTS",
                    "KEY_ALARM15_CONTENTS",
                    "KEY_ALARM16_CONTENTS",
                    "KEY_ALARM17_CONTENTS",
                    "KEY_ALARM18_CONTENTS",
                    "KEY_ALARM19_CONTENTS",
                    "KEY_ALARM20_CONTENTS"
            };


    private int num_of_alarm;
    private Alarm[] array_alarm;
    private SharedPreferences sharedPreferences;

    public int getNum_of_alarm() {
        return num_of_alarm;
    }

    private AlarmDB(Context context) {
        this.num_of_alarm = 0;
        this.array_alarm = new Alarm[AlarmDB.MAX_ALARM];
        this.sharedPreferences = context.getSharedPreferences(AlarmDB.DB_NAME, MODE_PRIVATE);
    }


    // throws exception when the preference has a fault
    public static AlarmDB getInstance(Context context) {
        AlarmDB alarmDB = new AlarmDB(context);

        try {
            alarmDB.getAlarmPreferences();
        } catch (Exception e) {
            Methods.generateToast(context.getApplicationContext(),
                    R.string.message_on_throw_database_fault);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.deleteSharedPreferences(AlarmDB.DB_NAME);
            }
            e.printStackTrace();
        }

        alarmDB.updateAndPutPreferences();

        return alarmDB;
    }


    public void sortAlarm() {
        Arrays.sort(array_alarm, 0, num_of_alarm);
    }


    private void getAlarmPreferences() throws Exception {
        int num_of_alarm = sharedPreferences.getInt(AlarmDB.NUM_OF_ALARM, 0);

        this.num_of_alarm = num_of_alarm;
        for (int i = 0; i < num_of_alarm; ++i) {
            long alarm_time = sharedPreferences.getLong(AlarmDB.ALARM_TIME[i], 0L);
            String alarm_contents = sharedPreferences.getString(AlarmDB.ALARM_CONTENTS[i], null);
            boolean alarm_activated = sharedPreferences.getBoolean(AlarmDB.ALARM_ACTIVATED[i], false);

            if (alarm_time == 0L || alarm_contents == null) throw new Exception("stub!");

            this.array_alarm[i] = new Alarm(
                    alarm_time,
                    alarm_contents,
                    alarm_activated
            );
        }
    }


    public void putAlarmOnPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(AlarmDB.NUM_OF_ALARM,
                num_of_alarm);
        for (int i = 0; i < num_of_alarm; i++) {
            Alarm alarm = array_alarm[i];
            editor.putLong(AlarmDB.ALARM_TIME[i],
                    alarm.getTime());
            editor.putString(AlarmDB.ALARM_CONTENTS[i],
                    alarm.getContents());
            editor.putBoolean(AlarmDB.ALARM_ACTIVATED[i],
                    alarm.isActivated());
        }
        editor.apply();
    }


    public boolean insertAlarm(Alarm alarm) {
        if (num_of_alarm >= AlarmDB.MAX_ALARM) return false;

        array_alarm[num_of_alarm] = alarm;
        ++num_of_alarm;

        return this.updateAndPutPreferences();
    }

    public boolean insertAlarm(long time, String contents, boolean activated) {
        return this.insertAlarm(new Alarm(time, contents, activated));
    }

    public boolean insertAlarm(Calendar calendar, String contents, boolean activated) {
        return this.insertAlarm(calendar.getTimeInMillis(), contents, activated);
    }


    public boolean deleteAlarm(int index) {
        if (index < 0 || index >= num_of_alarm) return false;

        for (int i = index; i < num_of_alarm-1; i++) {
            array_alarm[i] = array_alarm[i+1];
        }

        array_alarm[num_of_alarm-1] = null;
        --num_of_alarm;

        return this.updateAndPutPreferences();
    }


    public boolean updateAndPutPreferences() {
        Calendar current_calendar = Calendar.getInstance();

        for (int i = 0; i < num_of_alarm; ++i) {
            array_alarm[i].updateAlarmDate(current_calendar);
        }

        this.sortAlarm();
        this.putAlarmOnPreferences();

        return true;
    }


    public Alarm getEarliestAlarm(boolean ignore_disabled_alarm) {
        for (int i = 0; i < num_of_alarm; i++) {
            if (array_alarm[i].isActivated() || !ignore_disabled_alarm) return array_alarm[i];
        }
        return null;
    }

    public Alarm getEarliestAlarm() {
        return getEarliestAlarm(true);
    }


    // DEV CODE
    public static void printAlarmDB(Context context) {
        AlarmDB alarmDB = getInstance(context);

        int num_of_alarms = alarmDB.num_of_alarm;
        System.out.println("## print all alarms (containing " + num_of_alarms + ")");
        for (int i = 0; i < num_of_alarms; ++i) {
            Alarm alarm = alarmDB.array_alarm[i];

            String date_text = new SimpleDateFormat(context.getResources().getString(R.string.simple_date_format), Locale.getDefault())
                    .format(alarm.getTime());

            System.out.println("# alarm " + i);

            System.out.println(date_text);
            System.out.println(alarm.getContents());
            System.out.println(alarm.isActivated());
        }
    }
}
