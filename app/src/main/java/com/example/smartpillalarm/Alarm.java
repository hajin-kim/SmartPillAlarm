package com.example.smartpillalarm;

import java.util.Calendar;

public class Alarm implements Comparable<Alarm> {
    private long time;
    private boolean activated;
    private String drugProdCode;
    private String drugName;
    private String drugInfo;
    private int num_pill;


    public Alarm(long time, boolean activated, String drugProdCode) {
        this.time = time;
        this.activated = activated;
        this.drugProdCode = drugProdCode;
        this.drugName = null;
        this.drugInfo = drugName;
        this.num_pill = Integer.MAX_VALUE;
    }

    public Alarm(long time, boolean activated, String drugProdCode, String drugName, String drugInfo, int num_pill) {
        this.time = time;
        this.drugInfo = drugInfo;
        this.activated = activated;
        this.drugName = drugName;
        this.drugProdCode = drugProdCode;
        this.num_pill = num_pill;
    }

    @Override
    public int compareTo(Alarm alarm) {
        // TODO Auto-generated method stub
        return Long.compare(this.time, alarm.time);
    }

    public long getTime() {
        return time;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getDrugProdCode() {
        return drugProdCode;
    }

    public String getDrugName() {
        return drugName;
    }

    public String getDrugInfo() {
        return drugInfo;
    }

    public int getNum_pill() {
        return num_pill;
    }

    public boolean decreaseNum_pill() {
        if (num_pill <= 0) return false;
        --num_pill;
        return true;
    }

    public void updateAlarmDate(Calendar current_calendar) {
        Calendar nextNotifyTime = Calendar.getInstance();
        nextNotifyTime.setTimeInMillis(time);

        /*
        TODO:
        this algorithm should be optimized
        H.K.
         */
        // if alarm date is before current time
        while (current_calendar.after(nextNotifyTime)) {
            nextNotifyTime.add(Calendar.DATE, 1);
//            // show a toast message for the new alarm
//            Methods.generateDateToast(context, R.string.message_alarm_generated, nextNotifyTime.getTime());
        }

        time = nextNotifyTime.getTimeInMillis();
    }
}
