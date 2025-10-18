package com.pulsarstats.mobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pulsarstats.mobile.utils.LocaleHelper;
import com.pulsarstats.mobile.utils.PreferencesHelper;
import com.pulsarstats.mobile.utils.SignalRManager;

public class SplashActivity extends AppCompatActivity {
    
    private TextInputEditText ipEditText;
    private TextInputEditText portEditText;
    private MaterialButton connectButton;
    private ProgressBar progressBar;
    private TextView statusTextView;
    
    private PreferencesHelper prefsHelper;
    private SignalRManager signalRManager;
    
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    checkBatteryOptimization();
                } else {
                    Toast.makeText(this, "Bildirim izni reddedildi. Ayarlardan manuel olarak açabilirsiniz.", Toast.LENGTH_SHORT).show();
                    checkBatteryOptimization();
                }
            });
    
    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.pulsarstats.mobile.utils.ThemeUtils.applySavedTheme(this);
        // Apply saved locale
        LocaleHelper.setLocale(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        prefsHelper = new PreferencesHelper(this);
        signalRManager = SignalRManager.getInstance(this);
        
        initViews();
        checkPermissions();
        checkSavedServer();
        setupListeners();
    }
    
    private void checkPermissions() {
        // Android 9 ve altı için storage permission
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ için notification permission gerekli
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                checkBatteryOptimization();
            }
        } else {
            checkBatteryOptimization();
        }
    }
    
    private void checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            String packageName = getPackageName();
            
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                new AlertDialog.Builder(this)
                        .setTitle("Pil Optimizasyonu")
                        .setMessage("Uygulamanın arka planla doğru çalışabilmesi için pil optimizasyonu kapatılacaktır. Seçeneğinizi bir sonraki ekranda belirtiniz.")
                        .setPositiveButton("Devam", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.parse("package:" + packageName));
                            startActivity(intent);
                        })
                        .show();
            }
        }
    }

    
    private void initViews() {
        ipEditText = findViewById(R.id.ipEditText);
        portEditText = findViewById(R.id.portEditText);
        connectButton = findViewById(R.id.connectButton);
        progressBar = findViewById(R.id.progressBar);
        statusTextView = findViewById(R.id.statusTextView);
    }
    
    private void checkSavedServer() {
        // Check if we're being forced to enter a new IP (coming from "Change Server")
        boolean forceIpEntry = getIntent().getBooleanExtra("FORCE_IP_ENTRY", false);
        
        if (forceIpEntry) {
            // User wants to change server - show empty form
            statusTextView.setText("Enter new server address");
            statusTextView.setVisibility(View.VISIBLE);
            return;
        }
        
        String savedIp = prefsHelper.getServerIp();
        if (savedIp != null && !savedIp.trim().isEmpty()) {
            int savedPort = prefsHelper.getServerPort();
            
            // Auto-fill the fields
            ipEditText.setText(savedIp);
            portEditText.setText(String.valueOf(savedPort));
            
            statusTextView.setText("Saved server found. Auto-connecting...");
            statusTextView.setVisibility(View.VISIBLE);
            
            // Auto-connect after 1 second
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                attemptConnection();
            }, 1000);
        }
    }
    
    private void setupListeners() {
        connectButton.setOnClickListener(v -> attemptConnection());
        
        signalRManager.setListener(new SignalRManager.SystemDataListener() {
            @Override
            public void onDataReceived(com.pulsarstats.mobile.models.SystemData data) {
                // Bu listener artık veri işlemeyecek, boş bırakılabilir.
            }
            
            @Override
            public void onConnectionChanged(boolean connected) {
                runOnUiThread(() -> {
                    if (connected) {
                        hideProgress();
                        openMainActivity();
                    } else {
                        hideProgress();
                        showError(getString(R.string.error_connection_failed));
                    }
                });
            }
            
            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    hideProgress();
                    showError(getString(R.string.error_connection_failed) + "\n" + e.getMessage());
                });
            }

            @Override
            public void onScreenshotReceived(String base64Image, int width, int height) {
                // SplashActivity'de ekran görüntüsü ile ilgili bir işlem yapılmayacak.
            }
        });
    }
    
    private void attemptConnection() {
        String ip = ipEditText.getText() != null ? ipEditText.getText().toString().trim() : "";
        String portStr = portEditText.getText() != null ? portEditText.getText().toString().trim() : "5000";
        
        if (ip.isEmpty()) {
            showError(getString(R.string.error_invalid_ip));
            return;
        }
        
        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            port = 5000;
        }
        
        showProgress();
        
        // Save IP and port BEFORE connecting
        prefsHelper.saveServerIp(ip);
        prefsHelper.saveServerPort(port);
        
        // Connect to server
        signalRManager.connect(ip, port);
        
        // Connection timeout - if not connected in 15 seconds, show error
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (!signalRManager.isConnected()) {
                runOnUiThread(() -> {
                    hideProgress();
                    showError("Bağlantı zaman aşımına uğradı. Lütfen kontrol edin:\n• Sunucu çalışıyor mu\n• IP adresi doğru mu\n• Port 5000 açık mı");
                });
            }
        }, 15000); // 15 seconds timeout
    }
    
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void showProgress() {
        connectButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        statusTextView.setText(R.string.connecting);
        statusTextView.setVisibility(View.VISIBLE);
    }
    
    private void hideProgress() {
        connectButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }
    
    private void showError(String message) {
        statusTextView.setText(message);
        statusTextView.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
