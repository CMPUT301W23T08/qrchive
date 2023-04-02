package com.example.qrchive.Classes;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Player {

    private String email;
    private String playerName;
    private String deviceID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int rank = -1;




    public Player(String playerName, String email, String deviceID){
        this.playerName = playerName;
        this.email = email;
        this.deviceID = deviceID;
    }

    public Player(String playerName, String email, String deviceID, int rank){
        this.playerName = playerName;
        this.email = email;
        this.deviceID = deviceID;
        this.rank = rank;
    }


    public void setRank(int rank){
        this.rank = rank;
    }
    public String getEmail(){return this.email;}
    public String getUserName(){return this.playerName;}
    public String getDeviceID(){return this.deviceID;}
    public String getRank(){
        if(this.rank == -1){
            return "";
        }else{
            return "Best Code: " + Integer.toString(rank) + "pts";
        }
    }
    public int getNumericalRank(){
        return this.rank;
    }
    public int getQRCount(){
        //TODO: get player qr count
        return 0;
    }
    public int getPoints(){
        //TODO: get player points
        return 0;
    }

    public void getQRCount(final OnQRCountQueryListener listener) {
        db.collection("ScannedCodes")
                .whereEqualTo("userDID", deviceID)
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
}
