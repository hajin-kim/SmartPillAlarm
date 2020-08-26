package com.example.smartpillalarm;

public class Alarm implements Comparable<Alarm> {
    public long time;
    public String contents;
    public boolean activated;

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
}
