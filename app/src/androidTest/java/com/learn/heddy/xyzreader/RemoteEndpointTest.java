package com.learn.heddy.xyzreader;

/**
 * Created by hyeryungpark on 3/10/17.
 */

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.learn.heddy.xyzreader.remote.Config;
import com.learn.heddy.xyzreader.remote.RemoteEndpointUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RemoteEndpointTest {
    private final String LOG_TAG = RemoteEndpointTest.class.getSimpleName();

//    @Test
//    public void testRemoteEndpointBasic(){
//        JSONArray value = RemoteEndpointUtil.fetchJsonArray();
//
//        assertNotNull(value);
//    }

    @Test
    public void letsReadTheHtml() {
        String value = null;

        try {
            value = RemoteEndpointUtil.fetchPlainText(Config.BASE_URL);
            Log.d(LOG_TAG, ""+value);
        } catch (Exception e){
            Log.d(LOG_TAG, "Exception: "+e);
        }
    }

}
