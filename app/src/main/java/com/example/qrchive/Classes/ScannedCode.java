package com.example.qrchive.Classes;

import com.google.common.hash.Hashing;
import com.google.firebase.firestore.GeoPoint;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

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

    // Constructor which uses code and assumes no location
    public ScannedCode(String code, String date, String locationImage, String userDID, String did) {
        this(Hashing.sha256().hashString(code, StandardCharsets.UTF_8).toString(),
                Hashing.sha256().hashString(code, StandardCharsets.UTF_8).asInt(),
                date, new GeoPoint(0,0), false, locationImage, userDID);
        // even though i pass (0,0) for geopoint, it SHOULD NOT BE USED as hasLocation is false
    }

    // Constructor which uses code and assumes location
    public ScannedCode(String code, String date, GeoPoint location, String locationImage, String userDID) {
        this(Hashing.sha256().hashString(code, StandardCharsets.UTF_8).toString(),
                Hashing.sha256().hashString(code, StandardCharsets.UTF_8).asInt(),
                date, location, true, locationImage, userDID);
    }

    // Base Constructor
    public ScannedCode(String hash, int hashVal, String date, GeoPoint location, boolean hasLocation, String locationImage, String userDID) {
        this.hash = hash;
        this.hashVal = hashVal;
        this.date = date;
        this.hasLocation = hasLocation;
        this.location = location;
        this.locationImage = locationImage;
        this.userDID = userDID;

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

    public String getDate() {
        return date;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getUserDID() {
        return userDID;
    }

    public int getPoints() {
        return this.points;
    }
    public String getName() {
        return this.name;
    }

    public String getAscii() {
        return this.ascii;
    }
}