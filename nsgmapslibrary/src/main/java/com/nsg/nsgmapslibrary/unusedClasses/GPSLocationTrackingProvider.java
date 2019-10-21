package com.nsg.nsgmapslibrary.unusedClasses;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.nsg.nsgmapslibrary.Classes.NSGGetRouteOnMap;
import com.nsg.nsgmapslibrary.database.db.SqlHandler;
import com.nsg.nsgmapslibrary.database.dto.EdgeDataT;

import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
/**
 * Created by sailaja.ch on 04/09/2019
 */
public class GPSLocationTrackingProvider extends Fragment implements LocationListener {
    private GoogleMap mMap;
    private LatLng dubai;
    Button btn_start;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    TextView tv_latitude, tv_longitude, tv_address, txtUpdatedOn;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Double latitude, longitude;
    Geocoder geocoder;
    private String strAdd, mLastUpdateTime;
    private List<EdgeDataT> visitList;
    private SqlHandler sqlHandler;
    private List<LatLng> latlngPoints;
    private Double userLocatedLat, userLocatedLongi;
    private LocationManager mLocationManager;
    private Marker mPosition;

    public GPSLocationTrackingProvider() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlHandler = new SqlHandler(getContext());
        new NSGGetRouteOnMap(17.4734772, 78.5712697, 17.4755, 78.5523);


    }
    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.maplite, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment1 = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googlemap) {
                mMap=googlemap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


            }

        });

        return rootView;
    }
    */


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "onResume");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(TAG, "onPause");
        mLocationManager.removeUpdates(this);
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocatedLat= location.getLatitude();
        Log.e("Lattitude","Lattitude @@@@@ "+ userLocatedLat);
        //loclat.setText(lat);
        userLocatedLongi=location.getLongitude();
        Log.e("Longitude","Longitude @@@@@ "+ userLocatedLongi);
        LatLng position = new LatLng(userLocatedLat, userLocatedLongi);
      //  GoogleMap mMap=NSGGetRouteOnMap.

//        mMap.addMarker(new MarkerOptions().position(position).title("Your position").
//                icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_circle)));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 11));


        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        }catch(Exception e)
        {

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "Provider " + provider + " has now status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "Provider " + provider + " is enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "Provider " + provider + " is disabled");
    }
}
