package com.example.AccelerometerApp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.accelerometerapp.R;

public class AccelerometerLockService extends Service {
    private static final int NOTIFICATION_ID = 1; // Unique ID for the notification

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            startAccelerometerLockActivity();
            // Example: Display a notification
            showForegroundNotification();

            return START_STICKY; // Keeps the service running after the app is killed
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private void showForegroundNotification() {
            // Create a notification channel (required for Android 8.0 and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "channel_id",
                        "Foreground Service Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                }
            }

            // Create a notification
            Notification notification = new NotificationCompat.Builder(this, "channel_id")
                    .setContentTitle("Your App is Running")
                    .setContentText("Your background task is ongoing")
                    .setSmallIcon(R.drawable.ic_notification_icon)
                    .build();

            // Display the notification
            startForeground(NOTIFICATION_ID, notification);
        }

    private void startAccelerometerLockActivity() {
        Intent intent = new Intent(this, AccelerometerLockActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    }

