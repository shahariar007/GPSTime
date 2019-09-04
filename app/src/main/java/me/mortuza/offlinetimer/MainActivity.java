package me.mortuza.offlinetimer;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.mortuza.offlinetimer.model.SpeedUPModel;
import me.mortuza.offlinetimer.roomDatabase.AppDatabase;
import me.mortuza.offlinetimer.roomDatabase.DatabaseInitializer;

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
    // TextView checkSpeedManual;
    TextView checkSpeedTime;
    ImageView imageCar;
    TextView working;
    TextView topSpeed;
    TextView totalRun;
    TextView avg;
    int x = 0;
    private PermissionManager mRequestPermissionHandler;

    // private StringBuilder stringBuilder;
    private SimpleDateFormat formatter;
    double lat;
    double lon;
    Location lastLocation;
    SpeedUPModel speedUPModel;
    int TOP_SPEED = -1;
    boolean flagInsert = true;

    String Time = "";
    int ACTUAL_SPEED = 0;
    int MANUAL_SPEED = 0;
    String ADDRESS = "";
    private Calendar calendar;
    private ValueAnimator valueAnimator;
    private TextView mLot;
    private TextView mAcc;
    private TextView mLat;
    private TextView mSpeed;
    int S0TO30 = 1;
    int S30TO60 = 2;
    int S60TO80 = 3;
    int S80TO100 = 4;
    int LAST_STAGE = 0;
    int NOW_STATE = 0;
    int TOTAL_KILO = 0;
    DecimalFormat topFormat = new DecimalFormat("000");
    DecimalFormat speedFormat = new DecimalFormat("000");
    DecimalFormat totalSpeedKmh = new DecimalFormat("00.##");
    int totalSpeed = 0;
    private SpannableStringBuilder builder;
    private int generalLength;
    private SpannableString str3;
    private int avgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_layout);
        checkSpeedAndTime = findViewById(R.id.checkSpeedAndTime);
        checkSpeedAndTime.setTextColor(Color.DKGRAY);
        checkSpeedAddress = findViewById(R.id.checkSpeedAddress);
        // checkSpeedManual = findViewById(R.id.checkSpeedManual);
        checkSpeedTime = findViewById(R.id.checkSpeedTime);
        imageCar = findViewById(R.id.imageCar);
        working = findViewById(R.id.working);
        topSpeed = findViewById(R.id.topSpeed);
        totalRun = findViewById(R.id.totalRun);
        avg = findViewById(R.id.avg);
        formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        handlePermissionClicked();
        calendar = Calendar.getInstance();

        if (!isLocationEnabled(this)) {
            Toast.makeText(this, "Please turn on your location service", Toast.LENGTH_SHORT).show();
            return;
        }

        sensorsInitialization();
        registListener();
        TOP_SPEED = DatabaseInitializer.getLastContent(AppDatabase.getAppDatabase(this));
        topSpeed.setText(topFormat.format(TOP_SPEED) + " ");
        mLot = findViewById(R.id.lot);
        mAcc = findViewById(R.id.acc);
        mLat = findViewById(R.id.lat);
        mSpeed = findViewById(R.id.speed);
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
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, this);
    }

    protected void onResume() {
        super.onResume();
        if (mSensorManager != null)
            registListener();
    }

    protected void onPause() {
        super.onPause();
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(final Location location) {

        //stringBuilder = new StringBuilder();

        calendar.setTimeInMillis(location.getTime());

        Time = formatter.format(calendar.getTime()) + " ";
        ACTUAL_SPEED = (int) ((location.getSpeed() * 3600) / 1000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAddress(location.getLatitude(), location.getLongitude());
            }
        }).start();

        //MANUAL_SPEED = ((int) (getSpeed(location, lastLocation) * 3600) / 1000);

        checkSpeedTime.setText(Time);
        checkSpeedAndTime.setText(setTexts(String.valueOf(speedFormat.format(ACTUAL_SPEED))), TextView.BufferType.SPANNABLE);

        // if (lastLocation != null)
        //checkSpeedManual.setText(MANUAL_SPEED + " ");
        totalSpeed += location.getSpeed();
        totalRun.setText(totalSpeedKmh.format((float) totalSpeed / 1000));


        this.lastLocation = location;
        x++;
        working.setText("Working ON: " + x + " Second");
        topSpeed.setText(String.valueOf(topFormat.format(TOP_SPEED)));
        avgs = (((totalSpeed / x) * 3600) / 1000);
        avg.setText(String.valueOf(speedFormat.format(avgs)));

        if (TOP_SPEED <= ACTUAL_SPEED) {
            flagInsert = true;
            TOP_SPEED = ACTUAL_SPEED;
        } else {
            flagInsert = false;
        }

        if (flagInsert) {
            speedUPModel = new SpeedUPModel();
            speedUPModel.setAddress(ADDRESS);
            speedUPModel.setDateTimes(Time + " ");
            speedUPModel.setLat((float) location.getLatitude());
            speedUPModel.setLat((float) location.getLongitude());
            speedUPModel.setTopSpeed(ACTUAL_SPEED);
        } else {
            speedUPModel = null;
        }
        mAcc.setText("Accuracy : " + location.getAccuracy());
        mLat.setText("Lat : " + location.getLatitude());
        mLot.setText("Lon : " + location.getLongitude());
        mSpeed.setText("Altitude : " + location.getAltitude());
        insert();
        stateCal(ACTUAL_SPEED);


    }

    public void insert() {
        if (speedUPModel != null && flagInsert)
            DatabaseInitializer.insert(AppDatabase.getAppDatabase(this), speedUPModel);
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
        if (currentLocation == null || oldLocation == null)
            return 0.0;
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
    //TO

    public void getAddress(double a, double b) {

        if (!isNetworkConnected()) {
            ADDRESS = "Turn on Internet for address";
        }

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
            ADDRESS = address;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        checkSpeedAddress.setText(ADDRESS);
                    } catch (Exception e) {
                        e.getMessage();
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
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

    public void animation(int state) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        valueAnimator = new ValueAnimator();
        ColorDrawable drawable = (ColorDrawable) imageCar.getBackground();
        int startColor = drawable.getColor();

        if (state == S0TO30) {
            valueAnimator.setIntValues(startColor, Color.MAGENTA);
        } else if (state == S30TO60) {
            valueAnimator.setIntValues(startColor, Color.BLUE);
        } else if (state == S60TO80) {
            valueAnimator.setIntValues(startColor, Color.GREEN);
        } else if (state == S80TO100) {
            valueAnimator.setIntValues(startColor, Color.RED);
        }
        valueAnimator.setDuration(3000);
        //valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                imageCar.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }

    public void stateCal(int speed) {

        if (speed > -1 && speed <= 30) {
            NOW_STATE = S0TO30;
        } else if (speed >= 30 && speed <= 60) {
            NOW_STATE = S30TO60;
        } else if (speed >= 60 && speed <= 80) {
            NOW_STATE = S60TO80;
        } else {
            NOW_STATE = S80TO100;
        }
        if (LAST_STAGE != NOW_STATE) {
            animation(NOW_STATE);
        }
        LAST_STAGE = NOW_STATE;
    }

    @Override
    protected void onDestroy() {
        if (valueAnimator != null) {
            valueAnimator.cancel();

        }
        super.onDestroy();
    }

    public SpannableStringBuilder setTexts(String originalText) {
        generalLength = 3;
        String[] x = originalText.split("(?!^)");
        builder = new SpannableStringBuilder();
        if (originalText.length() == 3) {
            for (String s : x) {
                if (Integer.valueOf(s) == 0) {
                    generalLength -= 1;
                } else {
                    break;
                }
            }
            Log.d("DigitUIActivity", "setTexts: " + generalLength);

            if (generalLength == 3) {
                str3 = new SpannableString(originalText);
                str3.setSpan(new ForegroundColorSpan(Color.RED), 0, originalText.length(), 0);
                builder.append(str3);
            } else if (generalLength == 2) {
                str3 = new SpannableString(originalText);
                str3.setSpan(new ForegroundColorSpan(Color.RED), 1, originalText.length(), 0);
                builder.append(str3);
            } else if (generalLength == 1) {
                str3 = new SpannableString(originalText);
                str3.setSpan(new ForegroundColorSpan(Color.RED), 2, originalText.length(), 0);
                builder.append(str3);
            } else {
                str3 = new SpannableString(originalText);
                str3.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, originalText.length(), 0);
                builder.append(str3);
            }
        }
        checkSpeedAndTime.setTextColor(Color.DKGRAY);
        return builder;

    }
}


