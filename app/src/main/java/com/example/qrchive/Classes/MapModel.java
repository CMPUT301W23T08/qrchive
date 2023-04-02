package com.example.qrchive.Classes;

import static android.service.controls.ControlsProviderService.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;

import java.util.Map;

/** Class: Map Model:
 *  Author: Justin Meimar
 *
 *  Provides support to the MapsFragment through a GeoQuery interface.
 *  GeoQueries are used in two instances: 1) draw markers on the map, 2) retrieve a list of codes within a specifies radius.
 *  The base query provides the abstracted query for both. The MapsFragment implements the callback functions from
 *  the GeoQueryListener interface to response any time a code has been queried.
 * */
public class MapModel {

    private FirebaseFirestore db;
    private GeoFirestore geoFirestore;

    public MapModel() {
        db = FirebaseFirestore.getInstance();
    }

    /** @Method: setMapGeoQuery
     *  @Param: latitude, longitude, radius: Configure the parameters of the query
     *  @Param: callback: Instance of MapFragment which implements the callback for drawing a map marker.
     *    called when the map is loaded and triggers the call back to draw a map marker at a particular location
     * */
    public void setMapGeoQuery(double latitude, double longitude, double radius, GeoQueryListener callback ) {
        GeoPoint currentLocation = new GeoPoint(latitude, longitude);
        baseGeoQuery(currentLocation, radius, callback, GeoQueryType.MAP_QUERY);
    }

    /** @Method: searchGeoQuery
     *  @Param: latitude, longitude, radius: Configure the parameters of the query
     *  @Param: callback: Instance of MapsFragment which implements the callback for adding the queried code
     *    to a scrollable listAdapter which displays the codes in the given radius.
     * */
    public void searchGeoQuery(double latitude, double longitude, double radius, GeoQueryListener callback) {
        GeoPoint currentLocation = new GeoPoint(latitude, longitude);
        baseGeoQuery(currentLocation, radius, callback, GeoQueryType.SEARCH_QUERY);
    }

    /** @Method: baseGeoQuery
     * @Param: All parameters are passed trivially by @setMapGeoQuery or @searchGeoQuery
     * @Param: queryType passed to direct the callback used.
     * Provides a general geoQuery for which specific functionality is determined by the queryType Enum paramter
     * */
    public void baseGeoQuery(GeoPoint currentLocation, double radius, GeoQueryListener callback, GeoQueryType queryType) {

        geoFirestore = new GeoFirestore(db.collection("ScannedCodes"));
        db.collection("ScannedCodes")
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
        GeoQuery geoQuery = geoFirestore.queryAtLocation(currentLocation, radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoPoint location) {
                // Add code to retrieve the document from Firestore
                db.collection("ScannedCodes")
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
                                            ScannedCode scannedCode = new ScannedCode(
                                                    document.get("hash").toString(),
                                                    Integer.parseInt(docData.get("hashVal").toString()),
                                                    document.getTimestamp("date").toDate(),
                                                    (GeoPoint) docData.get("location"),
                                                    true,
                                                    "placeholder_img.png",
                                                    docData.get("userDID").toString(),
                                                    document.getId());

                                            // Trigger the appropriate callback.
                                            switch (queryType) {
                                                case SEARCH_QUERY:
                                                    callback.onCodeGeoSearched(scannedCode);
                                                    break;
                                                case MAP_QUERY:
                                                    callback.onCodeGeoQueried(scannedCode);
                                                    break;
                                            }
                                        } else { Log.d(TAG, "document is null"); }
                                    }
                                } else { Log.d(TAG, "task not successful"); }
                            }
                        });
            }
            @Override
            public void onGeoQueryReady() {}
            @Override
            public void onKeyExited(String key) {}
            @Override
            public void onGeoQueryError(@NonNull Exception e) {}
            @Override
            public void onKeyMoved(String key, GeoPoint location) {}
        });
    }
}