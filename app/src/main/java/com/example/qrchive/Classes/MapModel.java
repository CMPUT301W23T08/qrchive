package com.example.qrchive.Classes;

import com.firebase.geofire.GeoLocation;

public class MapModel {

    private final double radius = 1000; // 1000 m radius

    public void getNearbyQRCodes(double latitude, double longitude) {

        // TODO: Make firebase Query for point nearby (latitude, longitude) given radius
        GeoLocation origin = new GeoLocation(latitude, longitude);
    }
}
