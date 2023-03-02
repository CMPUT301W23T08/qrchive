package com.example.qrchive;

import static org.junit.jupiter.api.Assertions.*;

import com.example.qrchive.Classes.ScannableCode;

import org.junit.jupiter.api.Test;

public class ScannableCodeUnitTest {
    private ScannableCode getMockCode(){
        // Note that the hash in Java takes out the new line character
        ScannableCode mockCode = new ScannableCode("BFG5DGW54");
        // Mock hash is 8227ad036b504e39fe29393ce170908be2b1ea636554488fa86de5d9d6cd2c32
        // it has four zeroes, 22, 44, 55, 88 which is 4 + 2 + 4 + 5 + 8 = 23 score
        // the first 6 bits are b000010
        return mockCode;
    }

    @Test
    public void testGetScore() {
        ScannableCode code = getMockCode();
        assertEquals(23, code.getScore());
    }

    @Test
    public void testGetName() {
        ScannableCode code = getMockCode();

        String expected = "cool GloMoMegaSpectralCrab";

        assertEquals(expected, code.getName());
    }

    @Test
    public void testGetAscii() {
        ScannableCode code = getMockCode();

        // Eyebrows, closed eyes, ears, nose, smile, round face
        String expected =
                "          , - ~ ~ ~ - ,\n" +
                "      , '               ' ,\n" +
                "    ,      ~        ~       ,\n" +
                "   ,       _        _        ,\n" +
                " \\,                           ,/\n" +
                " /,                           ,\\\n" +
                "  ,           /,,\\            ,\n" +
                "   ,                         ,\n" +
                "    ,     '~,_____,~'       ,\n" +
                "      ,                  , '\n" +
                "        ' - , _ _ _ ,  '\n";

        assertEquals(expected, code.getAscii());
    }
}
