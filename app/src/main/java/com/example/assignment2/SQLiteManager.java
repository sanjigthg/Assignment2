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
    // Obtains the database value for the address by searching the database and providing the
    // latitude and longitude value into the function that it is being called to, which in this case
    // is the onSearchButtonClick().
    public Cursor getLatLongForAddress(String address) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"latitude", "longitude"};
        String selection = "address LIKE ?";
        String[] selectionArgs = {"%" + address + "%"};

        return db.query("Location", projection, selection, selectionArgs, null, null, null);
    }
    // Corresponds to the onAddAddressButtonClick and obtains the address value from the function
    // which stores into the database.
    public long addAddress(String userEnteredAddress) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("address", userEnteredAddress);


        return db.insert("Location", null, values);
    }
    // Corresponds to the onDeleteButtonClick function, where it obtains the address from the function
    // and deletes the table values that hold this address value.
    public int deleteAddressByUserInput(String userEnteredDeleteAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Location", "address=?", new String[]{userEnteredDeleteAddress});
    }
    // Corresponds to the onUpdateButtonClick function that will put in the new longitude and latitude values
    // into the database with the corresponding address value from the user input.
    public int updateAddress(String userEnteredUpdateAddress, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        return db.update("Location", values, "address LIKE ?", new String[]{userEnteredUpdateAddress});
    }

    // Error checking to ensure that the database does not constantly keep inputting the values
    // from the coordinates.txt file.
    public long getTableCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "Location");
        db.close();
        return count;
    }
}
