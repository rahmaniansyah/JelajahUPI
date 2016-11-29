package com.example.rahmaniansyahdp.lokassiv3;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rahmaniansyah DP on 27/11/2016.
 */

public class OpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1 ;
    public static final String DATABASE_NAME = "db_jelajahupi" ;
    public static final String TABLE_CREATE =
            "CREATE TABLE POSTJELAJAHUPI (ID INTEGER PRIMARY KEY AUTOINCREMENT, NM_LOKASI TEXT, LEFT_E TEXT, RIGHT_E TEXT,UP_S TEXT, DOWN_S TEXT)" ;

    public OpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create database
        db.execSQL(TABLE_CREATE) ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS POSTJELAJAHUPI");
    }
}
