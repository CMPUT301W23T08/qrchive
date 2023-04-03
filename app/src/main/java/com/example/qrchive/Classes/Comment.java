package com.example.qrchive.Classes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents a comment made by a user, containing the username, a hash code,
 * the comment content, and the date it was created.
 */
public class Comment {
    String userName;
    String hash;
    String content;
    Date date;

    /**
     * Gets the username of the user who made the comment.
     * @return The username of the user who made the comment.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the hash of the code associated with this comment
     * @return The hash of the code associated with this comment
     */
    public String getHash() {
        return hash;
    }

    /**
     * Gets the content of the comment
     * @return comment content
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the date the comment was created
     * @return The date the comment was created
     */
    public String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
        return formatter.format(date);
    }


    /**
     * Creates a new comment object
     * @param userName The username of the user who made the comment
     * @param hash The hash of the code associated with this comment
     * @param content The content of the comment
     * @param date The date the comment was created
     */
    public Comment(String userName, String hash, String content, Date date) {
        this.userName = userName;
        this.hash = hash;
        this.content = content;
        this.date = date;
    }
}
