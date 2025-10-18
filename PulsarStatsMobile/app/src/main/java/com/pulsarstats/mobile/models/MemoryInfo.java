package com.pulsarstats.mobile.models;

import com.google.gson.annotations.SerializedName;

public class MemoryInfo {
    @SerializedName(value = "totalGB", alternate = {"TotalGB"})
    private double totalGB;

    @SerializedName(value = "usedGB", alternate = {"UsedGB"})
    private double usedGB;

    @SerializedName(value = "availableGB", alternate = {"AvailableGB"})
    private double availableGB;

    @SerializedName(value = "usagePercent", alternate = {"UsagePercent"})
    private double usagePercent;

    @SerializedName(value = "swapTotalGB", alternate = {"SwapTotalGB"})
    private double swapTotalGB;

    @SerializedName(value = "swapUsedGB", alternate = {"SwapUsedGB"})
    private double swapUsedGB;

    @SerializedName(value = "swapUsagePercent", alternate = {"SwapUsagePercent"})
    private double swapUsagePercent;

    public double getTotalGB() { return totalGB; }
    public void setTotalGB(double totalGB) { this.totalGB = totalGB; }
    
    public double getUsedGB() { return usedGB; }
    public void setUsedGB(double usedGB) { this.usedGB = usedGB; }
    
    public double getAvailableGB() { return availableGB; }
    public void setAvailableGB(double availableGB) { this.availableGB = availableGB; }
    
    public double getUsagePercent() { return usagePercent; }
    public void setUsagePercent(double usagePercent) { this.usagePercent = usagePercent; }

    public double getSwapTotalGB() { return swapTotalGB; }
    public void setSwapTotalGB(double swapTotalGB) { this.swapTotalGB = swapTotalGB; }

    public double getSwapUsedGB() { return swapUsedGB; }
    public void setSwapUsedGB(double swapUsedGB) { this.swapUsedGB = swapUsedGB; }

    public double getSwapUsagePercent() { return swapUsagePercent; }
    public void setSwapUsagePercent(double swapUsagePercent) { this.swapUsagePercent = swapUsagePercent; }
}
