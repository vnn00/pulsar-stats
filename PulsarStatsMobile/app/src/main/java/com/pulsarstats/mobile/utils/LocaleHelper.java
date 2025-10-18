package com.pulsarstats.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "selected_language";
    private static final String PREFS_NAME = "SystemMonitorPrefs";

    public static Context setLocale(Context context, String language) {
        persist(context, language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    public static Context setLocale(Context context) {
        return setLocale(context, getPersistedLanguage(context));
    }

    private static String getPersistedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(SELECTED_LANGUAGE, "system");
    }

    private static void persist(Context context, String language) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(SELECTED_LANGUAGE, language).apply();
    }

    private static Context updateResources(Context context, String language) {
        Locale locale;
        if (language.equals("system")) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else if (language.equals("en")) {
            // English is the default, use "en" explicitly to override system locale
            locale = Locale.forLanguageTag("en");
        } else {
            locale = Locale.forLanguageTag(language);
        }
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }

    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale;
        if (language.equals("system")) {
            locale = Resources.getSystem().getConfiguration().locale;
        } else if (language.equals("en")) {
            // English is the default, use "en" explicitly to override system locale
            locale = Locale.forLanguageTag("en");
        } else {
            locale = Locale.forLanguageTag(language);
        }
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    public static String getCurrentLanguage(Context context) {
        return getPersistedLanguage(context);
    }
}
