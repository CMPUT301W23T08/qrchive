package com.example.qrchive;

public class ScannedCode {
    private String scannedCodeDID;
    private String codeDID;
    private String date;
    private String location;
    private String userDID;

    public ScannedCode(String scannedCodeDID, String codeDID, String date, String location, String userDID) {
        this.scannedCodeDID = scannedCodeDID;
        this.codeDID = codeDID;
        this.date = date;
        this.location = location;
        this.userDID = userDID;
    }

    public String getScannedCodeDID() {
        return scannedCodeDID;
    }

    public String getCodeDID() {
        return codeDID;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getUserDID() {
        return userDID;
    }
}
