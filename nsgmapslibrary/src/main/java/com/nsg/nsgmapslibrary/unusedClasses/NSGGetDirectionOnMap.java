package com.nsg.nsgmapslibrary.unusedClasses;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.nsg.nsgmapslibrary.R;
import com.nsg.nsgmapslibrary.SupportClasses.DirectionsJSONParser;
import com.nsg.nsgmapslibrary.database.db.SqlHandler;
import com.nsg.nsgmapslibrary.database.dto.EdgeDataT;
import com.nsg.nsgmapslibrary.interfaces.ILoadTiles;
import com.nsg.nsgmapslibrary.interfaces.LatLngInterpolator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

public class NSGGetDirectionOnMap extends Fragment implements LocationListener ,GoogleMap.CancelableCallback{
    LatLng SourcePosition, DestinationPosition;
    String sourceLat, sourceLng, destLat, destLng;
    LatLng dubai;
    private double latitude, longitude;
    private List<EdgeDataT> visitList;
    Marker markerSource, markerDestination;
    private Polyline mPolyline;
    private Marker marker;
    private GoogleMap mMap;
    private SqlHandler sqlHandler;
    GoogleMap.CancelableCallback callback;
    ILoadTiles mCallback;
    private double userLocatedLat, userLocatedLongi;
    // private Marker marker;
    private float v;
    private int emission = 0;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    List<LatLng> path ;
    ArrayList<LatLng> latlngPoints = new ArrayList<>();
    Location currentLocation;


    private boolean isTilesLoaded = false;
    StringBuilder sb = new StringBuilder();

    public NSGGetDirectionOnMap() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public NSGGetDirectionOnMap(double v1, double v2, double v3, double v4) {
        SourcePosition = new LatLng(v1, v2);
        DestinationPosition = new LatLng(v3, v4);
        sourceLat = String.valueOf(v1);
        sourceLng = String.valueOf(v2);
        destLat = String.valueOf(v3);
        destLng = String.valueOf(v4);


    }

    /*
    public static NSGGetDirectionOnMap newInstance(LatLng Source, LatLng destination) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("Source",Source);
        bundle.putParcelable("Destination",destination);

        NSGGetDirectionOnMap fragment = new NSGGetDirectionOnMap();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
           bundle.getParcelable("Source");
           bundle.getParcelable("Destination");
        }
    }
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlHandler = new SqlHandler(getContext());
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
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
        currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        currentLocation = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 1000, 0  , this);



    }
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
                mMap.clear(); //clear old markers
                // Routes 24.989044, 55.063247  to 25.016036, 54.982303

               // 55.063247,24.989044
                mMap.clear(); //clear old markers
                dubai =new LatLng(24.989044,55.063247);
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(24.989044,55.063247))
                        .zoom(15)
                        .tilt(45)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

                mMap.addMarker(new MarkerOptions()
                        .position(dubai)
                        .title("United Arab Emirates").snippet("DP World")
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.boat_marker)));
              // getDirection();
                drawRoute();

                marker= mMap.addMarker(new MarkerOptions()
                        .position(SourcePosition)
                        .title("United Arab Emirates").snippet("DP World")
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.boat_marker)));
              //  animateMarkerToFinalDestination(marker, latLng, new LatLngInterpolator.Spherical());

             ///   CarMoveAnim.startcarAnimation(marker,mMap, SourcePosition,DestinationPosition,3000,callback);

               // showMarker(currentLocation);
              //  animateCamera(currentLocation);

               animateMarkerToFinalDestination(marker,DestinationPosition,new LatLngInterpolator.Spherical());
               animateCarMove(marker,SourcePosition,DestinationPosition,1000);


            }


        });

        return rootView;
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

    private void animateCarMove(final Marker marker, final LatLng beginLatLng, final LatLng endLatLng, final long duration) {
        Bitmap mMarkerIcon;
        mMarkerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.boat_marker);
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();

        final Interpolator interpolator = new LinearInterpolator();
        // set car bearing for current part of path
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);
//        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));

        handler.post(new Runnable() {
            @Override
            public void run() {
                // calculate phase/Time of animation
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                // calculate new position for marker
                double lat = (endLatLng.latitude - beginLatLng.latitude) * t + beginLatLng.latitude;
                double lngDelta = endLatLng.longitude - beginLatLng.longitude;

                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * t + beginLatLng.longitude;
                //set First position of marker
                marker.setPosition(new LatLng(lat, lng));
                lat= marker.getPosition().latitude;
                lng=marker.getPosition().longitude;
                Log.e("Location Moving Matrker","NEW MARKER OF CAR ------------"+ new LatLng(lat,lng));
                //Add marker at Source Position
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .title("Moving Marker ").snippet(" Moving Marker ")
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.circle_pink)));
                Log.e("Location Moving Matrker","NEW MARKER OF CAR ------------"+ marker.getPosition()+ marker.getSnippet());

                //Get Latitude and Longitude from Back ground service
                // Intent intent = new Intent(getContext(), GoogleService.class);
               //  getContext().startService(intent);

                // Double finaluserLocatedLat= DecimalUtils.round(userLocatedLat, 4);
               //  System.out.println(userLocatedLat + " is rounded to: " + finaluserLocatedLat);

               //  Double finaluserLocatedLongi = DecimalUtils.round(userLocatedLongi, 4);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("Position");
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.RED);
                polylineOptions.width(6);
                polylineOptions.addAll(latlngPoints);

                // if not end of line segment of path
                if (t < 1.0) {
                    // call next marker position
                    handler.postDelayed(this, 10);

                } else {

/*                    for(int i=0;i<latlngPoints.size();i++) {
                        LatLng prevLatLng = latlngPoints.get(i - 1);
                        LatLng currLatLng = latlngPoints.get(i);
                        LatLng nextLatLng = latlngPoints.get(i + 1);

                        float beginAngle = (float)(180 * getAngle(prevLatLng, currLatLng) / Math.PI);
                        float endAngle = (float)(180 * getAngle(currLatLng, nextLatLng) / Math.PI);
                        animateCarMove(marker,SourcePosition,DestinationPosition,1000);
                        //animateCarTurn(marker, beginAngle, endAngle, 3000);
                    }
                    */
                }
            }
        });
    }
    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return Math.atan2(Math.sin(dl) * Math.cos(f2) , Math.cos(f1) * Math.sin(f2) - Math.sin(f1) * Math.cos(f2) * Math.cos(dl));
    }





    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    public double calculateETA(double distance){

        int speedIs1KmMinute = 100;
        double estimatedDriveTimeInMinutes = distance / speedIs1KmMinute;
        //Toast.makeText(this,String.valueOf(distance)),Toast.LENGTH_SHORT).show();
       // Toast.makeText(this,String.valueOf(estimatedDriveTimeInMinutes+" Time"),Toast.LENGTH_SHORT).show();

        return estimatedDriveTimeInMinutes;
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
    private String getDirectionsUrl(LatLng origin,LatLng dest){
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        Log.e("getDirectionsUrl ","str_origin"+ str_origin);
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        Log.e("getDirectionsUrl ","str_dest"+ str_dest);
        String key = "key="+"AIzaSyAVDBoNqoKCd_XIG9ebOuR4SQxwHu56IXU";
        String parameters = str_origin+"&"+str_dest+"&"+key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
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
    private String HttpPost(String myUrl) throws IOException, JSONException {
        String LoginResponse="";
        StringBuilder sb = new StringBuilder();
        String result = "";
        URL url = new URL(myUrl);
        Log.e("URL ", " URL: " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "text/plain");

        conn.connect();
        Log.e("Response Code", "ResponseCode: " + conn.getResponseCode());
        result = conn.getResponseMessage();
        Log.e("Response Message", "Response Message: " + result);

        if(conn.getResponseCode()!=200){

        }else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = null;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                LoginResponse= sb.append(output).append(" ").toString();
                // Log.e("Login Response "," From server ############ "+LoginResponse);
            }
        }
        conn.disconnect();
        return LoginResponse;
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocatedLat= location.getLatitude();
        Log.e("Lattitude","Lattitude @@@@@ "+ userLocatedLat);
        //loclat.setText(lat);
        userLocatedLongi=location.getLongitude();
        Log.e("Longitude","Longitude @@@@@ "+ userLocatedLongi);


        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
           // String Adderess=addresses.get(0).getAddressLine(0)+", "+
            //      addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2)
            //locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
            //      addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
        }catch(Exception e)
        {

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

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


                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                Log.e("Route","All points from the route"+ lineOptions.getPoints());
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(20);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            }else
                Toast.makeText(getContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }



    public void getDirection(){
        //Alternate Method to get Routes and Directions...
        // LatLng barcelona = new LatLng(41.385064,2.173403);
        markerSource= mMap.addMarker(new MarkerOptions().position(SourcePosition).title("Marker Source"));

        //  LatLng madrid = new LatLng(40.416775,-3.70379);
        mMap.addMarker(new MarkerOptions().position(DestinationPosition).title("Marker Destination"));

        //Define list to get all latlng for the route


        StringBuilder sb=new StringBuilder();
        sb.append(sourceLat).append(",").append(sourceLng);
        Log.e("Source","sb------- "+ sb.toString());

        StringBuilder sb1=new StringBuilder();
        sb1.append(destLat).append(",").append(destLng);
        Log.e("Destination","sb1 -------- "+ sb1.toString());

        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAVDBoNqoKCd_XIG9ebOuR4SQxwHu56IXU")
                .build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context,sb.toString(),sb1.toString());
        Log.e("Source","sb------- "+ req);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(10);
            mMap.addPolyline(opts);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dubai, 15));
        animateCarMove(markerSource,SourcePosition,DestinationPosition,1000);


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
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.circle_pink)));
                Log.e("Location Moving Matrker","NEW MARKER OF CAR ------------"+ marker.getPosition()+ marker.getSnippet());
                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 10ms later.
                    handler.postDelayed(this, 10);
                }
            }
        });
    }
    private void animateCamera(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void showMarker(@NonNull Location currentLocation) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (marker == null)
            marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng));
        else
            animateMarkerToFinalDestination(marker, latLng, new LatLngInterpolator.Spherical());

    }

 /*
    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }


public void addEnteredModePath(){
    //55.059240,24.987458,55.061470,24.989390
    String nearestPoint = GenerateLinePoint( 55.059240,24.987458,55.061470,24.989390,55.060177,24.988403);
    Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
    String[] nearestDataStr = nearestPoint.split(",");
    double latitude = Double.parseDouble(nearestDataStr[0]);
    double longitude = Double.parseDouble(nearestDataStr[1]);
    mPositionMarker = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(longitude,latitude))
            .title("currentLocation")
            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.black_car)));

    String nearestPoint1= GenerateLinePoint( 55.059240,24.987458,55.061470,24.989390,55.060606,24.988780);
    Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint1);
    String[] nearestDataStr1 = nearestPoint1.split(",");
    double latitude1 = Double.parseDouble(nearestDataStr1[0]);
    double longitude1 = Double.parseDouble(nearestDataStr1[1]);
    mPositionMarker = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(longitude1,latitude1))
            .title("currentLocation")
            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.black_car)));

    String nearestPoint2 = GenerateLinePoint( 55.059240,24.987458,55.061470,24.989390,55.060920,24.989083);
    Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint2);
    String[] nearestDataStr2 = nearestPoint2.split(",");
    double latitude2 = Double.parseDouble(nearestDataStr2[0]);
    double longitude2 = Double.parseDouble(nearestDataStr2[1]);
    mPositionMarker = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(longitude2,latitude2))
            .title("currentLocation")
            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.black_car)));

    String nearestPoint3 = GenerateLinePoint( 55.059240,24.987458,55.061470,24.989390,55.061234,24.989292);
    Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint3);
    String[] nearestDataStr3 = nearestPoint3.split(",");
    double latitude3 = Double.parseDouble(nearestDataStr3[0]);
    double longitude3 = Double.parseDouble(nearestDataStr3[1]);
    mPositionMarker = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(longitude3,latitude3))
            .title("currentLocation")
            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.black_car)));


    String nearestPoint4 = GenerateLinePoint( 55.059240,24.987458,55.061470,24.989390,55.060486,24.9882);
    Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint4);
    String[] nearestDataStr4 = nearestPoint4.split(",");
    double latitude4 = Double.parseDouble(nearestDataStr4[0]);
    double longitude4= Double.parseDouble(nearestDataStr4[1]);
    mPositionMarker = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(longitude4,latitude4))
            .title("currentLocation")
            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.black_car)));

    String nearestPoint5 = GenerateLinePoint( 55.059240,24.987458,55.061470,24.989390,55.061160,24.989113);
    Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint4);
    String[] nearestDataStr5 = nearestPoint5.split(",");
    double latitude5 = Double.parseDouble(nearestDataStr5[0]);
    double longitude5= Double.parseDouble(nearestDataStr5[1]);
    mPositionMarker = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(longitude5,latitude5))
            .title("currentLocation")
            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.black_car)));

}
    public void addGPSMarkers(){

        Marker m1 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(24.988403,55.060177))
                .title("United Arab Emirates").snippet("DP World")
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.circle_pink)));

        Marker m2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(24.988780,55.060606))
                .title("United Arab Emirates").snippet("DP World")
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.circle_pink)));

        Marker m3 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(24.989083,55.060920))
                .title("United Arab Emirates").snippet("DP World")
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.circle_pink)));

        Marker m4 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(24.989292,55.061234))
                .title("United Arab Emirates").snippet("DP World")
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.circle_pink)));

        Marker m5 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(24.9882,55.060486))
                .title("United Arab Emirates").snippet("DP World")
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.circle_pink)));
        Marker m6 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(24.989113,55.061160))
                .title("United Arab Emirates").snippet("DP World")
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.circle_pink)));

    }

*/


}
