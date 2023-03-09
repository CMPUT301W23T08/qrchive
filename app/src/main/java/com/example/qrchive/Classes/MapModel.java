package com.example.qrchive.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.qrchive.R;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapModel {

    private final double radius = 0.03;
    private List<ScannedCode> nearByCodes;
    private GeoPoint center;
    private FirebaseFirestore db;
    private GeoFirestore geoFirestore;
    private double latitude;
    private double longitude;

    public MapModel(double latitude, double longitude) {

        // initialize members
        nearByCodes = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void getNearbyQRCodes() {

        geoFirestore = new GeoFirestore(db.collection("ScannedCodes"));
        GeoPoint currentLocation = new GeoPoint(this.latitude, this.longitude);
        GeoQuery geoQuery = geoFirestore.queryAtLocation(currentLocation, this.radius);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoPoint location) {
                // Document with key entered the search radius
                // Add code to retrieve the document from Firestore
            }

            @Override
            public void onKeyExited(String key) {
                // Document with key exited the search radius
            }

            @Override
            public void onKeyMoved(String key, GeoPoint location) {
                // Document with key moved within the search radius
            }

            @Override
            public void onGeoQueryReady() {
                // All documents within the search radius have been loaded
            }

            @Override
            public void onGeoQueryError(Exception error) {
                // Handle error
            }
        });
    }
}
