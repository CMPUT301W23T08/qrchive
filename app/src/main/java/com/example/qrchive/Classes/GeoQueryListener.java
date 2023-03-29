package com.example.qrchive.Classes;

public interface GeoQueryListener {

    /** Draw Marker on Map when the QR query hits
    */
    public void onCodeGeoQueried(ScannedCode code);

    /** Add ScannedCode to list when query hits.
     */
    public void onCodeGeoSearched(ScannedCode code);
}
