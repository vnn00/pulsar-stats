package com.pulsarstats.mobile.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.pulsarstats.mobile.models.SystemData;

public class SignalRManager {
    private static final String TAG = "SignalRManager";
    private static SignalRManager instance;
    
    private HubConnection hubConnection;
    private final Gson gson;
    private SystemDataListener listener;
    private final Handler handler;
    private String serverIp;
    private int serverPort;
    private SystemData lastData; // Store last received data

    public interface SystemDataListener {
        void onDataReceived(SystemData data);
        void onConnectionChanged(boolean connected);
        void onError(Exception e);
        void onScreenshotReceived(String base64Image, int width, int height);
    }
    
    private SignalRManager(Context context) {
        gson = new Gson();
        handler = new Handler(Looper.getMainLooper());
    }
    
    public static synchronized SignalRManager getInstance(Context context) {
        if (instance == null) {
            instance = new SignalRManager(context.getApplicationContext());
        }
        return instance;
    }
    
    public void setListener(SystemDataListener listener) {
        this.listener = listener;
        if (listener != null && isConnected()) {
            handler.post(() -> listener.onConnectionChanged(true));
        }
    }
    
    public synchronized void connect(String serverIp, int port) {
        boolean reconnectingToSameServer = hubConnection != null
                && hubConnection.getConnectionState() == HubConnectionState.CONNECTED
                && serverIp.equals(this.serverIp)
                && port == this.serverPort;

        if (reconnectingToSameServer) {
            Log.d(TAG, "Already connected to requested server");
            if (listener != null) {
                handler.post(() -> listener.onConnectionChanged(true));
            }
            return;
        }

        if (hubConnection != null) {
            try {
                Log.d(TAG, "Stopping existing SignalR connection before reconnecting");
                hubConnection.stop().blockingAwait();
            } catch (Exception ex) {
                Log.w(TAG, "Error while stopping existing connection", ex);
            }
            hubConnection = null;
        }

        this.serverIp = serverIp;
        this.serverPort = port;

        String hubUrl = String.format("http://%s:%d/systemhub", serverIp, port);
        hubConnection = HubConnectionBuilder.create(hubUrl).build();
        
        hubConnection.on("ReceiveSystemUpdate", (data) -> {
            try {
                SystemData systemData = gson.fromJson(data, SystemData.class);
                lastData = systemData; // Store for ForegroundMonitorService
                if (listener != null) {
                    handler.post(() -> listener.onDataReceived(systemData));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing system data", e);
            }
        }, String.class);
        
        hubConnection.on("ReceiveScreenshot", (screenshotJson) -> {
            try {
                Log.d(TAG, "📸 ReceiveScreenshot event triggered! JSON length: " + screenshotJson.length());
                com.google.gson.JsonObject jsonObject = gson.fromJson(screenshotJson, com.google.gson.JsonObject.class);
                String base64Data = jsonObject.get("data").getAsString();
                int width = jsonObject.get("width").getAsInt();
                int height = jsonObject.get("height").getAsInt();
                Log.d(TAG, "✅ Screenshot parsed: " + width + "x" + height + ", data: " + base64Data.length() + " chars");
                if (listener != null) {
                    handler.post(() -> listener.onScreenshotReceived(base64Data, width, height));
                } else {
                    Log.e(TAG, "❌ Screenshot received but listener is NULL!");
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error parsing screenshot data", e);
            }
        }, String.class);
        
        hubConnection.onClosed(exception -> {
            Log.w(TAG, "SignalR connection closed", exception);
            if (listener != null) {
                handler.post(() -> listener.onConnectionChanged(false));
            }
        });
        
        hubConnection.start().subscribe(() -> {
            if (listener != null) {
                handler.post(() -> listener.onConnectionChanged(true));
            }
        }, throwable -> {
            if (listener != null) {
                handler.post(() -> listener.onError(new Exception("Connection failed", throwable)));
            }
        });
    }
    
    public synchronized void disconnect() {
        if (hubConnection != null) {
            try {
                hubConnection.stop().blockingAwait();
            } catch (Exception ex) {
                Log.w(TAG, "Error while stopping connection", ex);
            }
            hubConnection = null;
        }
        lastData = null;
        serverIp = null;
        serverPort = 0;
        if (listener != null) {
            handler.post(() -> listener.onConnectionChanged(false));
        }
    }
    
    public boolean isConnected() {
        return hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED;
    }
    
    public SystemData getLastData() {
        return lastData;
    }
    
    public void requestScreenshot(Runnable onSuccess, Runnable onFailure) {
        if (!isConnected()) {
            Log.e(TAG, "Cannot request screenshot: Not connected");
            if (onFailure != null) handler.post(onFailure);
            return;
        }
        
        new Thread(() -> {
            try {
                String urlString = String.format("http://%s:%d/api/screenshot/capture", this.serverIp, this.serverPort);
                Log.d(TAG, "Requesting screenshot from: " + urlString);
                
                java.net.URI uri = java.net.URI.create(urlString);
                java.net.URL url = uri.toURL();
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(30000); // Screenshot can take time
                conn.setDoOutput(true);
                
                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Screenshot request response: " + responseCode);
                
                if (responseCode == 200) {
                    if (onSuccess != null) handler.post(onSuccess);
                } else {
                    Log.e(TAG, "Screenshot request failed with code: " + responseCode);
                    if (onFailure != null) handler.post(onFailure);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Screenshot request exception", e);
                if (onFailure != null) handler.post(onFailure);
            }
        }).start();
    }
}
