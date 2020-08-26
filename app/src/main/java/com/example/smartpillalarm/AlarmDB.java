package com.example.smartpillalarm;

import android.content.Context;
import android.content.SharedPreferences;

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


    int num_of_alarm;

    Alarm[] array_alarm;


    AlarmDB() {
        num_of_alarm = 0;
        array_alarm = new Alarm[AlarmDB.MAX_ALARM];
    }


    // throws exception when the preference has a fault
    public static AlarmDB getInstance(SharedPreferences sharedPreferences) throws Exception {
        AlarmDB alarmDB = new AlarmDB();

        alarmDB.getAlarmPreferences(sharedPreferences);
        boolean db_is_changed = alarmDB.catchOldAlarm();

        if (db_is_changed) {
            alarmDB.sortAlarm();
            alarmDB.putAlarmOnPreferences(sharedPreferences);
        }

        return alarmDB;
    }


    public void sortAlarm() {
        Arrays.sort(array_alarm, 0, num_of_alarm);
    }


    private void getAlarmPreferences(SharedPreferences sharedPreferences) throws Exception {
        int num_of_alarm = sharedPreferences.getInt(AlarmDB.NUM_OF_ALARM, 0);

        this.num_of_alarm = num_of_alarm;
        for (int i = 0; i < num_of_alarm; ++i) {
            long alarm_time = sharedPreferences.getLong(AlarmDB.ALARM_TIME[i], 0L);
            String alarm_contents = sharedPreferences.getString(AlarmDB.ALARM_CONTENTS[i], null);
            boolean alarm_activated = sharedPreferences.getBoolean(AlarmDB.ALARM_ACTIVATED[i], false);

            if (alarm_time == 0L) throw new Exception("stub!");

            this.array_alarm[i] = new Alarm(
                    alarm_time,
                    alarm_contents,
                    alarm_activated
            );
        }
    }


    public void putAlarmOnPreferences(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(AlarmDB.NUM_OF_ALARM,
                num_of_alarm);
        for (int i = 0; i < num_of_alarm; i++) {
            Alarm alarm = array_alarm[i];
            editor.putLong(AlarmDB.ALARM_TIME[i],
                    alarm.time);
            editor.putString(AlarmDB.ALARM_CONTENTS[i],
                    alarm.contents);
            editor.putBoolean(AlarmDB.ALARM_ACTIVATED[i],
                    alarm.activated);
        }
        editor.apply();
    }


    public boolean insertAlarm(long time, String contents, boolean activated, SharedPreferences sharedPreferences) {
        if (num_of_alarm >= AlarmDB.MAX_ALARM) return false;

        array_alarm[num_of_alarm] = new Alarm(time, contents, activated);
        ++num_of_alarm;

        this.catchOldAlarm();
        this.sortAlarm();
        this.putAlarmOnPreferences(sharedPreferences);

        return true;
    }


    public boolean insertAlarm(Calendar calendar, String contents, boolean activated, SharedPreferences sharedPreferences) {
        return insertAlarm(calendar.getTimeInMillis(), contents, activated, sharedPreferences);
    }


    public boolean deleteAlarm(int index, SharedPreferences sharedPreferences) {
        if (index < 0 || index >= num_of_alarm) return false;

        for (int i = index; i < num_of_alarm-1; i++) {
            array_alarm[i] = array_alarm[i+1];
        }

        array_alarm[num_of_alarm-1] = null;
        --num_of_alarm;

        this.catchOldAlarm();
        this.sortAlarm();
        this.putAlarmOnPreferences(sharedPreferences);

        return true;
    }


    public Alarm getEarliestAlarm() {
        return getEarliestAlarm(true);
    }

    public Alarm getEarliestAlarm(boolean ignore_disabled_alarm) {
        for (int i = 0; i < num_of_alarm; i++) {
            if (array_alarm[i].activated || !ignore_disabled_alarm) return array_alarm[i];
        }
        return null;
    }


    public boolean catchOldAlarm() {
        boolean db_is_changed = false;

        int num_of_alarm = this.num_of_alarm;
        Calendar current_calendar = Calendar.getInstance();

        for (int i = 0; i < num_of_alarm; ++i) {
            Alarm alarm = this.array_alarm[i];

            Calendar nextNotifyTime = Calendar.getInstance();
            nextNotifyTime.setTimeInMillis(alarm.time);

            /*
            this algorithm should be optimized
            H.K.
             */
            // if alarm date is before current time
            while (current_calendar.after(nextNotifyTime)) {
                db_is_changed = true;
                nextNotifyTime.add(Calendar.DATE, 1);

//                // show a toast message for the new alarm
//                Methods.generateDateToast(context, R.string.message_alarm_generated, nextNotifyTime.getTime());
            }

            this.array_alarm[i].time = nextNotifyTime.getTimeInMillis();
        }

        return db_is_changed;
    }


    // DEV CODE
    public static void printAlarmDB(Context context, AlarmDB alarmDB) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DB_NAME, MODE_PRIVATE);

        int num_of_alarms = sharedPreferences.getInt(NUM_OF_ALARM, 0);
        System.out.println("## print all alarms (containing " + num_of_alarms + ")");
        for (int i = 0; i < num_of_alarms; ++i) {
            Alarm alarm = alarmDB.array_alarm[i];

            String date_text = new SimpleDateFormat(context.getResources().getString(R.string.simple_date_format), Locale.getDefault())
                    .format(alarm.time);

            System.out.println("# alarm " + i);

            System.out.println(date_text);
            System.out.println(alarm.activated);
            System.out.println(alarm.contents);
        }
    }


    // DEV CODE
    public static void printAlarmDB(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DB_NAME, MODE_PRIVATE);

        AlarmDB alarmDB = null;
        try {
            alarmDB = getInstance(sharedPreferences);
        } catch (Exception e) {
            System.out.println("preference has a fault!");
            e.printStackTrace();
            return;
        }

        int num_of_alarms = alarmDB.num_of_alarm;
        System.out.println("## print all alarms (containing " + num_of_alarms + ")");
        for (int i = 0; i < num_of_alarms; ++i) {
            Alarm alarm = alarmDB.array_alarm[i];

            String date_text = new SimpleDateFormat(context.getResources().getString(R.string.simple_date_format), Locale.getDefault())
                    .format(alarm.time);

            System.out.println("# alarm " + i);

            System.out.println(date_text);
            System.out.println(alarm.activated);
            System.out.println(alarm.contents);
        }
    }
}
