package com.nsg.nsgmapslibrary.Classes;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.SphericalUtil;
import com.nsg.nsgmapslibrary.R;
import com.nsg.nsgmapslibrary.SupportClasses.CarMoveAnim;
import com.nsg.nsgmapslibrary.SupportClasses.DirectionsJSONParser;
import com.nsg.nsgmapslibrary.database.db.SqlHandler;
import com.nsg.nsgmapslibrary.interfaces.ILoadTiles;
import com.nsg.nsgmapslibrary.interfaces.LatLngInterpolator;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by sailaja.ch on 05/09/2019
 */
public class NSGGetRouteOnMap extends Fragment implements LocationListener{
    LatLng SourcePosition, DestinationPosition;
    LatLng convertedSrcPosition,convertedDestinationPoisition;
    String sourceLat, sourceLng, destLat, destLng;
    String sourceDataLatLng,destinationDataLatLng;
    LatLng dubai;
    Marker markerSource, markerDestination,mPositionMarker;
    private Polyline mPolyline;
    private GoogleMap mMap;
    private SqlHandler sqlHandler;
    GoogleMap.CancelableCallback callback;
    ILoadTiles mCallback;
    private double userLocatedLat, userLocatedLongi;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
   // List<LatLng> path;
   // ArrayList<LatLng> latlngPoints = new ArrayList<>();
    Polyline polyline;
    LatLng currentGpsPosition;
    String distance = "";
    String duration = "";
    StringBuilder sb = new StringBuilder();
    private ArrayList<LatLng> currentLocationList=new ArrayList<LatLng>();

    public NSGGetRouteOnMap() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public NSGGetRouteOnMap(double v1, double v2, double v3, double v4) {
        //get Cordinates from MainActivity
        SourcePosition = new LatLng(v1, v2);
        DestinationPosition = new LatLng(v3, v4);
        sourceLat = String.valueOf(v1);
        sourceLng = String.valueOf(v2);
        destLat = String.valueOf(v3);
        destLng = String.valueOf(v4);
        sourceDataLatLng=String.valueOf(v1).concat("").concat(String.valueOf(v2));
        destinationDataLatLng=String.valueOf(v3).concat("").concat(String.valueOf(v4));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlHandler = new SqlHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.maplite, container, false);
        final TextView tv=(TextView)rootView.findViewById(R.id.tv);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment1 = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googlemap) {
                mMap = googlemap;
                String BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() +File.separator + "MBTILES" + File.separator +"DubaiPort_1251ff"+".mbtiles";
                // Environment.getExternalStorageDirectory() + File.separator + "samples"+ File.separator + sectionName+".mbtiles"
               // Log.e("URL FORMAT","URL FORMAT ****************** "+ BASE_MAP_URL_FORMAT);
                TileProvider tileProvider = new ExpandedMBTilesTileProvider(new File(BASE_MAP_URL_FORMAT.toString()), 256, 256);
                TileOverlay tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                        .tileProvider(tileProvider));
                tileOverlay.setTransparency(0.5f - tileOverlay.getTransparency());
                tileOverlay.setVisible(true);
                //17.4734772,78.5712697
                /*
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(24.989044, 55.063247))
                        .zoom(15)
                        .tilt(45)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);


                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(24.989044, 55.063247))
                        .title("United Arab Emirates").snippet("DP World")
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.boat_marker)));
                        */

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(17.4734772,78.5712697))
                        .zoom(15)
                        .tilt(45)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

                /*
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(17.4734772,78.5712697))
                        .title("United Arab Emirates").snippet("DP World")
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.boat_marker)));
                        */
                drawRoute();


                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        if(mPositionMarker!=null ){
                            mPositionMarker.remove();
                        }
                        double lat=location.getLatitude();
                        double longi=location.getLongitude();
                        LatLng nearestPoint=GenerateLinePoint(78.57098,17.47349,78.57096,17.47342,longi,lat);
                        Log.e("NearestPoint","NearestPoint"+nearestPoint);
                        /*
                        CameraPosition googlePlex = CameraPosition.builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                .zoom(15)
                                .tilt(45)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
                        //currentGpsPosition=new LatLng(nearestPoint);
                       mPositionMarker= mMap.addMarker(new MarkerOptions()
                                .position(nearestPoint)
                                .title("currentLocation")
                                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.purple_car)));
                                */

                        CameraPosition googlePlex = CameraPosition.builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                .zoom(15)
                                .tilt(45)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
                        currentGpsPosition=new LatLng(location.getLatitude(),location.getLongitude());
                        mPositionMarker= mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                .title("currentLocation")
                                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.purple_car)));



                    }
                });


            }
        });

        return rootView;
    }
    private void addMarkers(){
        markerSource= mMap.addMarker(new MarkerOptions().position(SourcePosition).title("Marker Source"));
        markerDestination=mMap.addMarker(new MarkerOptions().position(DestinationPosition).title("Marker Destination"));

    }
    private String  addAttributes(String Date,String remarks,String text) {

        sb.append(Date ).append(remarks ).append(text );
        return sb.toString();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private LatLng GenerateLinePoint(double startPointX, double startPointY, double endPointX, double endPointY, double pointX, double pointY)
    {
        double k = ((endPointY - startPointY) * (pointX - startPointX) - (endPointX - startPointX) * (pointY - startPointY)) / (Math.pow(endPointY - startPointY, 2)
                + Math.pow(endPointX - startPointX, 2));
        double resultX = pointX - k * (endPointY - startPointY);
        double resultY = pointY + k * (endPointX - startPointX);
        LatLng nearestPoint=new LatLng(resultX,resultY);
        StringBuilder sb=new StringBuilder();
        sb.append(resultX).append(",").append(resultY);

        return nearestPoint;
    }

    private void drawRoute(){
        markerSource= mMap.addMarker(new MarkerOptions().position(SourcePosition).title("Marker Source"));
        markerDestination=mMap.addMarker(new MarkerOptions().position(DestinationPosition).title("Marker Destination"));

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(SourcePosition, DestinationPosition);
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }


    /*
    private void ReDrawRoute(LatLng position1,LatLng position2 ){
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(position1, position2);
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }
    */

    private String getDirectionsUrl(LatLng origin,LatLng dest){
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        Log.e("getDirectionsUrl ","str_origin"+ str_origin);
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        Log.e("getDirectionsUrl ","str_dest"+ str_dest);
        String key = "key="+"AIzaSyAVDBoNqoKCd_XIG9ebOuR4SQxwHu56IXU";
        String parameters = str_origin+"&"+str_dest+"&"+key+"&departure_time=now";
       // String parameters1 = str_origin+"&"+str_dest+"&"+key+"&"+"mode=driving"+"&"+"alternatives=true"+"&"+"units=metric"+"&"+"dir_action=navigate"+"&"+"sensor"+"&"+"waypoints"+"&";
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+"&"+parameters+"";
        Log.e("getDirectionsUrl ","url ----"+ url);
        return url;
    }

    /**  Method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            // HttpPost(strUrl);
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.e("Download URL ","url ----"+ urlConnection);
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            Log.d("DownloadTask","Load Url ---- : " + url);
            String data = "";
            try{
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
         ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

                }
                Log.e("Route","All points from the route-----"+ points.size());
                Log.e("Route","All points from the route-----"+ points);

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
               // lineOptions.width(10);
               // lineOptions.color(Color.RED);
               // lineOptions.startCap(new SquareCap());
               // lineOptions.endCap(new SquareCap());

                lineOptions.color(Color.BLACK).width(16);
                polyline = mMap.addPolyline(lineOptions);
                lineOptions.color(Color.CYAN).width(15);
                polyline = mMap.addPolyline(lineOptions);

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);
              //  zoomRoute(mMap,points);
                Log.e("Final distance", "Distance:" + distance + ", Duration:" + duration);

            }else
                Toast.makeText(getContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }
    public void loadNavigationView(String lat,String lng){
        Uri navigation = Uri.parse("google.navigation:q="+lat+","+lng+"");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocatedLat= location.getLatitude();
        Log.e("Lattitude","Lattitude @@@@@ "+ userLocatedLat);
        //loclat.setText(lat);
        userLocatedLongi=location.getLongitude();
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
    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }
    private void animateMarker(GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                               final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = myMap.getProjection();
        final long duration = 600000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                Location location=new Location(String.valueOf(directionPoint.get(i)));
                Location newlocation=new Location(String.valueOf(directionPoint.get(i+1)));
                marker.setAnchor(0.5f, 0.5f);
                marker.setRotation(location.bearingTo(newlocation)  - 45);
                if (i < directionPoint.size()) {
                    marker.setPosition(directionPoint.get(i));
                }
                i++;

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
    private double showDistance() {
       double distance = SphericalUtil.computeDistanceBetween(SourcePosition,DestinationPosition);
        Log.e("Distance -----","Distance between Source and Destination -------- "+distance);
     //   mTextView.setText("The markers are " + formatNumber(distance) + " apart.");
        return distance;
    }

    private static double distance(LatLng source,LatLng destination, String unit) {
         double lat1=source.latitude;
         double lon1=source.longitude;
         double lat2=destination.latitude;
         double lon2=destination.longitude;
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }
    public void showdistance(){
        double lat=SourcePosition.latitude;
        double lnag=SourcePosition.longitude;
        Location selected_location=new Location("locationA");
        selected_location.setLatitude(lat);
        selected_location.setLongitude(lnag);

        Location near_locations=new Location("locationB");

        double lat1=DestinationPosition.latitude;
        double lnag1=DestinationPosition.longitude;

        near_locations.setLatitude(lat1);
        near_locations.setLongitude(lnag1);
        double distance=selected_location.distanceTo(near_locations)/1000;
        Log.e("POSITION ALERT", " DISTANCE ------ " + distance);
        BigDecimal bd = new BigDecimal(distance).setScale(0, RoundingMode.HALF_EVEN);
        distance = bd.doubleValue();
        String totalDistance=String.valueOf(distance);


        int speedIs10MetersPerMinute = 10;
        double estimatedDriveTimeInMinutes = distance/speedIs10MetersPerMinute;
        Log.e("Time ","Time in seconds----"+ estimatedDriveTimeInMinutes);


        //Log.e("Time ","Time in seconds----"+ time);

       // convertSecondsToTime(time);
     //   String totalResponse= totalDistance +" KM "+"\n"+ convertSecondsToTime(time) +"";



    }
    public String convertSecondsToTime(int seconds){
        int p1 = seconds % 60;
        int p2 = seconds / 60;
        int p3 = p2 % 60;

        p2 = p2 / 60;
        String result = "HH:MM:SS - " +p2 + ":" + p3 + ":" + p1;

        System.out.print("HH:MM:SS - " +p2 + ":" + p3 + ":" + p1);
        System.out.print("\n");
            return result;
    }
    public  void animateMarkerToFinalDestination(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 2000;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                Double lat=marker.getPosition().latitude;
                Double longi= marker.getPosition().longitude;
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,longi))
                        .title("Moving Marker ").snippet(" Moving Marker ")
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.purple_car)));
                Log.e("Location Moving Matrker","NEW MARKER OF CAR ------------"+ marker.getPosition()+ marker.getSnippet());

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 10ms later.
                    handler.postDelayed(this, 10);
                }
            }
        });
    }
}

                          /*
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                               // ObjectAnimator animator = ObjectAnimator.ofFloat(R.drawable.purple_car, "x", 0f, 100f);
                              //  R.drawable.purple_car.animate().x(100f);
                            }
                        }, 100);//just mention the time when you want to launch your action


                        //  animateMarkerToFinalDestination(mPositionMarker,DestinationPosition,new LatLngInterpolator.Spherical());

                       // CarMoveAnim.startcarAnimation(mPositionMarker,mMap, SourcePosition,DestinationPosition,3000,callback);

                           currentLocationList.add(currentGpsPosition);
                        Circle circle = mMap.addCircle(
                                new CircleOptions()
                                        .center(currentGpsPosition)
                                        .radius(100)
                                        .strokeWidth(0f)
                                        .strokeColor(Color.parseColor("#2271cce7"))
                                        .fillColor(Color.parseColor("#2271cce7")));

                        if (points.contains(currentGpsPosition)) {
                            Log.e("POSITION ALERT", "YOU ARE ON THE ROUTE PROVIDED ");
                        } else {
                            int index = points.indexOf(currentGpsPosition);
                            System.out.println("INDEX *************** " + index );


                            Toast toast = Toast.makeText(getContext(), " ROUTE DEVIATED ", Toast.LENGTH_LONG);
                            toast.setMargin(50, 50);
                            toast.show();

                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(currentGpsPosition).title("Marker Current Position"));
                            markerDestination = mMap.addMarker(new MarkerOptions().position(DestinationPosition).title("Marker Destination"));

                           // drawRoute(currentGpsPosition, DestinationPosition);
                            toast.cancel();
                            mPositionMarker = mMap.addMarker(new MarkerOptions()
                                    .position(currentGpsPosition)
                                    .title("currentLocation").snippet("DP World")
                                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.purple_car)));

                            /*
                            mMap.addCircle(
                                    new CircleOptions().center(
                                            new LatLng(
                                                    bounds.getCenter().latitude,
                                                    bounds.getCenter().longitude
                                            )
                                    )
                                            .radius(50000)
                                            .strokeWidth(0f)
                                            .fillColor(0x550000FF)
                            );


                        }
                        */

