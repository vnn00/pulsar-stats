package com.pulsarstats.mobile.models;

import com.google.gson.annotations.SerializedName;

public class SystemInfo {
    @SerializedName(value = "osVersion", alternate = {"OsVersion"})
    private String osVersion;

    @SerializedName(value = "computerName", alternate = {"ComputerName"})
    private String computerName;

    @SerializedName(value = "userName", alternate = {"UserName"})
    private String userName;

    @SerializedName(value = "uptime", alternate = {"Uptime"})
    private String uptime;

    @SerializedName(value = "cpuArchitecture", alternate = {"CpuArchitecture"})
    private String cpuArchitecture;

    @SerializedName(value = "totalRamGB", alternate = {"TotalRamGB"})
    private double totalRamGB;

    @SerializedName(value = "cpuCores", alternate = {"CpuCores"})
    private int cpuCores;

    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }

    public String getComputerName() { return computerName; }
    public void setComputerName(String computerName) { this.computerName = computerName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUptime() { return uptime; }
    public void setUptime(String uptime) { this.uptime = uptime; }

    public String getCpuArchitecture() { return cpuArchitecture; }
    public void setCpuArchitecture(String cpuArchitecture) { this.cpuArchitecture = cpuArchitecture; }

    public double getTotalRamGB() { return totalRamGB; }
    public void setTotalRamGB(double totalRamGB) { this.totalRamGB = totalRamGB; }

    public int getCpuCores() { return cpuCores; }
    public void setCpuCores(int cpuCores) { this.cpuCores = cpuCores; }
}
