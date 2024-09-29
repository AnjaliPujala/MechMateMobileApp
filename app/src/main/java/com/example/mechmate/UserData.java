package com.example.mechmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserData extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_data.db";
    private static final int DATABASE_VERSION = 3; // Increment this version for updates

    // Table for user details
    private static final String TABLE_USERS = "userDetails";

    // Table for requests
    private static final String TABLE_REQUESTS = "userRequests";

    // Columns for user details
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone"; // Make sure the column name is "phone"
    public static final String COLUMN_PASSWORD = "password";

    // Columns for user requests
    public static final String COLUMN_VEHICLE = "vehicle";
    public static final String COLUMN_QUERY = "query";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_ID = "id"; // For primary key in requests
    public static final String COLUMN_STATUS="status";
    public UserData(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseCreation", "Creating userDetails table");
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PHONE + " TEXT PRIMARY KEY, " + // The "phone" column is created here
                COLUMN_PASSWORD + " TEXT)");

        Log.d("DatabaseCreation", "Creating userRequests table");
        db.execSQL("CREATE TABLE " + TABLE_REQUESTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Auto-increment ID
                COLUMN_PHONE + " TEXT, " + // Foreign key referencing users
                COLUMN_VEHICLE + " TEXT, " +
                COLUMN_QUERY + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_DATETIME + " TEXT, " +
                COLUMN_STATUS + " TEXT DEFAULT 'Pending'," +
                "FOREIGN KEY (" + COLUMN_PHONE + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_PHONE + "))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseUpgrade", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        onCreate(db);
    }

    // Insert user data
    public long insertData(String name, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_PASSWORD, password);


        long result = -1;
        try {
            result = db.insert(TABLE_USERS, null, values);
        } catch (Exception e) {
            Log.e("DatabaseError", "Error inserting data: " + e.getMessage());
        } finally {
            db.close();
        }

        Log.d("InsertResult", "Insert result: " + result);
        return result;
    }

    // Check login details
    public boolean checkLoginDetails(String phone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_PHONE}; // You can select any column, phone is sufficient
        String selection = COLUMN_PHONE + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {phone, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    // Insert a request for a user
    public void insertRequest(String phone, String vehicle, String query, String location, String datetime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, phone); // Foreign key
        values.put(COLUMN_VEHICLE, vehicle);
        values.put(COLUMN_QUERY, query);
        values.put(COLUMN_LOCATION, location);
        values.put(COLUMN_DATETIME, datetime);

        long result = db.insert(TABLE_REQUESTS, null, values);
        Log.d("InsertRequestResult", "Insert request result: " + result);
        db.close();
    }

    // Fetch user requests based on phone number
    public ArrayList<Requests> fetchRequests(String phone) {
        ArrayList<Requests> requestsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_REQUESTS + " WHERE " + COLUMN_PHONE + " = ?", new String[]{phone});

        while (cur.moveToNext()) {
            Requests requests = new Requests();
            requests.vehicle = cur.getString(cur.getColumnIndex(COLUMN_VEHICLE));
            requests.query = cur.getString(cur.getColumnIndex(COLUMN_QUERY));
            requests.location = cur.getString(cur.getColumnIndex(COLUMN_LOCATION));
            requests.status=cur.getString(cur.getColumnIndex(COLUMN_STATUS));
            requestsList.add(requests);
        }
        cur.close();
        db.close();
        return requestsList;
    }
}
