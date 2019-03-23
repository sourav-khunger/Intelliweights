package com.orynastarkina.intelliweights.utils;

import android.util.Log;


public class BLEDataBridge {

    private static String TAG = "BLEDataBridge";
    public static void onBLEData(String data){
        Log.i(TAG, data);
    }
}
