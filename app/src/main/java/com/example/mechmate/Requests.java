package com.example.mechmate;

import java.io.Serializable;

public class Requests implements Serializable {

    public String vehicle;
    public String query;
    public String location;
    public String status; // Make sure to have the status field

    // Constructor
    public Requests() {
        this.vehicle = vehicle;
        this.query = query;
        this.location = location;
        this.status = status;
    }
}
