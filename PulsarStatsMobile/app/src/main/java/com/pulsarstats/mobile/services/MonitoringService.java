package com.pulsarstats.mobile.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;
import com.pulsarstats.mobile.MainActivity;
import com.pulsarstats.mobile.R;
import com.pulsarstats.mobile.models.NotificationSettings;
import com.pulsarstats.mobile.models.SystemData;
import com.pulsarstats.mobile.utils.PreferencesHelper;
import com.pulsarstats.mobile.utils.SignalRManager;

public class MonitoringService extends Service {
    
    private static final String CHANNEL_ID = "system_monitor_channel";
    private static final String ALERT_CHANNEL_ID = "system_monitor_alerts";
    private static final String CHANNEL_NAME = "System Monitor";
    private static final String ALERT_CHANNEL_NAME = "System Alerts";
    private static final int NOTIFICATION_ID = 1001;
    private static final int MAX_ALERT_NOTIFICATIONS = 2;
    
    private SignalRManager signalRManager;
    private PreferencesHelper preferencesHelper;
    private NotificationSettings settings;
    private NotificationManager notificationManager;
    private Vibrator vibrator;
    
    // Son bildirim zamanları (ms)
    private long lastCpuNotificationTime = 0;
    private long lastRamNotificationTime = 0;
    private long lastCpuTempNotificationTime = 0;
    private long lastGpuTempNotificationTime = 0;
    private long lastDiskTempNotificationTime = 0;
    
    // Bildirim ID takibi
    private int lastAlertId = 2000;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        preferencesHelper = new PreferencesHelper(this);
        settings = preferencesHelper.getNotificationSettings();
        signalRManager = SignalRManager.getInstance(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        createNotificationChannel();
        startPeriodicThresholdChecks();
    }
    
    private android.os.Handler thresholdHandler;
    private Runnable thresholdRunnable;
    
    private void startPeriodicThresholdChecks() {
        thresholdHandler = new android.os.Handler(getMainLooper());
        thresholdRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    SystemData data = signalRManager.getLastData();
                    if (data != null && settings.isEnabled()) {
                        checkThresholds(data);
                    }
                } catch (Exception e) {
                    android.util.Log.e("MonitoringService", "Threshold check error", e);
                }
                // 5 saniyede bir kontrol et
                thresholdHandler.postDelayed(this, 5000);
            }
        };
        thresholdHandler.post(thresholdRunnable);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Check if this is a settings refresh request
        if (intent != null && "REFRESH_SETTINGS".equals(intent.getAction())) {
            // Reload settings and reset notification timestamps
            PreferencesHelper prefsHelper = new PreferencesHelper(this);
            settings = prefsHelper.getNotificationSettings();
            lastCpuNotificationTime = 0;
            lastRamNotificationTime = 0;
            lastCpuTempNotificationTime = 0;
            lastGpuTempNotificationTime = 0;
        }
        
        startForeground(NOTIFICATION_ID, createForegroundNotification());
        return START_STICKY;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Service notification channel (low importance)
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription(getString(R.string.notification_channel_description));
            notificationManager.createNotificationChannel(channel);
            
            // Alert notification channel (high importance with sound and vibration)
            NotificationChannel alertChannel = new NotificationChannel(
                ALERT_CHANNEL_ID,
                ALERT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            alertChannel.setDescription("Threshold alerts with sound and vibration");
            alertChannel.enableVibration(true);
            alertChannel.setSound(
                android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION),
                null
            );
            notificationManager.createNotificationChannel(alertChannel);
        }
    }
    
    private Notification createForegroundNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_monitoring))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }
    
    private void setupSignalR() {
        signalRManager.setListener(new SignalRManager.SystemDataListener() {
            @Override
            public void onDataReceived(SystemData data) {
                if (settings.isEnabled()) {
                    checkThresholds(data);
                }
            }
            
            @Override
            public void onConnectionChanged(boolean connected) {
                // Handle connection status if needed
            }
            
            @Override
            public void onError(Exception e) {
                // Handle error if needed
            }

            @Override
            public void onScreenshotReceived(String base64Image, int width, int height) {
                // MonitoringService'de ekran görüntüsü ile ilgili bir işlem yapılmayacak.
            }
        });
    }
    
    private void checkThresholds(SystemData data) {
        // Ayarları yeniden yükle (değişmiş olabilir)
        settings = preferencesHelper.getNotificationSettings();
        
        // CPU
        if (settings.isCpuEnabled() && data.getCpuInfo() != null) {
            double cpuUsage = data.getCpuInfo().getUsagePercent();
            if (cpuUsage >= settings.getCpuThreshold()) {
                if (shouldSendNotification(lastCpuNotificationTime)) {
                    sendNotification(
                        getString(R.string.notification_high_cpu, (int)cpuUsage),
                        "CPU Uyarısı"
                    );
                    lastCpuNotificationTime = System.currentTimeMillis();
                }
            }
        }
        
        // RAM
        if (settings.isRamEnabled() && data.getMemoryInfo() != null) {
            double ramUsage = data.getMemoryInfo().getUsagePercent();
            if (ramUsage >= settings.getRamThreshold()) {
                if (shouldSendNotification(lastRamNotificationTime)) {
                    sendNotification(
                        getString(R.string.notification_high_ram, (int)ramUsage),
                        "RAM Uyarısı"
                    );
                    lastRamNotificationTime = System.currentTimeMillis();
                }
            }
        }
        
        // Temperature
        if (settings.isCpuTempEnabled() && data.getTemperatureInfo() != null) {
            Double cpuTemp = data.getTemperatureInfo().getCpuTemp();
            if (cpuTemp != null && cpuTemp >= settings.getCpuTempThreshold()) {
                if (shouldSendNotification(lastCpuTempNotificationTime)) {
                    sendNotification(
                        getString(R.string.notification_high_cpu_temp, cpuTemp),
                        "CPU Sıcaklık Uyarısı"
                    );
                    lastCpuTempNotificationTime = System.currentTimeMillis();
                }
            }
        }
        
        if (settings.isGpuTempEnabled() && data.getTemperatureInfo() != null) {
            Double gpuTemp = data.getTemperatureInfo().getGpuTemp();
            if (gpuTemp != null && gpuTemp >= settings.getGpuTempThreshold()) {
                if (shouldSendNotification(lastGpuTempNotificationTime)) {
                    sendNotification(
                        getString(R.string.notification_high_gpu_temp, gpuTemp),
                        "GPU Sıcaklık Uyarısı"
                    );
                    lastGpuTempNotificationTime = System.currentTimeMillis();
                }
            }
        }
    }
    
    private boolean shouldSendNotification(long lastNotificationTime) {
        if (settings.isAlwaysRepeat()) {
            return true; // Sürekli tekrarla
        }
        
        long currentTime = System.currentTimeMillis();
        long intervalMillis = settings.getRepeatInterval() * 60 * 1000; // dakikayı ms'ye çevir
        
        return (currentTime - lastNotificationTime) >= intervalMillis;
    }
    
    private void sendNotification(String message, String title) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );
        
        // Benzersiz ID oluştur ama sınırlı tut (max 2 alert bildirimi)
        lastAlertId++;
        if (lastAlertId > 2001) {
            lastAlertId = 2000; // 2000-2001 arası döngü
        }
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        
        // Ses ve titreşim sistem ayarlarından gelecek, burada belirtmeye gerek yok
        
        notificationManager.notify(lastAlertId, builder.build());
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thresholdHandler != null && thresholdRunnable != null) {
            thresholdHandler.removeCallbacks(thresholdRunnable);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
