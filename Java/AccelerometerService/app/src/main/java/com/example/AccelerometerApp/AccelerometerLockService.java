package com.example.AccelerometerApp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.accelerometerservice.R;

public class AccelerometerLockService extends Service implements SensorEventListener {

    private static final int NOTIFICATION_ID = 1234196516; // Notification ID
    private static final float SPEED_THRESHOLD = 100f;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Register the sensor listener
        if (accelerometer != null) {
            assert sensorManager != null;
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public void onDestroy() {
        // Unregister the sensor listener and remove the notification when the service is destroyed
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float idleSpeed = 9.810014f;
            float speed = calculateSpeed(event.values[0], event.values[1], event.values[2]) - idleSpeed;
            Log.d("Debug", "Running");
            if (speed > SPEED_THRESHOLD) {
                // Implement your lock screen logic here
                // For simplicity, let's log a message
                Log.d("Speed", "Speed: " + speed + " m/s - Locking screen");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this example
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private float calculateSpeed(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, AccelerometerLockService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("AccelerometerLockService is running")
                .setContentText("Tracking device speed")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .setOngoing(true); // Make the notification ongoing to keep it persistent

        return builder.build();
    }
}
