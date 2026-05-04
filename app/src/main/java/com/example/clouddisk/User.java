package com.example.clouddisk;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String password;

    // 构造方法
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}