package com.example.AccelerometerApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Received BOOT_COMPLETED broadcast");

            Intent serviceIntent = new Intent(context, AccelerometerLockService.class);
            serviceIntent.setAction("com.example.AccelerometerApp.START_FOREGROUND_SERVICE");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e("Debug", "Starting as foreground service");
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
                Log.e("Debug", "mmmm cheese");
            }
        }
    }
}
