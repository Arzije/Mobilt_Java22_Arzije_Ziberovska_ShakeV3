package com.example.shakev3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView x_value;
    private TextView y_value;
    private TextView z_value;
    private ImageView imageView;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor lightSensor;
    private long lastShakeTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.design);

        x_value = findViewById(R.id.x_value);
        y_value = findViewById(R.id.y_value);
        z_value = findViewById(R.id.z_value);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        handleLightSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometerSensor != null) {
            sensorManager.unregisterListener(this, accelerometerSensor);
        }
        if (lightSensor != null) {
            sensorManager.unregisterListener(this, lightSensor);
        }
    }

    private void handleLightSensor() {
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xValue = sensorEvent.values[0];
            float yValue = sensorEvent.values[1];
            float zValue = sensorEvent.values[2];

            x_value.setText("X-value: " + xValue);
            y_value.setText("Y-value: " + yValue);
            z_value.setText("Z-value: " + zValue);

            double accelerationMagnitude = Math.sqrt(xValue * xValue + yValue * yValue + zValue * zValue);

            final double shakeThreshold = 11.0;
            final long shakeInterval = 1000;

            if (accelerationMagnitude > shakeThreshold) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShakeTime > shakeInterval) {
                    // Display a log message
                    Log.d("ShakeDetection", "Shake detected!");
                    Log.d("Sensor-valueX",  "X-value: " + xValue);
                    Log.d("Sensor-valueY",  "Y-value: " + yValue);
                    Log.d("Sensor-valueZ",  "Z-value: " + zValue);

                    lastShakeTime = currentTime;
                    rotateImage(xValue, yValue, zValue);
                }
            }
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            handleLightSensorChange(sensorEvent.values[0]);
        }
    }

    private void handleLightSensorChange(float lightValue) {
        FragmentManager fm = getSupportFragmentManager();
        ColorFragment colorFragment = (ColorFragment) fm.findFragmentById(R.id.frame);

        final float lightThreshold = 15000;

        if (lightValue > lightThreshold) {
            if (colorFragment == null) {
                fm.beginTransaction()
                        .add(R.id.frame, ColorFragment.class, null)
                        .commit();
            } else {
                colorFragment.getView().setVisibility(View.VISIBLE);
            }
        } else {
            if (colorFragment != null) {
                colorFragment.getView().setVisibility(View.GONE);
            }
        }
    }

    private void rotateImage(float x, float y, float z) {
        float xDegree = (float) Math.toDegrees(x);
        float yDegree = (float) Math.toDegrees(y);
        float zDegree = (float) Math.toDegrees(z);

        imageView.setRotation(imageView.getRotation() + xDegree + yDegree + zDegree);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (accelerometerSensor != null) {
            sensorManager.unregisterListener(this, accelerometerSensor);
        }
        if (lightSensor != null) {
            sensorManager.unregisterListener(this, lightSensor);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accelerometerSensor != null) {
            sensorManager.unregisterListener(this, accelerometerSensor);
        }
        if (lightSensor != null) {
            sensorManager.unregisterListener(this, lightSensor);
        }
    }
}
