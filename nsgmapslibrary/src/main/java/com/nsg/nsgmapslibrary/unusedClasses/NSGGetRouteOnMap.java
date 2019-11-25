package com.nsg.nsgmapslibrary.unusedClasses;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.SphericalUtil;
import com.nsg.nsgmapslibrary.Classes.ExpandedMBTilesTileProvider;
import com.nsg.nsgmapslibrary.R;
import com.nsg.nsgmapslibrary.SupportClasses.Util;
import com.nsg.nsgmapslibrary.database.db.SqlHandler;
import com.nsg.nsgmapslibrary.database.dto.EdgeDataT;
import com.nsg.nsgmapslibrary.interfaces.ILoadTiles;
import com.nsg.nsgmapslibrary.interfaces.LatLngInterpolator;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NSGGetRouteOnMap extends Fragment {
    private LatLng SourcePosition, DestinationPosition;
    private double sourceLat, sourceLng, destLat, destLng;
    private ProgressDialog dialog;
    private GoogleMap mMap;
    private SqlHandler sqlHandler;
    private ILoadTiles mCallback;
    private double userLocatedLat, userLocatedLongi;
    private List points;
    private List<LatLng> path;
    private ArrayList<LatLng> latlngPoints = new ArrayList<>();
    private LatLng currentGpsPosition, nearestPositionPoint,lastKnownLocation;
    private List<LatLng> convertedPoints;
    private Marker sourceMarker, destinationMarker, mPositionMarker;
    private String SourcePoint;
    private String DestinationPoint;
    private int routeDeviationDistance = 500;
    private Bitmap mMarkerIcon;
    private String MESSAGE;
    private int enteredMode;
    private Location location;
    private LocationManager locationManager;
    StringBuilder sb = new StringBuilder();
    private int locationFakeGpsListener=0;
    List<LatLng> LatLngDataArray=new ArrayList<LatLng>();
    private List<EdgeDataT> edgeDataList;
    HashMap<LatLng,String> AllPointEdgeNo;
    private List<EdgeDataT> edgeOfElementsList;
    private List distancesList;
    private List<EdgeDataT> distanceValuesEdgeList;
    private List<LatLng> nearestPointValuesList;
    EdgeDataT distanceEdge =new EdgeDataT();
    List<EdgeDataT>EdgeWithoutDuplicates;
    private int mIndexCurrentPoint=0;
    String GeometryDirectionText="";
    HashMap<LatLng,String> PositionMarkingPointsList;
    private String BASE_MAP_URL_FORMAT;

    public NSGGetRouteOnMap() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public NSGGetRouteOnMap(String BASE_MAP_URL_FORMAT,double v1, double v2, double v3, double v4, int enteredMode, int routeDeviationDistance) {
        this.SourcePosition = new LatLng(v1, v2);
        this.DestinationPosition = new LatLng(v3, v4);
        this.DestinationPosition = new LatLng(v4, v3);
        this.sourceLat = v2;
        this.sourceLng = v1;
        this.destLat = v4;
        this.destLng = v3;
        this.BASE_MAP_URL_FORMAT = BASE_MAP_URL_FORMAT;
        this.enteredMode = enteredMode;
        this.routeDeviationDistance = routeDeviationDistance;
        this.SourcePoint = String.valueOf(v1).concat(" ").concat(String.valueOf(v2));
        this.DestinationPoint = String.valueOf(v3).concat(" ").concat(String.valueOf(v4));
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
        mMarkerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.car_icon_32);
        View rootView = inflater.inflate(R.layout.maplite, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment1 = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googlemap) {
                mMap = googlemap;
                //BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator + "DubaiPort_1251ff" + ".mbtiles";
                TileProvider tileProvider = new ExpandedMBTilesTileProvider(new File(BASE_MAP_URL_FORMAT.toString()), 256, 256);
                TileOverlay tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                       .tileProvider(tileProvider));
                tileOverlay.setTransparency(0.5f - tileOverlay.getTransparency());tileOverlay.setVisible(true);
                if (Util.isInternetAvailable(getActivity()) == true && mMap != null) {
                    dialog = new ProgressDialog(getActivity(), R.style.ProgressDialog);
                    dialog.setMessage("Fetching Route");
                    dialog.setMax(100);
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GetRouteDetails();
                            if (MESSAGE.equals("Sucess")) {
                                addMarkers();
                                MoveWithGpsPointInBetWeenAllPoints();
                                /*
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

                                mMap.setMyLocationEnabled(true);
                                mMap.getUiSettings().setZoomControlsEnabled(true);
                                mMap.getUiSettings().setCompassEnabled(true);
                                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                mMap.getUiSettings().setMapToolbarEnabled(true);
                                mMap.getUiSettings().setZoomGesturesEnabled(true);
                                mMap.getUiSettings().setScrollGesturesEnabled(true);
                                mMap.getUiSettings().setTiltGesturesEnabled(true);
                                mMap.getUiSettings().setRotateGesturesEnabled(true);
                                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                    @Override
                                    public void onMyLocationChange(Location location) {
                                        if (mPositionMarker != null) {
                                            mPositionMarker.remove();
                                        }

                                        // currentGpsPosition=new LatLng(location.getLatitude(),location.getLongitude());
                                        // updateUI(currentGpsPosition);

                                        // Log.e("NEAREST POSITION","NEAREST POSITION"+ nearestPositionPoint);
                                        // MoveInGPSPoints(currentGpsPosition);
                                        getLatLngPoints();
                                        currentGpsPosition = LatLngDataArray.get(locationFakeGpsListener);
                                        MoveWithGpsPointInBetWeenAllPoints(currentGpsPosition);
                                        locationFakeGpsListener=locationFakeGpsListener+1;
                                    }
                                });
                                */

                               // MoveWithGpsPointInBetWeenAllPoints();

                            }else{
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "Not Able to get Route from Service", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 30);
                } else {
                    Toast.makeText(getActivity(), "please turn on wifi/mobiledata", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (float) (earthRadius * c);
        return dist;
    }

    private void GetRouteDetails(){
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy =
                                new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            String httprequest = "http://202.53.11.74/dtnavigation/api/routing/routenavigate";
                            String FeatureResponse = HttpPost(httprequest,SourcePoint,DestinationPoint);
                            Log.e("RESPONSE", "RESPONSE" + FeatureResponse);
                            JSONObject jsonObject = null;
                            try {
                                if(FeatureResponse!=null){
                                    String delQuery = "DELETE  FROM " + EdgeDataT.TABLE_NAME;
                                   // Log.e("DEL QUERY","DEL QUERY " + delQuery);
                                    sqlHandler.executeQuery(delQuery.toString());
                                    jsonObject = new JSONObject(FeatureResponse);
                                    String ID = String.valueOf(jsonObject.get("$id"));
                                    MESSAGE = jsonObject.getString("Message");
                                    String Status = jsonObject.getString("Status");
                                    String TotalDistance = jsonObject.getString("TotalDistance");
                                    JSONArray jSonRoutes = new JSONArray(jsonObject.getString("Route"));
                                   // Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes);
                                    for (int i = 0; i < jSonRoutes.length(); i++) {
                                        points=new ArrayList();
                                        convertedPoints=new ArrayList<LatLng>();
                                       // Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes.get(i));
                                        // List Routes=new ArrayList();
                                        // Routes.add(jSonRoutes.get(i));
                                        JSONObject Routes = new JSONObject(jSonRoutes.get(i).toString());
                                        String $id = Routes.getString("$id");
                                        String EdgeNo = Routes.getString("EdgeNo");
                                        String GeometryText = Routes.getString("GeometryText");
                                      //  Log.e("GeometryText", "GeometryText" + GeometryText);
                                        String Geometry = Routes.getString("Geometry");
                                       // Log.e("Geometry", "Geometry----" + Geometry);
                                        JSONObject geometryObject = new JSONObject(Routes.getString("Geometry"));
                                        String $id1 = geometryObject.getString("$id");
                                        String type = geometryObject.getString("type");
                                      //  Log.e("type", "type----" + type);
                                        String coordinates = geometryObject.getString("coordinates");
                                       // Log.e("coordinates", "coordinates----" + coordinates);
                                        JSONArray jSonLegs = new JSONArray(geometryObject.getString("coordinates"));
                                       // Log.e("jSonLegs", "jSonLegs----" + jSonLegs);
                                        for (int j = 0; j < jSonLegs.length(); j++) {
                                         //   Log.e("JSON LEGS", "JSON CORDINATES" + jSonLegs.get(j));
                                            points.add(jSonLegs.get(j));
                                         //   Log.e("JSON LEGS", " LATLNG RESULT------ " + points.size());
                                        }
                                       // Log.e("JSON LEGS", " LATLNG RESULT------ " + points.size());
                                        String  stPoint=String.valueOf(jSonLegs.get(0));
                                        // String  endPoint=String.valueOf(jSonLegs.get(jSonLegs.length()-1));

                                        stPoint=stPoint.replace("[","");
                                        stPoint=stPoint.replace("]","");
                                        String [] firstPoint=stPoint.split(",");
                                        Double stPointLat= Double.valueOf(firstPoint[0]);
                                        Double stPointLongi= Double.valueOf(firstPoint[1]);
                                        LatLng stVertex=new LatLng(stPointLongi,stPointLat);
                                        //    endPoint=endPoint.replace("[","");
                                        //    endPoint=endPoint.replace("]","");
                                        //    String [] secondPoint=endPoint.split(",");
                                        //   Double endPointLat= Double.valueOf(secondPoint[0]);
                                        //    Double endPointLongi= Double.valueOf(secondPoint[1]);
                                        //    LatLng endVertex=new LatLng(endPointLongi,endPointLat);

                                        //    double distance=showDistance(stVertex,endVertex);
                                        //    String distanceInKM = String.valueOf(distance/1000);
                                        //    Log.e("Distance -----","Distance in KM-------- "+ distanceInKM);
                                        StringBuilder query = new StringBuilder("INSERT INTO ");
                                        query.append(EdgeDataT.TABLE_NAME).append("(edgeNo,distanceInVertex,startPoint,allPoints,geometryText,endPoint) values (")
                                                .append("'").append(EdgeNo).append("',")
                                                .append("'").append("distanceInKM").append("',")
                                                .append("'").append(jSonLegs.get(0)).append("',")
                                                .append("'").append(points).append("',")
                                                .append("'").append(GeometryText).append("',")
                                                .append("'").append(jSonLegs.get(jSonLegs.length()-1)).append("')");
                                        sqlHandler.executeQuery(query.toString());
                                        sqlHandler.closeDataBaseConnection();
                                        for (int p = 0; p < points.size(); p++) {
                                           // Log.e("JSON LEGS", "JSON POINTS LIST ---- " + points.get(p));
                                            String listItem = points.get(p).toString();
                                            listItem = listItem.replace("[", "");
                                            listItem = listItem.replace("]", "");
                                           // Log.e("JSON LEGS", "JSON POINTS LIST ---- " + listItem);
                                            String[] subListItem = listItem.split(",");
                                           // Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem.length);
                                           // Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[0]);
                                           // Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[1]);
                                            Double y = Double.valueOf(subListItem[0]);
                                            Double x = Double.valueOf(subListItem[1]);
                                            StringBuilder sb=new StringBuilder();
                                            //  sb.append(x).append(",").append(y).append(":");
                                            //  LocationPerpedicularPoints.add(sb.toString());
                                            LatLng latLng = new LatLng(x, y);
                                           // Log.e("JSON LEGS", " LATLNG RESULT------ " + latLng);
                                            convertedPoints.add(latLng);
                                            for (int k = 0; k < convertedPoints.size(); k++) {
                                                MarkerOptions markerOptions = new MarkerOptions();
                                                PolylineOptions polylineOptions = new PolylineOptions();
                                                if(polylineOptions!=null && mMap!=null) {
                                                    markerOptions.position(convertedPoints.get(k));
                                                    markerOptions.title("Position");
                                                    // polylineOptions.color(Color.RED);
                                                    // polylineOptions.width(6);
                                                    polylineOptions.addAll(convertedPoints);
                                                    // polylineOptions.color(Color.GREEN).width(10);
                                                    // polylineOptions.color(Color.BLACK).width(8);
                                                    // Polyline polyline =
                                                    mMap.addPolyline(polylineOptions);
                                                    polylineOptions.color(Color.CYAN).width(18);
                                                    mMap.addPolyline(polylineOptions);

                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }catch (Exception ex){

                        }
                        dialog.dismiss();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        dialog.dismiss();
    }
    private String HttpPost(String myUrl,String latLng1,String latLng2) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String LoginResponse = "";
        String result = "";
        URL url = new URL(myUrl);
        Log.v("URL ", " URL: " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "text/plain");
        JSONObject jsonObject = buidJsonObject(latLng1,latLng2);
        Log.e(" Message", " jsonObject: " + jsonObject);
        setPostRequestContent(conn, jsonObject);
        conn.connect();
        Log.e("Response Code", "ResponseCode: " + conn.getResponseCode());
        result = conn.getResponseMessage();
        Log.e("Response Message", "Response Message: " + result);

        if (conn.getResponseCode() != 200) {

        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = null;
            //   System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                LoginResponse = sb.append(output).append(" ").toString();
                //      Log.e("Login Response "," From server ############ "+LoginResponse);
            }
        }
        conn.disconnect();
        return LoginResponse;
    }

    private JSONObject buidJsonObject(String latLng1,String latLng2) throws JSONException {
        JSONObject buidJsonObject = new JSONObject();
        buidJsonObject.accumulate("UserData", buidJsonObject1());
        buidJsonObject.accumulate("StartNode", latLng1);
        buidJsonObject.accumulate("EndNode", latLng2);
        return buidJsonObject;
    }

    private JSONObject buidJsonObject1() throws JSONException {
        JSONObject buidJsonObject1 = new JSONObject();
        buidJsonObject1.accumulate("username", "admin");
        buidJsonObject1.accumulate("password", "admin");
        return buidJsonObject1;
    }

    private void setPostRequestContent(HttpURLConnection conn,
                                       JSONObject jsonObject) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        // Log.i(LoginActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }
    private  List<EdgeDataT> getAllEdgesData() {
        String query = "SELECT * FROM " + EdgeDataT.TABLE_NAME;
        Cursor c1 = sqlHandler.selectQuery(query);
        edgeDataList = (List<EdgeDataT>) SqlHandler.getDataRows(EdgeDataT.MAPPING, EdgeDataT.class, c1);
        sqlHandler.closeDataBaseConnection();
       // Log.e("EdgesDataList","EdgesDataList "+edgeDataList.size());
        return edgeDataList;
    }

    private void updateUI(LatLng newLocation) {
        if (mPositionMarker != null) {
            animateCar(newLocation);
            boolean contains = mMap.getProjection()
                    .getVisibleRegion()
                    .latLngBounds
                    .contains(newLocation);
            if (!contains) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
            }
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    newLocation, 15.5f));
            mPositionMarker = mMap.addMarker(new MarkerOptions().position(newLocation).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_32)));
        }
    }
    public void MoveWithGpsPointInBetWeenAllPoints(){
        getLatLngPoints();
        getAllEdgesData();
        AllPointEdgeNo=new HashMap<>();
        distancesList=new ArrayList();
        distanceValuesEdgeList=new ArrayList<EdgeDataT>();
        EdgeWithoutDuplicates=new ArrayList<EdgeDataT>();
        edgeOfElementsList=new ArrayList<EdgeDataT>();
        PositionMarkingPointsList=new HashMap<>();
        nearestPointValuesList=new ArrayList();
        LatLng source = null,destination=null;
        String FirstCordinate="",SecondCordinate="";
       // Log.e("EdgePoints Data","EdgePoints Data Geometry " + edgeDataList.size());
        if (edgeDataList != null && edgeDataList.size() > 0) {
            for (int i = 0; i < edgeDataList.size(); i++) {
                EdgeDataT edge = new EdgeDataT(); //creating object for EDGETABLE
                edge = edgeDataList.get(i);
                int edgeNo = edge.getEdgeNo(); //Edge Number
                String stPoint = edge.getStartPoint(); //Start Point
                String endPoint = edge.getEndPoint();//End Point
                String points = edge.getAllPoints(); // All points in the edge
                String geometryText  = edge.getGeometryText(); // Geometry Direction text
               // Log.e("EdgePoints Data","EdgePoints Data Geometry " + geometryText+" : "+ edgeNo);
                //[[55.07252845510704,24.986485718893903], [55.07252691395126,24.986503080465624], [55.07252858393359,24.9865204314153], [55.072533418545014,24.986537282374343], [55.072541282105426,24.9865531573588]]
                if(points!=null){
                    String AllPoints = points.replace("[", "");
                    AllPoints = AllPoints.replace("]", "");
                    String[] AllPointsArray = AllPoints.split(", ");
                   // Log.e("ALL POINTS", "ALL POINTS" + AllPointsArray.length);

                    for (int ap = 0; ap < AllPointsArray.length; ap++) {
                        // AllPointsList.add(AllPointsArray[ap]);
                        String[] data=AllPointsArray[ap].split(",");
                        double lat = Double.valueOf(data[0]);
                        double longi = Double.valueOf(data[1]);
                        LatLng newPoint=new LatLng(lat,longi);
                        EdgeDataT element = new EdgeDataT(edgeNo,newPoint.toString(),geometryText);
                        edgeOfElementsList.add(element);

                    }

                }

            }

        }
        for(int fakegps=0 ;fakegps<LatLngDataArray.size();fakegps++) {
            currentGpsPosition = LatLngDataArray.get(4);
            Log.e("currentGpsPosition ", " currentGpsPosition---- " + currentGpsPosition);
          EdgeWithoutDuplicates = removeDuplicates(edgeOfElementsList);
            if(EdgeWithoutDuplicates!=null && EdgeWithoutDuplicates.size()>0) {
                String pPoint = "", vertexDistance = "", geometryDirectionTxt = "";
                for (int i = 0; i < EdgeWithoutDuplicates.size(); i++) {
                    //  Log.e("EdgeWithoutDuplicates ", " EdgeWithoutDuplicates List----- " + EdgeWithoutDuplicates.get(i));
                    EdgeDataT edgeWithoutDuplicatesObj = new EdgeDataT();
                    edgeWithoutDuplicatesObj = EdgeWithoutDuplicates.get(i);
                    String geometryText = edgeWithoutDuplicatesObj.getGeometryText();
                    Log.e("EdgeWithoutDuplicates ", " EdgeWithoutDuplicates ALL POINTS----- " + edgeWithoutDuplicatesObj.getAllPoints());
                    String PositionMarkingPointString = edgeWithoutDuplicatesObj.getAllPoints();
                    int EdgeNo = edgeWithoutDuplicatesObj.getEdgeNo();

                    String latlongStr = PositionMarkingPointString.replace("lat/lng: (", "");
                    latlongStr = latlongStr.replace(")", "");

                    String[] latlong = latlongStr.split(",");
                    double latitude = Double.parseDouble(latlong[0]);
                    double longitude = Double.parseDouble(latlong[1]);
                    LatLng PositionMarkingPointLatLng = new LatLng(longitude, latitude);
                    // Log.e("currentGpsPosition ", "currentGpsPosition POINT----------" + currentGpsPosition);
                    //  Log.e("Position ", " PositionMarkingPointLatLng----- " + PositionMarkingPointLatLng);
                    double distance = distFrom(PositionMarkingPointLatLng.latitude, PositionMarkingPointLatLng.longitude, currentGpsPosition.longitude, currentGpsPosition.latitude);
                    EdgeDataT edgeDistance = new EdgeDataT(String.valueOf(distance), PositionMarkingPointLatLng.toString(), geometryText);
                    distanceValuesEdgeList.add(edgeDistance);
                    //  Log.e("Position ", " Distances Edge List----- " + distanceValuesEdgeList.size());
                    PositionMarkingPointsList.put(PositionMarkingPointLatLng,String.valueOf(distance));
                    distancesList.add(distance);
                    Collections.sort(distancesList);
                    Log.e("Position ", " Distances Edge List----- " + distancesList.size());

                }

                for(int i=0;i<distancesList.size();i++) {
                    Log.e("Sorted ArrayList ", "in Ascending order : " + distancesList.get(i));
                   // Log.e("Sorted ArrayList ", "in Ascending order : " + PositionMarkingPointsList.get(i));
                }
                Iterator<Map.Entry<LatLng, String>> itr = PositionMarkingPointsList.entrySet().iterator();

                while(itr.hasNext())
                {
                    Map.Entry<LatLng, String> entry = itr.next();
                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    Log.e("Distance Edge Values", "PositionMarkingPointsList LIST ----- " + " Key ="+entry.getKey() +" Value"+ entry.getValue());
                }
                Log.e("Distance Edge Values", "PositionMarkingPointsList LIST ----- " + distanceValuesEdgeList.size());
                 Log.e("Distance Edge Values", "Distance Edge Values LIST ----- " + distanceValuesEdgeList.size());
                //  Log.e("Distance ", "Distance SORTED LIST ----- " + distancesList.size());
                String FirstShortestDistance = String.valueOf(distancesList.get(0));
                String SecondShortestDistance = String.valueOf(distancesList.get(1));

                for (EdgeDataT edgeDataObj1 : distanceValuesEdgeList) {
                    if (edgeDataObj1.getDistanceInVertex().equals(FirstShortestDistance)) {
                        // String obj= edgeDataObj;
                        // Log.e("EdgeData Obj", "EdgeData OBJ1 ----- " + edgeDataObj1.getPositionMarkingPoint());
                        // Log.e("EdgeData Obj", "EdgeData OBJ1 ----- " + edgeDataObj1.getGeometryText());
                        Log.e("EdgeData Obj", "EdgeData OBJ1 ----- " + edgeDataObj1.getDistanceInVertex());
                        String str1= edgeDataObj1.getDistanceInVertex();
                        FirstCordinate = edgeDataObj1.getPositionMarkingPoint();

                    }
                }
                for (EdgeDataT edgeDataObj2 : distanceValuesEdgeList) {
                    if (edgeDataObj2.getDistanceInVertex().equals(SecondShortestDistance)) {
                        // String obj= edgeDataObj;
                        // Log.e("EdgeData Obj", "EdgeData OBJ2 ----- " + edgeDataObj2.getPositionMarkingPoint());
                        // Log.e("EdgeData Obj", "EdgeData OBJ2 ----- " + edgeDataObj2.getGeometryText());
                        Log.e("EdgeData Obj", "EdgeData OBJ2 ----- " + edgeDataObj2.getDistanceInVertex());
                        String str2= edgeDataObj2.getDistanceInVertex();
                        SecondCordinate = edgeDataObj2.getPositionMarkingPoint();

                    }
                }

                String First = FirstCordinate.replace("lat/lng: (", "");
                First = First.replace(")", "");
                String[] FirstLatLngsData = First.split(",");
                double FirstLatitude = Double.valueOf(FirstLatLngsData[0]);
                double FirstLongitude = Double.valueOf(FirstLatLngsData[1]);
                source = new LatLng(FirstLongitude, FirstLatitude);


                String Second = SecondCordinate.replace("lat/lng: (", "");
                Second = Second.replace(")", "");
                String[] SecondLatLngsData = Second.split(",");
                double SecondLatitude = Double.valueOf(SecondLatLngsData[0]);
                double SecondLongitude = Double.valueOf(SecondLatLngsData[1]);
                destination = new LatLng(SecondLongitude, SecondLatitude);
                Log.e("Sorted ArrayList ", "Source----- : " + source);
                Log.e("Sorted ArrayList ", "Destination----- : " + destination);

                String nearestPoint =  GenerateLinePoint( FirstLongitude,FirstLatitude,SecondLongitude,SecondLatitude,currentGpsPosition.longitude,currentGpsPosition.latitude);
                //  GenerateLinePoint(new LatLng(currentGpsPosition.longitude, currentGpsPosition.latitude), src, dest);
                Log.e("Sorted ArrayList ", "Nearest PositionPoint----- : " + nearestPoint);
                //  String nearPoint = nearestPoint.toString();
                String nearPoint1 = nearestPoint.replace("lat/lng: (", "");
                String nearPoint2 = nearPoint1.replace(")", "");
                String[] nearestPointStr = nearPoint2.split(",");
                double lat = Double.parseDouble(nearestPointStr[0]);
                double longi = Double.parseDouble(nearestPointStr[1]);
                nearestPositionPoint = new LatLng(longi,lat);
                Log.e("Sorted ArrayList ", "Nearest PositionPoint----- : " + nearestPositionPoint);

                nearestPointValuesList.add(nearestPositionPoint);
            }
        }
        Log.e("Sorted ArrayList ", "Nearest PositionPoint List ----- : " + nearestPointValuesList.size());
        for(int k=0;k<nearestPointValuesList.size();k++){
            Log.e("Nearest ArrayList ", "Nearest Points List ----- : " + nearestPointValuesList.get(k));
        }


        CameraPosition googlePlex = CameraPosition.builder()
                .target(nearestPositionPoint)
                .zoom(25)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
        mPositionMarker = mMap.addMarker(new MarkerOptions()
                .position(nearestPositionPoint)
                .title("currentLocation")
                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.red_marker_24)));
        //  Log.e("Route Deviation ---","Route Deviation "+routeDeviationDistance);
        //  verifyRouteDeviation(routeDeviationDistance);
        Log.e("NEAREST POSITION---","NEAREST POSITION POINT "+ nearestPositionPoint);
        if(nearestPointValuesList.size()>1) {
            //  Log.e("NEAREST POSITION---","NEAREST Source Position ------ "+ nearestPointValuesList.get(0));
            //  Log.e("NEAREST POSITION---","NEAREST Destination Position ------- "+  nearestPointValuesList.get(1));
            animateCarMove(mPositionMarker, nearestPointValuesList.get(0), nearestPointValuesList.get(1), 10000);

        }
    }


    private LatLng findNearestPoint(final LatLng p, final LatLng start, final LatLng end) {
        if (start.equals(end)) {
            return start;
        }

        final double s0lat = Math.toRadians(p.latitude);
        final double s0lng = Math.toRadians(p.longitude);
        final double s1lat = Math.toRadians(start.latitude);
        final double s1lng = Math.toRadians(start.longitude);
        final double s2lat = Math.toRadians(end.latitude);
        final double s2lng = Math.toRadians(end.longitude);

        double s2s1lat = s2lat - s1lat;
        double s2s1lng = s2lng - s1lng;
        final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng)
                / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);
        if (u <= 0) {
            return start;
        }
        if (u >= 1) {
            return end;
        }

        return new LatLng(start.latitude + (u * (end.latitude - start.latitude)),
                start.longitude + (u * (end.longitude - start.longitude)));


    }
    public int getLatLngPoints(){
        /*Route--1
        LatLngDataArray.add(new LatLng(24.986486,55.072528));
        LatLngDataArray.add(new LatLng(24.986599,55.072608));
        LatLngDataArray.add(new LatLng(24.986734,55.072730));
        LatLngDataArray.add(new LatLng(24.986857,55.072905));
        LatLngDataArray.add(new LatLng(24.986903,55.072949));
        LatLngDataArray.add(new LatLng(24.986908,55.072972));
        LatLngDataArray.add(new LatLng(24.986901,55.072986));
        LatLngDataArray.add(new LatLng(24.986898,55.073016));
        LatLngDataArray.add(new LatLng(24.986865,55.073056));
       // LatLngDataArray.add(new LatLng(24.986726,55.073200));
        LatLngDataArray.add(new LatLng(24.986652,55.073279));
        LatLngDataArray.add(new LatLng(24.986502,55.073438));
        LatLngDataArray.add(new LatLng(24.986242,55.073715));
        LatLngDataArray.add(new LatLng(24.986131,55.073830));
        LatLngDataArray.add(new LatLng(24.986097,55.073878));
        */

        LatLngDataArray.add(new LatLng(24.978782,55.067291));
        LatLngDataArray.add(new LatLng(24.9786559890011,55.0669970292443));
        LatLngDataArray.add(new LatLng(24.9784084059624,55.0668971973738));

        LatLngDataArray.add(new LatLng(24.9780250515799,55.0664619304187));
        LatLngDataArray.add(new LatLng(24.9779931053814,55.0662263272045));

        LatLngDataArray.add(new LatLng(24.9776097509989,55.0658150198983));
        LatLngDataArray.add(new LatLng(24.9773577661311,55.0656917069084));
        LatLngDataArray.add(new LatLng(24.9771317173355,55.0654359148503));


        LatLngDataArray.add(new LatLng(24.977125768683,55.0652485322961));
        LatLngDataArray.add(new LatLng(24.9771644349243,55.0651711998134));

        LatLngDataArray.add(new LatLng(24.9772566390383,55.0648737671876));
        LatLngDataArray.add(new LatLng(24.9776314041467,55.0646596156971));
        LatLngDataArray.add(new LatLng(24.9778187867009,55.0642937735675));
        LatLngDataArray.add(new LatLng(24.9780150922339,55.064287824915));

        LatLngDataArray.add(new LatLng(24.9781372775566,55.0643145938513));
        LatLngDataArray.add(new LatLng(24.9783557120769,55.0645658649335));
        LatLngDataArray.add(new LatLng(24.9784799199414,55.0647757333942));

        LatLngDataArray.add(new LatLng(24.9787326187002,55.0650612687149));
        LatLngDataArray.add(new LatLng(24.9789624746334,55.0652568604096));

        LatLngDataArray.add(new LatLng(24.9790310031104,55.0654124771594));
        LatLngDataArray.add(new LatLng(24.9791309404726,55.0655595278495));

        LatLngDataArray.add(new LatLng(24.9793522303461,55.0657065785397));
        LatLngDataArray.add(new LatLng(24.9795049917427,55.06600067992));

        LatLngDataArray.add(new LatLng(24.9797548351483,55.0661562966698));
        LatLngDataArray.add(new LatLng(24.9799104518981,55.0664161338116));
        LatLngDataArray.add(new LatLng(24.9800632132947,55.0665146434973));

        LatLngDataArray.add(new LatLng(24.980123175712,55.0666745432769));
        LatLngDataArray.add(new LatLng(24.9802174023679,55.0667944681116));
        LatLngDataArray.add(new LatLng(24.9802216853977,55.0668301600267));

        LatLngDataArray.add(new LatLng(24.9802145470147,55.0668744180014));

        LatLngDataArray.add(new LatLng(24.9801460185377,55.0669458018315));
        LatLngDataArray.add(new LatLng(24.9800717793543,55.0670728650492));
        LatLngDataArray.add(new LatLng(24.979964703609,55.0671913622073));

        LatLngDataArray.add(new LatLng(24.979878,55.067205));



        return LatLngDataArray.size();

    }

    private void animateCar(final LatLng destination) {
        final LatLng startPosition = mPositionMarker.getPosition();
        final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);
        final LatLngInterpolator latLngInterpolator = (LatLngInterpolator) new LatLngInterpolator.LinearFixed();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(5000); // duration 5 seconds
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    float v = animation.getAnimatedFraction();
                    LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                    mPositionMarker.setPosition(newPosition);
                } catch (Exception ex) {
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        valueAnimator.start();
    }


    /*
        This interface defines the interpolate method that allows us to get LatLng coordinates for
        a location a fraction of the way between two points. It also utilizes a Linear method, so
        that paths are linear, as they should be in most streets.
     */
    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements NSGGetRouteOnMap.LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }


    private BitmapDescriptor bitmapDescriptorFromVector1(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.car_icon_32);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public Bitmap addPaddingLeftForBitmap(Bitmap bitmap, int paddingLeft) {
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth() + paddingLeft, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        //canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, paddingLeft, 0, null);
        return outputBitmap;
    }
    private void animateCarMove(final Marker marker, final LatLng beginLatLng, final LatLng endLatLng, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();
        // set car bearing for current part of path
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);
        // Bitmap opBitMap= addPaddingLeftForBitmap(mMarkerIcon,60);
        //Bitmap opBitMap= setBounds(mMarkerIcon,10,10);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0,mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));
        //marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, centerX,centerY, matrix, true)));
        handler.post(new Runnable() {
            @Override
            public void run() {
                // calculate phase of animation
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                // calculate new position for marker
                double lat = (endLatLng.latitude - beginLatLng.latitude) * t + beginLatLng.latitude;
                double lngDelta = endLatLng.longitude - beginLatLng.longitude;
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * t + beginLatLng.longitude;
                marker.setPosition(new LatLng(lat, lng));

                // centerMapAt(new LatLng(lat,lng));
                // if not end of line segment of path
                if (t < 1.0) {
                    // call next marker position
                    handler.postDelayed(this, 16);
                } else {
                    // call turn animation
                    nextTurnAnimation();
                }
            }
        });
    }
    private void nextTurnAnimation() {
        mIndexCurrentPoint++;
        Log.e("EdgeListPoints","--------------"+nearestPointValuesList.size());
        if (mIndexCurrentPoint < nearestPointValuesList.size() - 1) {
            LatLng prevLatLng =  nearestPointValuesList.get(mIndexCurrentPoint - 1);
            LatLng currLatLng =  nearestPointValuesList.get(mIndexCurrentPoint);
            LatLng nextLatLng =  nearestPointValuesList.get(mIndexCurrentPoint + 1);

            float beginAngle = (float)(90 * getAngle(prevLatLng, currLatLng) / Math.PI);
            float endAngle = (float)(90 * getAngle(currLatLng, nextLatLng) / Math.PI);

            animateCarTurn(mPositionMarker, beginAngle, endAngle, 100);
        }
    }
    private void animateCarTurn(final Marker marker, final float startAngle, final float endAngle, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();

        final float dAndgle = endAngle - startAngle;

        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                Matrix m = new Matrix();
                float angle=startAngle + dAndgle * t;
                m.postRotate(angle);
                //  Bitmap opBitMap= setBounds(mMarkerIcon,10,10);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(),mMarkerIcon.getHeight(), m, true)));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    nextMoveAnimation();
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void nextMoveAnimation() {
      //  lastDistancesList=new ArrayList();
        if (mIndexCurrentPoint < nearestPointValuesList.size()){

            LatLng cameraPosition= (LatLng) nearestPointValuesList.get(mIndexCurrentPoint);
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(new LatLng(cameraPosition.latitude,cameraPosition.longitude))
                    .zoom(20).bearing(0).tilt(10).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 500, null);
            Log.e("CameraPOS","CameraPos--------- "+ mIndexCurrentPoint);
            Log.e("CameraPOS","CameraPos--------- "+ nearestPointValuesList.size());
            animateCarMove(mPositionMarker, nearestPointValuesList.get(mIndexCurrentPoint), nearestPointValuesList.get(mIndexCurrentPoint+1), 1000);

        }
    }

    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return Math.atan2(Math.sin(dl) * Math.cos(f2) , Math.cos(f1) * Math.sin(f2) - Math.sin(f1) * Math.cos(f2) * Math.cos(dl));
    }


    private interface LatLngInterpolatorNew {
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
    public void verifyRouteDeviation(int markDistance){
        //To Verify Route Deviation
        //currentLocationList.add(currentGpsPosition);
        String nearestPoint = GenerateLinePoint( 78.5712697,17.4734772,78.5523,17.4755,currentGpsPosition.longitude,currentGpsPosition.latitude);
        Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
        String[] nearestDataStr = nearestPoint.split(",");
        double latitude = Double.parseDouble(nearestDataStr[0]);
        double longitude = Double.parseDouble(nearestDataStr[1]);
        LatLng nearestPosition=new LatLng(longitude,latitude);
        double returnedDistance= showDistance(currentGpsPosition,nearestPosition);
        Log.e("returnedDistance", "returnedDistance --------- "+ returnedDistance);
        Circle circle = mMap.addCircle(
                new CircleOptions()
                        .center(nearestPosition)
                        .radius(markDistance)
                        .strokeWidth(0f)
                        .strokeColor(Color.parseColor("#2271cce7"))
                        .fillColor(Color.parseColor("#2271cce7")));
        if(returnedDistance> markDistance){
            Toast toast = Toast.makeText(getContext(), " ROUTE DEVIATED ", Toast.LENGTH_LONG);
            toast.setMargin(50, 50);
            toast.show();
          //  drawDeviatedRoute(currentGpsPosition, DestinationPosition);
            mPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentGpsPosition)
                    .title("currentLocation").snippet("DP World")
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.car_icon_48)));

            /*
             NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify=new Notification.Builder
               (getApplicationContext()).setContentTitle(tittle).setContentText(body).
               setContentTitle(subject).setSmallIcon(R.drawable.abc).build();

               notify.flags |= Notification.FLAG_AUTO_CANCEL;
               notif.notify(0, notify);
             */
        }else{

        }
    }
    private double showDistance(LatLng latlng1,LatLng latLng2) {
        double distance = SphericalUtil.computeDistanceBetween(latlng1,latLng2);
        Log.e("Distance -----","Distance between Source and Destination -------- "+distance);
        //   mTextView.setText("The markers are " + formatNumber(distance) + " apart.");
        return distance;
    }
    private String GenerateLinePoint(double startPointX, double startPointY, double endPointX, double endPointY, double pointX, double pointY)
    {
        double k = ((endPointY - startPointY) * (pointX - startPointX) - (endPointX - startPointX) * (pointY - startPointY)) / (Math.pow(endPointY - startPointY, 2)
                + Math.pow(endPointX - startPointX, 2));
        double resultX = pointX - k * (endPointY - startPointY);
        double resultY = pointY + k * (endPointX - startPointX);
        StringBuilder sb=new StringBuilder();
        sb.append(resultX).append(",").append(resultY);

        return sb.toString();
    }


    public void addMarkers(){
        LatLng position1= new LatLng(sourceLat,sourceLng);
        // Log.e("URL FORMAT","Uposition2 T ****************** "+ position1);
        sourceMarker = mMap.addMarker(new MarkerOptions()
                .position(position1)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.source_red)));
        CameraPosition googlePlex = CameraPosition.builder()
                .target(position1)
                .zoom(18)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
        LatLng position2= new LatLng(destLat,destLng);
        destinationMarker= mMap.addMarker(new MarkerOptions()
                .position(position2)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.destination_green)));
        CameraPosition googlePlex1 = CameraPosition.builder()
                .target(position2)
                .zoom(18)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex1), 1000, null);
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

    private List<EdgeDataT> removeDuplicates(List<EdgeDataT>edgeOfElementsList)
    {
       // Log.e("ALL POINTS ", "EdgePoints Size----- " + edgeOfElementsList.size());
        Iterator<EdgeDataT> it = edgeOfElementsList.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }

        for (int i = 0; i < edgeOfElementsList.size(); i++) {
            EdgeDataT edge=new EdgeDataT();
            edge=edgeOfElementsList.get(i);
            String EdgePosition1=edge.getAllPoints();
          //  Log.e("List", "List Items -------" + EdgePosition1);

            for (int j = i + 1; j < edgeOfElementsList.size()-1 ; j++) {
                EdgeDataT edge1=edgeOfElementsList.get(j);

                String EdgePosition2=edge1.getAllPoints();
             //   Log.e("List", "List Items -------" + EdgePosition2);
                if (EdgePosition1.equals(EdgePosition2)) {
                    edgeOfElementsList.remove(j);
                }
            }
        }
        System.out.println(edgeOfElementsList);
     //   Log.e("List","List"+edgeOfElementsList.size());
        for(int k=0;k<edgeOfElementsList.size();k++) {
            EdgeDataT edge = edgeOfElementsList.get(k);
           Log.e("List", "List Items -------" + edge.getAllPoints());
          //  Log.e("List", "EdgeWithout duplicates -------" + edge.getGeometryText());
         //   Log.e("List", "List Items -------" + edge.getEdgeNo());
            EdgeDataT edgeNewObj=new EdgeDataT( edge.getEdgeNo(), edge.getAllPoints(),edge.getGeometryText());
            EdgeWithoutDuplicates.add(edgeNewObj);
        }
        return EdgeWithoutDuplicates;

    }
    public  void animateMarkerToFinalDestination(final Marker marker, final LatLng finalPosition, final com.nsg.nsgmapslibrary.interfaces.LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 150000;

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
              /*  mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,longi))
                        .title("Moving Marker ").snippet(" Moving Marker ")
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.circle_pink)));
                        */
                Log.e("Location Moving Matrker","NEW MARKER OF CAR ------------"+ marker.getPosition()+ marker.getSnippet());
                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 10ms later.
                    handler.postDelayed(this, 10);
                }else{
                    animateCarTurn(mPositionMarker, 0, 90, 10000);
                }
            }
        });
    }


}



                                    /*
                                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                    mMap.setMyLocationEnabled(true);
                                    mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                        @Override
                                        public void onMyLocationChange(Location location) {
                                            if (mPositionMarker != null) {
                                                // mPositionMarker.remove();
                                            }
                                            if (locationManager != null) {
                                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                    return;
                                                }
                                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                                lastKnownLocation=new LatLng(location.getLatitude(),location.getLongitude());
                                               Marker gpsMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(lastKnownLocation)
                                                        .title("currentLocation")
                                                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.red_marker_24)));
                                            }

                                            currentGpsPosition=new LatLng(location.getLatitude(),location.getLongitude());
                                            mPositionMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(currentGpsPosition)
                                                    .title("currentLocation")
                                                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.green_marker_24)));
                                            animateMarker(mPositionMarker,currentGpsPosition,true);

                                        }

                                    });
                                    */