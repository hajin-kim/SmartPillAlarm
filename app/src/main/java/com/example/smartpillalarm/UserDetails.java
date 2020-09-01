package com.example.smartpillalarm;

public class UserDetails {
    public String e_mail;
    public String age;
    public String id;
    public Boolean gender;
    public Boolean pregnancy;
    public Boolean blood_pressure;
    public Boolean diabetes;

    public UserDetails(){}

    public UserDetails(String e_mail, String id, String age, Boolean gender, Boolean pregnancy, Boolean blood_pressure, Boolean diabetes) {
        this.e_mail = e_mail;
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.pregnancy = pregnancy;
        this.blood_pressure = blood_pressure;
        this.diabetes = diabetes;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Boolean getPregnancy() {
        return pregnancy;
    }

    public void setPregnancy(Boolean pregnancy) {
        this.pregnancy = pregnancy;
    }

    public Boolean getBlood_pressure() {
        return blood_pressure;
    }

    public void setBlood_pressure(Boolean blood_pressure) {
        this.blood_pressure = blood_pressure;
    }

    public Boolean getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(Boolean diabetes) {
        this.diabetes = diabetes;
    }
}