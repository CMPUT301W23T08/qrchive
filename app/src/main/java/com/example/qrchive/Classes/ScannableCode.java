package com.example.qrchive.Classes;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import java.lang.Math;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

public class ScannableCode {
    private String code;
    private String hash;
    private int hashVal;

    //TODO: Subject to change
    public ScannableCode(String code) {
        this.code = code;
        this.hash = Hashing.sha256().hashString(code, StandardCharsets.UTF_8).toString();
        this.hashVal = Hashing.sha256().hashString(code, StandardCharsets.UTF_8).asInt();
    }

    public String getCode() {
        return this.code;
    }

    public String getHash() {
        return this.hash;
    }

    /**
     * Returns the score for a hash using the proposed scoring algorithm from the project description.
     * @return an integer representing the score.
     */
    public int getScore() {
        int score = 0;

        char previous = '\0';
        int chainLength = 1;
        for(int i = 0; i < hash.length(); ++i) {
            if(hash.charAt(i) == previous){
                chainLength += 1;
            }else{
                // Add certain amount to score
                if(chainLength > 1 || previous == '0'){
                    // if previous is 0 with 1 chain we still add 1
                    int base = 1;
                    if('0' <= previous && previous <= '9'){
                        base = previous - '0';
                    }else if('a' <= previous && previous <= 'f'){
                        base = previous - 'a' + 10;
                    }

                    score += (int)Math.pow(base, chainLength-1);
                }

                // Set variables to look for the next chain
                previous = hash.charAt(i);
                chainLength = 1;
            }
        }

        return score;
    }

    /**
     * Creates a name for a given hash based on the first 6 bits of the hash as proposed in the outline.
     * @return Returns the name as a string.
     */
    public String getName() {
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

        return name.toString();
    }

    /**
     * Creates an ascii art representation of the hash based on the first 6 bits as proposed.
     * @return Returns a string containing the ascii art representation.
     */
    public String getAscii() {
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
            return asciiSquare.toString();
        }else{
            return asciiRound.toString();
        }
    }
}
