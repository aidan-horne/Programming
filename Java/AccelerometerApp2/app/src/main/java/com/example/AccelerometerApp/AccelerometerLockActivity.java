package com.example.AccelerometerApp;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.accelerometerapp.R;

public class AccelerometerLockActivity extends AppCompatActivity implements LocationListener  {

    private static final float SPEED_THRESHOLD = 5f;
    private boolean isScreenLocked = false; // Track the screen lock state
    private boolean codeToggled = false;

    private final ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // The user accepted the device admin activation
                    Intent data = result.getData();
                } else {
                    promptUserToEnableDeviceAdmin();
                }
            });

    private void promptUserToEnableDeviceAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        launchSomeActivity.launch(intent);
    }

    private boolean isDeviceAdmin() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, MyDeviceAdminReceiver.class);

        return devicePolicyManager != null &&
                devicePolicyManager.isAdminActive(componentName);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the sensor manager
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Initialize the location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Find views by ID
        Button btnToggleLock = findViewById(R.id.btnToggleLock);
        Button btnToggleCode = findViewById(R.id.btnToggleCode);

        // Set click listeners
        btnToggleLock.setOnClickListener(view -> toggleLock());
        btnToggleCode.setOnClickListener(view -> toggleCode());

        // Request device administrator privileges
        if (!isDeviceAdmin()) {
            promptUserToEnableDeviceAdmin();
        }

        // Request GPS location updates
        requestLocationUpdates(locationManager);
    }

    // New method to request GPS location updates
    private void requestLocationUpdates(LocationManager locationManager) {
        if (locationManager != null) {
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000, // Update interval in milliseconds
                        1,    // Update distance in meters
                        this   // LocationListener
                );
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (codeToggled) {
            updateStatus("Status: Active");
        } else {
            updateStatus("Status: Inactive");
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Original accelerometer code
            float idleSpeed = 9.810014f;
            float speed = calculateSpeed(event.values[0], event.values[1], event.values[2]) - idleSpeed;

            processSpeed(speed);
        }
    }

    // New method to handle GPS speed
    private void processSpeed(float speed) {
        if (isDeviceAdmin()) {
            Log.d("Speed", "Speed: " + speed + " m/s");

            if (codeToggled) {
                if (speed > SPEED_THRESHOLD) {
                    // Device is moving faster than the threshold, lock the screen
                    try {
                        if (isDeviceAdmin()) {
                            lockScreen();
                            Log.d("LockState", "Screen Locked");
                        }
                    } catch (SecurityException ex) {
                        Toast.makeText(this, "You must enable this app as a device administrator\n\n" + "Please enable it and press the back button to return here.", Toast.LENGTH_LONG).show();
                        ComponentName admin = new ComponentName(this, MyDeviceAdminReceiver.class);
                        Intent intent = new Intent(
                                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
                                DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
                        this.startActivity(intent);
                    }
                } else {
                    // Device is moving slower than the threshold, unlock the screen
                    if (isScreenLocked) {
                        unlockScreen();
                    }
                }
            }
        }
    }

    // New method to handle GPS updates
    @Override
    public void onLocationChanged(Location location) {
        // Access GPS speed information from the Location object
        float gpsSpeed = location.getSpeed(); // Speed in meters/second
        processSpeed(gpsSpeed);
    }



    private void lockScreen() {
        if (isDeviceAdmin()) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (devicePolicyManager != null) {
                devicePolicyManager.lockNow();
                isScreenLocked = true;
            } else {
                // Handle the case where DevicePolicyManager is null
                updateStatus("Status: Error locking screen");
            }
        } else {
            // Handle the case where the app is not a device admin
            updateStatus("Status: Not a device admin");
        }
    }

    private void unlockScreen() {
        if (isDeviceAdmin()) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (devicePolicyManager != null) {
                devicePolicyManager.lockNow();
                isScreenLocked = false;
                Log.d("LockState", "Screen Unlocked");
            } else {
                // Handle the case where DevicePolicyManager is null
                updateStatus("Status: Error unlocking screen");
            }
        } else {
            // Handle the case where the app is not a device admin
            updateStatus("Status: Not a device admin");
        }
    }

    private void updateStatus(String status) {
        // Assuming you have a TextView with the ID txtStatus to display the status
        TextView txtStatus = findViewById(R.id.tvStatus);
        if (txtStatus != null) {
            txtStatus.setText(status);
        } else {
            Log.e("AccelerometerLockActivity", "TextView with ID txtStatus not found.");
        }
    }

    private float calculateSpeed(float x, float y, float z) {
        // Calculate the speed based on accelerometer values
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    private void toggleLock() {
        if (isScreenLocked) {
            unlockScreen();
        } else {
            lockScreen();
        }
    }

    private void toggleCode() {
        codeToggled = !codeToggled;
    }
}
