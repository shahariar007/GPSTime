package me.mortuza.offlinetimer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    TextView checkSpeedAddress;
    TextView checkSpeedManual;
    TextView checkSpeedTime;
    TextView working;
    int x = 0;
    private PermissionManager mRequestPermissionHandler;

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
        checkSpeedAddress = findViewById(R.id.checkSpeedAddress);
        checkSpeedManual = findViewById(R.id.checkSpeedManual);
        checkSpeedTime = findViewById(R.id.checkSpeedTime);
        working = findViewById(R.id.working);
        formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        handlePermissionClicked();

        if (!isLocationEnabled(this)) {
            Toast.makeText(this, "Please turn on your location service", Toast.LENGTH_SHORT).show();
            return;
        }

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

    private void registListener() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    protected void onResume() {
        super.onResume();
        if(mSensorManager!=null)
        registListener();
    }

    protected void onPause() {
        super.onPause();
        if (mSensorManager!=null)
        mSensorManager.unregisterListener(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {

        stringBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(location.getTime());
//
//        stringBuilder.append("TIME ==" + formatter.format(calendar.getTime()));
//        stringBuilder.append('\n');
//        stringBuilder.append('\n');
//
//
//        stringBuilder.append("manual speed maths sqrt == " + speed + " m/s");
//        stringBuilder.append('\n');
//        stringBuilder.append('\n');
//
//        stringBuilder.append("manual km speed calculation == " + (int) ((speed) * 3600) / 1000 + " km/h");
//        stringBuilder.append('\n');
//        stringBuilder.append('\n');
//
//        if (lastLocation != null) {
//
//
//            stringBuilder.append("manual km speed sin == " + ((getSpeed(location, lastLocation) * 3600) / 1000) + " km/h");
//            stringBuilder.append('\n');
//            stringBuilder.append('\n');
//
//            stringBuilder.append("manual distance== " + abc);
//            stringBuilder.append('\n');
//            stringBuilder.append('\n');
//
//            stringBuilder.append("Time dif  ==" + (location.getTime() - lastLocation.getTime()));
//            stringBuilder.append('\n');
//            stringBuilder.append('\n');
//        }
//
//
//        stringBuilder.append("Accuracy == " + location.getAccuracy());
//        stringBuilder.append('\n');
//        stringBuilder.append('\n');
//
//        stringBuilder.append("Speed == " + location.getSpeed() + " m/s");
//        stringBuilder.append('\n');
//        stringBuilder.append('\n');
//
//        stringBuilder.append("kilo Speed == " + (int) ((location.getSpeed() * 3600) / 1000) + " km/h");
//        stringBuilder.append('\n');
//        stringBuilder.append('\n');
//
//        stringBuilder.append("Latitude == " + location.getLatitude());
//        stringBuilder.append('\n');
//
//        stringBuilder.append("Longitude == " + location.getLongitude());
//        stringBuilder.append('\n');
//        stringBuilder.append('\n');
//
//        stringBuilder.append("Altitude == " + location.getAltitude());
//        stringBuilder.append('\n');
//        stringBuilder.append('\n');
//
//        stringBuilder.append("ElapsedRealtimeNanos == " + location.getElapsedRealtimeNanos());
//        stringBuilder.append('\n');
//
//        stringBuilder.append("Time Mil == " + location.getTime());
//        stringBuilder.append('\n');
//        stringBuilder.append('\n');
//
//        stringBuilder.append("Address == " + getAddress(location.getLatitude(), location.getLongitude()));
//        stringBuilder.append('\n');
//        stringBuilder.append('\n');
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            stringBuilder.append("SpeedAccuracyMetersPerSecond ==" + location.getSpeedAccuracyMetersPerSecond() + " m/s");
//            stringBuilder.append('\n');
//            stringBuilder.append('\n');
//
//            stringBuilder.append("SpeedAccuracykiloMetersPerSecond ==" + (int) (location.getSpeedAccuracyMetersPerSecond() * 3600) / 1000 + " km/h");
//            stringBuilder.append('\n');
//            stringBuilder.append('\n');
//        }

        checkSpeedTime.setText(formatter.format(calendar.getTime()) + " ");
        checkSpeedAndTime.setText((int) ((location.getSpeed() * 3600) / 1000) + "");
        checkSpeedAddress.setText(getAddress(location.getLatitude(), location.getLongitude()));

        if (lastLocation != null)
            checkSpeedManual.setText(((int) (getSpeed(location, lastLocation) * 3600) / 1000) + "");

        this.lastLocation = location;
        x++;
        working.setText(" working count= " + x);
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

    public static double getSpeed(Location currentLocation, Location oldLocation) {
        double newLat = currentLocation.getLatitude();
        double newLon = currentLocation.getLongitude();

        double oldLat = oldLocation.getLatitude();
        double oldLon = oldLocation.getLongitude();

        if (currentLocation.hasSpeed()) {
            return currentLocation.getSpeed();
        } else {
            double radius = 6371000;
            double dLat = Math.toRadians(newLat - oldLat);
            double dLon = Math.toRadians(newLon - oldLon);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(newLat)) * Math.cos(Math.toRadians(oldLat)) *
                            Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double distance = Math.round(radius * c);

            abc = distance;

            double timeDifferent = currentLocation.getTime() - oldLocation.getTime();
            return ((distance / timeDifferent) * 3600) / 1000;
        }
    }

    public String getAddress(double a, double b) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(a, b, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            return address;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private void handlePermissionClicked() {
        mRequestPermissionHandler = new PermissionManager();
        mRequestPermissionHandler.requestPermission(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
        }, 123, new RequestPermissionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Permission success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed() {
                Toast.makeText(MainActivity.this, "Please access this permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionHandler.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
}


