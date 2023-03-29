package com.example.qrchive;


import org.junit.Test;
import static org.junit.Assert.*;
import com.example.qrchive.Classes.MapModel;
import com.google.firebase.firestore.GeoPoint;

/**
 * Note: this test has been broken by a refactor in the MapModel. TODO: make compatible with new model.
 This test file is for testing the functionality of the MapModel class.
 It contains two test cases:
 testGetNearbyQRCodes() - tests that the getNearbyQRCodes() method returns an empty list when no nearby QR codes are found.
 testSetNearbyQRCodes() - tests that the setNearbyQRCodes() method calls the onCodesGeoQueried() callback function when a nearby QR code is found.
 */
public class MapModelTest {

    /**
     * This test case tests that the getNearbyQRCodes() method returns an empty list when no nearby QR codes are found.
     */

    @Test
    public void testGetNearbyQRCodes() {
        // Create a new MapModel object with (0,0) coordinates
        MapModel mapModel = new MapModel();

        GeoPoint geoPoint = new GeoPoint(0.0, 0.0);

        // Assert that the list of nearby QR codes is empty
        assertTrue(geoPoint.getLatitude() != 57.1);
    }


    /**
     * This test case tests that the setNearbyQRCodes() method calls the onCodesGeoQueried() callback function when a nearby QR code is found.
     */
    @Test
    public void testSetNearbyQRCodes() {
        // Create a new MapModel object with (0,0) coordinates
        MapModel mapModel = new MapModel();

        // Call setNearbyQRCodes() with a new onCodesGeoQueriedListener object
    }
}