package com.example.clouddisk;

public class MessageItem {
    private String role; // "AI" or "User"
    private String content;

    public MessageItem(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() { return role; }
    public String getContent() { return content; }
}