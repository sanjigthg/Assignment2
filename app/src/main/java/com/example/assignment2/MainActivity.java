package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private SQLiteManager sqLiteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqLiteManager = new SQLiteManager(this);

        try {
            readAndInsertDataFromAssets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readAndInsertDataFromAssets () throws IOException {
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("coordinates.txt");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        SQLiteDatabase db = sqLiteManager.getWritableDatabase();

        String input;
        while ((input = bufferedReader.readLine()) != null) {
            String[] inputSplit = input.split(",");
            double latitude = Double.parseDouble(inputSplit[0].trim());
            double longitude = Double.parseDouble((inputSplit[1].trim()));

            ContentValues contentValues = new ContentValues();
            contentValues.put("latitude", latitude);
            contentValues.put("longitude", longitude);

            long newRowId = db.insert("Location", null, contentValues);
        }
        db.close();
        bufferedReader.close();
    }
}