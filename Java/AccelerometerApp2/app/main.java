import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class AccelerometerLockActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private static final float SPEED_THRESHOLD = 5.0f; // Speed threshold in m/s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Initialize the power manager
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, getClass().getName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float speed = calculateSpeed(event.values[0], event.values[1], event.values[2]);

            if (speed > SPEED_THRESHOLD) {
                // Device is moving faster than the threshold, lock the screen
                lockScreen();
            } else {
                // Device is moving slower than the threshold, unlock the screen
                unlockScreen();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this example
    }

    private float calculateSpeed(float x, float y, float z) {
        // Calculate the speed based on accelerometer values
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    private void lockScreen() {
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire();
            // Lock the screen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void unlockScreen() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            // Unlock the screen
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
