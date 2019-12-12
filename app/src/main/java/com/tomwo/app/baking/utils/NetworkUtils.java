package com.tomwo.app.baking.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by wooldridgetm on 9/16/17.
 */

public class NetworkUtils
{
    public static boolean isConnected(Context context)
    {
        if (context == null) return false;

        ConnectivityManager mgmr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            mgmr = context.getSystemService(ConnectivityManager.class);
        } else
        {
            mgmr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        NetworkInfo networkInfo = mgmr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

}
