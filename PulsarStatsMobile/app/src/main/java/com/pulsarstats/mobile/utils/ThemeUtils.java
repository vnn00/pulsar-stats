package com.pulsarstats.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Centralized helper for persisting and applying the app theme across activities.
 */
public final class ThemeUtils {

    private static final String PREF_NAME = "SystemMonitorPrefs";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String LEGACY_THEME_KEY = "tema";

    public static final String MODE_SYSTEM = "system";
    public static final String MODE_LIGHT = "light";
    public static final String MODE_DARK = "dark";

    private ThemeUtils() {
        // Utility class
    }

    /**
     * Applies the persisted theme immediately.
     */
    public static void applySavedTheme(Context context) {
        String mode = getSavedTheme(context);
        applyThemeMode(mode);
    }

    /**
     * Persists the provided theme mode and applies it.
     */
    public static void saveTheme(Context context, String mode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_THEME_MODE, mode).apply();
        applyThemeMode(mode);
    }

    /**
     * Returns the stored theme mode, migrating legacy values if necessary.
     */
    public static String getSavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        migrateLegacyThemeIfNeeded(prefs);
        return prefs.getString(KEY_THEME_MODE, MODE_SYSTEM);
    }

    /**
     * Applies the requested theme mode via AppCompatDelegate.
     */
    public static void applyThemeMode(String mode) {
        int nightMode;
        if (MODE_LIGHT.equals(mode)) {
            nightMode = AppCompatDelegate.MODE_NIGHT_NO;
        } else if (MODE_DARK.equals(mode)) {
            nightMode = AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    private static void migrateLegacyThemeIfNeeded(SharedPreferences prefs) {
        if (prefs.contains(KEY_THEME_MODE)) {
            return; // Already migrated
        }

        if (!prefs.contains(LEGACY_THEME_KEY)) {
            return;
        }

        String legacyValue = prefs.getString(LEGACY_THEME_KEY, MODE_SYSTEM);
        String normalized = normalizeLegacyTheme(legacyValue);
        prefs.edit()
                .putString(KEY_THEME_MODE, normalized)
                .remove(LEGACY_THEME_KEY)
                .apply();
    }

    private static String normalizeLegacyTheme(String legacyValue) {
        if (legacyValue == null) {
            return MODE_SYSTEM;
        }

        String value = legacyValue.trim().toLowerCase();
        if (value.contains("ayd") || value.contains("light")) {
            return MODE_LIGHT;
        }
        if (value.contains("kar") || value.contains("dark")) {
            return MODE_DARK;
        }
        return MODE_SYSTEM;
    }
}
