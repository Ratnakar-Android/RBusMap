package com.redbus.map.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.redbus.map.R;
import com.redbus.map.batterystatus.RBusBatteryManager;

public class MapViewActivity extends AppCompatActivity {

    private static final String TAG  = "MapViewActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //RedBusMapProvider.getInstance(this);


    }
}
