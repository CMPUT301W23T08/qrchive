package com.example.qrchive.Classes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    String userName;
    String codeDID;
    String content;
    Date date;

    public String getUserName() {
        return userName;
    }

    public String getCodeDID() {
        return codeDID;
    }

    public String getContent() {
        return content;
    }

    public String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
        return formatter.format(date);
    }

    public Comment(String userName, String codeDID, String content, Date date) {
        this.userName = userName;
        this.codeDID = codeDID;
        this.content = content;
        this.date = date;
    }
}