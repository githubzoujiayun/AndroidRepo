package com.bs.clothesroom.provider;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

import com.bs.clothesroom.provider.CloseInfo.Season;
import com.bs.clothesroom.provider.CloseInfo.Style;
import com.bs.clothesroom.provider.CloseInfo.Type;

public class CloseInfo implements IInfo{
    
    public final static String JSON_KEY_SEASON = "season";
    public final static String JSON_KEY_STYLE = "style";
    public final static String JSON_KEY_TYPE = "type";
    
    public final static String COLUMN_NAME_SEASON = "season";
    public final static String COLUMN_NAME_STYLE = "style";
    public final static String COLUMN_NAME_TYPE = "type";
    public final static String COLUMN_NAME_MIMETYPE = "mimetype";
    public final static String COLUMN_NAME_DATA = "_data";
    public final static String COLUMN_NAME_USERID = "user_id";
    
    public final static String MIMETYPE_VEDIO = "vedio";
    public final static String MIMETYPE_IMAGE = "image";

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
    public String mMimeType;
    public String mMediaPath;
    
    public CloseInfo(Style style, Season season, Type type) {
        mSeason = season;
        mStyle = style;
        mType = type;
    }

    public CloseInfo() {
    }

    public static CloseInfo fromJson(JSONObject json) throws JSONException {
        CloseInfo info = new CloseInfo();
        info.mSeason = Season.valueOf(json.getString(JSON_KEY_SEASON));
        info.mStyle = Style.valueOf(json.getString(JSON_KEY_STYLE));
        info.mType = Type.valueOf(json.getString(JSON_KEY_TYPE));
        return info;
    }
    
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_KEY_SEASON, mSeason);
        json.put(JSON_KEY_STYLE, mStyle);
        json.put(JSON_KEY_TYPE, mType);
        return json;
    }
    
    public void getAllCloseImage(ContentResolver resoler,int userId) {
        String selection = COLUMN_NAME_MIMETYPE+ " = ? , "+COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[]{
                MIMETYPE_IMAGE,String.valueOf(userId)
        };
        resoler.query(ImageInfo.CONTENT_URI, null, selection, selectionArgs, null);
    }
    
    public void getAllCloseVedio(ContentResolver resoler,int userId) {
        String selection = COLUMN_NAME_MIMETYPE+ " = ? , "+COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[]{
          MIMETYPE_VEDIO,String.valueOf(userId)
        };
        resoler.query(ImageInfo.CONTENT_URI, null, selection, selectionArgs, null);
    }
    
    public void getAllClose(ContentResolver resoler,int userId) {
        String selection = COLUMN_NAME_USERID+ " = ?";
        String selectionArgs[] = new String[]{
                String.valueOf(userId)
        };
        resoler.query(ImageInfo.CONTENT_URI,null ,selection, selectionArgs, null);
    }

    @Override
    public String toString() {
        return "CloseInfo [mSeason=" + mSeason + ", mStyle=" + mStyle
                + ", mType=" + mType + "]";
    }
    
    public static class VedioInfo extends CloseInfo {
        public static final Uri CONTENT_URI = Uri.parse(RoomProvider.CONTENT_URI + "/medias");
    }
    
    public static class ImageInfo extends CloseInfo {
        public static final Uri CONTENT_URI = Uri.parse(RoomProvider.CONTENT_URI + "/medias");
    }
}
