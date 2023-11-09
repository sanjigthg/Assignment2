package com.example.assignment2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class LocationAdapter extends ArrayAdapter<String> {

    public LocationAdapter(@NonNull Context context, List<String> values) {
        super(context, R.layout.listview_layout, values);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        @SuppressLint("ViewHolder") View listItemView = inflater.inflate(R.layout.listview_layout, parent, false);

        String entry = getItem(position);

        TextView addressTextView = listItemView.findViewById(R.id.address_textview);
        TextView latLngTextView = listItemView.findViewById(R.id.latitude_longitude_textview);

        if (entry != null) {
            String[] inputSplit = entry.split(", ");
            if (inputSplit.length >= 4) {
                String address = inputSplit[0];
                String latLng = inputSplit[4] + " " + inputSplit[5];
                addressTextView.setText(address);
                latLngTextView.setText(latLng);
            }
        }
        return listItemView;
    }
}
