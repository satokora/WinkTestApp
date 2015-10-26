package com.it494.skora.turnpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.Handler;

public class EyeGesture extends BroadcastReceiver {
    private int count = 0;
    public EyeGesture() {
        count = 0;
    }
    private String currentCount;
    private final Handler mHandler = new Handler();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra("gesture").equals("WINK")) {
            count++;
            //Disable Camera Snapshot
            abortBroadcast();
            setCurrentCount("Winked " + count + " times");


        } else {
            Log.e("SOMETHING", "is detected " + intent.getStringExtra("gesture"));
        }
    }

    public void setCurrentCount(String strCount){
        currentCount = strCount;
    }
    public String getCurrentCount(){
        return currentCount;
    }
}
