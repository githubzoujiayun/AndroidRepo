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

    public String userName;
    public String password;
    public String sex;
    public String age;
    public String phone;
    public String email;
    public String job;
    public String weight;
    public String height;
    public String bust;
    public String waist;
    public String hips;
    
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
        if (json.has(password)){
            this.password = json.getString(JSON_KEY_PASSWORD);
        }
        this.sex = json.getString(JSON_KEY_SEX);
        this.age = json.getString(JSON_KEY_AGE);
        this.phone = json.getString(JSON_KEY_PHONE);
        this.email = json.getString(JSON_KEY_EMAIL);
        this.job = json.getString(JSON_KEY_JOB);
    }
    
    public static UserInfo fromJson(JSONObject json) {
        try {
            return new UserInfo(json);
        } catch (JSONException e) {
            throw new RuntimeException("json is not well formate.");
        }
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
