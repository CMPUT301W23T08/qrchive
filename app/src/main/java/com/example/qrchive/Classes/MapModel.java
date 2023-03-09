package com.example.qrchive.Classes;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;
import org.w3c.dom.Document;

import java.util.ArrayList;
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
        Log.d("CALL GET NEAT BY QR CODES", "==================");

        db.collection("ScannedCodes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                if (data.get("location") != null) {
                                    Log.d("QR Code", data.get("location").toString());
                                } else {
                                    Log.d("QR Code", "data.get(Code) == null");
                                }

                            }
                        } else {

                        }
                    }
                });

        geoFirestore = new GeoFirestore(db.collection("ScannedCodes"));
        GeoPoint currentLocation = new GeoPoint(this.latitude, this.longitude);
        GeoQuery geoQuery = geoFirestore.queryAtLocation(currentLocation, this.radius);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onGeoQueryReady() {
                // All documents within the search radius have been loaded

            }

            @Override
            public void onGeoQueryError(@NonNull Exception e) {
                // Handle error
            }

            @Override
            public void onKeyEntered(String key, GeoPoint location) {
                // Document with key entered the search radius
                // Add code to retrieve the document from Firestore
                db.collection("ScannedCodes")
                        .document(key)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("GOOD --- GOOD", "==================");
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        Map<String, Object> docData = document.getData();
                                        docData.get("location");
                                    }
                                } else {
                                    Log.d("NO GOOD --- HERE", "==================");
                                }
                            }
                        });
            }

            @Override
            public void onKeyExited(String key) {
                // Document with key exited the search radius

                Log.d("CODE NO MATCH HERE", "==================");
            }

            @Override
            public void onKeyMoved(String key, GeoPoint location) {
                // Document with key moved within the search radius
            }
        });
    }
}