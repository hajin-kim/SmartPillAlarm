package com.example.smartpillalarm;

import java.util.ArrayList;

public class PillData {
    public String item_seq;
    public String item_name;
    public String pack_unit;
    public ArrayList<String> item_efficacy;

    public PillData(){}  // No argument constructor

    public ArrayList<String> getItem_efficacy() {
        return item_efficacy;
    }

    public void setItem_efficacy(ArrayList<String> item_efficacy) {
        this.item_efficacy = item_efficacy;
    }

    public PillData(String item_seq, String item_name, String pack_unit, ArrayList<String> item_efficacy){
        this.item_name = item_name;
        this.item_seq = item_seq;
        this.pack_unit = pack_unit;
        this.item_efficacy = item_efficacy;
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
