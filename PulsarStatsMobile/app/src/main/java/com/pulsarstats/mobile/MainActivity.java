package com.pulsarstats.mobile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.pulsarstats.mobile.models.SystemData;
import com.pulsarstats.mobile.models.DiskInfo;
import com.pulsarstats.mobile.services.ForegroundMonitorService;
import com.pulsarstats.mobile.utils.LocaleHelper;
import com.pulsarstats.mobile.utils.PreferencesHelper;
import com.pulsarstats.mobile.utils.SignalRManager;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private View connectionIndicator;
    private TextView connectionStatusText;
    
    // CPU
    private TextView cpuUsageText;
    private TextView cpuFrequencyText;
    
    // GPU
    private TextView gpuNameText;
    private TextView gpuUsageText;
    private TextView gpuMemoryText;
    
    // Memory
    private TextView memoryUsageText;
    private TextView memoryDetailsText;
    private TextView swapMemoryText;
    
    // Temperature
    private TextView cpuTempText;
    private TextView gpuTempText;
    private TextView motherboardTempText;
    
    // Disk
    private LinearLayout diskContainer;
    
    // Network
    private TextView networkDownloadText;
    private TextView networkUploadText;
    
    // System Info
    private TextView systemDeviceText;
    private TextView systemOsText;
    private TextView systemUptimeText;
    private TextView systemArchText;
    private TextView systemRamText;
    private TextView systemCoresText;
    private TextView systemScreenText;
    
    private PreferencesHelper prefsHelper;
    private SignalRManager signalRManager;
    
    private static final int REQUEST_WRITE_STORAGE = 100;
    
    // Throttle variables for update interval
    private long lastUpdateTimestamp = 0;
    private int updateIntervalMillis = 3000; // Default 3 seconds

    private boolean screenshotPending = false;
    private final Handler screenshotTimeoutHandler = new Handler(Looper.getMainLooper());
    private final Runnable screenshotTimeoutRunnable = () -> {
        if (screenshotPending) {
            screenshotPending = false;
            Toast.makeText(this, R.string.screenshot_timeout_warning, Toast.LENGTH_LONG).show();
        }
    };
    
    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setContentView
        com.pulsarstats.mobile.utils.ThemeUtils.applySavedTheme(this);
        
        // Apply saved locale
        LocaleHelper.setLocale(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prefsHelper = new PreferencesHelper(this);
        signalRManager = SignalRManager.getInstance(this);
        
        // Load update interval from preferences
        loadUpdateInterval();
        
        // Check if server IP is saved, if not redirect to SplashActivity
        String savedIp = prefsHelper.getServerIp();
        int savedPort = prefsHelper.getServerPort();
        
        if (savedIp == null || savedIp.trim().isEmpty()) {
            // No server configured, go to splash
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        // Ensure SignalR is connected with saved credentials
        if (!signalRManager.isConnected()) {
            signalRManager.connect(savedIp, savedPort);
        }
        
        // Start foreground service for background monitoring
        startForegroundMonitoringService();
        
        initViews();
        setupToolbar();
        setupSignalR();
        
        // Apply keep screen on setting
        applyKeepScreenOnSetting();
    }
    
    private void loadUpdateInterval() {
        android.content.SharedPreferences prefs = getSharedPreferences("SystemMonitorPrefs", MODE_PRIVATE);
        int intervalSeconds = prefs.getInt("update_interval_seconds", 3);
        updateIntervalMillis = intervalSeconds * 1000;
        android.util.Log.d("MainActivity", "⏱️ Loaded update interval: " + intervalSeconds + "s (" + updateIntervalMillis + "ms)");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Reload update interval in case it changed in settings
        loadUpdateInterval();
        
        // Activity'ye her geri dönüldüğünde, listener'ın MainActivity olduğuna emin ol.
        setupSignalR(); 
        
        // Sadece UI durumunu güncelle, yeniden bağlanma!
        updateConnectionStatus(signalRManager.isConnected());
        if (!signalRManager.isConnected()) {
            // Eğer bir şekilde bağlantı kopmuşsa, yeniden bağlanmayı dene.
            reconnect();
        }
        
        // Reapply keep screen on setting in case it changed in settings
        applyKeepScreenOnSetting();
    }
    
    private void applyKeepScreenOnSetting() {
        android.content.SharedPreferences prefs = getSharedPreferences("SystemMonitorPrefs", MODE_PRIVATE);
        boolean keepScreenOn = prefs.getBoolean("keep_screen_on", false);
        
        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        connectionIndicator = findViewById(R.id.connectionIndicator);
        connectionStatusText = findViewById(R.id.connectionStatusText);
        
        cpuUsageText = findViewById(R.id.cpuUsageText);
        cpuFrequencyText = findViewById(R.id.cpuFrequencyText);
        
        gpuNameText = findViewById(R.id.gpuNameText);
        gpuUsageText = findViewById(R.id.gpuUsageText);
        gpuMemoryText = findViewById(R.id.gpuMemoryText);
        
        memoryUsageText = findViewById(R.id.memoryUsageText);
        memoryDetailsText = findViewById(R.id.memoryDetailsText);
        swapMemoryText = findViewById(R.id.swapMemoryText);
        
        cpuTempText = findViewById(R.id.cpuTempText);
        gpuTempText = findViewById(R.id.gpuTempText);
        motherboardTempText = findViewById(R.id.motherboardTempText);
        
        diskContainer = findViewById(R.id.diskContainer);
        
        networkDownloadText = findViewById(R.id.networkDownloadText);
        networkUploadText = findViewById(R.id.networkUploadText);
        
        systemDeviceText = findViewById(R.id.systemDeviceText);
        systemOsText = findViewById(R.id.systemOsText);
        systemUptimeText = findViewById(R.id.systemUptimeText);
        systemArchText = findViewById(R.id.systemArchText);
        systemRamText = findViewById(R.id.systemRamText);
        systemCoresText = findViewById(R.id.systemCoresText);
        systemScreenText = findViewById(R.id.systemScreenText);
        
        // Display fallback info until PC data arrives
        displayFallbackInfo();
        
        // Yenileme mantığını güncelle
        swipeRefresh.setOnRefreshListener(this::handleRefresh);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_screenshot) {
            requestScreenshot();
            return true;
        } else if (id == R.id.action_settings) {
            openSettings();
            return true;
        } else if (id == R.id.action_refresh) {
            handleRefresh();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void setupSignalR() {
        signalRManager.setListener(new SignalRManager.SystemDataListener() {
            @Override
            public void onDataReceived(SystemData data) {
                runOnUiThread(() -> {
                    // Throttle updates based on user's preferred interval
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastUpdateTimestamp < updateIntervalMillis) {
                        // Skip this update - too soon
                        return;
                    }
                    lastUpdateTimestamp = currentTime;
                    
                    // Veri geliyorsa bağlantı var demektir
                    updateConnectionStatus(true);
                    updateUI(data);
                });
            }
            
            @Override
            public void onConnectionChanged(boolean connected) {
                runOnUiThread(() -> updateConnectionStatus(connected));
            }
            
            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    updateConnectionStatus(false);
                    swipeRefresh.setRefreshing(false);
                });
            }
            
            @Override
            public void onScreenshotReceived(String base64Image, int width, int height) {
                android.util.Log.d("MainActivity", "🖼️ Screenshot EVENT received! Size: " + base64Image.length() + " chars, " + width + "x" + height);
                runOnUiThread(() -> showScreenshot(base64Image, width, height));
            }
        });
    }

    // Bu metod bağlantı yoksa yeniden dener.
    private void reconnect() {
        String ip = prefsHelper.getServerIp();
        int port = prefsHelper.getServerPort();
        signalRManager.connect(ip, port); // reconnect yerine connect kullanmak daha güvenli
    }

    // Akıllı yenileme metodu
    private void handleRefresh() {
        if (signalRManager.isConnected()) {
            Toast.makeText(this, getString(R.string.connected), Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false); // Yenileme animasyonunu hemen durdur
        } else {
            Toast.makeText(this, "Yeniden bağlanılıyor...", Toast.LENGTH_SHORT).show();
            reconnect();
            // Animasyon 3 saniye sonra otomatik duracak
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                swipeRefresh.setRefreshing(false);
            }, 3000);
        }
    }
    
    private void updateConnectionStatus(boolean connected) {
        if (connected) {
            connectionIndicator.setBackgroundTintList(
                getColorStateList(R.color.status_connected)
            );
            connectionStatusText.setText(R.string.connected);
            swipeRefresh.setRefreshing(false); // Bağlantı başarılı, animasyonu durdur
        } else {
            connectionIndicator.setBackgroundTintList(
                getColorStateList(R.color.status_disconnected)
            );
            connectionStatusText.setText(R.string.disconnected);
            // Yenileme animasyonunu KALDIRDIK - kullanıcı manuel yenileyebilir
        }
    }
    
    private void updateUI(SystemData data) {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false); // Veri geldiğinde yenileme animasyonunu durdur
        }

        // CRITICAL FIX: Data received means we're connected!
        // Force connection status to "connected" when data arrives
        if (!signalRManager.isConnected()) {
            updateConnectionStatus(true);
        }

        if (data == null) {
            return;
        }
        
        // Update PC System Info
        displayPcSystemInfo(data);
        
        // CPU
        if (data.getCpuInfo() != null) {
            cpuUsageText.setText(String.format(Locale.getDefault(), 
                getString(R.string.cpu_usage) + ": %.1f%%", data.getCpuInfo().getUsagePercent()));
            cpuFrequencyText.setText(String.format(Locale.getDefault(), 
                getString(R.string.cpu_frequency) + ": %.2f GHz", data.getCpuInfo().getFrequency() / 1000.0));
        } 
        
        // GPU
        if (data.getGpuInfo() != null) {
            gpuNameText.setText(data.getGpuInfo().getGpuName());
            gpuUsageText.setText(String.format(Locale.getDefault(), 
                getString(R.string.gpu_usage) + ": %.1f%%", data.getGpuInfo().getUsagePercent()));
            gpuMemoryText.setText(String.format(Locale.getDefault(), 
                getString(R.string.gpu_memory) + ": %.0f MB / %.0f MB (%.1f%%)", 
                data.getGpuInfo().getMemoryUsedMB(),
                data.getGpuInfo().getMemoryTotalMB(),
                data.getGpuInfo().getMemoryUsagePercent()));
        } else {
            gpuNameText.setText(getString(R.string.gpu_not_available));
            gpuUsageText.setText(getString(R.string.gpu_usage) + ": --%");
            gpuMemoryText.setText(getString(R.string.gpu_memory) + ": -- MB / -- MB (--%)");
        }
        
        // Memory
        if (data.getMemoryInfo() != null) {
            memoryUsageText.setText(String.format(Locale.getDefault(), 
                getString(R.string.memory_used) + ": %.1f%%", data.getMemoryInfo().getUsagePercent()));
            memoryDetailsText.setText(String.format(Locale.getDefault(), 
                "%.1f GB / %.1f GB", 
                data.getMemoryInfo().getUsedGB(), 
                data.getMemoryInfo().getTotalGB()));
            
            // Swap memory
            if (data.getMemoryInfo().getSwapTotalGB() > 0) {
                swapMemoryText.setText(String.format(Locale.getDefault(), 
                    getString(R.string.memory_swap) + ": %.1f GB / %.1f GB (%.1f%%)", 
                    data.getMemoryInfo().getSwapUsedGB(),
                    data.getMemoryInfo().getSwapTotalGB(),
                    data.getMemoryInfo().getSwapUsagePercent()));
            } else {
                swapMemoryText.setText(getString(R.string.memory_swap) + ": " + getString(R.string.gpu_not_available));
            }
        }
        
        // Temperature
        if (data.getTemperatureInfo() != null) {
            if (data.getTemperatureInfo().getCpuTemp() != null) {
                cpuTempText.setText(String.format(Locale.getDefault(), 
                    "CPU: %.0f°C", data.getTemperatureInfo().getCpuTemp()));
                cpuTempText.setTextColor(getTempColor(data.getTemperatureInfo().getCpuTemp()));
            } else {
                cpuTempText.setText("CPU: " + getString(R.string.temp_not_available));
            }
            
            if (data.getTemperatureInfo().getGpuTemp() != null) {
                gpuTempText.setText(String.format(Locale.getDefault(), 
                    "GPU: %.0f°C", data.getTemperatureInfo().getGpuTemp()));
                gpuTempText.setTextColor(getTempColor(data.getTemperatureInfo().getGpuTemp()));
            } else {
                gpuTempText.setText("GPU: " + getString(R.string.temp_not_available));
            }
            
            if (data.getTemperatureInfo().getMotherboardTemp() != null) {
                motherboardTempText.setText(String.format(Locale.getDefault(), 
                    getString(R.string.temp_motherboard) + ": %.0f°C", data.getTemperatureInfo().getMotherboardTemp()));
            } else {
                motherboardTempText.setText(getString(R.string.temp_motherboard) + ": " + getString(R.string.temp_not_available));
            }
        }
        
        // Disks
        if (data.getDiskInfo() != null && !data.getDiskInfo().isEmpty()) {
            diskContainer.removeAllViews();
            for (DiskInfo disk : data.getDiskInfo()) {
                TextView diskView = new TextView(this);
                diskView.setText(String.format(Locale.getDefault(),
                    "%s: %.0f%% (%.0f GB / %.0f GB)",
                    disk.getDriveName(),
                    disk.getUsagePercent(),
                    disk.getUsedGB(),
                    disk.getTotalGB()));
                diskView.setTextSize(14);
                diskView.setPadding(0, 8, 0, 8);
                diskContainer.addView(diskView);
            }
        }
        
        // Network
        if (data.getNetworkInfo() != null) {
            // Server sends MB/s (DownloadSpeedMBps), convert to Mbps by multiplying by 8
            double downloadMbps = data.getNetworkInfo().getDownloadSpeedMbps() * 8.0;
            double uploadMbps = data.getNetworkInfo().getUploadSpeedMbps() * 8.0;
            
            networkDownloadText.setText(String.format(Locale.getDefault(), 
                "↓ %.2f Mbps", downloadMbps));
            networkUploadText.setText(String.format(Locale.getDefault(), 
                "↑ %.2f Mbps", uploadMbps));
        } else {
            networkDownloadText.setText("↓ -- Mbps");
            networkUploadText.setText("↑ -- Mbps");
        }
    }
    
    private int getTempColor(double temp) {
        if (temp >= 85) {
            return getColor(R.color.temp_critical);
        } else if (temp >= 70) {
            return getColor(R.color.temp_warning);
        } else {
            return getColor(R.color.temp_normal);
        }
    }
    
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
    private void requestScreenshot() {
        // Check storage permission for Android < 10
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                android.util.Log.d("MainActivity", "📸 Requesting WRITE_EXTERNAL_STORAGE permission");
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
                return; // Will retry after permission granted
            }
        }
        
        if (!signalRManager.isConnected()) {
            Toast.makeText(this, R.string.screenshot_not_connected, Toast.LENGTH_SHORT).show();
            android.util.Log.e("MainActivity", "Screenshot request failed: Not connected");
            return;
        }

        android.util.Log.d("MainActivity", "📸 Screenshot requested by user");
        Toast.makeText(this, R.string.screenshot_requesting, Toast.LENGTH_SHORT).show();

        screenshotTimeoutHandler.removeCallbacks(screenshotTimeoutRunnable);
        screenshotPending = true;
        screenshotTimeoutHandler.postDelayed(screenshotTimeoutRunnable, 10000);

        signalRManager.requestScreenshot(
            () -> {
                // Success - screenshot will be delivered via onScreenshotReceived
                android.util.Log.d("MainActivity", "✅ Screenshot request sent successfully");
                // Don't show toast - will show when image is received
            },
            () -> {
                // Failure
                android.util.Log.e("MainActivity", "❌ Screenshot request FAILED");
                screenshotPending = false;
                screenshotTimeoutHandler.removeCallbacks(screenshotTimeoutRunnable);
                Toast.makeText(this, R.string.screenshot_failed, Toast.LENGTH_SHORT).show();
            }
        );
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                android.util.Log.d("MainActivity", "✅ Storage permission granted, retrying screenshot");
                requestScreenshot(); // Retry screenshot after permission granted
            } else {
                android.util.Log.e("MainActivity", "❌ Storage permission DENIED");
                Toast.makeText(this, "Storage permission required to save screenshots", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showScreenshot(String base64Image, int width, int height) {
        try {
            android.util.Log.d("MainActivity", "📸 Screenshot received: " + base64Image.length() + " chars, " + width + "x" + height);
            screenshotPending = false;
            screenshotTimeoutHandler.removeCallbacks(screenshotTimeoutRunnable);
            
            // Decode base64 to bitmap
            byte[] imageBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            
            if (bitmap == null) {
                Toast.makeText(this, "Failed to decode screenshot", Toast.LENGTH_SHORT).show();
                android.util.Log.e("MainActivity", "Bitmap decode failed!");
                return;
            }
            
            android.util.Log.d("MainActivity", "✅ Bitmap decoded: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            
            // Save to gallery SILENTLY (no dialog)
            String savedPath = saveScreenshotToGallery(bitmap);
            
            if (savedPath != null) {
                android.util.Log.d("MainActivity", "✅ Screenshot saved to gallery: " + savedPath);
                Toast.makeText(this, 
                    String.format(Locale.getDefault(), "✅ Screenshot saved! (%dx%d)\nPictures/SystemMonitor/", width, height), 
                    Toast.LENGTH_LONG).show();
            } else {
                android.util.Log.e("MainActivity", "❌ Screenshot save FAILED!");
                Toast.makeText(this, 
                    "⚠️ Screenshot received but save failed. Check permissions!", 
                    Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Screenshot processing error", e);
            Toast.makeText(this, "Failed to process screenshot: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private String saveScreenshotToGallery(android.graphics.Bitmap bitmap) {
        try {
            String displayName = "SystemMonitor_" + System.currentTimeMillis() + ".png";
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // Use MediaStore for Android 10+
                android.content.ContentValues values = new android.content.ContentValues();
                values.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, displayName);
                values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/SystemMonitor");
                values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 1);

                android.net.Uri uri = getContentResolver().insert(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                );

                if (uri != null) {
                    try (java.io.OutputStream out = getContentResolver().openOutputStream(uri)) {
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                    }
                    values.clear();
                    values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0);
                    getContentResolver().update(uri, values, null, null);
                    return android.os.Environment.DIRECTORY_PICTURES + "/SystemMonitor/" + displayName;
                }
            } else {
                // Use legacy method for Android 9 and below
                java.io.File picturesDir = android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_PICTURES
                );
                java.io.File appDir = new java.io.File(picturesDir, "SystemMonitor");
                if (!appDir.exists() && !appDir.mkdirs()) {
                    return null;
                }
                
                java.io.File imageFile = new java.io.File(appDir, displayName);
                try (java.io.FileOutputStream out = new java.io.FileOutputStream(imageFile)) {
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out);
                }
                
                // Notify gallery
                android.content.Intent mediaScanIntent = new android.content.Intent(
                    android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
                );
                mediaScanIntent.setData(android.net.Uri.fromFile(imageFile));
                sendBroadcast(mediaScanIntent);
                
                return imageFile.getAbsolutePath();
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Failed to save screenshot", e);
        }
        return null;
    }
    
    private void startForegroundMonitoringService() {
        if (!prefsHelper.getNotificationSettings().isEnabled()) {
            stopService(new Intent(this, ForegroundMonitorService.class));
            stopService(new Intent(this, com.pulsarstats.mobile.services.MonitoringService.class));
            return;
        }
        
        // Start foreground service for persistent notification
        Intent foregroundIntent = new Intent(this, ForegroundMonitorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(foregroundIntent);
        } else {
            startService(foregroundIntent);
        }
        
        // Start monitoring service for threshold notifications
        Intent monitoringIntent = new Intent(this, com.pulsarstats.mobile.services.MonitoringService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(monitoringIntent);
        } else {
            startService(monitoringIntent);
        }
    }
    
    private void displayPcSystemInfo(SystemData data) {
        try {
            if (data.getSystemInfo() == null) {
                // Fallback to showing Android info if PC info not available
                displayFallbackInfo();
                return;
            }

            com.pulsarstats.mobile.models.SystemInfo sysInfo = data.getSystemInfo();
            
            // PC Computer name
            String computerName = sysInfo.getComputerName() != null ? sysInfo.getComputerName() : "Unknown";
            systemDeviceText.setText(String.format("PC: %s", computerName));
            
            // Windows OS version
            String osVersion = sysInfo.getOsVersion() != null ? sysInfo.getOsVersion() : "Unknown";
            systemOsText.setText(String.format("OS: %s", osVersion));
            
            // PC Uptime (parse from TimeSpan format)
            String uptimeStr = sysInfo.getUptime();
            systemUptimeText.setText(String.format("Uptime: %s", parseUptime(uptimeStr)));
            
            // CPU Architecture
            String arch = sysInfo.getCpuArchitecture() != null ? sysInfo.getCpuArchitecture() : "Unknown";
            systemArchText.setText(String.format("Architecture: %s", arch));
            
            // Total RAM from PC
            double totalRamGB = sysInfo.getTotalRamGB();
            if (totalRamGB > 0) {
                systemRamText.setText(String.format(Locale.getDefault(), "Total RAM: %.1f GB", totalRamGB));
            } else {
                systemRamText.setText("Total RAM: --");
            }
            
            // CPU Cores from PC
            int cores = sysInfo.getCpuCores();
            if (cores > 0) {
                systemCoresText.setText(String.format(Locale.getDefault(), "CPU Cores: %d", cores));
            } else {
                systemCoresText.setText("CPU Cores: --");
            }
            
            // User name
            String userName = sysInfo.getUserName() != null ? sysInfo.getUserName() : "Unknown";
            systemScreenText.setText(String.format("User: %s", userName));
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Failed to display PC system info", e);
            displayFallbackInfo();
        }
    }

    private String parseUptime(String uptimeStr) {
        if (uptimeStr == null || uptimeStr.isEmpty()) {
            return "Unknown";
        }
        
        try {
            // Parse TimeSpan format: "HH:MM:SS" or "D.HH:MM:SS"
            String[] parts = uptimeStr.split("[:.]");
            if (parts.length >= 3) {
                int days = 0;
                int hours, minutes;
                
                if (parts.length == 4) {
                    // Format: D.HH:MM:SS
                    days = Integer.parseInt(parts[0]);
                    hours = Integer.parseInt(parts[1]);
                    minutes = Integer.parseInt(parts[2]);
                } else {
                    // Format: HH:MM:SS
                    hours = Integer.parseInt(parts[0]);
                    minutes = Integer.parseInt(parts[1]);
                }
                
                if (days > 0) {
                    return String.format(Locale.getDefault(), "%dd %dh %dm", days, hours, minutes);
                } else {
                    return String.format(Locale.getDefault(), "%dh %dm", hours, minutes);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Failed to parse uptime: " + uptimeStr, e);
        }
        
        return uptimeStr;
    }

    private void displayFallbackInfo() {
        // Show Android info as fallback
        systemDeviceText.setText("Device: Waiting for PC data...");
        systemOsText.setText("OS: Connecting...");
        systemUptimeText.setText("Uptime: --");
        systemArchText.setText("Architecture: --");
        systemRamText.setText("Total RAM: --");
        systemCoresText.setText("CPU Cores: --");
        systemScreenText.setText("User: --");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        screenshotTimeoutHandler.removeCallbacksAndMessages(null);
        // signalRManager.disconnect();
    }
}
