package com.example.mechmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RequestsAdapter extends ArrayAdapter<Requests> {
    public RequestsAdapter(Context context, ArrayList<Requests> requests) {
        super(context, 0, requests);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Requests request = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_request, parent, false);
        }

        TextView vehicleTextView = convertView.findViewById(R.id.vehicleTextView);
        TextView queryTextView = convertView.findViewById(R.id.queryTextView);
        TextView locationTextView = convertView.findViewById(R.id.locationTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusTextView); // Assuming status is part of the Requests object

        vehicleTextView.setText(request.vehicle);
        queryTextView.setText(request.query);
        locationTextView.setText(request.location);

        statusTextView.setText(request.status); // Set the status here

        return convertView;
    }
}
