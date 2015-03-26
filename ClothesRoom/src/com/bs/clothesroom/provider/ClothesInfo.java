package com.bs.clothesroom.provider;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.bs.clothesroom.provider.ClothesInfo.Season;
import com.bs.clothesroom.provider.ClothesInfo.Style;
import com.bs.clothesroom.provider.ClothesInfo.Type;

public class ClothesInfo implements IInfo {
    
    public static final int FLAG_DOWNLOAD_START = 1;
    public static final int FLAG_DOWNLOAD_DONE = 2;
    public static final int FLAG_DOWNLOAD_FAILED = 3;

    public static final String JSON_KEY_SEASON = "season";
    public static final String JSON_KEY_STYLE = "style";
    public static final String JSON_KEY_TYPE = "type";
    public static final String JSON_KEY_MEDIA_NAME = "media_name";
    public static final String JSON_KEY_USERNAME = "username";
    public static final String JSON_KEY_SERVERID = "imageid";

    public final static String COLUMN_NAME_SEASON = "season";
    public final static String COLUMN_NAME_STYLE = "style";
    public final static String COLUMN_NAME_TYPE = "type";
    public final static String COLUMN_NAME_MIMETYPE = "mimetype";
    public final static String COLUMN_NAME_DATA = "_data";
    public final static String COLUMN_NAME_USERID = "user_id";
    public final static String COLUMN_NAME_SYN_SERVER_ID = "server_id";
    public final static String COLUMN_NAME_MEDIA_NAME = "media_name";
    public final static String COLUMN_NAME_DOWNLOAD_FLAG = "flag";

    public final static String MIMETYPE_VEDIO = "vedio";
    public final static String MIMETYPE_IMAGE = "image";
    private static final String[] PROJECTION = new String[] { _ID,
            COLUMN_NAME_SEASON, COLUMN_NAME_STYLE, COLUMN_NAME_TYPE,
            COLUMN_NAME_MIMETYPE, COLUMN_NAME_DATA, COLUMN_NAME_USERID,
            COLUMN_NAME_SYN_SERVER_ID,COLUMN_NAME_MEDIA_NAME,COLUMN_NAME_DOWNLOAD_FLAG };
    
    public static final Uri CONTENT_URI = Uri
            .parse(RoomProvider.CONTENT_URI + "/medias");

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
    public int mSynServerId;
    public int mId;
    public String mUserId;
    public String mMediaName;
    public int mFlag;

    public ClothesInfo(Style style, Season season, Type type) {
        mSeason = season;
        mStyle = style;
        mType = type;
    }

    public ClothesInfo() {
    }
    
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_MEDIA_NAME, mMediaName);
        values.put(COLUMN_NAME_DATA, mMediaPath);
        values.put(COLUMN_NAME_MIMETYPE, mMimeType);
        values.put(COLUMN_NAME_USERID, mUserId);
        values.put(COLUMN_NAME_SYN_SERVER_ID, mSynServerId);
        values.put(COLUMN_NAME_SEASON, mSeason.name());
        values.put(COLUMN_NAME_STYLE, mStyle.name());
        values.put(COLUMN_NAME_TYPE, mType.name());
        values.put(COLUMN_NAME_DOWNLOAD_FLAG, mFlag);
        return values;
    }

    public static ClothesInfo fromJson(JSONObject json) throws JSONException {
        ClothesInfo info = new ClothesInfo();
        info.mSeason = Season.valueOf(json.getString(JSON_KEY_SEASON));
        info.mStyle = Style.valueOf(json.getString(JSON_KEY_STYLE));
        info.mType = Type.valueOf(json.getString(JSON_KEY_TYPE));
        info.mUserId = json.getString(JSON_KEY_USERNAME);
        info.mSynServerId = json.getInt(JSON_KEY_SERVERID);
        return info;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_KEY_SEASON, mSeason);
        json.put(JSON_KEY_STYLE, mStyle);
        json.put(JSON_KEY_TYPE, mType);
        return json;
    }
    
    public Uri addToDatabase(ContentResolver resoler) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_DATA, mMediaPath);
        values.put(COLUMN_NAME_MIMETYPE,mMimeType);
        values.put(COLUMN_NAME_SEASON,mSeason.name());
        values.put(COLUMN_NAME_STYLE, mStyle.name());
        values.put(COLUMN_NAME_SYN_SERVER_ID, mSynServerId);
        values.put(COLUMN_NAME_TYPE, mType.name());
        values.put(COLUMN_NAME_USERID, mUserId);
        values.put(COLUMN_NAME_MEDIA_NAME, mMediaName);
        return resoler.insert(CONTENT_URI, values);
    }
    
    public static ClothesInfo[] getAllClothesImage(ContentResolver resoler, String userId) {
        String selection = COLUMN_NAME_MIMETYPE + " = ? AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { MIMETYPE_IMAGE,
                String.valueOf(userId) };
        Cursor c = resoler.query(ImageInfo.CONTENT_URI, PROJECTION, selection,
                selectionArgs, null);
        if (c == null)
            return null;
        int size = c.getCount();
        ClothesInfo infos[] = new ClothesInfo[size];
        int i = 0;
        while (c.moveToNext()) {
            infos[i] = new ClothesInfo();
            infos[i].mId = c.getInt(c.getColumnIndex(_ID));
            infos[i].mMediaPath = c.getString(c.getColumnIndex(COLUMN_NAME_DATA));
            infos[i].mMimeType = c.getString(c.getColumnIndex(COLUMN_NAME_MIMETYPE));
            infos[i].mSeason = Season.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_SEASON)));
            infos[i].mStyle = Style.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_STYLE)));
            infos[i].mType = Type.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_TYPE)));
            infos[i].mMediaName = c.getString(c.getColumnIndex(COLUMN_NAME_MEDIA_NAME));
            i++;
        }
        return infos;
    }
    
    public static int[] getVedioIds(ContentResolver resoler, String userId) {
        String selection = COLUMN_NAME_MIMETYPE + " = ? AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { MIMETYPE_VEDIO,
                String.valueOf(userId) };
        String projection[] = new String[] { COLUMN_NAME_SYN_SERVER_ID };
        Cursor c = resoler.query(ImageInfo.CONTENT_URI, projection, selection,
                selectionArgs, null);
        if (c == null)
            return null;
        int size = c.getCount();
        int ids[] = new int[size];
        int i = 0;
        while (c.moveToNext()) {
            ids[i++] = c.getInt(c.getColumnIndex(COLUMN_NAME_SYN_SERVER_ID));
        }
        return ids;
    }
    
    public static int[] getImageIds(ContentResolver resoler, String userId) {
        String selection = COLUMN_NAME_MIMETYPE + " = ? AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { MIMETYPE_IMAGE,
                String.valueOf(userId) };
        String projection[] = new String[] { COLUMN_NAME_SYN_SERVER_ID };
        Cursor c = resoler.query(ImageInfo.CONTENT_URI, projection, selection,
                selectionArgs, null);
        if (c == null)
            return null;
        int size = c.getCount();
        int ids[] = new int[size];
        int i = 0;
        while (c.moveToNext()) {
            ids[i++] = c.getInt(c.getColumnIndex(COLUMN_NAME_SYN_SERVER_ID));
        }
        return ids;
    }

    public static ClothesInfo[] getAllClothesVedio(ContentResolver resoler, int userId) {
        String selection = COLUMN_NAME_MIMETYPE + " = ? AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { MIMETYPE_VEDIO,
                String.valueOf(userId) };
        Cursor c = resoler.query(ImageInfo.CONTENT_URI, PROJECTION, selection,
                selectionArgs, null);
        if (c == null)
            return null;
        int size = c.getCount();
        ClothesInfo infos[] = new ClothesInfo[size];
        int i = 0;
        while (c.moveToNext()) {
            infos[i] = new ClothesInfo();
            infos[i].mId = c.getInt(c.getColumnIndex(_ID));
            infos[i].mMediaPath = c.getString(c.getColumnIndex(COLUMN_NAME_DATA));
            infos[i].mMimeType = c.getString(c.getColumnIndex(COLUMN_NAME_MIMETYPE));
            infos[i].mSeason = Season.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_SEASON)));
            infos[i].mStyle = Style.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_STYLE)));
            infos[i].mType = Type.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_TYPE)));
            infos[i].mMediaName = c.getString(c.getColumnIndex(COLUMN_NAME_MEDIA_NAME));
            infos[i].mFlag = c.getInt(c.getColumnIndex(COLUMN_NAME_DOWNLOAD_FLAG));
            infos[i].mSynServerId = c.getInt(c.getColumnIndex(COLUMN_NAME_SYN_SERVER_ID));
            infos[i].mUserId = c.getString(c.getColumnIndex(COLUMN_NAME_USERID));
            i++;
        }
        return infos;
    }

    public static ClothesInfo[] getAllClothes(ContentResolver resolver, int userId) {
        String selection = COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { String.valueOf(userId) };
        Cursor c = resolver.query(ImageInfo.CONTENT_URI, PROJECTION, selection,
                selectionArgs, null);
        if (c == null)
            return null;
        int size = c.getCount();
        ClothesInfo infos[] = new ClothesInfo[size];
        int i = 0;
        while (c.moveToNext()) {
            infos[i] = new ClothesInfo();
            infos[i].mId = c.getInt(c.getColumnIndex(_ID));
            infos[i].mMediaPath = c.getString(c.getColumnIndex(COLUMN_NAME_DATA));
            infos[i].mMimeType = c.getString(c.getColumnIndex(COLUMN_NAME_MIMETYPE));
            infos[i].mSeason = Season.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_SEASON)));
            infos[i].mStyle = Style.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_STYLE)));
            infos[i].mType = Type.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_TYPE)));
            infos[i].mMediaName = c.getString(c.getColumnIndex(COLUMN_NAME_MEDIA_NAME));
            infos[i].mFlag = c.getInt(c.getColumnIndex(COLUMN_NAME_DOWNLOAD_FLAG));
            infos[i].mSynServerId = c.getInt(c.getColumnIndex(COLUMN_NAME_SYN_SERVER_ID));
            infos[i].mUserId = c.getString(c.getColumnIndex(COLUMN_NAME_USERID));
            i++;
        }
        return infos;
    }
    
    public static ClothesInfo getImageInfoBySID(ContentResolver resolver,
            int sid, String userId) {
        ClothesInfo info = new ClothesInfo();
        String selection = COLUMN_NAME_USERID + " = ? AND "
                + COLUMN_NAME_SYN_SERVER_ID + " = ? AND "
                + COLUMN_NAME_MIMETYPE + " = ?";
        String selectionArgs[] = new String[] { userId, String.valueOf(sid),
                "image" };
        Cursor c = resolver.query(ImageInfo.CONTENT_URI, PROJECTION, selection,
                selectionArgs, null);
        if (c == null)
            return null;
        while (c.moveToNext()) {
            info = new ClothesInfo();
            info.mId = c.getInt(c.getColumnIndex(_ID));
            info.mMediaPath = c.getString(c.getColumnIndex(COLUMN_NAME_DATA));
            info.mMimeType = c
                    .getString(c.getColumnIndex(COLUMN_NAME_MIMETYPE));
            info.mSeason = Season.valueOf(c.getString(c
                    .getColumnIndex(COLUMN_NAME_SEASON)));
            info.mStyle = Style.valueOf(c.getString(c
                    .getColumnIndex(COLUMN_NAME_STYLE)));
            info.mType = Type.valueOf(c.getString(c
                    .getColumnIndex(COLUMN_NAME_TYPE)));
            info.mMediaName = c.getString(c
                    .getColumnIndex(COLUMN_NAME_MEDIA_NAME));
            info.mFlag = c.getInt(c.getColumnIndex(COLUMN_NAME_DOWNLOAD_FLAG));
            info.mSynServerId = c.getInt(c.getColumnIndex(COLUMN_NAME_SYN_SERVER_ID));
            info.mUserId = c.getString(c.getColumnIndex(COLUMN_NAME_USERID));
        }
        return info;
    }

    @Override
    public String toString() {
        return "ClothesInfo [mSeason=" + mSeason + ", mStyle=" + mStyle
                + ", mType=" + mType + ", mMimeType=" + mMimeType
                + ", mMediaPath=" + mMediaPath + ", mSynServerId="
                + mSynServerId + ", mId=" + mId + ", mUserId=" + mUserId
                + ", mMediaName=" + mMediaName + ", mFlag=" + mFlag + "]";
    }

    public static class VedioInfo extends ClothesInfo {
        public static final Uri CONTENT_URI = Uri
                .parse(RoomProvider.CONTENT_URI + "/medias");
    }

    public static class ImageInfo extends ClothesInfo {
        public static final Uri CONTENT_URI = Uri
                .parse(RoomProvider.CONTENT_URI + "/medias");
    }

    public static CursorLoader getVedioCursorLoader(Context context, String userId) {
        String selection = COLUMN_NAME_MIMETYPE + " = ? AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { MIMETYPE_VEDIO, userId };
        return new CursorLoader(context, ClothesInfo.CONTENT_URI, PROJECTION,
                selection, selectionArgs, null);
    }
    
    public static CursorLoader getImageCursorLoader(Context context, String userId) {
        String selection = COLUMN_NAME_MIMETYPE + " = ? AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { MIMETYPE_IMAGE, userId };
        return new CursorLoader(context, ClothesInfo.CONTENT_URI, PROJECTION,
                selection, selectionArgs, null);
    }
}
