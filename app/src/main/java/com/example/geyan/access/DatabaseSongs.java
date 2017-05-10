package com.example.geyan.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.geyan.model.UserSong;

/**
 * Created by geyan on 10/05/2017.
 */

public class DatabaseSongs extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Mp3Songs";
    private static final String TABLE_NAME = "songs";
    private static final String OWNER = "owner";
    private static final String SONG = "song";

    private SQLiteDatabase db;
    private static final String TABLE_CREATE = "create table songs (owner varchar(50),song varchar(50))";

    public DatabaseSongs(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(UserSong usersong){
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(OWNER,usersong.getOwner());
        contentValues.put(SONG,usersong.getSongName());
        db.insert(TABLE_NAME,null,contentValues);
    }
}
