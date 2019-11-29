package com.nsg.mapssample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nsg.nsgmapslibrary.Classes.HomeFragment;
import com.nsg.nsgmapslibrary.Classes.MainFragment;
import com.nsg.nsgmapslibrary.unusedClasses.NSGGetRouteOnMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends Activity implements SensorEventListener {
   // private HomeFragment.FragmentToActivity listener ;
    private TextView tv,tv1;
    private Button requestPost;
    double srcLatitude=55.067291;
    double srcLongitude=24.978782;
    double destLatitude=55.067205;
    double desLongitude=24.979878;
    private String BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator +"DubaiBasemap"+".mbtiles";
    private String tokenNumber,tokenResponse,updaterServiceResponse;


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    float azimuthInRadians;
    float azimuthInDegress;
    ImageView mPointer;
    private long lastUpdate;
   /*
    static {
        System.loadLibrary("native-lib");
    }
    public native String stringFromJNI();
    public native int add(int v1, int v2);
    public native int sub(int v1, int v2);
    public native int multiply(int v1, int v2);
    public native int divide(int v1, int v2);
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.sample_text);
        TextView tv1 = findViewById(R.id.textView);
        TextView tv2 = findViewById(R.id.textView2);
        TextView tv3 = findViewById(R.id.textView3);
        TextView tv4 = findViewById(R.id.textView4);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Bitmap circleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.navigate);


        //   mPointer = (ImageView) findViewById(R.id.pointer);

       // tv.setText(stringFromJNI());
      //  tv1.setText("Addition "+add(10,20));//result --30
      //  tv2.setText("Substarction "+sub(20,10));//result --10
      //  tv3.setText("Multiplication "+multiply(20,10));//reult 200
     //   tv4.setText("Division "+divide(20,10)); //result--2
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            float degree = Math.round(event.values[0]);
           Log.e("Degree","Degree " + degree);
            Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }
    public void startAnimation(){
        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                -azimuthInDegress,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setDuration(250);

        ra.setFillAfter(true);

        mPointer.startAnimation(ra);
        mCurrentDegree = -azimuthInDegress;
    }

}