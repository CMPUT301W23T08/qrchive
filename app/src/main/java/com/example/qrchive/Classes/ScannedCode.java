package com.example.qrchive.Classes;
import java.util.Random;

public class ScannedCode {
    private String scannedCodeDID;
    private String codeDID;
    private String date;
    private String location;
    private String userDID;
    private int points;
    private String name;

    public ScannedCode(String scannedCodeDID, String codeDID, String date, String location, String userDID) {
        Random random = new Random();
        this.scannedCodeDID = scannedCodeDID;
        this.codeDID = codeDID;
        this.date = date;
        this.location = location;
        this.userDID = userDID;
        this.name = Character.toString("abcdefghijklmnopqrstuvwxyz".charAt(random.nextInt(26))); //todo: for now, returns a char bw a-z
        this.points = random.nextInt(100) + 1; //todo: for now generating random points between 1 and 100
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

    public int getPoints() {
        return points;
    }
    public String getName() {
        return name;
    }
}
