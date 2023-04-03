package com.example.qrchive;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.qrchive.Classes.GalleryListItem;
import com.example.qrchive.Classes.ScannedCode;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;


/**
 * Test class for the GalleryListItem class.
 * It contains tests for setting and getting the image and QR code associated with a GalleryListItem object.
 */
public class GalleryListItemTest {

    private GalleryListItem galleryListItem;
    private Bitmap testBitmap;
    private ScannedCode testQr;

    /**
     * Set up GalleryListItem instance, a Bitmap and a ScannedCode for testing.
     */
    @Before
    public void setUp() {
        galleryListItem = new GalleryListItem();
        testBitmap = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
        testQr = new ScannedCode("BFG5DGW54", new Date(), new GeoPoint(0,0), "locImg", "user", "scannedCodeDID");
    }

    /**
     * Test setting and getting the image associated with GalleryListItem.
     */
    @Test
    public void testSetAndGetImage() {
        galleryListItem.setImage(testBitmap);
        Bitmap resultBitmap = galleryListItem.getImage();
        assertNotNull(resultBitmap);
        assertEquals(testBitmap, resultBitmap);
    }


    /**
     * Test setting and getting the scanned code associated with GalleryListItem.
     */
    @Test
    public void testSetAndGetQr() {
        galleryListItem.setQr(testQr);
        ScannedCode resultQr = galleryListItem.getQr();
        assertNotNull(resultQr);
        assertEquals(testQr, resultQr);
    }
}

