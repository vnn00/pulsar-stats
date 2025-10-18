package com.pulsarstats.mobile.models;

public class NotificationSettings {
    private boolean enabled;
    private boolean vibrate;
    private boolean sound;
    
    // Bildirim tekrar ayarları
    private boolean alwaysRepeat;
    private int repeatInterval; // 0=always, 5=5min, 15=15min, 30=30min, 60=1hour
    
    // CPU ayarları
    private boolean cpuEnabled;
    private int cpuThreshold;
    
    // RAM ayarları
    private boolean ramEnabled;
    private int ramThreshold;
    
    // CPU Sıcaklık ayarları
    private boolean cpuTempEnabled;
    private int cpuTempThreshold;
    
    // GPU Sıcaklık ayarları
    private boolean gpuTempEnabled;
    private int gpuTempThreshold;
    
    // Disk Sıcaklık ayarları
    private boolean diskTempEnabled;
    private int diskTempThreshold;

    public NotificationSettings() {
        // Default değerler
        this.enabled = true;
        this.vibrate = true;
        this.sound = false;
        
        // Bildirim tekrar ayarları - varsayılan kapalı, 15 dakika
        this.alwaysRepeat = false;
        this.repeatInterval = 15;
        
        this.cpuEnabled = true;
        this.cpuThreshold = 80;
        
        this.ramEnabled = true;
        this.ramThreshold = 90;
        
        this.cpuTempEnabled = true;
        this.cpuTempThreshold = 80;
        
        this.gpuTempEnabled = true;
        this.gpuTempThreshold = 85;
        
        this.diskTempEnabled = false;
        this.diskTempThreshold = 60;
    }

    // Getters
    public boolean isEnabled() { return enabled; }
    public boolean isVibrate() { return vibrate; }
    public boolean isSound() { return sound; }
    public boolean isAlwaysRepeat() { return alwaysRepeat; }
    public int getRepeatInterval() { return repeatInterval; }
    public boolean isCpuEnabled() { return cpuEnabled; }
    public int getCpuThreshold() { return cpuThreshold; }
    public boolean isRamEnabled() { return ramEnabled; }
    public int getRamThreshold() { return ramThreshold; }
    public boolean isCpuTempEnabled() { return cpuTempEnabled; }
    public int getCpuTempThreshold() { return cpuTempThreshold; }
    public boolean isGpuTempEnabled() { return gpuTempEnabled; }
    public int getGpuTempThreshold() { return gpuTempThreshold; }
    public boolean isDiskTempEnabled() { return diskTempEnabled; }
    public int getDiskTempThreshold() { return diskTempThreshold; }

    // Setters
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setVibrate(boolean vibrate) { this.vibrate = vibrate; }
    public void setSound(boolean sound) { this.sound = sound; }
    public void setAlwaysRepeat(boolean alwaysRepeat) { this.alwaysRepeat = alwaysRepeat; }
    public void setRepeatInterval(int repeatInterval) { this.repeatInterval = repeatInterval; }
    public void setCpuEnabled(boolean cpuEnabled) { this.cpuEnabled = cpuEnabled; }
    public void setCpuThreshold(int cpuThreshold) { this.cpuThreshold = cpuThreshold; }
    public void setRamEnabled(boolean ramEnabled) { this.ramEnabled = ramEnabled; }
    public void setRamThreshold(int ramThreshold) { this.ramThreshold = ramThreshold; }
    public void setCpuTempEnabled(boolean cpuTempEnabled) { this.cpuTempEnabled = cpuTempEnabled; }
    public void setCpuTempThreshold(int cpuTempThreshold) { this.cpuTempThreshold = cpuTempThreshold; }
    public void setGpuTempEnabled(boolean gpuTempEnabled) { this.gpuTempEnabled = gpuTempEnabled; }
    public void setGpuTempThreshold(int gpuTempThreshold) { this.gpuTempThreshold = gpuTempThreshold; }
    public void setDiskTempEnabled(boolean diskTempEnabled) { this.diskTempEnabled = diskTempEnabled; }
    public void setDiskTempThreshold(int diskTempThreshold) { this.diskTempThreshold = diskTempThreshold; }
}
