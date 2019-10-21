package com.nsg.nsgmapslibrary.samples;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.nsg.nsgmapslibrary.R;

//import static com.nsg.nsgmapslibrary.MapDisplayTest.TAG;

public class SpecialMap extends ConstraintLayout {
    MapView mapView;
    private OnMapReadyCallback mOnMapReadyCallback;
    private GoogleMap map;
    private ConstraintSet constraintSet;
    private String apikey="AIzaSyDuRBvxx9EyUSQODzJDpruKXb1gabXPmm8";

    public SpecialMap(Context context) {
        super(context);
        init();
       // inflateView();
    }

    public SpecialMap(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
       // inflateView();
    }

    public SpecialMap(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
        //inflateView();
    }

    public void onCreate(Bundle savedInstanceState, OnMapReadyCallback onMapReadyCallback,String apikey) {
        mapView.onCreate(savedInstanceState);
        mOnMapReadyCallback = onMapReadyCallback;
        apikey=apikey;
    }
    private void init() {
        setLayoutTransition(new LayoutTransition());
        constraintSet = new ConstraintSet();
        mapView = new MapView(getContext());
        mapView.setId(R.id.map);

        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_CONSTRAINT,
                LayoutParams.MATCH_CONSTRAINT
        );

        addView(mapView, 0, layoutParams);

        constraintSet.clone(this);
        constraintSet.connect(R.id.map, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(R.id.map, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        constraintSet.connect(R.id.map, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.map, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);        constraintSet.applyTo(this);

       // initOuterSpace();
       // initLayerButton();
      //  initMapType();
      //  addClickAction();
    }
    public void getMapAsync(OnMapReadyCallback callback) {
    }

    public void onResume() {
        mapView.onResume();

        try {
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(mOnMapReadyCallback);
    }

    public void onPause() {
        mapView.onPause();
    }


    public void onStart() {
        mapView.onStart();
    }


    public void onStop() {
        mapView.onStop();
    }


    public void onDestroy() {
        mapView.onDestroy();
    }
}

/*
 public void initialiseMap(){
       mapView.getMapAsync(new OnMapReadyCallback() {
           @Override
           public void onMapReady(GoogleMap googleMap) {
               map = googleMap;

               //Do what you want with the map!!
           }
       });
       MapsInitializer.initialize(getContext());
   }
    private void inflateView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.map_view,this,true);
       /// View view = LayoutInflater.from(getContext()).inflate(R.layout.map_view, this);
      //mapView = (MapView) view.findViewById(R.id.map);

        mapView = (MapView) findViewById(R.id.map);
        TextView text = (TextView) findViewById(R.id.tv);
        text.setText("HELLO MAPS");

        if(mapView != null){

          //  mapView.onCreate(null);  //Don't forget to call onCreate after get the mapView!


            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    //Do what you want with the map!!
                }
            });
            MapsInitializer.initialize(getContext());
        }else{
            Log.i("TAG", "onCreateView: mapView is null");
        }



}
 */

