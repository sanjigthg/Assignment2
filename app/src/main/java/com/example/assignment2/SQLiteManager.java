package com.example.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager sqLiteManager;
    private static final String DATABASE_NAME = "locationDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Location";
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
    public Cursor getLatLongForAddress(String address) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"latitude", "longitude"};
        String selection = "address LIKE ?";
        String[] selectionArgs = {"%" + address + "%"};

        return db.query("Location", projection, selection, selectionArgs, null, null, null);
    }

    public long addAddress(String userEnteredAddress) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("address", userEnteredAddress);


        return db.insert("Location", null, values);
    }
    public int deleteAddressByUserInput(String userEnteredDeleteAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Location", "address=?", new String[]{userEnteredDeleteAddress});
    }

    public int updateAddress(String userEnteredUpdateAddress, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        return db.update("Location", values, "address LIKE ?", new String[]{userEnteredUpdateAddress});
    }
    public void clearAllAddresses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Location", null, null);
        db.close();
    }

    public long getTableCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "Location");
        db.close();
        return count;
    }
}
