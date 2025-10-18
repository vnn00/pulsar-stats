package com.pulsarstats.mobile.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.preference.PreferenceManager;
import com.pulsarstats.mobile.services.ForegroundMonitorService;

/**
 * Boot receiver to auto-start monitoring service
 */
public class BootReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Check if auto-start is enabled
            boolean autoStart = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("auto_start", false);
            
            if (autoStart) {
                Intent serviceIntent = new Intent(context, ForegroundMonitorService.class);
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            }
        }
    }
}
