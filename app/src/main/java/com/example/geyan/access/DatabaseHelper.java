package com.example.geyan.access;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.geyan.model.UserInfo;
import com.example.geyan.model.UserSong;

/**
 * Created by geyan on 06/05/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Mp3Player";
    private static final String TABLE_NAME = "usersInfos";
    private static final String NAME = "name";
    private static final String EMAIL= "email";
    private static final String PASSWORD = "password";
    int id;
    private SQLiteDatabase db;
    private static final String TABLE_CREATE = "create table usersInfos (id int,name varchar(50),email varchar(50), password varchar(50))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(UserInfo usersinfo, int id){
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put(NAME,usersinfo.getName());
        contentValues.put(EMAIL,usersinfo.getEmail());
        contentValues.put(PASSWORD,usersinfo.getPassword());
        db = getWritableDatabase();
        db.insert(TABLE_NAME,null,contentValues);
    }
}
