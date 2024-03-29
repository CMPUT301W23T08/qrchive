package com.example.qrchive;

import static org.junit.jupiter.api.Assertions.*;

import com.example.qrchive.Classes.ScannedCode;
import com.google.firebase.firestore.GeoPoint;

import org.junit.jupiter.api.Test;

import java.util.Date;

public class ScannedCodeUnitTest {
    private ScannedCode getMockCode(){
        // Note that the hash in Java takes out the new line character
        ScannedCode mockCode = new ScannedCode("BFG5DGW54", new Date(), new GeoPoint(0,0), "locImg", "user", "scannedCodeDID");
        // Mock hash is 8227ad036b504e39fe29393ce170908be2b1ea636554488fa86de5d9d6cd2c32
        // it has four zeroes, 22, 44, 55, 88 which is 4 + 2 + 4 + 5 + 8 = 23 score
        // the first 6 bits are b000010
        return mockCode;
    }

    @Test
    public void testGetScore() {
        ScannedCode code = getMockCode();
        assertEquals(23, code.getPoints());
    }

    @Test
    public void testGetName() {
        ScannedCode code = getMockCode();

        String expected = "cool GloMoMegaSpectralCrab";

        assertEquals(expected, code.getName());
    }

    @Test
    public void testGetAscii() {
        ScannedCode code = getMockCode();
        assertEquals(code.getPoints(), 23);
    }
}
