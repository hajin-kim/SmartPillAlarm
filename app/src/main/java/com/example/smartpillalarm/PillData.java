package com.example.smartpillalarm;

public class PillData {
    public String item_seq;
    public String item_name;
    public String pack_unit;
    public Integer pill_count;

    public PillData(){}  // No argument constructor

    public PillData(String item_seq, String item_name, String pack_unit){
        this.item_name = item_name;
        this.item_seq = item_seq;
        this.pack_unit = pack_unit;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_seq() {
        return item_seq;
    }

    public void setItem_seq(String item_seq) {
        this.item_seq = item_seq;
    }

    public String getPack_unit() {
        return pack_unit;
    }

    public void setPack_unit(String pack_unit) {
        this.pack_unit = pack_unit;
    }
}
