package com.example.qrchive;

public class ScannedCode {
    public String getDID() {
        return DID;
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

    private String DID;
    private String codeDID;
    private String date;
    private String location;
    private String userDID;

    public ScannedCode(String DID, String codeDID, String date, String location, String userDID) {
        this.DID = DID;
        this.codeDID = codeDID;
        this.date = date;
        this.location = location;
        this.userDID = userDID;
    }
}
