package com.pulsarstats.mobile.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.pulsarstats.mobile.MainActivity;
import com.pulsarstats.mobile.R;
import com.pulsarstats.mobile.models.SystemData;
import com.pulsarstats.mobile.utils.PreferencesHelper;
import com.pulsarstats.mobile.utils.SignalRManager;
import java.util.Locale;

/**
 * Foreground service for persistent monitoring
 * Keeps app running in background and shows ongoing notification
 */
public class ForegroundMonitorService extends Service {
    
    private static final String CHANNEL_ID = "system_monitor_foreground";
    private static final int NOTIFICATION_ID = 1001;
    
    private SignalRManager signalRManager;
    private PreferencesHelper prefsHelper;
    private SystemData lastData;
    private boolean showPersistentNotification = true;
    
    private android.os.Handler updateHandler;
    private Runnable updateRunnable;
    
    @Override
    public void onCreate() {
        super.onCreate();
        signalRManager = SignalRManager.getInstance(this);
        prefsHelper = new PreferencesHelper(this);
        
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification("Başlatılıyor...", ""));
        
        startPeriodicUpdates();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("STOP_SERVICE".equals(action)) {
                stopSelf();
                return START_NOT_STICKY;
            }
        }
        
        // Service killed olursa restart et
        return START_STICKY;
    }
    
    private void startPeriodicUpdates() {
        updateHandler = new android.os.Handler(getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // SignalRManager'dan son veriyi al
                    SystemData data = signalRManager.getLastData();
                    if (data != null) {
                        lastData = data;
                        updateNotification(data);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ForegroundMonitor", "Update error", e);
                }
                
                // SharedPreferences'tan interval'ı oku (default 3 saniye)
                android.content.SharedPreferences prefs = getSharedPreferences("SystemMonitorPrefs", MODE_PRIVATE);
                int intervalSeconds = prefs.getInt("update_interval_seconds", 3);
                int intervalMillis = intervalSeconds * 1000;
                
                // DEBUG LOG
                android.util.Log.d("ForegroundMonitor", "⏱️ Update interval: " + intervalSeconds + "s (" + intervalMillis + "ms)");
                
                // Kullanıcı ayarına göre güncelle
                updateHandler.postDelayed(this, intervalMillis);
            }
        };
        updateHandler.post(updateRunnable);
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "System Monitor Foreground Service",
                    NotificationManager.IMPORTANCE_LOW // LOW = sessiz, bildirim çubuğunda görünür
            );
            channel.setDescription(getString(R.string.notification_channel_description));
            channel.setShowBadge(false);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private Notification createNotification(String title, String content) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 
                PendingIntent.FLAG_IMMUTABLE);
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notifications) // Notification icon
                .setContentIntent(pendingIntent)
                .setOngoing(true) // Kaydırılamaz, kalıcı
                .setPriority(NotificationCompat.PRIORITY_LOW) // Sessiz
                .build();
    }
    
    private void updateNotification(SystemData data) {
        // PreferenceHelper'dan persistent notification ayarını kontrol et
        android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        showPersistentNotification = prefs.getBoolean("persistent_notification", true);
        
        if (!showPersistentNotification) {
            // Kullanıcı kapatmış, minimal notification göster
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.notify(NOTIFICATION_ID, createNotification(getString(R.string.app_name), getString(R.string.notification_monitoring)));
            return;
        }
        
        // Data ile detaylı notification oluştur
        StringBuilder content = new StringBuilder();
        
        if (data.getCpuInfo() != null) {
            content.append(String.format(Locale.getDefault(), "CPU: %.0f%% ", data.getCpuInfo().getUsagePercent()));
        }
        
        if (data.getGpuInfo() != null) {
            content.append(String.format(Locale.getDefault(), "GPU: %.0f%% ", data.getGpuInfo().getUsagePercent()));
        }
        
        if (data.getMemoryInfo() != null) {
            content.append(String.format(Locale.getDefault(), "RAM: %.0f%% ", data.getMemoryInfo().getUsagePercent()));
        }
        
        if (data.getTemperatureInfo() != null) {
            if (data.getTemperatureInfo().getCpuTemp() != null && data.getTemperatureInfo().getCpuTemp() > 0) {
                content.append(String.format(Locale.getDefault(), "CPU: 🌡️ %.0f°C", data.getTemperatureInfo().getCpuTemp()));
            }
        }
        
        String title = getString(R.string.notification_title);
        String contentText = content.length() > 0 ? content.toString() : getString(R.string.notification_monitoring);
        
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, createNotification(title, contentText));
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateHandler != null && updateRunnable != null) {
            updateHandler.removeCallbacks(updateRunnable);
        }
        stopForeground(true);
        // Don't disconnect SignalRManager - MainActivity might still be using it
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Unbound service
    }
}
