package com.example.thrid;

public class Complaint_reg {

    private String caset;
    private String date;
    private String place;
    private String desc;
    private String id;

    public Complaint_reg(){

    }

    public Complaint_reg(String id, String caset, String date, String place, String desc) {
        this.caset = caset;
        this.date = date;
        this.place = place;
        this.desc = desc;
        this.id = id;
    }

    public String getCaset() {
        return caset;
    }

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public String getDesc() {
        return desc;
    }

    public String getId() {
        return id;
    }
}
