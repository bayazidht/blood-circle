package com.bloodcircle.app.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    private final SharedPreferences sharedPref;
    private final Context mContext;
    private final String localeCode;

    public LocaleHelper(Context context) {
        this.mContext = context;

        this.sharedPref = context.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE);
        localeCode = sharedPref.getString(SELECTED_LANGUAGE, "bn");
    }

    public String getLocal() {
        return sharedPref.getString(SELECTED_LANGUAGE, "bn");
    }

    public void setLocale(String languageCode) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SELECTED_LANGUAGE, languageCode);
        editor.apply();
    }

    public void setAppLocale() {
        Resources resources = mContext.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(localeCode));
        resources.updateConfiguration(configuration, displayMetrics);
    }
}
