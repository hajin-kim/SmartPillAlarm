package com.example.smartpillalarm;

public class UserDetails {
    public String e_mail;
    public String age;
    public String id;
    public String gender;
    public String pregnancy;
    public String blood_pressure;
    public String diabetes;

    public UserDetails(){}  // No argument constructor

    public UserDetails(String e_mail, String id, String age, String gender, String pregnancy, String blood_pressure, String diabetes) {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPregnancy() {
        return pregnancy;
    }

    public void setPregnancy(String pregnancy) {
        this.pregnancy = pregnancy;
    }

    public String getBlood_pressure() {
        return blood_pressure;
    }

    public void setBlood_pressure(String blood_pressure) {
        this.blood_pressure = blood_pressure;
    }

    public String getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(String diabetes) {
        this.diabetes = diabetes;
    }
}