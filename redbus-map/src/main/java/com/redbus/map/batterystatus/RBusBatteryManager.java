package com.redbus.map.batterystatus;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RBusBatteryManager {

    private static Context mContext;
    private static final String TAG  = "RBusBatteryManager";

    private static RBusBatteryManager singleton = new RBusBatteryManager( );

    /* Static 'instance' method */
    public static RBusBatteryManager getInstance(Context context) {
        mContext = context;
        return singleton;
    }

    public RBusBatteryManager() {
    }

    public void checkBatteryStatus(){

        // Read the batter status after starting the polling of location
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        Log.v(TAG, "Battery charging level" +isCharging);

        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        Log.v(TAG, "Battery charging level" +usbCharge);
        Log.v(TAG, "Battery charging level" +acCharge);


        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;

        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        Log.v(TAG, "Battery charging percentage " + batteryPct +" at particular time  is "+ currentTime);


    }


}
