package com.nsg.mapssample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

import com.nsg.nsgmapslibrary.Classes.MainFragment;
import com.nsg.nsgmapslibrary.Classes.NSGLiveTrackingRoutingApiClass2;
import com.nsg.nsgmapslibrary.Classes.NSGLiveTrackingRoutingApiClass3;
import com.nsg.nsgmapslibrary.Classes.NSGRoutingDirectionApi;

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
    String SourcePosition="";
    String DestinationPosition="";
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_map);

        Bundle NSGIBundle = getIntent().getExtras();
        charlsisNumber = NSGIBundle.getString("charlsisNumber");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
         if( charlsisNumber.equals("1HGBH41JXMN109185")) {
            srcLatitude = NSGIBundle.getDouble("srcLatitude");
            srcLongitude = NSGIBundle.getDouble("srcLongitude");
            destLatitude = NSGIBundle.getDouble("destLatitude");
            desLongitude = NSGIBundle.getDouble("desLongitude");
            enteredMode = NSGIBundle.getInt("enteredMode");
            bufferSize = NSGIBundle.getInt("bufferSize");
            Log.e("Route Details------", " Route Details------ " +" srcLatitude : "+ srcLatitude +"\n"+" srcLongitude : "+ srcLongitude +"\n"+" destLatitude : "+destLatitude+"\n"+" desLongitude : "+desLongitude+"\n");
            fragmentTransaction.add(R.id.map_container, new MainFragment(srcLatitude,srcLongitude,destLatitude,desLongitude,1,bufferSize));//getRoutes Direction
            //fragmentTransaction.add(R.id.map_container, new HomeFragment());
         }
         if(charlsisNumber.equals("1HGBH41JXMN109186")) {
            srcLatitude = NSGIBundle.getDouble("srcLatitude");
            srcLongitude = NSGIBundle.getDouble("srcLongitude");
            destLatitude = NSGIBundle.getDouble("destLatitude");
            desLongitude = NSGIBundle.getDouble("desLongitude");
            enteredMode = NSGIBundle.getInt("enteredMode");
            bufferSize = NSGIBundle.getInt("bufferSize");
            Log.e("Route Details------", " Route Details------ " +" srcLatitude : "+ srcLatitude +"\n"+" srcLongitude : "+ srcLongitude +"\n"+" destLatitude : "+destLatitude+"\n"+" desLongitude : "+desLongitude+"\n");
            fragmentTransaction.add(R.id.map_container, new NSGLiveTrackingRoutingApiClass2(srcLatitude,srcLongitude,destLatitude,desLongitude,enteredMode,bufferSize));//getRoutes Direction

        }
        else if( charlsisNumber.equals("1HGBH41JXMN109187")) {
            srcLatitude = NSGIBundle.getDouble("srcLatitude");
            srcLongitude = NSGIBundle.getDouble("srcLongitude");
            destLatitude = NSGIBundle.getDouble("destLatitude");
            desLongitude = NSGIBundle.getDouble("desLongitude");
            enteredMode = NSGIBundle.getInt("enteredMode");
            bufferSize = NSGIBundle.getInt("bufferSize");
            Log.e("Route Details------", " Route Details------ " +" srcLatitude : "+ srcLatitude +"\n"+" srcLongitude : "+ srcLongitude +"\n"+" destLatitude : "+destLatitude+"\n"+" desLongitude : "+desLongitude+"\n");
            fragmentTransaction.add(R.id.map_container, new NSGLiveTrackingRoutingApiClass3(srcLatitude,srcLongitude,destLatitude,desLongitude,enteredMode,bufferSize));//getRoutes Direction
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