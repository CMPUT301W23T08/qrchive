package com.example.qrchive;

import static org.junit.Assert.assertTrue;

import com.example.qrchive.Classes.Player;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

public class PlayerTest {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * This code test the process of create a new player ( also a test for getEmail(), getUserName(), getDeviceID(), getUserDID()
     */
    @Test
    public void CreatePlayer(){
        Player player = new Player("testing", "testing@gmail.com", "123454655765868", "13423423141412");
        assertTrue( player.getEmail().equals("testing@gmail.com"));
        assertTrue( player.getUserName().equals("testing"));
        assertTrue( player.getDeviceID().equals("123454655765868"));
        assertTrue( player.getUserDID().equals("13423423141412"));
    }
    /**
     * This code test the process of setting the player to a new email
     */
    public void setPLayerNameEmail() {
        Player player = new Player("testing", "testing@gmail.com", "123454655765868", "13423423141412");
        player.setEmail("anotherTesting@gmail.com");
        assertTrue( player.getEmail().equals("anotherTesting@gmail.com"));

    }
    /**
     * This code test the process of setting a new player name
     */
    public void setPLayerNameTest(){
        Player player = new Player("testing", "testing@gmail.com", "123454655765868", "13423423141412");
        player.setPlayerName("anotherName");
        assertTrue(player.getEmail().equals("anotherName"));
    }
    /**
     * This code test the process getting the numberical rank
     */
//    public void getNumericalRank() {
//        String userDID = "Ib0CFNDpCPrM2D7HqgPF"; // userID in firebase for testing
//
//        db.collection("ScannedCodes").whereEqualTo("userDID", userDID).get().get
//    }
//    public void getQRCount(){
//
//
//    }


}
