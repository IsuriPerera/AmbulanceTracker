package com.isuri.ambulancetracker.apptracker;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Isuri on 2017-10-19.
 */
public class InternetChecker {

    public boolean haveNetworkConnection(Activity activity) { //internet connection checking function
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                Toast.makeText(activity, activeNetwork.getTypeName() + " - Please Wait..!", Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                Toast.makeText(activity, activeNetwork.getTypeName() + " - Please Wait..!", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else {// not connected to the internet
            return false;
        }
    }
}
