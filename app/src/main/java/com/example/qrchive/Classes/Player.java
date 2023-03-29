package com.example.qrchive.Classes;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Player {

    private String email;
    private String playerName;
    private String deviceID;




    public Player(String playerName, String email, String deviceID){
        this.playerName = playerName;
        this.email = email;
        this.deviceID = deviceID;

    }


    public String getEmail(){return this.email;}
    public String getUserName(){return this.playerName;}
    public String getDeviceID(){return this.deviceID;}
    public int getQRCount(){
        //TODO: get player qr count
        return 0;
    }
    public int getPoints(){
        //TODO: get player points
        return 0;
    }

    public ArrayList<String> getPlayerFriends(){
        //TODO: get players Friends
        ArrayList<String> friends = new ArrayList<>();

        return friends;
    }



}
