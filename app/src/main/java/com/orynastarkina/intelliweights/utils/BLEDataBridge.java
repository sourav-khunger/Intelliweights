package com.orynastarkina.intelliweights.utils;

import android.util.Log;

/**
 * Created by Oryna Starkina on 22.03.2019.
 */
public class BLEDataBridge {

    private static String TAG = "BLEDataBridge";

    public static void onBLEData(String data){
        Log.i(TAG, data);
    }
}
