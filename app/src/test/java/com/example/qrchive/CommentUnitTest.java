package com.example.qrchive;

import static org.junit.jupiter.api.Assertions.*;
import com.example.qrchive.Classes.Comment;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class CommentUnitTest {
    private Comment getMockCode(){
        Comment mockComment = new Comment("a", "b", "c", new Date());
        return mockComment;
    }

    @Test
    public void testUserName() {
        Comment mockComment = getMockCode();
        assertEquals("a", mockComment.getUserName());
    }

    @Test
    public void testGetHash() {
        Comment mockComment = getMockCode();
        assertEquals("b", mockComment.getHash());
    }

    @Test
    public void testGetContent() {
        Comment mockComment = getMockCode();
        assertEquals("c", mockComment.getContent());
    }
}