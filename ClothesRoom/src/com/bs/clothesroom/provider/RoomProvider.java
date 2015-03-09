package com.bs.clothesroom.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class RoomProvider extends ContentProvider{
    
    public static final String PROVIDER_NAME = "com.bs.clothesroom.RoomProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://"+PROVIDER_NAME);
    
    private static final String DATABASE_NAME = "room.db";
    private static final int DATABASE_VERSION = 1;
    
    private static final String TABLE_NAME_USERS = "Users";
    private static final String TABLE_NAME_MEDIAS = "Medias";
    
    private static final int USERS = 1;
    private static final int USERS_ID = 2;
    private static final int MEDIA_FILES = 3;
    private static final int MEDIA_FILE_ID = 4;
    
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mUriMatcher.addURI(PROVIDER_NAME, "/users", USERS);
        mUriMatcher.addURI(PROVIDER_NAME, "/users/#", USERS_ID);
        mUriMatcher.addURI(PROVIDER_NAME, "/medias", MEDIA_FILES);
        mUriMatcher.addURI(PROVIDER_NAME, "/medias/#", MEDIA_FILE_ID);
    }
    
    private DatabaseHelper mHelper = null;
    private SQLiteDatabase mDb;
    
    private static class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table "+TABLE_NAME_USERS+" (" +
            		"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            		"UserName Text, " +
            		"PhoneNumber Text, " +
            		"EmailAddress Text, " +
            		"Age Integer, " +
            		"Sex Text, " +
            		"Bust Integer, " +
            		"Waist Integer, " +
            		"Hips Integer, " +
            		"Shoudler Integer" +
            		")");
            db.execSQL("create table "+TABLE_NAME_MEDIAS + " (" +
            		"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            		"MimeType Text, " +
            		"Size Integer, " +
            		"Time Text, " +
            		"_data Text, " +
            		"user_id integer" +
            		")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USERS);
            onCreate(db);
        }
        
    }
    
    

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        return 0;
    }

    @Override
    public String getType(Uri arg0) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        android.util.Log.e("qinchao","insert : uri = "+uri + " values = "+values);
        final int match = mUriMatcher.match(uri);
        long _id = -1;
        switch(match) {
            case USERS:{
                _id = mDb.insert(TABLE_NAME_USERS, null, values);
                break;
            }
            case MEDIA_FILES:{
                _id = mDb.insert(TABLE_NAME_MEDIAS, null, values);
                break;
            }
            default:
                throw new IllegalArgumentException("unkown uri.");
        }
        if (_id >= 0) {
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        mHelper = new DatabaseHelper(getContext());
        mDb = mHelper.getWritableDatabase();
        return mDb != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String orderBy) {
        final int match = mUriMatcher.match(uri);
        String _id = null;
        Cursor c = null;
        switch(match) {
            case USERS:{
//                mDb.qu
                break;
            }
            case USERS_ID:{
                _id = uri.getPathSegments().get(1);
                c = mDb.query(TABLE_NAME_USERS, projection, selection, selectionArgs, null, null, orderBy);
            }
            case MEDIA_FILES:{
                
                break;
            }
            case MEDIA_FILE_ID:{
                _id = uri.getPathSegments().get(1);
                c = mDb.query(TABLE_NAME_USERS, projection, selection, selectionArgs, null, null, orderBy);
                break;
            }
            default:
                throw new IllegalArgumentException("unkown uri.");
        }
        if (c != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Override
    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        return 0;
    }

}
