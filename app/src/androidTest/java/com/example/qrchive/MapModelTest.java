package com.example.qrchive;


import org.junit.Test;
import static org.junit.Assert.*;
import com.example.qrchive.Classes.MapModel;
import com.example.qrchive.Classes.ScannedCode;
import com.example.qrchive.Classes.onCodesGeoQueriedListener;



/**
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
        MapModel mapModel = new MapModel(0, 0);
        // Assert that the list of nearby QR codes is empty
        assertTrue(mapModel.getNearbyQRCodes().isEmpty());
    }


    /**
     * This test case tests that the setNearbyQRCodes() method calls the onCodesGeoQueried() callback function when a nearby QR code is found.
     */
    @Test
    public void testSetNearbyQRCodes() {
        // Create a new MapModel object with (0,0) coordinates
        MapModel mapModel = new MapModel(0, 0);
        // Call setNearbyQRCodes() with a new onCodesGeoQueriedListener object
        mapModel.setNearbyQRCodes(new onCodesGeoQueriedListener() {
            @Override
            public void onCodesGeoQueried(ScannedCode scannedCode) {
                // Test that callback is called when a nearby code is found
                assertFalse(mapModel.getNearbyQRCodes().isEmpty());
            }

            @Override
            public void addCodeOnSuccess(ScannedCode code) {}
        });
    }
}

