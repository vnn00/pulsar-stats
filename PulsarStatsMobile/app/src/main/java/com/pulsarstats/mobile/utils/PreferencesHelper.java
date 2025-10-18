package com.pulsarstats.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.pulsarstats.mobile.models.NotificationSettings;

public class PreferencesHelper {
    private static final String PREF_NAME = "SystemMonitorPrefs";
    private static final String KEY_SERVER_IP = "server_ip";
    private static final String KEY_SERVER_PORT = "server_port";
    private static final String KEY_NOTIFICATION_SETTINGS = "notification_settings";
    
    private final SharedPreferences prefs;
    private final Gson gson;
    
    public PreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    // Server IP
    public void saveServerIp(String ip) {
        prefs.edit().putString(KEY_SERVER_IP, ip).apply();
    }
    
    public String getServerIp() {
        return prefs.getString(KEY_SERVER_IP, null);
    }
    
    // Server Port
    public void saveServerPort(int port) {
        prefs.edit().putInt(KEY_SERVER_PORT, port).apply();
    }
    
    public int getServerPort() {
        return prefs.getInt(KEY_SERVER_PORT, 5000);
    }
    
    // Notification Settings
    public void saveNotificationSettings(NotificationSettings settings) {
        String json = gson.toJson(settings);
        prefs.edit().putString(KEY_NOTIFICATION_SETTINGS, json).apply();
    }
    
    public NotificationSettings getNotificationSettings() {
        String json = prefs.getString(KEY_NOTIFICATION_SETTINGS, null);
        if (json == null) {
            return new NotificationSettings(); // Default settings
        }
        return gson.fromJson(json, NotificationSettings.class);
    }
    
    // Clear all
    public void clear() {
        prefs.edit().clear().apply();
    }
}
