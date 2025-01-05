package com.bloodcircle.app.Tools;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import com.bloodcircle.app.R;
import com.google.android.material.snackbar.Snackbar;

public class NetworkHelper {

    private final Context mContext;

    public NetworkHelper(Context context) {
        this.mContext = context;
        //((Activity)mContext).setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (!isNetworkAvailable()) {
            Snackbar snackbar = Snackbar.make(((Activity)mContext).findViewById(R.id.main), R.string.no_internet_message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.refresh, view -> ((Activity) mContext).recreate());
            snackbar.show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
