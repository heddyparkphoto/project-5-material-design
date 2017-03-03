package com.learn.heddy.xyzreader.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by hyeryungpark on 3/2/17.
 */

public class Utility {

    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return (networkInfo!=null && networkInfo.isConnectedOrConnecting());
    }
}
