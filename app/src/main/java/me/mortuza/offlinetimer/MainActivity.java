package me.mortuza.offlinetimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
    private static final String TAG = "MainActivity";
    private static double abc;
    final String LOG_LABEL = "Location Listener>>";
    SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mOrientation;
    private LocationManager mLocationManager;
    TextView checkSpeedAndTime;
    private StringBuilder stringBuilder;
    private SimpleDateFormat formatter;
    double lat;
    double lon;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSpeedAndTime = findViewById(R.id.checkSpeedAndTime);
        formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        sensorsInitialization();
        registListener();
    }

    private void sensorsInitialization() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    private void registListener() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    protected void onResume() {
        super.onResume();
        registListener();
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        double speed = 0.0;
        if (lastLocation != null) {
            speed = (Math.sqrt(
                    Math.pow(location.getLongitude() - lastLocation.getLongitude(), 2)
                            + Math.pow(location.getLatitude() - lastLocation.getLatitude(), 2)
            )) / (location.getTime() - lastLocation.getTime());
        }


        stringBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(location.getTime());

        stringBuilder.append("TIME ==" + formatter.format(calendar.getTime()));
        stringBuilder.append('\n');
        stringBuilder.append('\n');


        stringBuilder.append("manual speed maths sqrt == " + speed + " m/s");
        stringBuilder.append('\n');
        stringBuilder.append('\n');

        stringBuilder.append("manual km speed calculation == " + (int) ((speed) * 3600) / 1000 + " km/h");
        stringBuilder.append('\n');
        stringBuilder.append('\n');

        if (lastLocation != null) {


            stringBuilder.append("manual km speed sin == " + getSpeed(location,lastLocation) + " km/h");
            stringBuilder.append('\n');
            stringBuilder.append('\n');

            stringBuilder.append("manual distance== " + abc);
            stringBuilder.append('\n');
            stringBuilder.append('\n');

            stringBuilder.append("Time dif  ==" + (location.getTime() - lastLocation.getTime()));
            stringBuilder.append('\n');
            stringBuilder.append('\n');
        }


        stringBuilder.append("Accuracy == " + location.getAccuracy());
        stringBuilder.append('\n');
        stringBuilder.append('\n');

        stringBuilder.append("Speed == " + location.getSpeed() + " m/s");
        stringBuilder.append('\n');
        stringBuilder.append('\n');

        stringBuilder.append("kilo Speed == " + (int) ((location.getSpeed() * 3600) / 1000) + " km/h");
        stringBuilder.append('\n');
        stringBuilder.append('\n');

        stringBuilder.append("Latitude == " + location.getLatitude());
        stringBuilder.append('\n');

        stringBuilder.append("Longitude == " + location.getLongitude());
        stringBuilder.append('\n');
        stringBuilder.append('\n');

        stringBuilder.append("Altitude == " + location.getAltitude());
        stringBuilder.append('\n');
        stringBuilder.append('\n');

        stringBuilder.append("ElapsedRealtimeNanos == " + location.getElapsedRealtimeNanos());
        stringBuilder.append('\n');

        stringBuilder.append("Time Mil == " + location.getTime());
        stringBuilder.append('\n');
        stringBuilder.append('\n');


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stringBuilder.append("SpeedAccuracyMetersPerSecond ==" + location.getSpeedAccuracyMetersPerSecond() + " m/s");
            stringBuilder.append('\n');
            stringBuilder.append('\n');

            stringBuilder.append("SpeedAccuracykiloMetersPerSecond ==" + (int) (location.getSpeedAccuracyMetersPerSecond() * 3600) / 1000 + " km/h");
            stringBuilder.append('\n');
            stringBuilder.append('\n');
        }


        checkSpeedAndTime.setText(stringBuilder.toString());

        this.lastLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("MainActivity", "onSensorChanged: " + sensorEvent.timestamp);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d("MainActivity", "onAccuracyChanged: " + sensor.getName() + " i=" + i);
    }

    public static double getSpeed(Location currentLocation, Location oldLocation)
    {
        double newLat = currentLocation.getLatitude();
        double newLon = currentLocation.getLongitude();

        double oldLat = oldLocation.getLatitude();
        double oldLon = oldLocation.getLongitude();

        if(currentLocation.hasSpeed()){
            return currentLocation.getSpeed();
        } else {
            double radius = 6371000;
            double dLat = Math.toRadians(newLat-oldLat);
            double dLon = Math.toRadians(newLon-oldLon);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(newLat)) * Math.cos(Math.toRadians(oldLat)) *
                            Math.sin(dLon/2) * Math.sin(dLon/2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double distance =  Math.round(radius * c);

            abc=distance;

            double timeDifferent = currentLocation.getTime() - oldLocation.getTime();
            return distance/timeDifferent;
        }
    }

}


