package com.example.seth.sensortest;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SensorTestActivity extends Activity implements SensorEventListener {

    @Bind(R.id.day1) TextView textDay1;
    @Bind(R.id.day2) TextView textDay2;
    @Bind(R.id.day3) TextView textDay3;
    @Bind(R.id.day4) TextView textDay4;
    @Bind(R.id.day5) TextView textDay5;
    @Bind(R.id.day6) TextView textDay6;
    @Bind(R.id.day7) TextView textDay7;
    @Bind(R.id.hiLow1) TextView textHiLow1;
    @Bind(R.id.hiLow2) TextView textHiLow2;
    @Bind(R.id.hiLow3) TextView textHiLow3;
    @Bind(R.id.hiLow4) TextView textHiLow4;
    @Bind(R.id.hiLow5) TextView textHiLow5;
    @Bind(R.id.hiLow6) TextView textHiLow6;
    @Bind(R.id.hiLow7) TextView textHiLow7;
    @Bind(R.id.weather1) ImageView imageWeather1;
    @Bind(R.id.weather2) ImageView imageWeather2;
    @Bind(R.id.weather3) ImageView imageWeather3;
    @Bind(R.id.weather4) ImageView imageWeather4;
    @Bind(R.id.weather5) ImageView imageWeather5;
    @Bind(R.id.weather6) ImageView imageWeather6;
    @Bind(R.id.weather7) ImageView imageWeather7;

    public static final String TAG = SensorTestActivity.class.getSimpleName();

    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;
    TextView textx, textz, textTemp;
    private int mAzimuth = 0;

    Button btnShowLocation;
    GPSTracker gps;

    float[] orientation = new float[3];
    float[] rMat = new float[9];

    String apiKey = "4b0324a6ed0c367123374acdc3476970";
    double latitude = 37.8267;
    double longitude = -122.423;
    String forecastURL = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude;

    private CurrentWeather mCurrentWeather;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sensor_test);

        ButterKnife.bind(this);

        btnShowLocation = (Button) findViewById(R.id.button1);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gps = new GPSTracker(SensorTestActivity.this);

                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    forecastURL = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude;

                    String coords = (String) "Lat: " + latitude + "\nLong: "
                            + longitude;
                    setLabel(coords);

                    getForecast();
                } else {
                    gps.showSettingsAlert();
                }
            }
        });

        // get textviews
        textx = (TextView) findViewById(R.id.azi);
        textz = (TextView) findViewById(R.id.zval);
        textTemp = (TextView) findViewById(R.id.temp);


        view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.BLUE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
    }

    public void getForecast() {
        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
    }

    private void updateDisplay() {
        TextView[] textViewDays = {textDay1,textDay2,textDay3,textDay4,textDay5,textDay6,textDay7};
        TextView[] textViewHiLows = {textHiLow1,textHiLow2,textHiLow3,textHiLow4,textHiLow5,textHiLow6,textHiLow7};
        ImageView[] imageViewWeathers = {imageWeather1,imageWeather2,imageWeather3,imageWeather4,imageWeather5,imageWeather6,imageWeather7};
        Drawable drawable;

        textTemp.setText(mCurrentWeather.getTemperature() + "°");
        for (int i = 0; i < 7; i++) {
            textViewDays[i].setText(mCurrentWeather.getFormattedTime(i) + ".");
            drawable = getResources().getDrawable(mCurrentWeather.getIconId(i));
            imageViewWeathers[i].setImageDrawable(drawable);
            textViewHiLows[i].setText(mCurrentWeather.getLowTemp()[i] + "°-" + mCurrentWeather.getHighTemp()[i] + "°");
        }
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray values = daily.getJSONArray("data");
        for (int i = 0; i < 7; i++) {
            JSONObject days = values.getJSONObject(i);
            currentWeather.setIcon(days.getString("icon"), i);
            currentWeather.setHighTemp(days.getDouble("temperatureMax"), i);
            currentWeather.setLowTemp(days.getDouble("temperatureMin"), i);
            currentWeather.setTime(days.getLong("time"), i);
        }

        Log.d(TAG, currentWeather.getFormattedTime(0));

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
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

        if( event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ){
            // calculate th rotation matrix
            SensorManager.getRotationMatrixFromVector( rMat, event.values );
            // get the azimuth value (orientation[0]) in degree
            mAzimuth = (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;
        }

    }

    private void displayAccelerometer(SensorEvent event) {

        // Many sensors return 3 values, one for each axis.

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];


        // display values using TextView
        textx.setText(mAzimuth + "°");
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