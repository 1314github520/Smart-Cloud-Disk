package com.example.clouddisk;

public class TransferTask {
    private long id;
    private String name;
    private int progress;
    private String statusText;
    private boolean isDownload;

    public TransferTask(long id, String name, boolean isDownload) {
        this.id = id;
        this.name = name;
        this.isDownload = isDownload;
        this.progress = 0;
        this.statusText = "准备中...";
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }
    public boolean isDownload() { return isDownload; }
}
