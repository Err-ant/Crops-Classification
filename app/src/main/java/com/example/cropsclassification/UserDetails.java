package com.example.cropsclassification;

public class UserDetails {
    public String fullName, email, dob, gender, mobile;

    public UserDetails(){

    }

    public UserDetails(String textFullName, String email, String textDob, String textGender, String textMobile) {
        this.fullName = textFullName;
        this.email = email;
        this.dob = textDob;
        this.gender = textGender;
        this.mobile = textMobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
