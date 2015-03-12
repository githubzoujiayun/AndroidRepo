package com.bs.clothesroom.controller;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 3835107854348824176L;
    
    private static final String JSON_KEY_USERNAME = "username";
    private static final String JSON_KEY_PASSWORD = "password";
    private static final String JSON_KEY_SEX = "sex";
    private static final String JSON_KEY_AGE = "age";
    private static final String JSON_KEY_PHONE = "phone";
    private static final String JSON_KEY_EMAIL = "email";
    private static final String JSON_KEY_JOB = "job";

    String userName;
    String password;
    String sex;
    String age;
    String phone;
    String email;
    String job;
    
    public UserInfo(String userName, String password, String sex, String age,
            String phone, String email, String work) {
        this.userName = userName;
        this.password = password;
        this.sex = sex;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.job = work;
    }
    
    public UserInfo(JSONObject json) throws JSONException {
        this.userName = json.getString(JSON_KEY_USERNAME);
        this.password = json.getString(JSON_KEY_PASSWORD);
        this.sex = json.getString(JSON_KEY_SEX);
        this.age = json.getString(JSON_KEY_AGE);
        this.phone = json.getString(JSON_KEY_PHONE);
        this.email = json.getString(JSON_KEY_EMAIL);
        this.job = json.getString(JSON_KEY_JOB);
    }
    
    public static UserInfo fromJson(JSONObject json) throws JSONException {
        return new UserInfo(json);
    }
    
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_KEY_USERNAME, userName);
        json.put(JSON_KEY_PASSWORD, password);
        json.put(JSON_KEY_SEX, sex);
        json.put(JSON_KEY_AGE, age);
        json.put(JSON_KEY_PHONE, phone);
        json.put(JSON_KEY_EMAIL, email);
        json.put(JSON_KEY_JOB, job);
        return json;
    }
}
