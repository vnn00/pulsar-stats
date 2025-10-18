package com.pulsarstats.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.pulsarstats.mobile.models.NotificationSettings;
import com.pulsarstats.mobile.utils.LocaleHelper;
import com.pulsarstats.mobile.utils.PreferencesHelper;
import com.pulsarstats.mobile.utils.SignalRManager;

public class SettingsActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private PreferencesHelper prefsHelper;
    private NotificationSettings settings;
    
    // Language
    private RadioGroup languageGroup;
    private RadioButton languageSystem;
    private RadioButton languageTurkish;
    private RadioButton languageEnglish;
    
    // Theme
    private RadioGroup themeGroup;
    private RadioButton themeSystem;
    private RadioButton themeLight;
    private RadioButton themeDark;
    
    // Update Interval
    private RadioGroup updateIntervalGroup;
    private RadioButton interval1sec;
    private RadioButton interval3sec;
    private RadioButton interval5sec;
    private RadioButton interval10sec;
    
    // Genel
    private SwitchMaterial notificationEnabledSwitch;
    private SwitchMaterial alwaysRepeatSwitch;
    private RadioGroup repeatIntervalGroup;
    private RadioButton interval5min;
    private RadioButton interval15min;
    private RadioButton interval30min;
    private RadioButton interval60min;
    
    // CPU
    private SwitchMaterial cpuEnabledSwitch;
    private Slider cpuThresholdSlider;
    private TextView cpuThresholdText;
    
    // RAM
    private SwitchMaterial ramEnabledSwitch;
    private Slider ramThresholdSlider;
    private TextView ramThresholdText;
    
    // CPU Temp
    private SwitchMaterial cpuTempEnabledSwitch;
    private Slider cpuTempThresholdSlider;
    private TextView cpuTempThresholdText;
    
    // GPU Temp
    private SwitchMaterial gpuTempEnabledSwitch;
    private Slider gpuTempThresholdSlider;
    private TextView gpuTempThresholdText;
    
    // Disk Temp

    private TextView updateIntervalLabel;
    private TextView repeatIntervalLabel;
    private boolean lastNotificationsEnabled;
    private SwitchMaterial diskTempEnabledSwitch;
    private Slider diskTempThresholdSlider;
    private TextView diskTempThresholdText;
    
    // Auto Start
    private SwitchMaterial autoStartSwitch;
    private SwitchMaterial keepScreenOnSwitch;
    
    // Buttons
    private MaterialButton changeServerButton;
    
    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.pulsarstats.mobile.utils.ThemeUtils.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        
        // Apply saved locale before setContentView
        LocaleHelper.setLocale(this);
        
        setContentView(R.layout.activity_settings);
        
        prefsHelper = new PreferencesHelper(this);
        settings = prefsHelper.getNotificationSettings();
        
        initViews();
        setupToolbar();
        loadSettings();
        setupListeners();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        
        languageGroup = findViewById(R.id.languageGroup);
        languageSystem = findViewById(R.id.languageSystem);
        languageTurkish = findViewById(R.id.languageTurkish);
        languageEnglish = findViewById(R.id.languageEnglish);
        
        themeGroup = findViewById(R.id.themeGroup);
        themeSystem = findViewById(R.id.themeSystem);
        themeLight = findViewById(R.id.themeLight);
        themeDark = findViewById(R.id.themeDark);
        
        updateIntervalGroup = findViewById(R.id.updateIntervalGroup);
    updateIntervalLabel = findViewById(R.id.updateIntervalLabel);
        interval1sec = findViewById(R.id.interval1sec);
        interval3sec = findViewById(R.id.interval3sec);
        interval5sec = findViewById(R.id.interval5sec);
        interval10sec = findViewById(R.id.interval10sec);
        
        notificationEnabledSwitch = findViewById(R.id.notificationEnabledSwitch);
        alwaysRepeatSwitch = findViewById(R.id.alwaysRepeatSwitch);
        repeatIntervalGroup = findViewById(R.id.repeatIntervalGroup);
    repeatIntervalLabel = findViewById(R.id.repeatIntervalLabel);
        interval5min = findViewById(R.id.interval5min);
        interval15min = findViewById(R.id.interval15min);
        interval30min = findViewById(R.id.interval30min);
        interval60min = findViewById(R.id.interval60min);
        
        cpuEnabledSwitch = findViewById(R.id.cpuEnabledSwitch);
        cpuThresholdSlider = findViewById(R.id.cpuThresholdSlider);
        cpuThresholdText = findViewById(R.id.cpuThresholdText);
        
        ramEnabledSwitch = findViewById(R.id.ramEnabledSwitch);
        ramThresholdSlider = findViewById(R.id.ramThresholdSlider);
        ramThresholdText = findViewById(R.id.ramThresholdText);
        
        cpuTempEnabledSwitch = findViewById(R.id.cpuTempEnabledSwitch);
        cpuTempThresholdSlider = findViewById(R.id.cpuTempThresholdSlider);
        cpuTempThresholdText = findViewById(R.id.cpuTempThresholdText);
        
        gpuTempEnabledSwitch = findViewById(R.id.gpuTempEnabledSwitch);
        gpuTempThresholdSlider = findViewById(R.id.gpuTempThresholdSlider);
        gpuTempThresholdText = findViewById(R.id.gpuTempThresholdText);
        
        diskTempEnabledSwitch = findViewById(R.id.diskTempEnabledSwitch);
        diskTempThresholdSlider = findViewById(R.id.diskTempThresholdSlider);
        diskTempThresholdText = findViewById(R.id.diskTempThresholdText);
        
        autoStartSwitch = findViewById(R.id.autoStartSwitch);
        keepScreenOnSwitch = findViewById(R.id.keepScreenOnSwitch);
        
        changeServerButton = findViewById(R.id.changeServerButton);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void loadSettings() {
        // Language
        String currentLanguage = LocaleHelper.getCurrentLanguage(this);
        if (currentLanguage.equals("tr")) {
            languageTurkish.setChecked(true);
        } else if (currentLanguage.equals("en")) {
            languageEnglish.setChecked(true);
        } else {
            languageSystem.setChecked(true);
        }
        
        // Genel
        notificationEnabledSwitch.setChecked(settings.isEnabled());
        lastNotificationsEnabled = settings.isEnabled();
        alwaysRepeatSwitch.setChecked(settings.isAlwaysRepeat());
        
        // Tekrar aralığı
        int interval = settings.getRepeatInterval();
        if (interval == 5) {
            interval5min.setChecked(true);
        } else if (interval == 15) {
            interval15min.setChecked(true);
        } else if (interval == 30) {
            interval30min.setChecked(true);
        } else if (interval == 60) {
            interval60min.setChecked(true);
        }
        
        // CPU
        cpuEnabledSwitch.setChecked(settings.isCpuEnabled());
        cpuThresholdSlider.setValue(settings.getCpuThreshold());
        cpuThresholdText.setText(getString(R.string.threshold_label, settings.getCpuThreshold(), "%"));
        
        // RAM
        ramEnabledSwitch.setChecked(settings.isRamEnabled());
        ramThresholdSlider.setValue(settings.getRamThreshold());
        ramThresholdText.setText(getString(R.string.threshold_label, settings.getRamThreshold(), "%"));
        
        // CPU Temp
        cpuTempEnabledSwitch.setChecked(settings.isCpuTempEnabled());
        cpuTempThresholdSlider.setValue(settings.getCpuTempThreshold());
        cpuTempThresholdText.setText(getString(R.string.threshold_label, settings.getCpuTempThreshold(), "°C"));
        
        // GPU Temp
        gpuTempEnabledSwitch.setChecked(settings.isGpuTempEnabled());
        gpuTempThresholdSlider.setValue(settings.getGpuTempThreshold());
        gpuTempThresholdText.setText(getString(R.string.threshold_label, settings.getGpuTempThreshold(), "°C"));
        
        // Disk Temp
        diskTempEnabledSwitch.setChecked(settings.isDiskTempEnabled());
        diskTempThresholdSlider.setValue(settings.getDiskTempThreshold());
        diskTempThresholdText.setText(getString(R.string.threshold_label, settings.getDiskTempThreshold(), "°C"));
        
        // Auto Start (from separate SharedPreference)
        android.content.SharedPreferences prefs = getSharedPreferences("SystemMonitorPrefs", MODE_PRIVATE);
        autoStartSwitch.setChecked(prefs.getBoolean("auto_start", false));
        keepScreenOnSwitch.setChecked(prefs.getBoolean("keep_screen_on", false));
        
        // Theme
        String themeMode = com.pulsarstats.mobile.utils.ThemeUtils.getSavedTheme(this);
        if (com.pulsarstats.mobile.utils.ThemeUtils.MODE_LIGHT.equals(themeMode)) {
            themeLight.setChecked(true);
        } else if (com.pulsarstats.mobile.utils.ThemeUtils.MODE_DARK.equals(themeMode)) {
            themeDark.setChecked(true);
        } else {
            themeSystem.setChecked(true);
        }
        
        // Update Interval
        int updateInterval = prefs.getInt("update_interval_seconds", 3);
        if (updateInterval == 1) {
            interval1sec.setChecked(true);
        } else if (updateInterval == 3) {
            interval3sec.setChecked(true);
        } else if (updateInterval == 5) {
            interval5sec.setChecked(true);
        } else if (updateInterval == 10) {
            interval10sec.setChecked(true);
        }

        applyNotificationEnabledState(settings.isEnabled());
    }
    
    private void setupListeners() {
        // Language change
        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String language;
            if (checkedId == R.id.languageTurkish) {
                language = "tr";
            } else if (checkedId == R.id.languageEnglish) {
                language = "en";
            } else {
                language = "system";
            }
            
            LocaleHelper.setLocale(this, language);
            
            // Restart app to apply new language
            Toast.makeText(this, "Dil değiştirildi. Uygulama yeniden başlatılıyor...", Toast.LENGTH_SHORT).show();
            
            // Restart the app completely
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            
            // Kill the process to ensure clean restart
            android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            handler.postDelayed(() -> {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }, 500);
        });
        
        // Theme change
        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String mode;
            if (checkedId == R.id.themeLight) {
                mode = com.pulsarstats.mobile.utils.ThemeUtils.MODE_LIGHT;
            } else if (checkedId == R.id.themeDark) {
                mode = com.pulsarstats.mobile.utils.ThemeUtils.MODE_DARK;
            } else {
                mode = com.pulsarstats.mobile.utils.ThemeUtils.MODE_SYSTEM;
            }
            com.pulsarstats.mobile.utils.ThemeUtils.saveTheme(this, mode);
        });
        
        // Update Interval change
        updateIntervalGroup.setOnCheckedChangeListener((group, checkedId) -> {
            android.content.SharedPreferences prefs = getSharedPreferences("SystemMonitorPrefs", MODE_PRIVATE);
            int seconds;
            if (checkedId == R.id.interval1sec) {
                seconds = 1;
            } else if (checkedId == R.id.interval3sec) {
                seconds = 3;
            } else if (checkedId == R.id.interval5sec) {
                seconds = 5;
            } else if (checkedId == R.id.interval10sec) {
                seconds = 10;
            } else {
                seconds = 3; // default
            }
            
            // LOG: Debug için
            android.util.Log.d("SettingsActivity", "🔄 Update interval changed to: " + seconds + " seconds");
            
            // Save to SharedPreferences
            prefs.edit().putInt("update_interval_seconds", seconds).apply();
            
            // Verify save
            int saved = prefs.getInt("update_interval_seconds", -1);
            android.util.Log.d("SettingsActivity", "✅ Verified saved interval: " + saved + " seconds");
            
            // Restart ForegroundMonitorService to apply new interval IMMEDIATELY
            try {
                Intent serviceIntent = new Intent(this, com.pulsarstats.mobile.services.ForegroundMonitorService.class);
                stopService(serviceIntent);
                android.util.Log.d("SettingsActivity", "🛑 Service stopped");
                
                // Small delay to ensure complete stop
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    Intent restartIntent = new Intent(this, com.pulsarstats.mobile.services.ForegroundMonitorService.class);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        startForegroundService(restartIntent);
                    } else {
                        startService(restartIntent);
                    }
                    android.util.Log.d("SettingsActivity", "▶️ Service restarted with interval: " + seconds + "s");
                }, 500);
                
            } catch (Exception e) {
                android.util.Log.e("SettingsActivity", "❌ Service restart failed", e);
            }
            
            Toast.makeText(this, "Update interval: " + seconds + "s", Toast.LENGTH_SHORT).show();
        });
        
        // Auto-save on switch changes
        notificationEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applyNotificationEnabledState(isChecked);
            autoSave();
        });
        alwaysRepeatSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applyNotificationEnabledState(notificationEnabledSwitch.isChecked());
            autoSave();
        });
        repeatIntervalGroup.setOnCheckedChangeListener((group, checkedId) -> autoSave());
        cpuEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> autoSave());
        ramEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> autoSave());
        cpuTempEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> autoSave());
        gpuTempEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> autoSave());
        diskTempEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> autoSave());
        
        // Auto-start switch
        autoStartSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            android.content.SharedPreferences prefs = getSharedPreferences("SystemMonitorPrefs", MODE_PRIVATE);
            prefs.edit().putBoolean("auto_start", isChecked).apply();
            String message = isChecked ? "Auto-start enabled" : "Auto-start disabled";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
        
        // Slider listeners with auto-save
        cpuThresholdSlider.addOnChangeListener((slider, value, fromUser) -> {
            cpuThresholdText.setText(getString(R.string.threshold_label, (int)value, "%"));
            if (fromUser) autoSave();
        });
        
        ramThresholdSlider.addOnChangeListener((slider, value, fromUser) -> {
            ramThresholdText.setText(getString(R.string.threshold_label, (int)value, "%"));
            if (fromUser) autoSave();
        });
        
        cpuTempThresholdSlider.addOnChangeListener((slider, value, fromUser) -> {
            cpuTempThresholdText.setText(getString(R.string.threshold_label, (int)value, "°C"));
            if (fromUser) autoSave();
        });
        
        gpuTempThresholdSlider.addOnChangeListener((slider, value, fromUser) -> {
            gpuTempThresholdText.setText(getString(R.string.threshold_label, (int)value, "°C"));
            if (fromUser) autoSave();
        });
        
        diskTempThresholdSlider.addOnChangeListener((slider, value, fromUser) -> {
            diskTempThresholdText.setText(getString(R.string.threshold_label, (int)value, "°C"));
            if (fromUser) autoSave();
        });
        
        // Buttons
        changeServerButton.setOnClickListener(v -> changeServer());
    }
    
    private void autoSave() {
        // Otomatik kaydet (sessizce)
        saveSettingsQuietly();
        boolean notificationsEnabled = settings.isEnabled();
        if (notificationsEnabled != lastNotificationsEnabled) {
            updateBackgroundServices(notificationsEnabled);
            lastNotificationsEnabled = notificationsEnabled;
        }
    }
    
    private void saveSettingsQuietly() {
        // Genel
        settings.setEnabled(notificationEnabledSwitch.isChecked());
        settings.setAlwaysRepeat(alwaysRepeatSwitch.isChecked());
        
        // Tekrar aralığı
        int checkedId = repeatIntervalGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.interval5min) {
            settings.setRepeatInterval(5);
        } else if (checkedId == R.id.interval15min) {
            settings.setRepeatInterval(15);
        } else if (checkedId == R.id.interval30min) {
            settings.setRepeatInterval(30);
        } else if (checkedId == R.id.interval60min) {
            settings.setRepeatInterval(60);
        }
        
        // CPU
        settings.setCpuEnabled(cpuEnabledSwitch.isChecked());
        settings.setCpuThreshold((int)cpuThresholdSlider.getValue());
        
        // RAM
        settings.setRamEnabled(ramEnabledSwitch.isChecked());
        settings.setRamThreshold((int)ramThresholdSlider.getValue());
        
        // CPU Temp
        settings.setCpuTempEnabled(cpuTempEnabledSwitch.isChecked());
        settings.setCpuTempThreshold((int)cpuTempThresholdSlider.getValue());
        
        // GPU Temp
        settings.setGpuTempEnabled(gpuTempEnabledSwitch.isChecked());
        settings.setGpuTempThreshold((int)gpuTempThresholdSlider.getValue());
        
        // Disk Temp
        settings.setDiskTempEnabled(diskTempEnabledSwitch.isChecked());
        settings.setDiskTempThreshold((int)diskTempThresholdSlider.getValue());
        
        // Save to preferences (sessizce, toast yok)
        prefsHelper.saveNotificationSettings(settings);
        
        // Notify MonitoringService to refresh settings and reset timestamps
        if (settings.isEnabled()) {
            Intent serviceIntent = new Intent(this, com.pulsarstats.mobile.services.MonitoringService.class);
            serviceIntent.setAction("REFRESH_SETTINGS");
            startService(serviceIntent);
        }
    }
    
    private void changeServer() {
        // Clear saved server IP and port
        prefsHelper.saveServerIp("");
        prefsHelper.saveServerPort(5000);
        SignalRManager.getInstance(this).disconnect();
        
        // Redirect to splash screen with force IP entry flag
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("FORCE_IP_ENTRY", true); // Don't auto-connect
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void applyNotificationEnabledState(boolean notificationsEnabled) {
        alwaysRepeatSwitch.setEnabled(notificationsEnabled);
        alwaysRepeatSwitch.setAlpha(notificationsEnabled ? 1f : 0.5f);
        updateIntervalLabel.setEnabled(notificationsEnabled);
        updateIntervalLabel.setAlpha(notificationsEnabled ? 1f : 0.5f);
        setRadioGroupEnabled(updateIntervalGroup, notificationsEnabled);

        boolean showRepeatOptions = !alwaysRepeatSwitch.isChecked();
        repeatIntervalLabel.setVisibility(showRepeatOptions ? View.VISIBLE : View.GONE);
        if (showRepeatOptions) {
            repeatIntervalLabel.setAlpha((notificationsEnabled) ? 1f : 0.5f);
        }
        repeatIntervalGroup.setVisibility(showRepeatOptions ? View.VISIBLE : View.GONE);
        setRadioGroupEnabled(repeatIntervalGroup, notificationsEnabled && showRepeatOptions);
    }

    private void setRadioGroupEnabled(RadioGroup group, boolean enabled) {
        group.setEnabled(enabled);
        group.setAlpha(enabled ? 1f : 0.5f);
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof RadioButton) {
                child.setAlpha(enabled ? 1f : 0.5f);
            } else {
                child.setAlpha(1f);
            }
        }
    }

    private void updateBackgroundServices(boolean notificationsEnabled) {
        Intent foregroundIntent = new Intent(this, com.pulsarstats.mobile.services.ForegroundMonitorService.class);
        Intent monitoringIntent = new Intent(this, com.pulsarstats.mobile.services.MonitoringService.class);

        if (notificationsEnabled) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                androidx.core.content.ContextCompat.startForegroundService(this, foregroundIntent);
                androidx.core.content.ContextCompat.startForegroundService(this, monitoringIntent);
            } else {
                startService(foregroundIntent);
                startService(monitoringIntent);
            }
        } else {
            stopService(foregroundIntent);
            stopService(monitoringIntent);
        }
    }
}
