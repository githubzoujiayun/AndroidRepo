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

    private static final String JSON_KEY_SEASON = "season";
    private static final String JSON_KEY_STYLE = "style";
    private static final String JSON_KEY_SITUATION = "situation";
    private static final String JSON_KEY_TYPE = "type";
    private static final String JSON_KEY_MEDIA_NAME = "media_name";
    private static final String JSON_KEY_USERNAME = "username";
    private static final String JSON_KEY_IMAGE_SERVERID = "imageid";
    private static final String JSON_KEY_VIDEO_SERVERID = "videoid";

    private final static String COLUMN_NAME_SEASON = "season";
    private final static String COLUMN_NAME_STYLE = "style";
    private final static String COLUMN_NAME_SITUATION = "situation";
    private final static String COLUMN_NAME_TYPE = "type";
    public final static String COLUMN_NAME_MIMETYPE = "mimetype";
    public final static String COLUMN_NAME_DATA = "_data";
    public final static String COLUMN_NAME_USERID = "user_id";
    public final static String COLUMN_NAME_SYN_SERVER_ID = "server_id";
    public final static String COLUMN_NAME_MEDIA_NAME = "media_name";
    public final static String COLUMN_NAME_DOWNLOAD_FLAG = "flag";

    public final static String MIMETYPE_VIDEO = "video";
    public final static String MIMETYPE_IMAGE = "image";
    private static final String[] PROJECTION = new String[] { _ID,
            COLUMN_NAME_SEASON, COLUMN_NAME_STYLE, COLUMN_NAME_TYPE,
            COLUMN_NAME_MIMETYPE, COLUMN_NAME_SITUATION, COLUMN_NAME_DATA,
            COLUMN_NAME_USERID, COLUMN_NAME_SYN_SERVER_ID,
            COLUMN_NAME_MEDIA_NAME, COLUMN_NAME_DOWNLOAD_FLAG };
    
    public static final Uri CONTENT_URI = Uri
            .parse(RoomProvider.CONTENT_URI + "/medias");

    public enum Season {
        SPRING, SUMMER, AUTUMN, WINTER;
        
        public static Season valueAt(int index) {
            for (Season season : Season.values()) {
                if (season.ordinal() == index) {
                    return season;
                }
            }
            return null;
        }
    }

    public enum Style {
        GENTLEMAN, LEISURE, BUSINESS, FASHION ,LETSTRE;
        
        public static Style valueAt(int index) {
            for (Style style : Style.values()) {
                if (style.ordinal() == index) {
                    return style;
                }
            }
            return null;
        }
    }
    
    public enum Situation {
        PUBLIC, OFFICE, COCKTAIL;

        public static Situation valueAt(int index) {
            for (Situation situation : Situation.values()) {
                if (situation.ordinal() == index) {
                    return situation;
                }
            }
            return null;
        }
    }

    public enum Type {
        SLEEVED, TROUSERS, OVERCOAT;

        public static Type valueAt(int index) {
            for (Type type : Type.values()) {
                if (type.ordinal() == index) {
                    return type;
                }
            }
            return null;
        }
    }
    
    public Season mSeason;
    public Style mStyle;
    public Situation mSituation;
    public Type mType;
    public String mMimeType;
    public String mMediaPath;
    public int mSynServerId;
    public int mId;
    public String mUserId;
    public String mMediaName;
    public int mFlag;

    public ClothesInfo(Style style, Season season, Situation situation, Type type) {
        mSeason = season;
        mStyle = style;
        mType = type;
        mSituation = situation;
    }

    public ClothesInfo() {
    }
    
    

//    public abstract ClothesInfo fromJson(JSONObject json) throws JSONException;

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_KEY_SEASON, mSeason);
        json.put(JSON_KEY_STYLE, mStyle);
        json.put(JSON_KEY_TYPE, mType);
        json.put(JSON_KEY_SITUATION, mSituation);
        return json;
    }
    
    public Uri addToDatabase(ContentResolver resoler) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_DATA, mMediaPath);
        values.put(COLUMN_NAME_MIMETYPE,mMimeType);
        values.put(COLUMN_NAME_SEASON,mSeason.name());
        values.put(COLUMN_NAME_STYLE, mStyle.name());
        values.put(COLUMN_NAME_TYPE, mType.name());
        values.put(COLUMN_NAME_SITUATION,mSituation.name());
        values.put(COLUMN_NAME_SYN_SERVER_ID, mSynServerId);
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
            infos[i] = new ImageInfo();
            infos[i].mId = c.getInt(c.getColumnIndex(_ID));
            infos[i].mMediaPath = c.getString(c.getColumnIndex(COLUMN_NAME_DATA));
            infos[i].mMimeType = c.getString(c.getColumnIndex(COLUMN_NAME_MIMETYPE));
            infos[i].mSeason = Season.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_SEASON)));
            infos[i].mStyle = Style.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_STYLE)));
            infos[i].mType = Type.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_TYPE)));
            infos[i].mSituation = Situation.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_SITUATION)));
            infos[i].mMediaName = c.getString(c.getColumnIndex(COLUMN_NAME_MEDIA_NAME));
            i++;
        }
        return infos;
    }
    
    public static int[] getVideoIds(ContentResolver resoler, String userId) {
        String selection = COLUMN_NAME_MIMETYPE + " = ? AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { MIMETYPE_VIDEO,
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

    public static ClothesInfo[] getAllClothesVideo(ContentResolver resoler, int userId) {
        String selection = COLUMN_NAME_MIMETYPE + " = ? AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { MIMETYPE_VIDEO,
                String.valueOf(userId) };
        Cursor c = resoler.query(ImageInfo.CONTENT_URI, PROJECTION, selection,
                selectionArgs, null);
        if (c == null)
            return null;
        int size = c.getCount();
        ClothesInfo infos[] = new ClothesInfo[size];
        int i = 0;
        while (c.moveToNext()) {
            infos[i] = new VideoInfo();
            infos[i].mId = c.getInt(c.getColumnIndex(_ID));
            infos[i].mMediaPath = c.getString(c.getColumnIndex(COLUMN_NAME_DATA));
            infos[i].mMimeType = c.getString(c.getColumnIndex(COLUMN_NAME_MIMETYPE));
            infos[i].mSeason = Season.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_SEASON)));
            infos[i].mStyle = Style.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_STYLE)));
            infos[i].mType = Type.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_TYPE)));
            infos[i].mSituation = Situation.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_SITUATION)));
            infos[i].mMediaName = c.getString(c.getColumnIndex(COLUMN_NAME_MEDIA_NAME));
            infos[i].mFlag = c.getInt(c.getColumnIndex(COLUMN_NAME_DOWNLOAD_FLAG));
            infos[i].mSynServerId = c.getInt(c.getColumnIndex(COLUMN_NAME_SYN_SERVER_ID));
            infos[i].mUserId = c.getString(c.getColumnIndex(COLUMN_NAME_USERID));
            i++;
        }
        return infos;
    }

//    public static ClothesInfo[] getAllClothes(ContentResolver resolver, int userId) {
//        String selection = COLUMN_NAME_USERID + " = ?";
//        String selectionArgs[] = new String[] { String.valueOf(userId) };
//        Cursor c = resolver.query(ImageInfo.CONTENT_URI, PROJECTION, selection,
//                selectionArgs, null);
//        if (c == null)
//            return null;
//        int size = c.getCount();
//        ClothesInfo infos[] = new ClothesInfo[size];
//        int i = 0;
//        while (c.moveToNext()) {
//            infos[i] = new ClothesInfo();
//            infos[i].mId = c.getInt(c.getColumnIndex(_ID));
//            infos[i].mMediaPath = c.getString(c.getColumnIndex(COLUMN_NAME_DATA));
//            infos[i].mMimeType = c.getString(c.getColumnIndex(COLUMN_NAME_MIMETYPE));
//            infos[i].mSeason = Season.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_SEASON)));
//            infos[i].mStyle = Style.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_STYLE)));
//            infos[i].mType = Type.valueOf(c.getString(c.getColumnIndex(COLUMN_NAME_TYPE)));
//            infos[i].mMediaName = c.getString(c.getColumnIndex(COLUMN_NAME_MEDIA_NAME));
//            infos[i].mFlag = c.getInt(c.getColumnIndex(COLUMN_NAME_DOWNLOAD_FLAG));
//            infos[i].mSynServerId = c.getInt(c.getColumnIndex(COLUMN_NAME_SYN_SERVER_ID));
//            infos[i].mUserId = c.getString(c.getColumnIndex(COLUMN_NAME_USERID));
//            i++;
//        }
//        return infos;
//    }
    
    public static ClothesInfo getImageInfoBySID(ContentResolver resolver,
            int sid, String userId) {
        ClothesInfo info = new ImageInfo();
        String selection = COLUMN_NAME_USERID + " = ? AND "
                + COLUMN_NAME_SYN_SERVER_ID + " = ? AND "
                + COLUMN_NAME_MIMETYPE + " = ?";
        String selectionArgs[] = new String[] { userId, String.valueOf(sid),
                MIMETYPE_IMAGE };
        Cursor c = resolver.query(ImageInfo.CONTENT_URI, PROJECTION, selection,
                selectionArgs, null);
        if (c == null)
            return null;
        while (c.moveToNext()) {
            info = new ImageInfo();
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
            info.mSituation = Situation.valueOf(c.getString(c
                    .getColumnIndex(COLUMN_NAME_SITUATION)));
            info.mMediaName = c.getString(c
                    .getColumnIndex(COLUMN_NAME_MEDIA_NAME));
            info.mFlag = c.getInt(c.getColumnIndex(COLUMN_NAME_DOWNLOAD_FLAG));
            info.mSynServerId = c.getInt(c.getColumnIndex(COLUMN_NAME_SYN_SERVER_ID));
            info.mUserId = c.getString(c.getColumnIndex(COLUMN_NAME_USERID));
        }
        return info;
    }
    
    public static ImageInfo[] getImageInfoArgs(ContentResolver resolver,
            String userId, Season season, Style style, Situation situation) {
        String selection = COLUMN_NAME_USERID + " = ? AND "
                + COLUMN_NAME_SEASON + " = ? AND " + COLUMN_NAME_STYLE
                + " = ? AND " + COLUMN_NAME_SITUATION + " = ? AND "
                + COLUMN_NAME_MIMETYPE + " = ?";
        String selectionArgs[] = new String[] { userId, season.name(),
                style.name(), situation.name(), MIMETYPE_IMAGE };
        Cursor c = resolver.query(ImageInfo.CONTENT_URI, PROJECTION, selection,
                selectionArgs, null);
        if (c == null)
            return null;
        ImageInfo info[] = new ImageInfo[c.getCount()];
        int i= 0;
        while (c.moveToNext()) {
            info[i] = new ImageInfo();
            info[i].mId = c.getInt(c.getColumnIndex(_ID));
            info[i].mMediaPath = c.getString(c.getColumnIndex(COLUMN_NAME_DATA));
            info[i].mMimeType = c
                    .getString(c.getColumnIndex(COLUMN_NAME_MIMETYPE));
            info[i].mSeason = Season.valueOf(c.getString(c
                    .getColumnIndex(COLUMN_NAME_SEASON)));
            info[i].mStyle = Style.valueOf(c.getString(c
                    .getColumnIndex(COLUMN_NAME_STYLE)));
            info[i].mType = Type.valueOf(c.getString(c
                    .getColumnIndex(COLUMN_NAME_TYPE)));
            info[i].mSituation = Situation.valueOf(c.getString(c
                    .getColumnIndex(COLUMN_NAME_SITUATION)));
            info[i].mMediaName = c.getString(c
                    .getColumnIndex(COLUMN_NAME_MEDIA_NAME));
            info[i].mFlag = c.getInt(c.getColumnIndex(COLUMN_NAME_DOWNLOAD_FLAG));
            info[i].mSynServerId = c.getInt(c
                    .getColumnIndex(COLUMN_NAME_SYN_SERVER_ID));
            info[i].mUserId = c.getString(c.getColumnIndex(COLUMN_NAME_USERID));
        }
        return info;
    }
    
    public static ClothesInfo getVideoInfoBySID(ContentResolver resolver,
            int sid, String userId) {
        ClothesInfo info = null;
        String selection = COLUMN_NAME_USERID + " = ? AND "
                + COLUMN_NAME_SYN_SERVER_ID + " = ? AND "
                + COLUMN_NAME_MIMETYPE + " = ?";
        String selectionArgs[] = new String[] { userId, String.valueOf(sid),
                MIMETYPE_VIDEO };
        Cursor c = resolver.query(ImageInfo.CONTENT_URI, PROJECTION, selection,
                selectionArgs, null);
        if (c == null)
            return null;
        while (c.moveToNext()) {
            info = new VideoInfo();
            info.mId = c.getInt(c.getColumnIndex(_ID));
            info.mMediaPath = c.getString(c.getColumnIndex(COLUMN_NAME_DATA));
            info.mMimeType = c
                    .getString(c.getColumnIndex(COLUMN_NAME_MIMETYPE));
            info.mMediaName = c.getString(c
                    .getColumnIndex(COLUMN_NAME_MEDIA_NAME));
            info.mFlag = c.getInt(c.getColumnIndex(COLUMN_NAME_DOWNLOAD_FLAG));
            info.mSynServerId = c.getInt(c.getColumnIndex(COLUMN_NAME_SYN_SERVER_ID));
            info.mUserId = c.getString(c.getColumnIndex(COLUMN_NAME_USERID));
        }
        return info;
    }
    
    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_MEDIA_NAME, mMediaName);
        values.put(COLUMN_NAME_DATA, mMediaPath);
        values.put(COLUMN_NAME_MIMETYPE, mMimeType);
        values.put(COLUMN_NAME_USERID, mUserId);
        values.put(COLUMN_NAME_SYN_SERVER_ID, mSynServerId);
        values.put(COLUMN_NAME_DOWNLOAD_FLAG, mFlag);
        values.put(COLUMN_NAME_SEASON, mSeason.name());
        values.put(COLUMN_NAME_STYLE, mStyle.name());
        values.put(COLUMN_NAME_TYPE, mType.name());
        values.put(COLUMN_NAME_SITUATION, Situation.COCKTAIL.name());
//        values.put(COLUMN_NAME_SITUATION, mSituation.name());
        return values;
    }

    @Override
    public String toString() {
        return "ClothesInfo [mSeason=" + mSeason + ", mStyle=" + mStyle
                + ", mSituation=" + mSituation + ", mType=" + mType
                + ", mMimeType=" + mMimeType + ", mMediaPath=" + mMediaPath
                + ", mSynServerId=" + mSynServerId + ", mId=" + mId
                + ", mUserId=" + mUserId + ", mMediaName=" + mMediaName
                + ", mFlag=" + mFlag + "]";
    }

    public static class VideoInfo extends ClothesInfo {
        public static final Uri CONTENT_URI = Uri
                .parse(RoomProvider.CONTENT_URI + "/medias");
        private static final String COLUMN_NAME_RELATIVE_IMAGEIDS = "relative_image_ids";
        public String mRelativeImageIds;
        public static VideoInfo fromJson(JSONObject json) throws JSONException {
            VideoInfo info = new VideoInfo();
            info.mUserId = json.getString(JSON_KEY_USERNAME);
            info.mSynServerId = json.getInt(JSON_KEY_VIDEO_SERVERID);
            info.mRelativeImageIds = json.getString("imageids");
            info.mSeason = Season.valueOf(json.getString(JSON_KEY_SEASON));
            info.mStyle = Style.valueOf(json.getString(JSON_KEY_STYLE));
            info.mType = Type.valueOf(json.getString(JSON_KEY_TYPE));
            return info;
        }
        
        @Override
        public ContentValues toContentValues() {
            ContentValues values = super.toContentValues();
            values.put(COLUMN_NAME_RELATIVE_IMAGEIDS, mRelativeImageIds);
            return values;
        }
    }

    public static class ImageInfo extends ClothesInfo {
        public static final Uri CONTENT_URI = Uri
                .parse(RoomProvider.CONTENT_URI + "/medias");
        
        public ContentValues toContentValues() {
            ContentValues values = super.toContentValues();
            return values;
        }
        
        public static ImageInfo fromJson(JSONObject json) throws JSONException {
            ImageInfo info = new ImageInfo();
            info.mSeason = Season.valueOf(json.getString(JSON_KEY_SEASON));
            info.mStyle = Style.valueOf(json.getString(JSON_KEY_STYLE));
            info.mType = Type.valueOf(json.getString(JSON_KEY_TYPE));
//            info.mSituation = Situation.valueOf(json.getString(JSON_KEY_SITUATION));
            info.mSituation = Situation.COCKTAIL;
            info.mUserId = json.getString(JSON_KEY_USERNAME);
            info.mSynServerId = json.getInt(JSON_KEY_IMAGE_SERVERID);
            return info;
        }
    }

    public static CursorLoader createVideoCursorLoader(Context context, String userId) {
        String selection = COLUMN_NAME_MIMETYPE + " = ? AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { MIMETYPE_VIDEO, userId };
        return new CursorLoader(context, ClothesInfo.CONTENT_URI, PROJECTION,
                selection, selectionArgs, null);
    }
    
    public static CursorLoader createImageCursorLoader(Context context, String userId) {
        String selection = COLUMN_NAME_MIMETYPE + " = ? AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { MIMETYPE_IMAGE, userId };
        return new CursorLoader(context, ClothesInfo.CONTENT_URI, PROJECTION,
                selection, selectionArgs, null);
    }
    
	public static CursorLoader createMediaCursorLoader(Context context,
			String userId, String season, String style, String situation) {
		String selection = COLUMN_NAME_SEASON + " = ? AND " + COLUMN_NAME_STYLE
				+ " = ? AND " + COLUMN_NAME_SITUATION + " = ? AND "
				+ COLUMN_NAME_USERID + " = ?";
		String selectionArgs[] = new String[] { season, style, situation, userId };
		return new CursorLoader(context, ClothesInfo.CONTENT_URI, PROJECTION,
				selection, selectionArgs, null);
	}
	
	public static CursorLoader createTypeCursorLoader(Context context,
            String userId, String type) {
        String selection = COLUMN_NAME_TYPE + " = ?  AND "
                + COLUMN_NAME_USERID + " = ?";
        String selectionArgs[] = new String[] { type, userId };
        return new CursorLoader(context, ClothesInfo.CONTENT_URI, PROJECTION,
                selection, selectionArgs, null);
    }
}
