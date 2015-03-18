package com.bs.clothesroom.provider;

import org.json.JSONException;
import org.json.JSONObject;

public class CloseInfo {
    
    public final static String JSON_KEY_SEASON = "season";
    public final static String JSON_KEY_STYLE = "style";
    public final static String JSON_KEY_TYPE = "type";

    public enum Season {
        SPRING, AUTUMN, SUMMER, WINTER
    }
    
    public enum Style {
        GENTLEMAN, LEISURE, BUSINESS, FASHION
    }
    
    public enum Type {
        SLEEVED, TROUSERS, OVERCOAT
    }
    
    public Season mSeason;
    public Style mStyle;
    public Type mType;
    
    public static CloseInfo fromJson(JSONObject json) throws JSONException {
        CloseInfo info = new CloseInfo();
        info.mSeason = Season.valueOf(json.getString(JSON_KEY_SEASON));
        info.mStyle = Style.valueOf(json.getString(JSON_KEY_STYLE));
        info.mType = Type.valueOf(json.getString(JSON_KEY_TYPE));
        return info;
    }
    
    public JSONObject toJson(CloseInfo info) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_KEY_SEASON, mSeason);
        json.put(JSON_KEY_STYLE, mStyle);
        json.put(JSON_KEY_TYPE, mType);
        return json;
    }
    
}
