package com.nsg.mapssample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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

public class HomeActivity extends Activity {
   // private HomeFragment.FragmentToActivity listener ;
    private TextView tv,tv1;
    private Button requestPost;
    double srcLatitude=55.067291;
    double srcLongitude=24.978782;
    double destLatitude=55.067205;
    double desLongitude=24.979878;
    private String BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator +"DubaiBasemap"+".mbtiles";
    private String tokenNumber,tokenResponse,updaterServiceResponse;
    static {
        System.loadLibrary("native-lib");
    }
    public native String stringFromJNI();
    public native int add(int v1, int v2);
    public native int sub(int v1, int v2);
    public native int multiply(int v1, int v2);
    public native int divide(int v1, int v2);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.sample_text);
        TextView tv1 = findViewById(R.id.textView);
        TextView tv2 = findViewById(R.id.textView2);
        TextView tv3 = findViewById(R.id.textView3);
        TextView tv4 = findViewById(R.id.textView4);
        tv.setText(stringFromJNI());
        tv1.setText("Addition "+add(10,20));//result --30
        tv2.setText("Substarction "+sub(20,10));//result --10
        tv3.setText("Multiplication "+multiply(20,10));//reult 200
        tv4.setText("Division "+divide(20,10)); //result--2
    }
}