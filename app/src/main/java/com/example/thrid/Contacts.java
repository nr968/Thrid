package com.example.thrid;

public class Contacts {

    private String name1;
    private String name2;
    private String name3;
    private String number1;
    private String number2;
    private String number3;
    private String id;

    public Contacts(){

    }

    public Contacts(String id,String name1, String name2, String name3, String number1, String number2, String number3) {
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
        this.number1 = number1;
        this.number2 = number2;
        this.number3 = number3;
        this.id = id;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    public String getName3() {
        return name3;
    }

    public String getNumber1() {
        return number1;
    }

    public String getNumber2() {
        return number2;
    }

    public String getNumber3() {
        return number3;
    }

    public String getId() {
        return id;
    }
}
