package com.nsg.mapssample;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

import com.nsg.nsgmapslibrary.Classes.MainFragment;
import com.nsg.nsgmapslibrary.Classes.NSGLiveTrackingRoutingApiClass2;

import java.io.File;

/**
 * Created by sailaja.ch NSGI on 27/09/2019
 */
public class NSGApiActivity extends FragmentActivity implements MainFragment.FragmentToActivity{
    //implements HomeFragment.FragmentToActivity{
    private double srcLatitude;
    private double srcLongitude;
    private double destLatitude;
    private double desLongitude;
    private int enteredMode;
    private int bufferSize=30;
    private String charlsisNumber;
    private String jobId="1",routeId;
    String SourcePosition="";
    String DestinationPosition="";
    private TextView tv;
    String BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator + "DubaiBasemap" + ".mbtiles";
    String CSVFile_Path= Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator + "RouteSample"+".csv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_map);

        Bundle NSGIBundle = getIntent().getExtras();
        charlsisNumber = NSGIBundle.getString("charlsisNumber");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
         if(charlsisNumber.equals("RD1")) {

             double srcLatitude=55.067291;
             double srcLongitude=24.978782;
             double destLatitude=55.067205;
             double desLongitude=24.979878;
            routeId="RD1";
            enteredMode = NSGIBundle.getInt("enteredMode");
            bufferSize = NSGIBundle.getInt("bufferSize");
            // fragmentTransaction.add(R.id.map_container, new MainMapFragment(srcLatitude,srcLongitude,destLatitude,desLongitude,1,bufferSize));//getRoutes Direction
            fragmentTransaction.add(R.id.map_container, new MainFragment(BASE_MAP_URL_FORMAT,CSVFile_Path,jobId,"RD1",1,bufferSize));//getRoutes Direction
        }else if(charlsisNumber.equals("RD2")) {
            routeId="RD2";
            enteredMode = NSGIBundle.getInt("enteredMode");
            bufferSize = NSGIBundle.getInt("bufferSize");
            Log.e("Route Details------", " Route Details------ " +" srcLatitude : "+ srcLatitude +"\n"+" srcLongitude : "+ srcLongitude +"\n"+" destLatitude : "+destLatitude+"\n"+" desLongitude : "+desLongitude+"\n");
            fragmentTransaction.add(R.id.map_container, new NSGLiveTrackingRoutingApiClass2(BASE_MAP_URL_FORMAT,CSVFile_Path,jobId,"RD2",enteredMode,bufferSize));//getRoutes Direction

        }
        fragmentTransaction.commit();
    }
    @Override
    public String communicate(String comm) {
        Log.d("received", "Recieved From ETA Listener---"+ comm);
         tv=(TextView)findViewById(R.id.tv);
         tv.setText(comm);

        //tv1=(TextView)findViewById(R.id.text1);
      //  tv1.setText(comm);
        return comm;
    }
    public void onResume() {
        super.onResume();
    }
}