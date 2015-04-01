package com.bs.clothesroom.provider;

import java.util.Arrays;

import com.bs.clothesroom.controller.Preferences;

import android.content.ContentProvider;
import android.content.ContentUris;
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
    private static final int DATABASE_VERSION = 8;
    
    private static final String TABLE_NAME_USERS = "Users";
    private static final String TABLE_NAME_MEDIAS = "Medias";
    
    private static final int USERS = 1;
    private static final int USERS_ID = 2;
    private static final int MEDIA_FILES = 3;
    private static final int MEDIA_FILES_ID = 4;
    private static final boolean DEBUG_SQL = true;
    
    private static UriMatcher sUriMatcher = null;
    
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(PROVIDER_NAME, "users", USERS);
        sUriMatcher.addURI(PROVIDER_NAME, "users/#", USERS_ID);
        sUriMatcher.addURI(PROVIDER_NAME, "medias", MEDIA_FILES);
        sUriMatcher.addURI(PROVIDER_NAME, "medias/#", MEDIA_FILES_ID);
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
            		"Age Text, " +
            		"Sex Text, " +
            		"Job Text" +
            		"Bust Text, " +
            		"Waist Text, " +
            		"Hips Text" +
//            		"Shoudler Integer" +
            		")");
            db.execSQL("create table "+TABLE_NAME_MEDIAS + " (" +
            		"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            		"mimetype Text, " +
            		"size Integer, " +
            		"time Text, " +
            		"_data Text, " +
            		"user_id Text," +
            		"season Text," +
            		"style Text," +
            		"type Text," +
            		"situation Text," +
            		"server_id Text," +
            		"media_name Text," +
            		"flag Text," +
            		"relative_image_ids Text" +
            		")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MEDIAS);
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
        final int match = sUriMatcher.match(uri);
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
                throw new IllegalArgumentException("unkown insert uri. " + uri);
        }
        if (_id >= 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri,_id);
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
        final int match = sUriMatcher.match(uri);
        log("query : selection = "+selection+" args = "+Arrays.toString(selectionArgs));
        String _id = null;
        Cursor c = null;
        switch(match) {
            case USERS:{
                break;
            }
            case USERS_ID:{
                _id = uri.getPathSegments().get(1);
                StringBuffer where = new StringBuffer();
                where.append(selection)
                    .append(" AND ")
                    .append(ClothesInfo._ID)
                    .append(" = ")
                    .append(_id);
                c = mDb.query(TABLE_NAME_USERS, projection, where.toString(), selectionArgs, null, null, orderBy);
                break;
            }
            case MEDIA_FILES:{
                c = mDb.query(TABLE_NAME_MEDIAS, projection, selection, selectionArgs, null, null, orderBy);
                break;
            }
            case MEDIA_FILES_ID:{
                _id = uri.getPathSegments().get(1);
                c = mDb.query(TABLE_NAME_MEDIAS, projection, selection, selectionArgs, null, null, orderBy);
                break;
            }
            default:
                throw new IllegalArgumentException("unkown query uri. " + uri+" match = "+match);
        }
        if (c != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int row = -1;
        String _id = uri.getLastPathSegment();
        String where = ClothesInfo._ID + " = ?";
        String args[] = new String[]{_id};
        Preferences.log("values : "+values);
        Preferences.log("id = "+_id);
        switch (match) {
        case MEDIA_FILES_ID:
            
            row = mDb.update(TABLE_NAME_MEDIAS, values, where, args);
            break;
        case USERS_ID:
            row = mDb.update(TABLE_NAME_USERS, values, where, args);
            break;

        default:
            throw new IllegalArgumentException("unkown insert uri. " + uri+" match = "+match);
        }
        if (row >= 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return 0;
    }

    private void log(String args){
        if(DEBUG_SQL) {
            android.util.Log.e("qinchao","sql.log --> " + args);
        }
    }
}
