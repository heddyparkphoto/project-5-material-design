package com.learn.heddy.xyzreader.remote;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/*
 * This file is from the Udacity starter code and has minor updates.
 *
 * 1.  Switch to Student's forum-provided url when the url in the starter code stopped working
 * before I completed this project.
 *
 */
public class Config {
    public static final URL BASE_URL;
    private static final String LOG_TAG = Config.class.getSimpleName();

    static {
        URL url = null;
        try {
            /*
             * Commenting out the out-of-service url temporarily
                url = new URL("https://dl.dropboxusercontent.com/u/231329/xyzreader_data/data.json" );
            */
            url = new URL("https://gist.githubusercontent.com/AtomicSpider/12b731e3e2a41f336cc1bd7fc904efbb/raw/3e6da3480b132f6d0eb99ca3711da4a2c901627e/data.json");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, ""+ e);
        }

        BASE_URL = url;
    }
}
