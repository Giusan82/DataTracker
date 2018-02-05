package com.example.android.datatracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.android.datatracker.utilities.Tasks;


public class StartService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("StartService", "BOOT COMPLETED");
        Tasks.sheduleNotification(context, true);
    }
}
