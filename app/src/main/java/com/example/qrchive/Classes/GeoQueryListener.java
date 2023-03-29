package com.example.qrchive.Classes;

import java.util.List;

public interface onCodesGeoQueriedListener {

    /** Draw Marker on Map when the QR query hits
    */
    public void onCodesGeoQueried(ScannedCode code);

    /** Add ScannedCode to list when query hits.
     */
    public void onCodeGeoSearched(ScannedCode code);
}
