package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqLiteManager = new SQLiteManager(this);
        addressInput = findViewById(R.id.addressInput);
        listView = findViewById(R.id.database_values_list);
        textView = findViewById(R.id.locationResult);

        try {
            if (sqLiteManager.getTableCount() == 0) {
                readAndInsertDataFromAssets();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        displayDatabaseValues();
    }
    // Geocoder functionality in order to convert the latitude and longitude pairs
    // which provides the equivalent address value thats returned back into the
    // readAndInsertDataFromAssets in order to insert the required values.
    private String getAddressFromCoordinates (Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String addressValue = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    stringBuilder.append(address.getAddressLine(i)).append(", ");
                }
                addressValue = stringBuilder.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressValue;
    }
    // Function in order to obtain values from the coordinates.txt file
    // Converts the contents of the file using the inputstream and splitting the data
    // into the seperate variables, latitude and longitude which are placed into the DB.
    private void readAndInsertDataFromAssets () throws IOException {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("coordinates.txt");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            SQLiteDatabase db = sqLiteManager.getWritableDatabase();

            String input;
            while ((input = bufferedReader.readLine()) != null) {
                String[] inputSplit = input.split(",");
                if (inputSplit.length >= 2) {
                    double latitude = Double.parseDouble(inputSplit[0].trim());
                    double longitude = Double.parseDouble((inputSplit[1].trim()));

                    String geocodedAddress = getAddressFromCoordinates(this, latitude, longitude);
                    String[] geocodedAddressNeed = geocodedAddress.split(",");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("address", geocodedAddressNeed[0]);
                    contentValues.put("latitude", latitude);
                    contentValues.put("longitude", longitude);

                    db.insert("Location", null, contentValues);
                }
            }
            db.close();
            bufferedReader.close();
        }
    // Function designed to search the database for the entered value from the user
    // Error checks if the address is in the database or not, prompts with a message
    // upon one of the conditions that are met.
    @SuppressLint("SetTextI18n")
    public void onSearchButtonClick(View view) {
        String userEnteredAddress = addressInput.getText().toString().trim();
        if (!userEnteredAddress.isEmpty()) {
            Cursor cursor = sqLiteManager.getLatLongForAddress(userEnteredAddress);

            if (cursor != null && cursor.moveToFirst()) {
                 double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                 double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

                String result = "Latitude: " + latitude + " Longitude: " + longitude;
                textView.setText(result);
            } else {
                textView.setText("Address not found in database.");
            }
            if (cursor != null) {
                cursor.close();
            }
        } else {
            Toast.makeText(this, "Please enter an address.", Toast.LENGTH_SHORT).show();
        }
        displayDatabaseValues();
    }

    // This function displays the database values at all times, and is used in the other functions
    // to update the immediate changes that are being done (add, delete or update)
    // It is formatted using the Adapter class and the listview layout to display the values accordingly
    private void displayDatabaseValues() {
        SQLiteDatabase db = sqLiteManager.getReadableDatabase();
        Cursor cursor = db.query("Location", null, null, null, null, null, null);

        List<String> databaseValues = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndex("address"));
                double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

                String entry = "Address: " + address + ", Latitude: " + latitude + ", Longitude: " + longitude;
                databaseValues.add(entry);
            } while (cursor.moveToNext());

            cursor.close();
        }
        LocationAdapter locationAdapter = new LocationAdapter(this, databaseValues);
        listView.setAdapter(locationAdapter);
    }

    // Allows the user to enter the address they want to input into the database, with base values
    // of 0 and 0 for the latitude and longitude.
    // Once the address has been added, the user can continue with the other functionality.
    public void onAddAddressButtonClick(View view) {
        String userEnteredAddress = addressInput.getText().toString().trim();

        if (!userEnteredAddress.isEmpty()) {

            long newRowId = sqLiteManager.addAddress(userEnteredAddress);
            String result = "Address has been added into database.";
            if (newRowId != -1) {
                Toast.makeText(this, "Address added with ID: " + newRowId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add address.", Toast.LENGTH_SHORT).show();
            }
            textView.setText(result);
        } else {
            Toast.makeText(this, "Please enter an address.", Toast.LENGTH_SHORT).show();
        }
        displayDatabaseValues();
    }

    // Deletes the requested address that user puts into the EditText line. Confirmation is given upon
    // any of the if/else statements.
    // In order to reflect the change right away, the database display is updated right away after this function
    // is called.
    public void onDeleteButtonClick(View view) {
        String userEnteredDeleteAddress = addressInput.getText().toString().trim();

        if (Geocoder.isPresent()) {
            int deletedRows = sqLiteManager.deleteAddressByUserInput(userEnteredDeleteAddress);

            if (deletedRows > 0) {
                Toast.makeText(this, "Address deleted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete address.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter an address to delete.", Toast.LENGTH_SHORT).show();
        }
        displayDatabaseValues();
    }

    // Updates the address value to obtain the latitude and longitude values using forward geocoding
    // Upon successful update, you can use the Get Address button to look at the new latitude and longitude
    public void onUpdateButtonClick(View view) {
        String userEnteredUpdateAddress = addressInput.getText().toString().trim();

        if (Geocoder.isPresent()) {
            double latitude = 0.0;
            double longitude = 0.0;
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(userEnteredUpdateAddress, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    latitude = addresses.get(0).getLatitude();
                    longitude = addresses.get(0).getLongitude();

                    int updatedRows = sqLiteManager.updateAddress(userEnteredUpdateAddress, latitude, longitude);

                    if (updatedRows > 0) {
                        Toast.makeText(this, "Address updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update address.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Could not find coordinates for the updated address.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Please enter an address to update.", Toast.LENGTH_SHORT).show();
        }
        displayDatabaseValues();
    }
}