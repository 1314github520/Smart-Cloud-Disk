package com.example.clouddisk;

public class CloudFile {
    private String name;    // 文件名
    private boolean isDirectory; // 是否是文件夹
    private long size;      // 文件大小
    private String fullPath; // 全路径 (WebDAV 相对路径)

    public CloudFile(String name, boolean isDirectory, long size) {
        this(name, isDirectory, size, null);
    }

    public CloudFile(String name, boolean isDirectory, long size, String fullPath) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.size = size;
        this.fullPath = fullPath;
    }

    // Getters
    public String getName() { return name; }
    public boolean isDirectory() { return isDirectory; }
    public long getSize() { return size; }
    public String getFullPath() { return fullPath; }
    public void setFullPath(String path) { this.fullPath = path; }
    public String getSizeText() {
        return isDirectory ? "" : (size / 1024 / 1024) + " MB";
    }
}