package com.pulsarstats.mobile.models;

import com.google.gson.annotations.SerializedName;

public class NetworkInfo {
    @SerializedName(value = "downloadSpeedMbps", alternate = {"DownloadSpeedMbps", "DownloadSpeedMBps"})
    private double downloadSpeedMbps;

    @SerializedName(value = "uploadSpeedMbps", alternate = {"UploadSpeedMbps", "UploadSpeedMBps"})
    private double uploadSpeedMbps;

    @SerializedName(value = "totalDownloadGB", alternate = {"TotalDownloadGB"})
    private double totalDownloadGB;

    @SerializedName(value = "totalUploadGB", alternate = {"TotalUploadGB"})
    private double totalUploadGB;

    public double getDownloadSpeedMbps() { return downloadSpeedMbps; }
    public void setDownloadSpeedMbps(double downloadSpeedMbps) { this.downloadSpeedMbps = downloadSpeedMbps; }
    
    public double getUploadSpeedMbps() { return uploadSpeedMbps; }
    public void setUploadSpeedMbps(double uploadSpeedMbps) { this.uploadSpeedMbps = uploadSpeedMbps; }
    
    public double getTotalDownloadGB() { return totalDownloadGB; }
    public void setTotalDownloadGB(double totalDownloadGB) { this.totalDownloadGB = totalDownloadGB; }
    
    public double getTotalUploadGB() { return totalUploadGB; }
    public void setTotalUploadGB(double totalUploadGB) { this.totalUploadGB = totalUploadGB; }
}
