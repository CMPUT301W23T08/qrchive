package com.example.qrchive.Classes;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.GeoPoint;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

/**
 * ScannedCode represents a code that has been captured and stores all the required metadata to operate
 * on such a code.
 *
 * @author Shelly & Grayden
 * @version 1.0
 */
public class ScannedCode {
    private String hash;
    private int hashVal;
    private String date;
    private boolean hasLocation;
    private GeoPoint location;
    private String locationImage;
    private String userDID;
    private int points;
    private String name;
    private String ascii;
    private String scannedCodeDID; // Document ID on firestore corresponding to this scannedCode


    /**
     * This is a constructor for ScannedCode which uses code and assumes location should not be used.
     *
     * @param code is the value of the QR code.
     * @param date is the date as a string.
     * @param locationImage is a string representing the location image.
     * @param userDID is the users ID.
     * @param scannedCodeDID is the scanned codes ID.
     */
    public ScannedCode(String code, String date, String locationImage, String userDID, String scannedCodeDID) {
        this(Hashing.sha256().hashString(code, StandardCharsets.UTF_8).toString(),
                Hashing.sha256().hashString(code, StandardCharsets.UTF_8).asInt(),
                date, new GeoPoint(0,0), false, locationImage, userDID, scannedCodeDID);
        // even though i pass (0,0) for geopoint, it SHOULD NOT BE USED as hasLocation is false
    }

    /**
     * This is a constructor for ScannedCode which uses code and assumes location should be used.
     *
     * @param code is the value of the QR code.
     * @param date is the date as a string.
     * @param location is the location stored as a GeoPoint.
     * @param locationImage is a string representing the location image.
     * @param userDID is the users ID.
     * @param scannedCodeDID is the scanned codes ID.
     */
    public ScannedCode(String code, String date, GeoPoint location, String locationImage, String userDID, String scannedCodeDID) {
        this(Hashing.sha256().hashString(code, StandardCharsets.UTF_8).toString(),
                Hashing.sha256().hashString(code, StandardCharsets.UTF_8).asInt(),
                date, location, true, locationImage, userDID, scannedCodeDID);
    }

    /**
     * This is a constructor for ScannedCode which uses hash.
     *
     * @param hash the hashed QR code.
     * @param hashVal the numerical value of the first 4 bytes of the hashed QR code.
     * @param date is the date as a string.
     * @param location is the location stored as a GeoPoint.
     * @param hasLocation is a boolean representing whether location should be used or not.
     * @param locationImage is a string representing the location image.
     * @param userDID is the users ID.
     * @param scannedCodeDID is the scanned codes ID.
     */
    // Base Constructor
    public ScannedCode(String hash, int hashVal, String date, GeoPoint location, boolean hasLocation, String locationImage, String userDID, String scannedCodeDID) {
        this.hash = hash;
        this.hashVal = hashVal;
        this.date = date;
        this.hasLocation = hasLocation;
        this.location = location;
        this.locationImage = locationImage;
        this.userDID = userDID;
        this.scannedCodeDID = scannedCodeDID;

        // Calculating points
        {
            int points = 0;
            char previous = '\0';
            int chainLength = 1;
            for(int i = 0; i < hash.length(); ++i) {
                if(hash.charAt(i) == previous){
                    chainLength += 1;
                }else{
                    // Add certain amount to points
                    if(chainLength > 1 || previous == '0'){
                        // if previous is 0 with 1 chain we still add 1
                        int base = 1;
                        if('0' <= previous && previous <= '9'){
                            base = previous - '0';
                        }else if('a' <= previous && previous <= 'f'){
                            base = previous - 'a' + 10;
                        }

                        points += (int)Math.pow(base, chainLength-1);
                    }

                    // Set variables to look for the next chain
                    previous = hash.charAt(i);
                    chainLength = 1;
                }
            }
            this.points = points;
        }

        // Calculating name
        {
            StringBuilder name = new StringBuilder();
            Hashtable<Integer, String> bitToString = new Hashtable<Integer, String>() {{
                put(0, "cool ");
                put(1, "hot ");
                put(2, "Fro");
                put(3, "Glo");
                put(4, "Mo");
                put(5, "Lo");
                put(6, "Mega");
                put(7, "Ultra");
                put(8, "Spectral");
                put(9, "Sonic");
                put(10, "Crab");
                put(11, "Shark");
            }};

            for(int i = 0; i < 6; ++i){
                if((hashVal & (1 << i)) != 0){
                    name.append(bitToString.get(i*2 + 1));
                }else{
                    name.append(bitToString.get(i*2));
                }
            }
            this.name = name.toString();
        }

        // Calculating ASCII
        {
            StringBuilder asciiRound = new StringBuilder();
            StringBuilder asciiSquare = new StringBuilder();
            asciiRound.append("          , - ~ ~ ~ - ,\n" +      // forehead
                    "      , '               ' ,\n");   // forehead
            asciiSquare.append("      , - ~ ~ ~ - ,\n" +          // forehead
                    "  , '               ' ,\n");       // forehead

            Hashtable<Integer, String> bitToRound = new Hashtable<Integer, String>() {{
                put(0, "    ,      ~        ~       ,\n"); // eyebrows
                put(1, "    ,                       ,\n"); // no eyebrows
                put(2, "   ,       @        @        ,\n"); // open eyes
                put(3, "   ,       _        _        ,\n"); // closed eyes
                put(4, " \\,                           ,/\n" +
                        " /,                           ,\\\n"); // ears
                put(5, "  ,                           ,\n" +
                        "  ,                           ,\n"); // no ears
                put(6, "  ,           /,,\\            ,\n" +
                        "   ,                         ,\n"); // nose
                put(7, "  ,                           ,\n" +
                        "   ,                         ,\n"); // no nose
                put(8, "    ,     '~,_____,~'       ,\n"); // smile
                put(9, "    ,       ,-~**~-,        ,\n"); // frown
            }};

            Hashtable<Integer, String> bitToSqaure = new Hashtable<Integer, String>() {{
                put(0, "  ,     ~        ~    ,\n"); // eyebrows
                put(1, "  ,                   ,\n"); // no eyebrows
                put(2, "  ,     @        @    ,\n"); // open eyes
                put(3, "  ,     _        _    ,\n"); // closed eyes
                put(4, " \\,                   ,/\n" +
                        " /,                   ,\\\n"); // ears
                put(5, "  ,                   ,\n" +
                        "  ,                   ,\n"); // no ears
                put(6, "  ,        /,,\\       ,\n" +
                        "  ,                   ,\n"); // nose
                put(7, "  ,                   ,\n" +
                        "  ,                   ,\n"); // no nose
                put(8, "  ,    '~,_____,~'    ,\n"); // smile
                put(9, "  ,      ,-~**~-,     ,\n"); // frown
            }};

            for(int i = 0; i < 5; ++i){
                if((hashVal & (1 << i)) != 0){
                    asciiRound.append(bitToRound.get(i*2 + 1));
                    asciiSquare.append(bitToSqaure.get(i*2 + 1));
                }else{
                    asciiRound.append(bitToRound.get(i*2));
                    asciiSquare.append(bitToSqaure.get(i*2));
                }
            }


            asciiRound.append("      ,                  , '\n" +
                    "        ' - , _ _ _ ,  '\n");
            asciiSquare.append("  ,                   , \n" +
                    "    ' - , _ _ _ , - ' \n");

            if((hashVal & (1 << 5)) != 0){
                this.ascii = asciiSquare.toString();
            }else{
                this.ascii = asciiRound.toString();
            }
        }
    }

    /**
     * A getter function for date.
     *
     * @return Returns the private attribute date.
     */
    public String getDate() {
        return date;
    }

    /**
     * A getter function for location as a GeoPoint.
     *
     * @return Returns the private attribute location as a GeoPoint.
     */
    public GeoPoint getLocation() {
        return location;
    }

    /**
     * A getter function for hasLocation.
     *
     * @return Returns the private attribute hasLocation.
     */
    public boolean getHasLocation() {
        return hasLocation;
    }

    /**
     * A getter function for location as a string.
     *
     * @return Returns the private attribute location as a string.
     */
    public String getLocationString() {
        GeoPoint location = this.getLocation();
        // return Location N/A on no location, otherwise return a location string
        return (!hasLocation)
                ? "Location N/A"
                : "Lat: " + location.getLatitude() + ", " +
                "Long: " + location.getLongitude();
    }

    /**
     * A getter function for userDID.
     *
     * @return Returns the private attribute userDID.
     */
    public String getUserDID() {
        return userDID;
    }

    /**
     * A getter function for hash.
     *
     * @return Returns the private attribute hash.
     */
    public String getHash() {
        return hash;
    }
    /**
     * A getter function for hashVal.
     *
     * @return Returns the private attribute hashVal.
     */
    public int getHashVal() {
        return hashVal;
    }
    /**
     * A getter function for locationImage.
     *
     * @return Returns the private attribute locationImage.
     */
    public String getLocationImage() {
        return locationImage;
    }

    /**
     * A getter function for points.
     *
     * @return Returns the private attribute points.
     */
    public int getPoints() {
        return this.points;
    }
    /**
     * A getter function for name.
     *
     * @return Returns the private attribute name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * A getter function for ascii.
     *
     * @return Returns the private attribute ascii.
     */
    public String getAscii() {
        return this.ascii;
    }

    /**
     * A getter function for scannedCodeDID.
     *
     * @return Returns the private attribute scannedCodeDID.
     */
    public String getScannedCodeDID() {
        return this.scannedCodeDID;
    }
}
