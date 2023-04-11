package com.example.dtbs;

import android.widget.EditText;

public class User {

    public String user_fullname,user_birthday,user_licence_no,user_email,user_code;

    public static String User_FullName,User_Birthday,User_licence,User_Email;
    public static String DateBooking;
    public static String InsNo,InsName;
    public static String BookedIns,BookedSlot;


    public User(){

    }


    public User(String fullname, String birthday, String licence_no, String email, String code) {
        this.user_fullname = fullname;
        this.user_birthday = birthday;
        this.user_licence_no = licence_no;
        this.user_email = email;
        this.user_code = code;
        
    }
}


