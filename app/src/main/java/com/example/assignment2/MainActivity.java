package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SQLiteManager sqLiteManager;
    private EditText addressInput;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqLiteManager = new SQLiteManager(this);
        addressInput = findViewById(R.id.addressInput);
        listView = findViewById(R.id.database_values_list);

        try {
            readAndInsertDataFromAssets();
        } catch (IOException e) {
            e.printStackTrace();
        }
        displayDatabaseValues();
    }

    private String getAddressFromCoordinates (Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String addressValue = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    stringBuilder.append(address.getAddressLine(i)).append("\n");
                }
                addressValue = stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressValue;
    }
    private void readAndInsertDataFromAssets () throws IOException {
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("coordinates.txt");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        SQLiteDatabase db = sqLiteManager.getWritableDatabase();

        String input;
        while ((input = bufferedReader.readLine()) != null) {
            String[] inputSplit = input.split(",");

            //String address = inputSplit[0].trim();
            double latitude = Double.parseDouble(inputSplit[0].trim());
            double longitude = Double.parseDouble((inputSplit[1].trim()));

            String geocodedAddress = getAddressFromCoordinates(this, latitude, longitude);

            ContentValues contentValues = new ContentValues();
            //contentValues.put("address", geocodedAddress);
            contentValues.put("latitude", latitude);
            contentValues.put("longitude", longitude);

            long newRowId = db.insert("Location", null, contentValues);
        }
        db.close();
        bufferedReader.close();
    }

    public void onSearchButtonClick(View view) {
        String userEnteredAddress = addressInput.getText().toString().trim();

        if (!userEnteredAddress.isEmpty()) {
            searchForAddress(userEnteredAddress);
        } else {
            Toast.makeText(this, "Please enter an address.", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchForAddress(String address) {
        Cursor cursor = sqLiteManager.getLatLongForAddress(address);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            Log.d("LatLng", "Latitude: " + latitude + ", Longitude: " + longitude);
        } else {
            Log.d("LatLng", "Address not found in database.");
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void displayDatabaseValues() {
        SQLiteDatabase db = sqLiteManager.getReadableDatabase();
        Cursor cursor = db.query("Location", null, null, null, null, null, null);

        List<String> databaseValues = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex("address"));
                @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

                String entry = "Address: " + address + ", Latitude: " + latitude + ", Longitude: " + longitude;
                databaseValues.add(entry);
            } while (cursor.moveToNext());

            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databaseValues);
        listView.setAdapter(adapter);
    }
}