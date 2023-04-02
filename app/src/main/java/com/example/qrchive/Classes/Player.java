package com.example.qrchive.Classes;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;


/**
 * this class represents a player in the game
 *
 * @author Zayd & Grayden
 */
public class Player {

    private String email;
    private String playerName;
    private String deviceID;
    private String userDID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int rank = -1;


    /**
     * Constructs a new Player object with the given parameters.
     * @param playerName the name of the player
     * @param email the email address of the player
     * @param deviceID the device ID of the player's device
     * @param userDID the unique user ID of the player
     */
    public Player(String playerName, String email, String deviceID, String userDID){
        this.playerName = playerName;
        this.email = email;
        this.deviceID = deviceID;
        this.userDID = userDID;
    }

    /**
     * Creates a new player with the given parameters.
     * @param playerName the name of the player
     * @param email the email of the player
     * @param deviceID the device ID of the player
     * @param rank the rank of the player
     * @param userDID the unique user device ID of the player
     */
    public Player(String playerName, String email, String deviceID, int rank, String userDID){
        this.playerName = playerName;
        this.email = email;
        this.deviceID = deviceID;
        this.rank = rank;
        this.userDID = userDID;
    }

    /**
     * Sets the email of the player.
     * @param email The email to be set for the player.
\     */
    public void setEmail(String email){ this.email = email; }

    /**
     * sets the players user name
     * @param name The name to be set for the player
     */
    public void setPlayerName(String name){ this.playerName = name;}

    /**
     * Sets the players rank
     * @param rank The rank to be set for the player
     */
    public void setRank(int rank){
        this.rank = rank;
    }

    /**
     * returns the users email
     * @return
     */
    public String getEmail(){return this.email;}
    /**
     * returns the users user name
     * @return playerName
     */
    public String getUserName(){return this.playerName;}
    /**
     * returns the users device id
     * @return deviceID
     */
    public String getDeviceID(){return this.deviceID;}
    /**
     * returns the users users distinct id
     * @return userDID
     */
    public String getUserDID(){ return this.userDID; }
    /**
     * returns the users rank
     * @return rank
     */
    public String getRank(){
        if(this.rank == -1){
            return "";
        }else{
            return "Best Code: " + Integer.toString(rank) + "pts";
        }
    }
    /**
     * returns the users numerical rank
     * @return rank
     */
    public int getNumericalRank(){
        return this.rank;
    }

    /**
     * Queries the database for the number of QR codes scanned by the player with the specified userDID,
     * and returns the result to the provided OnQRCountQueryListener.
     * @param listener OnQRCountQueryListener to be notified of the query results
     */
    public void getQRCount(final OnQRCountQueryListener listener) {
        db.collection("ScannedCodes")
                .whereEqualTo("userDID", userDID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            listener.onQRCount(count);
                        } else {
                            listener.onError(task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * Retrieves the player's score by getting all the scanned codes from the "ScannedCodes" collection
     * that have the same userDID as the player's userDID, calculating the score by summing up the
     * points of each scanned code, and returning the score through the OnPlayerScoreRetrieved callback.
     * @param listener an OnPlayerScoreRetrieved object that will receive the player's score when it is retrieved
     */
    public void getScore(OnPlayerScoreRetrieved listener){


        db.collection("ScannedCodes").whereEqualTo("userDID", userDID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();

                        int score = 0;
//    public ScannedCode(String hash, int hashVal, Date date, GeoPoint location, boolean hasLocation, String locationImage, String userDID, String scannedCodeDID) {

                        for (DocumentSnapshot document : docs) {
                            Map<String, Object> docData = document.getData();
                            ScannedCode scannedCode = new ScannedCode
                                    (docData.get("hash").toString(),
                                            Integer.parseInt(docData.get("hashVal").toString()),
                                            document.getTimestamp("date").toDate(),
                                            (GeoPoint) docData.get("location"),
                                            (boolean) docData.get("hasLocation"),
                                            docData.get("locationImage").toString(),
                                            docData.get("userDID").toString(),
                                            document.getId());

                            score += scannedCode.getPoints();
                            System.out.println("player score " + score);

                        }

                        listener.onScoresRetrieved(score);
                    }
                });


    }

    /**
     * An interface for listening to the retrieval of a player's score.
     */
    public interface OnPlayerScoreRetrieved {
        void onScoresRetrieved(int score);
    }

}
