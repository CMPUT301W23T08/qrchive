package com.example.qrchive.Classes;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
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

import kotlin.Triple;

public class MapModel {

    private final double radius = 200;
    private GeoPoint center;
    private FirebaseFirestore db;
    private List<ScannedCode> nearByCodes;
    private GeoFirestore geoFirestore;
    private double latitude;
    private double longitude;

    public MapModel(double latitude, double longitude) {

        // initialize members
        this.longitude = longitude;
        this.latitude = latitude;
        nearByCodes = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    public List<ScannedCode> getNearbyQRCodes() {
        return nearByCodes;
    }

    public void setNearbyQRCodes(onCodesGeoQueriedListener callback) {


        geoFirestore = new GeoFirestore(db.collection("ScannedCodesTest"));
        GeoPoint currentLocation = new GeoPoint(this.latitude, this.longitude);

        // Geo Query requires the fields g: hashed location and l: GeoPoint representing location.
        // there is a way to rename the fields with settings, so that we could use
        db.collection("ScannedCodesTest")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            GeoPoint scannedLocation = document.getGeoPoint("location");
                            geoFirestore.setLocation(document.getId(), scannedLocation);
                        }
                    }
                });

        // Make a GeoQuery for documents with location within radius of current location.
        GeoQuery geoQuery = geoFirestore.queryAtLocation(currentLocation, this.radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onGeoQueryReady() {
                // All documents within the search radius have been loaded
                Log.d("======= ON GEOQUERY READY", nearByCodes.toString());
                callback.onCodesGeoQueried(nearByCodes);
            }

            @Override
            public void onGeoQueryError(@NonNull Exception e) {
                // Handle error
            }

            @Override
            public void onKeyEntered(String key, GeoPoint location) {
                Log.d("======= ON KEY ENTER", "==================");

                // Add code to retrieve the document from Firestore
                db.collection("ScannedCodesTest")
                        .document(key)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        Map<String, Object> docData = document.getData();
                                        if (docData != null) {
                                            GeoPoint codeLocation = (GeoPoint) docData.get("location");
                                            ScannedCode scannedCode = new ScannedCode(
                                                    document.getId(),
                                                    docData.get("date").toString(),
                                                    (GeoPoint) docData.get("location"),
                                                    "placeholder_img.png",
                                                    docData.get("userDID").toString()
                                            );
                                            nearByCodes.add(scannedCode);
                                            Log.d("================= Found the code", docData.toString());
                                        } else { Log.d("================= Code is NUILL", ":("); }
                                    }
                                } else { Log.d("================= Task Unsuccessful?", ":("); }
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