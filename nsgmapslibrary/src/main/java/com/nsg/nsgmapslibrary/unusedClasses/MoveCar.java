package com.nsg.nsgmapslibrary.unusedClasses;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nsg.nsgmapslibrary.R;

import java.util.ArrayList;
import java.util.List;

public class MoveCar  extends AppCompatActivity {
    private GoogleMap googleMap;
    SupportMapFragment mapFragment;
    Marker marker;
    private boolean isMarkerRotating = false;
    ArrayList<LatLng> listOfPoints = new ArrayList<>();
    int currentPt = 0;
    LatLng finalPosition;
    Marker mMarker;
    List<LatLng> path ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        //new location details

        listOfPoints.add(new LatLng(30.701623, 76.684220));
        listOfPoints.add(new LatLng(30.702486, 76.685487));
        listOfPoints.add(new LatLng(30.703135, 76.684891));
        listOfPoints.add(new LatLng(30.703256, 76.685000));
        listOfPoints.add(new LatLng(30.703883, 76.685941));
        listOfPoints.add(new LatLng(30.703413, 76.685190));
    }

    private void setUpMapIfNeeded() {
        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        loadMap(googleMap);
                    }
                });
            }
        }
    }

    private void loadMap(GoogleMap map) {
        googleMap = map;

        mMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(30.701623, 76.684220)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));


        final Handler handler = new Handler();
        //Code to move car along static latitude and longitude

  handler.postDelayed(new Runnable() {
        @Override
        public void run() {

            if (currentPt < listOfPoints.size()) {
                //post again
                Log.d("tess", "inside run ");
                Location targetLocation = new Location(LocationManager.GPS_PROVIDER);
                targetLocation.setLatitude(listOfPoints.get(currentPt).latitude);
                targetLocation.setLongitude(listOfPoints.get(currentPt).longitude);
                animateMarkerNew(targetLocation, mMarker);
                handler.postDelayed(this, 3000);
                currentPt++;
            } else {
                Log.d("tess", "call back removed");
                //removed callbacks
                handler.removeCallbacks(this);
            }
        }
    }, 3000);
/*
        //Here move marker along real time updates
        final RequestParams params = new RequestParams();
        params.put("source_lattitude", "lat");
        params.put("source_longitude", "long");
        params.put("date", "date");

        //new handler
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                LoopjHttpClient.post(getString(R.string.default_upload_website), params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        try {
                            JSONObject jsonObject = new JSONObject(new String(responseBody));
                            String status = jsonObject.getString("status");
                            String text = jsonObject.getString("text");
                            //reading json array
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            String source = jsonArray.getJSONObject(0).getString("source");

                            String[] latLong = source.split(",");
                            Location location = new Location(LocationManager.GPS_PROVIDER);
                            location.setLatitude(Double.parseDouble(latLong[0]));
                            location.setLongitude(Double.parseDouble(latLong[1]));
                            //calling method to animate marker
                            animateMarkerNew(location, mMarker);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("onFailure", "onFailure");
                    }
                });

                handler.postDelayed(this, 3000);
            }
        }, 3000);
        */

    }


    private void animateMarkerNew(final Location destination, final Marker marker) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(15.5f)
                                .build()));

                        marker.setRotation(getBearing(startPosition, new LatLng(destination.getLatitude(), destination.getLongitude())));
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

                }
            });
            valueAnimator.start();
        }
    }

    public interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    //Method for finding bearing between two points
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
}
