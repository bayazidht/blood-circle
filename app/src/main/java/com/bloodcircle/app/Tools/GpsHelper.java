package com.bloodcircle.app.Tools;

import android.content.Context;
import android.content.Intent;

import android.location.LocationManager;
import android.provider.Settings;

import com.bloodcircle.app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class GpsHelper {

    private final Context mContext;

    public GpsHelper(Context context) {
        this.mContext = context;
    }

    public boolean isGpsEnabled() {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!gps_enabled && !network_enabled) {
            new MaterialAlertDialogBuilder(mContext)
                    .setTitle(R.string.enable_location_title)
                    .setMessage(R.string.enable_location_message)
                    .setIcon(R.drawable.ic_location)
                    .setCancelable(false)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.enable_location_title, (paramDialogInterface, paramInt) -> mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .show();
        } else {
            return true;
        }
        return false;
    }
}
