package com.nsg.mapssample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

import com.nsg.nsgmapslibrary.Classes.HomeFragment;
import com.nsg.nsgmapslibrary.Classes.NSGLiveTrackingRoutingApiClass1;

public class HomeActivity extends FragmentActivity implements HomeFragment.FragmentToActivity {
   // private HomeFragment.FragmentToActivity listener ;
    private TextView tv,tv1;
    private double srcLatitude=55.067291;
    private double srcLongitude=24.978782;
    private double destLatitude=55.067205;
    private double desLongitude=24.979878;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_map);
        Fragment frag=new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_container, new HomeFragment(srcLatitude,srcLongitude,destLatitude,desLongitude,1,20));//getRoutes Direction
        fragmentTransaction.commit();
    }
    @Override
    public String communicate(String comm) {
        Log.d("received", "Recieved---"+ comm);
       // tv=(TextView)findViewById(R.id.tv);
       // tv.setText(comm);
        tv1=(TextView)findViewById(R.id.tv1);
        tv1.setText(comm);
        return comm;
    }
    public void onResume() {
        super.onResume();
    }
}