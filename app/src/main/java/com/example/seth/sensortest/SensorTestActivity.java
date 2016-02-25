package com.example.seth.sensortest;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seth.sensortest.R;

public class SensorTestActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;
    TextView textx, textz;

    Button btnShowLocation;
    GPSTracker gps;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sensor_test);

        btnShowLocation = (Button) findViewById(R.id.button1);

        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gps = new GPSTracker(SensorTestActivity.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    String coords = (String) "Lat: " + latitude + "\nLong: "
                            + longitude;
                    setLabel(coords);
                } else {
                    gps.showSettingsAlert();
                }
            }
        });

        // get textviews
        textx = (TextView) findViewById(R.id.azi);
        textz = (TextView) findViewById(R.id.zval);


        view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.BLUE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
    }

    public void setLabel(String s) {
        TextView info1 = (TextView) findViewById(R.id.coord);
        info1.setText(s);
    }

    @Override
    public void onSensorChanged (SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            displayAccelerometer(event);
            checkShake(event);
        }

    }

    private void displayAccelerometer(SensorEvent event) {

        // Many sensors return 3 values, one for each axis.

        float x = event.values[0];
        float z = event.values[2];

        // display values using TextView
        textx.setText(x + "°");
        textz.setText(Math.round(z*-9) + "°");

    }

    private void checkShake(SensorEvent event) {

        // Movement
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            Toast.makeText(this, "Don't shake me!", Toast.LENGTH_SHORT).show();
            if (color) {
                view.setBackgroundColor(Color.BLUE);

            } else {
                view.setBackgroundColor(Color.RED);
            }
            color = !color;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
//public class Compass  implements SensorEventListener
//{
//    public static final float TWENTY_FIVE_DEGREE_IN_RADIAN = 0.436332313f;
//    public static final float ONE_FIFTY_FIVE_DEGREE_IN_RADIAN = 2.7052603f;
//
//    private SensorManager mSensorManager;
//    private float[] mGravity;
//    private float[] mMagnetic;
//    // If the device is flat mOrientation[0] = azimuth, mOrientation[1] = pitch
//    // and mOrientation[2] = roll, otherwise mOrientation[0] is equal to Float.NAN
//    private float[] mOrientation = new float[3];
//    private LinkedList<Float> mCompassHist = new LinkedList<Float>();
//    private float[] mCompassHistSum = new float[]{0.0f, 0.0f};
//    private int mHistoryMaxLength;
//
//    public Compass(Context context)
//    {
//        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
//        // Adjust the history length to fit your need, the faster the sensor rate
//        // the larger value is needed for stable result.
//        mHistoryMaxLength = 20;
//    }
//
//    public void registerListener(int sensorRate)
//    {
//        Sensor magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        if (magneticSensor != null)
//        {
//            mSensorManager.registerListener(this, magneticSensor, sensorRate);
//        }
//        Sensor gravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
//        if (gravitySensor != null)
//        {
//            mSensorManager.registerListener(this, gravitySensor, sensorRate);
//        }
//    }
//
//    public void unregisterListener()
//    {
//        mSensorManager.unregisterListener(this);
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy)
//    {
//
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event)
//    {
//        if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
//        {
//            mGravity = event.values.clone();
//        }
//        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
//        {
//            mMagnetic = event.values.clone();
//        }
//        if (!(mGravity == null || mMagnetic == null))
//        {
//            mOrientation = getOrientation();
//        }
//    }
//
//    private void getOrientation()
//    {
//        float[] rotMatrix = new float[9];
//        if (SensorManager.getRotationMatrix(rotMatrix, null,
//                mGravity, mMagnetic))
//        {
//            float inclination = (float) Math.acos(rotMatrix[8]);
//            // device is flat
//            if (inclination < TWENTY_FIVE_DEGREE_IN_RADIAN
//                    || inclination > ONE_FIFTY_FIVE_DEGREE_IN_RADIAN)
//            {
//                float[] orientation = sensorManager.getOrientation(rotMatrix, mOrientation);
//                mCompassHist.add(orientation[0]);
//                mOrientation[0] = averageAngle();
//            }
//            else
//            {
//                mOrientation[0] = Float.NAN;
//                clearCompassHist();
//            }
//        }
//    }
//
//    private void clearCompassHist()
//    {
//        mCompassHistSum[0] = 0;
//        mCompassHistSum[1] = 0;
//        mCompassHist.clear();
//    }
//
//    public float averageAngle()
//    {
//        int totalTerms = mCompassHist.size();
//        if (totalTerms > mHistoryMaxLength)
//        {
//            float firstTerm = mCompassHist.removeFirst();
//            mCompassHistSum[0] -= Math.sin(firstTerm);
//            mCompassHistSum[1] -= Math.cos(firstTerm);
//            totalTerms -= 1;
//        }
//        float lastTerm = mCompassHist.getLast();
//        mCompassHistSum[0] += Math.sin(lastTerm);
//        mCompassHistSum[1] += Math.cos(lastTerm);
//        float angle = (float) Math.atan2(mCompassHistSum[0] / totalTerms, mCompassHistSum[1] / totalTerms);
//
//        return angle;
//    }
//}