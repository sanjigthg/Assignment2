package com.example.assignment2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager sqLiteManager;
    private static final String DATABASE_NAME = "locationDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Location";
    private static final String COUNTER = "Counter";
    private static final String ID_FIELD = "id";
    private static final String ADDRESS_FIELD = "address";
    private static final String LAT_FIELD = "latitude";
    private static final String LONG_FIELD = "longitude";

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String locationTableQuery = "CREATE TABLE IF NOT EXISTS Location " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "address TEXT, " +
                "latitude REAL, " +
                "longitude REAL)";
        db.execSQL(locationTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
