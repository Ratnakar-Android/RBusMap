package com.redbus.map.batterystatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PowerConnectionReceiver extends BroadcastReceiver {

    private static final String TAG  = "PowerConnectionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            Log.v(TAG, "The device is charging");
        } else {
            intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED);
            Log.v(TAG, "The device is not charging");
        }
    }
}
