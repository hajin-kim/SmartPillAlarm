package com.example.smartpillalarm;

import java.util.Calendar;

public class Alarm implements Comparable<Alarm> {
    private long time;
    private String contents;
    private boolean activated;

    public Alarm(long time, String contents, boolean activated) {
        this.time = time;
        this.contents = contents;
        this.activated = activated;
    }

    @Override
    public int compareTo(Alarm alarm) {
        // TODO Auto-generated method stub
        return Long.compare(this.time, alarm.time);
    }

    public long getTime() {
        return time;
    }

    public String getContents() {
        return contents;
    }

    public boolean isActivated() {
        return activated;
    }

    public void updateAlarmDate(Calendar current_calendar) {
        Calendar nextNotifyTime = Calendar.getInstance();
        nextNotifyTime.setTimeInMillis(time);

        /*
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
