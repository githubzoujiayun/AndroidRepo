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
    private static final String JSON_KEY_BUST = "bust";
    private static final String JSON_KEY_WAIST = "waist";
    private static final String JSON_KEY_HIPS = "hips";
    private static final String JSON_KEY_HEIGHT = "height";
    private static final String JSON_KEY_WEIGHT = "weight";
    

    public String userName = null;
    public String password = "";
    public String sex = "";
    public String age = "";
    public String phone = "";
    public String email = "";
    public String job = "";
    public String weight = "";
    public String height = "";
    public String bust = "";
    public String waist = "";
    public String hips = "";
    
    public UserInfo(String userName, String password, String sex, String age,
            String phone, String email, String work, String bust, String waist, String hips, String height, String weight) {
        this.userName = userName;
        this.password = password;
        this.sex = sex;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.job = work;
        this.bust = bust;
        this.waist = waist;
        this.hips = hips;
        this.height = height;
        this.waist = waist;
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
        this.bust = json.getString(JSON_KEY_BUST);
        this.waist = json.getString(JSON_KEY_WAIST);
        this.hips = json.getString(JSON_KEY_HIPS);
        this.weight = json.getString(JSON_KEY_WEIGHT);
        this.height = json.getString(JSON_KEY_HEIGHT);
    }
    
    public static UserInfo fromJson(JSONObject json) {
        try {
            return new UserInfo(json);
        } catch (JSONException e) {
//            throw new RuntimeException("json is not well formate.");
            e.printStackTrace();
            return null;
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
        json.put(JSON_KEY_BUST, bust);
        json.put(JSON_KEY_WAIST, waist);
        json.put(JSON_KEY_HIPS, hips);
        json.put(JSON_KEY_HEIGHT, height);
        json.put(JSON_KEY_WEIGHT, weight);
        return json;
    }
}
