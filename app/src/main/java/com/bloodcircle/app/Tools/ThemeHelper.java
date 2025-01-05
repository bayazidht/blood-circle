package com.bloodcircle.app.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    private static final String SELECTED_THEME = "Theme.Helper.Selected.Theme";
    private final SharedPreferences sharedPref;

    public ThemeHelper(Context context) {
        this.sharedPref = context.getSharedPreferences("THEME", Context.MODE_PRIVATE);
    }

    public int getSelectedTheme() {
        return sharedPref.getInt(SELECTED_THEME, -1);
    }

    public void setSelectedTheme(int themeCode) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SELECTED_THEME, themeCode);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(themeCode);
    }
}
