package com.example.mechmate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int SMS_PERMISSION_REQUEST_CODE = 2;
    private FusedLocationProviderClient fusedLocationClient;
    private Button raiseRequest;
    private EditText locationField;
    public String phone;
    Button historyBtn;
    private UserData userData;
   ArrayList<Requests> req;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        raiseRequest = findViewById(R.id.raiseRequest);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        Log.d("HomeActivity", "Received phone number: " + phone);
        userData = new UserData(this);



        raiseRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRequestDialog();

            }
        });
        historyBtn=findViewById(R.id.historyBtn);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRequests();
            }
        });
    }

    private void openRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.request_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText vehicleType = dialogView.findViewById(R.id.vehicleType);
        EditText query = dialogView.findViewById(R.id.query);
        locationField = dialogView.findViewById(R.id.location);
        Button sendRequestButton = dialogView.findViewById(R.id.sendRequestButton);

        // Fetch location when dialog is opened
        fetchLocation();

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vehicle = vehicleType.getText().toString();
                String queryText = query.getText().toString();
                String locationText = locationField.getText().toString();

                if (!vehicle.isEmpty() && !queryText.isEmpty() && !locationText.isEmpty()) {
                    sendRequestSMS(vehicle, queryText, locationText);
                    dialog.dismiss();
                } else {
                    Toast.makeText(HomeActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void fetchLocation() {
        // Check if the user has granted permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // If permission is granted, fetch the current location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Convert latitude and longitude to a location name
                                String locationName = getLocationName(location.getLatitude(), location.getLongitude());
                                locationField.setText(locationName);
                            } else {
                                locationField.setText("Unable to fetch location");
                            }
                        }
                    });
        }
    }

    private String getLocationName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String locationName = "Location not found";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                // Combine different parts of the address into a readable string
                locationName = address.getLocality() + ", " + address.getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return locationName;
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, fetch the location
                fetchLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // SMS permission granted, call sendRequestSMS method if needed
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendRequestSMS(String vehicle, String query, String location) {
        // Check if SMS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        } else {
            sendSMS(vehicle, query, location);
        }
    }

    private void sendSMS(String vehicle, String query, String location) {
        // Example mechanic phone numbers
        String[] mechanicNumbers = {"9014056134", "9494423180"};

        String message = "Request: from " + phone + "\nVehicle: " + vehicle + "\nQuery: " + query + "\nLocation: " + location;

        SmsManager smsManager = SmsManager.getDefault();

        try {
            for (String number : mechanicNumbers) {
                smsManager.sendTextMessage(number, null, message, null, null);
            }
            Toast.makeText(this, "Request sent to mechanics", Toast.LENGTH_LONG).show();
            saveRequestToDatabase(vehicle, query, location);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void saveRequestToDatabase(String vehicle, String query, String location) {
        // Get the current date and time
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Insert the request into the database
        userData.insertRequest(phone, vehicle, query, location, currentDateTime);
        Toast.makeText(this, "Request saved to database", Toast.LENGTH_SHORT).show();
    }
    private void fetchRequests() {
        Log.d("HomeActivity", "fetchRequests() called");
        req = userData.fetchRequests(phone);

        // Check if requests are successfully fetched
        if (req != null && !req.isEmpty()) {
            Log.d("HomeActivity", "Requests fetched: " + req.size());
            // Create an Intent to start HistoryActivity
            Intent historyIntent = new Intent(HomeActivity.this, HistoryActivity.class);
            historyIntent.putExtra("requestHistory", req); // Ensure Requests class implements Serializable
            startActivity(historyIntent);
        } else {
            Log.d("HomeActivity", "No requests found.");
            Toast.makeText(this, "No requests found.", Toast.LENGTH_SHORT).show();
        }
    }



}
