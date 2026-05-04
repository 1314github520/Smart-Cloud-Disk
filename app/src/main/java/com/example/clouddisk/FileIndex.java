package com.example.clouddisk;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "file_index")
public class FileIndex {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String owner;    // 所属用户名 (用于多账号隔离)
    public String name;     // 文件名
    public String fullPath; // 云端路径
    public boolean isDir;  // 是否是文件夹
    public long size;      // 文件大小

    public FileIndex(String owner, String name, String fullPath, boolean isDir, long size) {
        this.owner = owner;
        this.name = name;
        this.fullPath = fullPath;
        this.isDir = isDir;
        this.size = size;
    }
}
