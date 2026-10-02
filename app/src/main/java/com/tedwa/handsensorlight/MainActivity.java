package com.tedwa.handsensorlight;

import android.app.Activity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.content.Context;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean lightOn = false;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);

        sensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        proximitySensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        cameraManager =
                (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            status.setText("Flashlight unavailable");
        }

        if (proximitySensor == null) {
            status.setText("Proximity sensor not available");
        } else {
            status.setText("Bring your hand near the phone");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (proximitySensor != null) {
            sensorManager.registerListener(
                    this,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        turnLight(false);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

            float distance = event.values[0];

            if (distance < proximitySensor.getMaximumRange()) {
                turnLight(true);
                status.setText("HAND DETECTED\nLIGHT ON");
            } else {
                turnLight(false);
                status.setText("HAND AWAY\nLIGHT OFF");
            }
        }
    }

    private void turnLight(boolean on) {

        try {
            if (cameraId != null && lightOn != on) {
                cameraManager.setTorchMode(cameraId, on);
                lightOn = on;
            }
        } catch (Exception e) {
            status.setText("Flashlight error");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
  }
