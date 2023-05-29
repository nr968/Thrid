package com.example.thrid;

public class Users {

    private String firstname;
    private String lastname;
    private String password;
    private String flat;
    private String street;
    private String city;
    private String state;
    private String email;
    private String mobile;
    private String aadhar;
    private String id;

    public Users(){

    }

    public Users(String id,String firstname, String lastname, String password, String flat, String street, String city, String state, String email, String mobile, String aadhar) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.flat = flat;
        this.street = street;
        this.city = city;
        this.state = state;
        this.email = email;
        this.mobile = mobile;
        this.aadhar = aadhar;
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }

    public String getFlat() {
        return flat;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAadhar() {
        return aadhar;
    }

    public String getId() {
        return id;
    }
}
