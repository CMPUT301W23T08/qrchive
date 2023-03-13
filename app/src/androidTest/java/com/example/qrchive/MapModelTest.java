package com.example.qrchive;


import org.imperiumlabs.geofirestore.GeoFirestore;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.qrchive.Classes.MapModel;
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.Classes.onCodesGeoQueriedListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;


import org.junit.Test;
import static org.junit.Assert.*;

public class MapModelTest {

    @Test
    public void testGetNearbyQRCodes() {
        MapModel mapModel = new MapModel(0, 0);
        assertTrue(mapModel.getNearbyQRCodes().isEmpty());
    }

    @Test
    public void testSetNearbyQRCodes() {
        MapModel mapModel = new MapModel(0, 0);
        mapModel.setNearbyQRCodes(new onCodesGeoQueriedListener() {
            @Override
            public void onCodesGeoQueried(ScannedCode scannedCode) {
                // Test that callback is called when a nearby code is found
                assertFalse(mapModel.getNearbyQRCodes().isEmpty());
            }
        });
    }
}

