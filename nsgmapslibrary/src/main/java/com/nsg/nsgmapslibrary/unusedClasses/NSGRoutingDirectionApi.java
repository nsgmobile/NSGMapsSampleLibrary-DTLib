package com.nsg.nsgmapslibrary.unusedClasses;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.SphericalUtil;
import com.nsg.nsgmapslibrary.Classes.ExpandedMBTilesTileProvider;
import com.nsg.nsgmapslibrary.R;
import com.nsg.nsgmapslibrary.SupportClasses.ETACalclator;
import com.nsg.nsgmapslibrary.SupportClasses.Util;
import com.nsg.nsgmapslibrary.database.db.SqlHandler;
import com.nsg.nsgmapslibrary.database.dto.EdgeDataT;
import com.nsg.nsgmapslibrary.database.dto.GeometryT;
import com.nsg.nsgmapslibrary.interfaces.ILoadTiles;

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
import java.util.List;

public class NSGRoutingDirectionApi extends Fragment implements GoogleMap.CancelableCallback {
    LatLng SourcePosition, DestinationPosition;
    LatLng convertedSrcPosition,convertedDestinationPoisition;
    double sourceLat, sourceLng, destLat, destLng;
    private TextView tv,tv1,tv2;
    LatLng dubai;
    // String SourcePoint="55.058305953226821 24.987833937938962";
    //  //  String DestinationPoint="55.058641837922082 24.987354394841542";
    String SourcePoint;
    String DestinationPoint;
    Marker markerSource, markerDestination,mPositionMarker;
    private Polyline mPolyline;
    private GoogleMap mMap;
    private SqlHandler sqlHandler;
    GoogleMap.CancelableCallback callback;
    ILoadTiles mCallback;
    private double userLocatedLat, userLocatedLongi;
    private List points;
    private List<LatLng> convertedPoints;
    LatLng currentGpsPosition;
    String distance = "";
    String duration = "";
    StringBuilder sb = new StringBuilder();
    private List LocationPerpedicularPoints=new ArrayList();
    private ArrayList<LatLng> currentLocationList=new ArrayList<LatLng>();
    private Marker sourceMarker,destinationMarker;
    private List<EdgeDataT> edgeDataList;
    Handler handler;
    private int index=0;
    private int next=0;
    private int enteredMode;
    private int routeDeviationDistance;
    List<LatLng> LatLngDataArray=new ArrayList<LatLng>();
    private String currentGpsPoint;
    private Polyline line;
    private List polyLines;
    private Circle mCircle;
    Bitmap mMarkerIcon;
    int mIndexCurrentPoint=0;
    List<LatLng> nearestPointArray=new ArrayList<LatLng>();
    List<LatLng>edgeDataPointsList;
    List<String>AllPointsList;
    private ProgressDialog dialog;
    LatLng nearestPositionPoint;
    List<LatLng>nearestPointValuesList;

    private LatLng newCenterLatLng,PointData;
    private List distancesList;
    private List distanceValuesList;
    HashMap<String, String> hash_map;
    public NSGRoutingDirectionApi() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public NSGRoutingDirectionApi(double v1, double v2, double v3, double v4,int mode,int radius ) {
        //get Cordinates from MainActivity
        SourcePosition = new LatLng(v2, v1);
        DestinationPosition = new LatLng(v4, v3);
        sourceLat = v2;
        sourceLng = v1;
        destLat = v4;
        destLng =v3;
        enteredMode = mode;
        routeDeviationDistance=radius;
        Log.e("Entered Mode", "Entered Mode" + enteredMode);
        Log.e("Entered Mode", "Entered Mode" + routeDeviationDistance);

        SourcePoint=String.valueOf(v1).concat(" ").concat(String.valueOf(v2));
        DestinationPoint=String.valueOf(v3).concat(" ").concat(String.valueOf(v4));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlHandler = new SqlHandler(getContext());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMarkerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.car_icon_32);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.maplite, container, false);
        tv=(TextView)rootView.findViewById(R.id.tv);
        tv1=(TextView)rootView.findViewById(R.id.tv1);
        tv2=(TextView)rootView.findViewById(R.id.tv2);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment1 = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onMapReady(GoogleMap googlemap) {
                mMap = googlemap;
                String BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator +"DubaiPort_1251ff"+".mbtiles";
                // Environment.getExternalStorageDirectory() + File.separator + "samples"+ File.separator + sectionName+".mbtiles"
                // Log.e("URL FORMAT","URL FORMAT ****************** "+ BASE_MAP_URL_FORMAT);
                TileProvider tileProvider = new ExpandedMBTilesTileProvider(new File(BASE_MAP_URL_FORMAT.toString()), 256, 256);
                TileOverlay tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                        .tileProvider(tileProvider));
                tileOverlay.setTransparency(0.5f - tileOverlay.getTransparency());
                tileOverlay.setVisible(true);
                if (Util.isInternetAvailable(getActivity()) == true && mMap != null && tileOverlay.isVisible()==true) {
                    //
                    dialog = new ProgressDialog(getActivity(), R.style.ProgressDialog);
                    dialog.setMessage("Fetching Route");
                    dialog.setMax(100);
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DownloadFeaturesFromServer download= new DownloadFeaturesFromServer();
                            download.execute();
                        }
                    }, 10);

                } else {
                    // TODO - needs to show toast message activation required
                }
                addMarkers();
                final LatLng position1 = new LatLng(sourceLat, sourceLng);
                getAllEdgesData();
                if(edgeDataList!=null && edgeDataList.size()>0){
                    if(enteredMode==1) {
                        nearestPointArray=new ArrayList<>();
                        edgeDataPointsList = new ArrayList<LatLng>();
                        AllPointsList=new ArrayList<>();
                       // CalculateNearestViaNearestPoint();
                        MoveWithGpsPointInBetWeenAllPoints();
                    }else if(enteredMode==2){
                        CalculateNearestViaFakeGPS();

                    }
                }else{
                    dialog = new ProgressDialog(getActivity(), R.style.ProgressDialog);
                    dialog.setMessage("Fetching Route");
                    dialog.setMax(100);
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //  GetRouteDetails();
                        }
                    }, 10);
                }

            }
        });

        return rootView;
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public void addMarkers(){

        LatLng position1= new LatLng(sourceLat,sourceLng);
        Log.e("URL FORMAT","Uposition2 T ****************** "+ position1);
        sourceMarker = mMap.addMarker(new MarkerOptions()
                .position(position1)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.red_marker_24)));
        CameraPosition googlePlex = CameraPosition.builder()
                .target(position1)
                .zoom(18)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);


        LatLng position2= new LatLng(destLat,destLng);
        Log.e("URL FORMAT","Uposition2 T ****************** "+ position2);
        destinationMarker= mMap.addMarker(new MarkerOptions()
                .position(position2)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.green_marker_24)));
        CameraPosition googlePlex1 = CameraPosition.builder()
                .target(position2)
                .zoom(18)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex1), 1000, null);

    }

    class DownloadFeaturesFromServer extends AsyncTask<String, String, String> {
        String FeatureResponse = "";
        // Download features from server using URL and get the data and inserted to Respective tables like DT, SS,RMU ect...
        // and process that json data and insert to respective tables ....;
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... f_url) {
            try {
                String httprequest = "http://202.53.11.74/dtnavigation/api/routing/routenavigate";
                try {
                    FeatureResponse = HttpPost(httprequest,SourcePoint,DestinationPoint);
                    Log.e("RESPONSE", "RESPONSE" + FeatureResponse);
                    //{"$id":"1","Message":"Sucess","Status":"Success","TotalDistance":158.891838,
                    // "Route":[{"$id":"2","EdgeNo":"894","GeometryText":"0","Geometry":{"$id":"3","type":"LineString","coordinates":[[472233.15880000032,2764734.6520000007],[472248.05449999962,2764731.7961999997],[472255.30360000022,2764730.4064000007],[472258.1058,2764730.0001999997],[472260.93340000045,2764729.8501999993],[472264.23180000018,2764729.9999],[472267.49590000045,2764730.4978],[472270.36359999981,2764731.2358999997],[472273.1481999997,2764732.2429000009],[472287.20359999966,2764738.0950000007],[472291.11450000014,2764739.7233000007]]}},{"$id":"4","EdgeNo":"807","GeometryText":"0","Geometry":{"$id":"5","type":"LineString","coordinates":[[472291.11450000014,2764739.7233000007],[472290.56520000007,2764742.9920000006],[472290.42860000022,2764744.4061999992],[472290.49149999954,2764745.8255000003],[472290.75250000041,2764747.2221000008],[472291.34240000043,2764749.4847999997]]}},{"$id":"6","EdgeNo":"651","GeometryText":"0","Geometry":{"$id":"7","type":"LineString","coordinates":[[472282.38850000035,2764750.7881000005],[472282.80910000019,2764749.9809000008],[472283.36330000032,2764749.2588],[472284.03430000041,2764748.6437999997],[472284.80179999955,2764748.1544000003],[472285.64250000007,2764747.8055000007],[472286.53089999966,2764747.6076999996],[472287.4402999999,2764747.5669],[472288.1738,2764747.6624],[472288.34289999958,2764747.6843999997],[472289.21140000038,2764747.9565999992],[472290.01960000023,2764748.3752999995],[472290.74299999978,2764748.9277999997],[472291.34240000043,2764749.4847999997]]}},{"$id":"8","EdgeNo":"897","GeometryText":"0","Geometry":{"$id":"9","type":"LineString","coordinates":[[472257.58829999994,2764706.4168999996],[472264.65139999986,2764724.2607000004],[472267.49590000045,2764730.4978],[472271.2089999998,2764736.7921999991],[472273.32809999958,2764739.7956000008],[472275.608,2764742.6788999997],[472282.38850000035,2764750.7881000005]]}},{"$id":"10","EdgeNo":"898","GeometryText":"0","Geometry":{"$id":"11","type":"LineString","coordinates":[[472266.96239999961,2764681.4626],[472257.58829999994,2764706.4168999996]]}}]}

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            //  dialog.setTitle("Downloaded Features ");
            //    dialog.dismiss();
            JSONObject jsonObject = null;
            try {
                if(FeatureResponse!=null){
                    String delQuery = "DELETE  FROM " + EdgeDataT.TABLE_NAME;
                    Log.e("DEL QUERY","DEL QUERY " + delQuery);
                    sqlHandler.executeQuery(delQuery.toString());

                    jsonObject = new JSONObject(FeatureResponse);
                    String ID = String.valueOf(jsonObject.get("$id"));
                    String Message = jsonObject.getString("Message");
                    String Status = jsonObject.getString("Status");
                    String TotalDistance = jsonObject.getString("TotalDistance");
                    JSONArray jSonRoutes = new JSONArray(jsonObject.getString("Route"));
                    Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes);
                    for (int i = 0; i < jSonRoutes.length(); i++) {
                        points=new ArrayList();
                        convertedPoints=new ArrayList<LatLng>();
                        Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes.get(i));
                        // List Routes=new ArrayList();
                        // Routes.add(jSonRoutes.get(i));
                        JSONObject Routes = new JSONObject(jSonRoutes.get(i).toString());
                        String $id = Routes.getString("$id");
                        String EdgeNo = Routes.getString("EdgeNo");
                        String GeometryText = Routes.getString("GeometryText");
                        Log.e("GeometryText", "GeometryText" + GeometryText);
                        String Geometry = Routes.getString("Geometry");
                        Log.e("Geometry", "Geometry----" + Geometry);
                        JSONObject geometryObject = new JSONObject(Routes.getString("Geometry"));
                        String $id1 = geometryObject.getString("$id");
                        String type = geometryObject.getString("type");
                        Log.e("type", "type----" + type);
                        String coordinates = geometryObject.getString("coordinates");
                        Log.e("coordinates", "coordinates----" + coordinates);
                        JSONArray jSonLegs = new JSONArray(geometryObject.getString("coordinates"));
                        Log.e("jSonLegs", "jSonLegs----" + jSonLegs);
                        for (int j = 0; j < jSonLegs.length(); j++) {
                            Log.e("JSON LEGS", "JSON CORDINATES" + jSonLegs.get(j));
                            // Log.e("JSON LEGS","JSON CORDINATES  SIZE ----- "+ jSonLegs.length());
                            points.add(jSonLegs.get(j));
                            Log.e("JSON LEGS", " LATLNG RESULT------ " + points.size());

                            StringBuilder query = new StringBuilder("INSERT INTO ");
                            query.append(GeometryT.TABLE_NAME).append("(ID,message,status,totaldistance,edgeNo,geometryType,geometry) values (")
                                    .append("'").append(ID).append("',")
                                    .append("'").append(Message).append("',")
                                    .append("'").append(Status).append("',")
                                    .append("'").append(TotalDistance).append("',")
                                    .append("'").append(EdgeNo).append("',")
                                    .append("'").append(type).append("',")
                                    .append("'").append(jSonLegs.get(j)).append("')");
                            sqlHandler.executeQuery(query.toString());
                            sqlHandler.closeDataBaseConnection();


                        }
                        StringBuilder query = new StringBuilder("INSERT INTO ");
                        query.append(EdgeDataT.TABLE_NAME).append("(edgeNo,startPoint,endPoint) values (")
                                .append("'").append(EdgeNo).append("',")
                                .append("'").append(jSonLegs.get(0)).append("',")
                                .append("'").append(jSonLegs.get(jSonLegs.length()-1)).append("')");
                        sqlHandler.executeQuery(query.toString());
                        sqlHandler.closeDataBaseConnection();
                        for (int p = 0; p < points.size(); p++) {
                            Log.e("JSON LEGS", "JSON POINTS LIST ---- " + points.get(p));
                            String listItem = points.get(p).toString();
                            listItem = listItem.replace("[", "");
                            listItem = listItem.replace("]", "");
                            Log.e("JSON LEGS", "JSON POINTS LIST ---- " + listItem);
                            String[] subListItem = listItem.split(",");
                            Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem.length);
                            Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[0]);
                            Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[1]);
                            Double y = Double.valueOf(subListItem[0]);
                            Double x = Double.valueOf(subListItem[1]);
                            StringBuilder sb=new StringBuilder();
                            //  sb.append(x).append(",").append(y).append(":");
                            //  LocationPerpedicularPoints.add(sb.toString());
                            LatLng latLng = new LatLng(x, y);
                            Log.e("JSON LEGS", " LATLNG RESULT------ " + latLng);
                            convertedPoints.add(latLng);
                            for (int k = 0; k < convertedPoints.size(); k++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                PolylineOptions polylineOptions = new PolylineOptions();
                                if(polylineOptions!=null && mMap!=null) {
                                    markerOptions.position(convertedPoints.get(k));
                                    markerOptions.title("Position");
                                    polylineOptions.color(Color.RED);
                                    polylineOptions.width(6);
                                    polylineOptions.addAll(convertedPoints);
                                    polylineOptions.color(Color.BLUE).width(8);
                                    //final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.circle_red);
                                    //  mMap.addMarker(new MarkerOptions().position(SourcePosition)).title("").icon(icon));
                                    mMap.addPolyline(polylineOptions);

                                }

                                // polyline.setClickable(true);
                                //polyline.setTag(redLinesList.get(i).getSlno());
                                //mMap.addMarker(markerOptions);
                            }

                        }


                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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


    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }
    private  List<EdgeDataT> getAllEdgesData() {
        String query = "SELECT * FROM " + EdgeDataT.TABLE_NAME;
        Cursor c1 = sqlHandler.selectQuery(query);
        edgeDataList = (List<EdgeDataT>) SqlHandler.getDataRows(EdgeDataT.MAPPING, EdgeDataT.class, c1);
        sqlHandler.closeDataBaseConnection();
        return edgeDataList;
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
    private void drawMarkerWithCircle(LatLng gpsPosition,double radius){
        // double radiusInMeters = 400.0;


        CircleOptions circleOptions = new CircleOptions().center(gpsPosition).radius(radius).fillColor(Color.parseColor("#2271cce7")).strokeColor(Color.parseColor("#2271cce7")).strokeWidth(3);
        mCircle = mMap.addCircle(circleOptions);

        //  MarkerOptions markerOptions = new MarkerOptions().position(gpsPosition);
        // mPositionMarker = mMap.addMarker(markerOptions);
    }

    private double showDistance(LatLng latlng1,LatLng latLng2) {
        double distance = SphericalUtil.computeDistanceBetween(latlng1,latLng2);
        Log.e("Distance -----","Distance between Source and Destination -------- "+distance);
        //   mTextView.setText("The markers are " + formatNumber(distance) + " apart.");
        return distance;
    }
    public int getLatLngPoints(){
        /*
        LatLngDataArray.add(new LatLng(24.978398, 55.067026));
        LatLngDataArray.add(new LatLng(24.978661, 55.066962));
        LatLngDataArray.add(new LatLng(24.978461, 55.067176));
        LatLngDataArray.add(new LatLng(24.978909, 55.067182));
        LatLngDataArray.add(new LatLng(24.978933, 55.067654));
        LatLngDataArray.add(new LatLng(24.979298, 55.067608));
        LatLngDataArray.add(new LatLng(24.979112, 55.067794));
        LatLngDataArray.add(new LatLng(24.979336, 55.067604));
        LatLngDataArray.add(new LatLng(24.979174, 55.067955));
        LatLngDataArray.add(new LatLng(24.979642, 55.067879));
        LatLngDataArray.add(new LatLng(24.979440, 55.068319));
        LatLngDataArray.add(new LatLng(24.979920, 55.068179));
        LatLngDataArray.add(new LatLng(24.979608, 55.068542));
        LatLngDataArray.add(new LatLng(24.979723, 55.068644));
        LatLngDataArray.add(new LatLng(24.980097, 55.068970));
        LatLngDataArray.add(new LatLng(24.980536, 55.069001));
        LatLngDataArray.add(new LatLng(24.980537, 55.069518));
        LatLngDataArray.add(new LatLng(24.980397, 55.069499));
        LatLngDataArray.add(new LatLng(24.980609, 55.069576));
        LatLngDataArray.add(new LatLng(24.980891, 55.069517));
        LatLngDataArray.add(new LatLng(24.981006, 55.069698));
        LatLngDataArray.add(new LatLng(24.981112, 55.069792));
        LatLngDataArray.add(new LatLng(24.981108, 55.070154));
        LatLngDataArray.add(new LatLng(24.981456, 55.070081));
        LatLngDataArray.add(new LatLng(24.981215, 55.070271));
        LatLngDataArray.add(new LatLng(24.981583, 55.070235));
        LatLngDataArray.add(new LatLng(24.981427, 55.070528));
        LatLngDataArray.add(new LatLng(24.981787, 55.070483));
        LatLngDataArray.add(new LatLng(24.981673, 55.070790));
        LatLngDataArray.add(new LatLng(24.982204, 55.070956));
        LatLngDataArray.add(new LatLng(24.982094, 55.071290));
        LatLngDataArray.add(new LatLng(24.982396, 55.071209));
        LatLngDataArray.add(new LatLng(24.982221, 55.071319));
        LatLngDataArray.add(new LatLng(24.982357, 55.071399));



        LatLngDataArray.add(new LatLng(24.978699,55.067081));
        LatLngDataArray.add(new LatLng(24.978960,55.067772));
        LatLngDataArray.add(new LatLng(24.979487,55.067970));
        LatLngDataArray.add(new LatLng(24.980118,55.068724));
        LatLngDataArray.add(new LatLng(24.980529,55.069521));
        LatLngDataArray.add(new LatLng(24.980913,55.069927));



         LatLngDataArray.add(new LatLng(24.986486,55.072528));
        LatLngDataArray.add(new LatLng(24.986599,55.072608 ));
        LatLngDataArray.add(new LatLng( 24.986734,55.072730));
        LatLngDataArray.add(new LatLng( 24.986857,55.072905));
        LatLngDataArray.add(new LatLng(24.986885,55.072964));
        LatLngDataArray.add(new LatLng( 24.986903,55.072949));
        LatLngDataArray.add(new LatLng( 24.986908,55.072972));
        LatLngDataArray.add(new LatLng(  24.986901,55.072986 ));
        LatLngDataArray.add(new LatLng( 24.986898,55.073016 ));
        LatLngDataArray.add(new LatLng(24.986865,55.073056));
        LatLngDataArray.add(new LatLng(  24.986726,55.073200));
        LatLngDataArray.add(new LatLng( 24.986652,55.073279));
        LatLngDataArray.add(new LatLng( 24.986502,55.073438));
        LatLngDataArray.add(new LatLng(  24.986242,55.073715));
        LatLngDataArray.add(new LatLng( 24.986131, 55.073830));
        LatLngDataArray.add(new LatLng( 24.986097,55.073878));

  */

        LatLngDataArray.add(new LatLng(24.986486,55.072528));
        LatLngDataArray.add(new LatLng(24.986599,55.072608 ));
        LatLngDataArray.add(new LatLng( 24.986734,55.072730));
        LatLngDataArray.add(new LatLng( 24.986857,55.072905));
        LatLngDataArray.add(new LatLng(24.986885,55.072964));
        LatLngDataArray.add(new LatLng( 24.986903,55.072949));
        LatLngDataArray.add(new LatLng( 24.986908,55.072972));
        LatLngDataArray.add(new LatLng(  24.986901,55.072986 ));
        LatLngDataArray.add(new LatLng( 24.986898,55.073016 ));
        LatLngDataArray.add(new LatLng(24.986865,55.073056));
        LatLngDataArray.add(new LatLng(  24.986726,55.073200));
        LatLngDataArray.add(new LatLng( 24.986652,55.073279));
        LatLngDataArray.add(new LatLng( 24.986502,55.073438));
        LatLngDataArray.add(new LatLng(  24.986242,55.073715));
        LatLngDataArray.add(new LatLng( 24.986131, 55.073830));
        LatLngDataArray.add(new LatLng( 24.986097,55.073878));



        return LatLngDataArray.size();
    }
    private void animateMarkerNew(final LatLng endPosition, final Marker marker) {
            //value animator test for location
        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            // final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(30000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(15.5f)
                                .build()));

                        marker.setRotation(getBearing(startPosition, endPosition));
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

    class ReRouteFeaturesFromServer extends AsyncTask<String, String, String> {
        String FeatureResponse = "";
        // Download features from server using URL and get the data and inserted to Respective tables like DT, SS,RMU ect...
        // and process that json data and insert to respective tables ....;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... f_url) {
            try {
                String httprequest = "http://202.53.11.74/dtnavigation/api/routing/routenavigate";
                try {
                    FeatureResponse = HttpPost(httprequest, currentGpsPoint, DestinationPoint);
                    Log.e("RESPONSE", "RESPONSE" + FeatureResponse);
                    //{"$id":"1","Message":"Sucess","Status":"Success","TotalDistance":158.891838,
                    // "Route":[{"$id":"2","EdgeNo":"894","GeometryText":"0","Geometry":{"$id":"3","type":"LineString","coordinates":[[472233.15880000032,2764734.6520000007],[472248.05449999962,2764731.7961999997],[472255.30360000022,2764730.4064000007],[472258.1058,2764730.0001999997],[472260.93340000045,2764729.8501999993],[472264.23180000018,2764729.9999],[472267.49590000045,2764730.4978],[472270.36359999981,2764731.2358999997],[472273.1481999997,2764732.2429000009],[472287.20359999966,2764738.0950000007],[472291.11450000014,2764739.7233000007]]}},{"$id":"4","EdgeNo":"807","GeometryText":"0","Geometry":{"$id":"5","type":"LineString","coordinates":[[472291.11450000014,2764739.7233000007],[472290.56520000007,2764742.9920000006],[472290.42860000022,2764744.4061999992],[472290.49149999954,2764745.8255000003],[472290.75250000041,2764747.2221000008],[472291.34240000043,2764749.4847999997]]}},{"$id":"6","EdgeNo":"651","GeometryText":"0","Geometry":{"$id":"7","type":"LineString","coordinates":[[472282.38850000035,2764750.7881000005],[472282.80910000019,2764749.9809000008],[472283.36330000032,2764749.2588],[472284.03430000041,2764748.6437999997],[472284.80179999955,2764748.1544000003],[472285.64250000007,2764747.8055000007],[472286.53089999966,2764747.6076999996],[472287.4402999999,2764747.5669],[472288.1738,2764747.6624],[472288.34289999958,2764747.6843999997],[472289.21140000038,2764747.9565999992],[472290.01960000023,2764748.3752999995],[472290.74299999978,2764748.9277999997],[472291.34240000043,2764749.4847999997]]}},{"$id":"8","EdgeNo":"897","GeometryText":"0","Geometry":{"$id":"9","type":"LineString","coordinates":[[472257.58829999994,2764706.4168999996],[472264.65139999986,2764724.2607000004],[472267.49590000045,2764730.4978],[472271.2089999998,2764736.7921999991],[472273.32809999958,2764739.7956000008],[472275.608,2764742.6788999997],[472282.38850000035,2764750.7881000005]]}},{"$id":"10","EdgeNo":"898","GeometryText":"0","Geometry":{"$id":"11","type":"LineString","coordinates":[[472266.96239999961,2764681.4626],[472257.58829999994,2764706.4168999996]]}}]}

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            //  dialog.setTitle("Downloaded Features ");
            //    dialog.dismiss();
            JSONObject jsonObject = null;
            try {
                if (FeatureResponse != null) {
                    String delQuery = "DELETE  FROM " + EdgeDataT.TABLE_NAME;
                    Log.e("DEL QUERY", "DEL QUERY " + delQuery);
                    sqlHandler.executeQuery(delQuery.toString());

                    jsonObject = new JSONObject(FeatureResponse);
                    String ID = String.valueOf(jsonObject.get("$id"));
                    String Message = jsonObject.getString("Message");
                    String Status = jsonObject.getString("Status");
                    String TotalDistance = jsonObject.getString("TotalDistance");
                    JSONArray jSonRoutes = new JSONArray(jsonObject.getString("Route"));
                    Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes);
                    for (int i = 0; i < jSonRoutes.length(); i++) {
                        points = new ArrayList();
                        convertedPoints = new ArrayList<LatLng>();
                        Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes.get(i));
                        // List Routes=new ArrayList();
                        // Routes.add(jSonRoutes.get(i));
                        JSONObject Routes = new JSONObject(jSonRoutes.get(i).toString());
                        String $id = Routes.getString("$id");
                        String EdgeNo = Routes.getString("EdgeNo");
                        String GeometryText = Routes.getString("GeometryText");
                        Log.e("GeometryText", "GeometryText" + GeometryText);
                        String Geometry = Routes.getString("Geometry");
                        Log.e("Geometry", "Geometry----" + Geometry);
                        JSONObject geometryObject = new JSONObject(Routes.getString("Geometry"));
                        String $id1 = geometryObject.getString("$id");
                        String type = geometryObject.getString("type");
                        Log.e("type", "type----" + type);
                        String coordinates = geometryObject.getString("coordinates");
                        Log.e("coordinates", "coordinates----" + coordinates);
                        JSONArray jSonLegs = new JSONArray(geometryObject.getString("coordinates"));
                        Log.e("jSonLegs", "jSonLegs----" + jSonLegs);
                        for (int j = 0; j < jSonLegs.length(); j++) {
                            Log.e("JSON LEGS", "JSON CORDINATES" + jSonLegs.get(j));
                            // Log.e("JSON LEGS","JSON CORDINATES  SIZE ----- "+ jSonLegs.length());
                            points.add(jSonLegs.get(j));
                            Log.e("JSON LEGS", " LATLNG RESULT------ " + points.size());

                            StringBuilder query = new StringBuilder("INSERT INTO ");
                            query.append(GeometryT.TABLE_NAME).append("(ID,message,status,totaldistance,edgeNo,geometryType,geometry) values (")
                                    .append("'").append(ID).append("',")
                                    .append("'").append(Message).append("',")
                                    .append("'").append(Status).append("',")
                                    .append("'").append(TotalDistance).append("',")
                                    .append("'").append(EdgeNo).append("',")
                                    .append("'").append(type).append("',")
                                    .append("'").append(jSonLegs.get(j)).append("')");
                            sqlHandler.executeQuery(query.toString());
                            sqlHandler.closeDataBaseConnection();


                        }
                        StringBuilder query = new StringBuilder("INSERT INTO ");
                        query.append(EdgeDataT.TABLE_NAME).append("(edgeNo,startPoint,endPoint) values (")
                                .append("'").append(EdgeNo).append("',")
                                .append("'").append(jSonLegs.get(0)).append("',")
                                .append("'").append(jSonLegs.get(jSonLegs.length() - 1)).append("')");
                        sqlHandler.executeQuery(query.toString());
                        sqlHandler.closeDataBaseConnection();
                        for (int p = 0; p < points.size(); p++) {
                            Log.e("JSON LEGS", "JSON POINTS LIST ---- " + points.get(p));
                            String listItem = points.get(p).toString();
                            listItem = listItem.replace("[", "");
                            listItem = listItem.replace("]", "");
                            Log.e("JSON LEGS", "JSON POINTS LIST ---- " + listItem);
                            String[] subListItem = listItem.split(",");
                            Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem.length);
                            Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[0]);
                            Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[1]);
                            Double y = Double.valueOf(subListItem[0]);
                            Double x = Double.valueOf(subListItem[1]);
                            StringBuilder sb = new StringBuilder();
                            //  sb.append(x).append(",").append(y).append(":");
                            //  LocationPerpedicularPoints.add(sb.toString());
                            LatLng latLng = new LatLng(x, y);
                            Log.e("JSON LEGS", " LATLNG RESULT------ " + latLng);
                            convertedPoints.add(latLng);
                            for (int k = 0; k < convertedPoints.size(); k++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                PolylineOptions polylineOptions = new PolylineOptions();
                                if (polylineOptions != null && mMap != null) {
                                    markerOptions.position(convertedPoints.get(k));
                                    markerOptions.title("Position");
                                    polylineOptions.color(Color.RED);
                                    polylineOptions.width(6);
                                    polylineOptions.addAll(convertedPoints);
                                    polylineOptions.color(Color.RED).width(8);
                                    //final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.circle_red);
                                    //  mMap.addMarker(new MarkerOptions().position(SourcePosition)).title("").icon(icon));
                                    mMap.addPolyline(polylineOptions);
                                    addMarkers();
                                }

                                // polyline.setClickable(true);
                                //polyline.setTag(redLinesList.get(i).getSlno());
                                //mMap.addMarker(markerOptions);
                            }

                        }


                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void animateCarMove(final Marker marker, final LatLng beginLatLng, final LatLng endLatLng, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();
        // set car bearing for current part of path
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);
        int centreX = mMarkerIcon.getWidth()/2;
        int centreY = mMarkerIcon.getHeight()/2;
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0,centreX, centreY, matrix, true)));

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
            LatLng prevLatLng = nearestPointValuesList.get(mIndexCurrentPoint - 1);
            LatLng currLatLng = nearestPointValuesList.get(mIndexCurrentPoint);
            LatLng nextLatLng = nearestPointValuesList.get(mIndexCurrentPoint + 1);

            float beginAngle = (float)(90 * getAngle(prevLatLng, currLatLng) / Math.PI);
            float endAngle = (float)(90 * getAngle(currLatLng, nextLatLng) / Math.PI);

            animateCarTurn(mPositionMarker, beginAngle, endAngle, 10);
        }
    }
    private void animateCarTurn(final Marker marker, final float startAngle, final float endAngle, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();

        final float dAndgle = endAngle - startAngle;

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                Matrix m = new Matrix();
                float angle=startAngle + dAndgle * t;
                m.postRotate(angle);
                int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
                int height = Resources.getSystem().getDisplayMetrics().heightPixels;
                Bitmap rotatedBitmap = Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), m, true);

                marker.setIcon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap));


                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    nextMoveAnimation();
                }
            }
        });
    }
    private void nextMoveAnimation() {
        if (mIndexCurrentPoint < nearestPointValuesList.size() - 1) {
            double resultdistance=showDistance(nearestPointValuesList.get(mIndexCurrentPoint),new LatLng(destLat,destLng)); //in km
            double resultMts=resultdistance*1000;
            String finalResultMts=String.format("%.2f", resultMts);
            double speed=10.0; //kmph
            ETACalclator calculator=new ETACalclator();
            double resultTime=calculator.cal_time(resultdistance, speed);
            System.out.println("\n The calculated Time(hr) : "+resultTime);
            tv.setText("Estimated Time : "+ resultTime +" SEC ");
            tv1.setText("DISTANCE : "+ finalResultMts +" Meters ");
            tv2.setText("Speed : "+ speed +"KMPH ");
            animateCarMove(mPositionMarker, nearestPointValuesList.get(mIndexCurrentPoint), nearestPointValuesList.get(mIndexCurrentPoint+1), 10000);
        }
    }

    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return Math.atan2(Math.sin(dl) * Math.cos(f2) , Math.cos(f1) * Math.sin(f2) - Math.sin(f1) * Math.cos(f2) * Math.cos(dl));
    }
    public void MoveWithLatLngEndPoints(){ //move in between LatLng end points
        getLatLngPoints();
        getAllEdgesData();

        LatLng srcP1=new LatLng(sourceLat,sourceLng);
        edgeDataPointsList.add(srcP1);
        if (edgeDataList != null && edgeDataList.size() > 0) {
            for (int i = 0; i < edgeDataList.size(); i++) {
                EdgeDataT edge = new EdgeDataT();
                edge = edgeDataList.get(i);
                edge.getEdgeNo();
                String stPoint = edge.getStartPoint();
                String endPoint = edge.getEndPoint();
                String points = edge.getAllPoints();
                //[[55.07252845510704,24.986485718893903], [55.07252691395126,24.986503080465624], [55.07252858393359,24.9865204314153], [55.072533418545014,24.986537282374343], [55.072541282105426,24.9865531573588]]
                Log.e("END POINTS", "END POINTS----" + endPoint);
                String endPointInfo = endPoint.replace("[", "");
                endPointInfo = endPointInfo.replace("]", "");
                String[] endPointInfoArray = endPointInfo.split(",");
                String lat=endPointInfoArray[0];
                String longi=endPointInfoArray[1];
                Log.e("END POINTS", "Lat-----------" + lat);
                Log.e("END POINTS", "longi---------" + longi);

                double endPtLat=Double.valueOf(lat);
                double endPtLongi=Double.valueOf(longi);
                LatLng finalEndPt=new LatLng(endPtLat,endPtLongi);
                edgeDataPointsList.add(finalEndPt);
            }
            Log.e("END POINTS LIST", "END POINTS List" + edgeDataPointsList.size());

            for (int j = 0; j < LatLngDataArray.size(); j++) {
                currentGpsPosition = LatLngDataArray.get(j);
                for(int epData=0;epData<edgeDataPointsList.size()-1;epData++) {
                    String FirstCordinate = String.valueOf(edgeDataPointsList.get(epData));

                    Log.e("FirstCordinate", "FirstCordinate----" + FirstCordinate);
                    String First = FirstCordinate.replace("lat/lng: (", "");
                    First = First.replace(")", "");
                    String[] FirstLatLngsData = First.split(",");

                    Double FirstLatitude = Double.valueOf(FirstLatLngsData[0]);
                    Double FirstLongitude = Double.valueOf(FirstLatLngsData[1]);
                    Log.e("Sorted ArrayList ", "-----FirstLatitude :" + FirstLatitude);
                    Log.e("Sorted ArrayList ", "-----FirstLongitude" + FirstLongitude);


                    // String[] SecondCordinateArray = SecondCordinate.split("#");
                    //  Log.e("Sorted ArrayList ", "in Ascending order ---AT 2--- :" + SecondCordinateArray[0]);
                    String SecondCordinate = String.valueOf(edgeDataPointsList.get(epData + 1));
                    Log.e("SecondCordinate", "SecondCordinate----" + SecondCordinate);
                    String Second = SecondCordinate.replace("lat/lng: (", "");

                    Second = Second.replace(")", "");
                    String[] SecondLatLngsData = Second.split(",");
                    Double SecondLatitude = Double.valueOf(SecondLatLngsData[0]);
                    Double SecondLongitude = Double.valueOf(SecondLatLngsData[1]);


                    String nearestPoint = GenerateLinePoint(FirstLatitude, FirstLongitude, SecondLatitude, SecondLongitude, currentGpsPosition.longitude, currentGpsPosition.latitude);
                    Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
                    String[] nearestDataStr = nearestPoint.split(",");
                    double latitude = Double.parseDouble(nearestDataStr[0]);
                    double longitude = Double.parseDouble(nearestDataStr[1]);
                    nearestPositionPoint = new LatLng(longitude, latitude);
                    nearestPointArray.add(nearestPositionPoint);
                    Log.e("NEAREST POINT", "NEAREST POINT LIST----------" + nearestPointArray.size());


                }
                mPositionMarker = mMap.addMarker(new MarkerOptions()
                        .position(nearestPositionPoint)
                        .title("currentLocation")
                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.circle_red)));
            }

            CameraPosition googlePlex = CameraPosition.builder()
                    .target(new LatLng(sourceLat,sourceLng))
                    .zoom(15)
                    .tilt(45)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

            // verifyRouteDeviation(routeDeviationDistance);
            //  animateMarkerToFinalDestination(mPositionMarker,DestinationPosition,new LatLngInterpolator.Spherical());
            // animateMarker(mMap,mPositionMarker,LatLngDataArray,false);
            // animateMarkerViaVertex(mPositionMarker,new LatLngInterpolator.Spherical());
            // animateMarkerNew(newCenterLatLng,mPositionMarker);
            for(int k=0;k<nearestPointArray.size();k++){
                Log.e("NEAREST POINT", "NEAREST POINT List----------" + nearestPointArray.get(k));
            }
          //  if(nearestPointArray!=null && nearestPointArray.size()>0) {
                animateCarMove(mPositionMarker, edgeDataPointsList.get(0), edgeDataPointsList.get(1), 1000);
          //  }

        }
    }
    public void MoveWithGpsPointInBetWeenAllPoints(){
        getLatLngPoints();
        getAllEdgesData();
        edgeDataPointsList = new ArrayList<LatLng>();

        //LatLng srcP1=new LatLng(sourceLat,sourceLng);
        // edgeDataPointsList.add(SourcePosition);
        if (edgeDataList != null && edgeDataList.size() > 0) {
            AllPointsList=new ArrayList<>();
            for (int i = 0; i < edgeDataList.size(); i++) {
                EdgeDataT edge = new EdgeDataT();
                edge = edgeDataList.get(i);
                edge.getEdgeNo();
                String stPoint = edge.getStartPoint();
                String endPoint = edge.getEndPoint();
                String points = edge.getAllPoints();
                //[[55.07252845510704,24.986485718893903], [55.07252691395126,24.986503080465624], [55.07252858393359,24.9865204314153], [55.072533418545014,24.986537282374343], [55.072541282105426,24.9865531573588]]
                if(points!=null){
                    String AllPoints = points.replace("[", "");
                    AllPoints = AllPoints.replace("]", "");
                    String[] AllPointsArray = AllPoints.split(", ");
                    Log.e("ALL POINTS", "ALL POINTS" + AllPointsArray.length);

                    for (int ap = 0; ap < AllPointsArray.length; ap++) {
                        AllPointsList.add(AllPointsArray[ap]);
                    }
                }
            }
        }
        Log.e("ALL POINTS ", "FROM DATABASE ----- " + AllPointsList.size());
        for (int pntCount = 0; pntCount < AllPointsList.size(); pntCount++) {
            String data = String.valueOf(AllPointsList.get(pntCount));
            String dataStr = data.replace("[", "");
            dataStr = dataStr.replace("]", "");
            String ptData[] = dataStr.split(",");
            double Lat = Double.parseDouble(ptData[0]);
            double Lang = Double.parseDouble(ptData[1]);
            PointData = new LatLng(Lat, Lang);
            edgeDataPointsList.add(PointData);
            Log.e("ALL POINTS ", "FROM DATABASE ----- " + edgeDataPointsList.get(pntCount));
        }
        Log.e("ALL POINTS ", "FROM DATABASE ----- " + AllPointsList.size());
        Log.e("ALL POINTS ", "FROM DATABASE ----- " + edgeDataPointsList.size());
        nearestPointValuesList=new ArrayList<LatLng>();
        for (int j = 0; j < LatLngDataArray.size(); j++) {
            currentGpsPosition = LatLngDataArray.get(j);
            // List<LatLng> EdgeWithoutDuplicates = new ArrayList<>(edgeDataPointsList);
            List<LatLng> EdgeWithoutDuplicates = removeDuplicates(edgeDataPointsList);
            if (EdgeWithoutDuplicates != null && EdgeWithoutDuplicates.size() > 0) {
                Log.e("currentGpsPosition ", "currentGpsPosition POINT----------" + currentGpsPosition);
                String FirstCordinate="",SecondCordinate="";
                distancesList = new ArrayList();
                distanceValuesList = new ArrayList();
                hash_map = new HashMap<String, String>();
                for (int epList = 0; epList < EdgeWithoutDuplicates.size(); epList++) {
                    LatLng PositionMarkingPoint = EdgeWithoutDuplicates.get(epList);
                    Log.e("currentGpsPosition ", "PositionMarking POINT----------" + PositionMarkingPoint);
                    Log.e("currentGpsPosition ", "currentGpsPosition POINT----------" + currentGpsPosition);

                    double distance = distFrom(PositionMarkingPoint.latitude,PositionMarkingPoint.longitude,currentGpsPosition.longitude,currentGpsPosition.latitude);
                    //distanceValuesList.add("A"+" # "+edgeDataPointsList.get(epList));
                    // Mapping string values to int keys
                    // List<LatLng> deduped = list.stream().distinct().collect(Collectors.toList());
                    ;
                    hash_map.put(String.valueOf(distance), String.valueOf(EdgeWithoutDuplicates.get(epList)));
                    // distanceValuesList.add("A"+" ");
                    //  Log.e("Sorted ArrayList ", "in Ascending order : " + distanceValuesList.get(epList));
                    distancesList.add(distance);
                    Collections.sort(distancesList);
                }
                for(int i=0;i<distancesList.size();i++) {
                    Log.e("Sorted ArrayList ", "in Ascending order : " + distancesList.get(i));
                }

                String FirstShortestDistance = String.valueOf(distancesList.get(0));
                String SecondShortestDistance = String.valueOf(distancesList.get(1));
                boolean answerFirst= hash_map.containsKey(FirstShortestDistance);
                if (answerFirst) {
                    System.out.println("The list contains " + FirstShortestDistance);
                    FirstCordinate = (String)hash_map.get(FirstShortestDistance);
                    Log.e("Sorted ArrayList ", "INDEX----- : " + FirstCordinate);
                } else {
                    System.out.println("The list does not contains "+ "FALSE");
                }
                boolean answerSecond= hash_map.containsKey(SecondShortestDistance);
                if (answerSecond) {
                    System.out.println("The list contains " + SecondShortestDistance);
                    SecondCordinate = (String)hash_map.get(SecondShortestDistance);
                    Log.e("Sorted ArrayList ", "INDEX----- : " + SecondCordinate);
                } else {
                    System.out.println("The list does not contains "+ "FALSE");
                }

                //  String[] FirstCordinateArray = FirstCordinate.split("#");
                //  Log.e("Sorted ArrayList ", "in Ascending order ----AT 1---: " + FirstCordinateArray[0]);
                String First= FirstCordinate.replace("lat/lng: (","");
                First= First.replace(")","");
                String[] FirstLatLngsData=First.split(",");
                Double FirstLatitude= Double.valueOf(FirstLatLngsData[0]);
                Double FirstLongitude= Double.valueOf(FirstLatLngsData[1]);
                Log.e("Sorted ArrayList ", "-----FirstLatitude :" + FirstLatitude);
                Log.e("Sorted ArrayList ", "-----FirstLongitude" + FirstLongitude);
                // String[] SecondCordinateArray = SecondCordinate.split("#");
                //  Log.e("Sorted ArrayList ", "in Ascending order ---AT 2--- :" + SecondCordinateArray[0]);
                String Second= SecondCordinate.replace("lat/lng: (","");
                Second= Second.replace(")","");
                String[] SecondLatLngsData=Second.split(",");
                Double SecondLatitude= Double.valueOf(SecondLatLngsData[0]);
                Double SecondLongitude= Double.valueOf(SecondLatLngsData[1]);

                Log.e("Sorted ArrayList ", "-----SecondLatitude :" + SecondLatitude);
                Log.e("Sorted ArrayList ", "-----SecondLongitude" + SecondLongitude);

                String nearestPoint = GenerateLinePoint(FirstLatitude, FirstLongitude, SecondLatitude, SecondLongitude, currentGpsPosition.longitude, currentGpsPosition.latitude);
                Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
                String[] nearestDataStr = nearestPoint.split(",");
                double latitude = Double.parseDouble(nearestDataStr[0]);
                double longitude = Double.parseDouble(nearestDataStr[1]);
                nearestPositionPoint = new LatLng(longitude, latitude);
                nearestPointValuesList.add(nearestPositionPoint);

            }
        }
        Log.e("NEAREST Point", "NEAREST POINT &&&&&&&&&&&&&&&&&&&&&& " + nearestPositionPoint);
        for(int i=0;i<nearestPointValuesList.size();i++) {
            Log.e("Sorted ArrayList ", " NEAREST POINT LIST VALUES : " + nearestPointValuesList.get(i));

        }
        Log.e("EdgeSt Point", "End point" + LatLngDataArray.size());
        // animateLatLngZoom(nearestPositionPoint, 15, 5, 10);
        // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(nearestPositionPoint.latitude,nearestPositionPoint.longitude, 48));
        mPositionMarker = mMap.addMarker(new MarkerOptions()
                .position(nearestPositionPoint)
                .title("currentLocation")
                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_car_symbol)));
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(sourceLat,sourceLng))
                .zoom(15)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
        // verifyRouteDeviation(routeDeviationDistance);
        //  animateMarkerToFinalDestination(mPositionMarker,DestinationPosition,new LatLngInterpolator.Spherical());
        // animateMarker(mMap,mPositionMarker,LatLngDataArray,false);
        // animateMarkerViaVertex(mPositionMarker,new LatLngInterpolator.Spherical());
        // animateMarkerNew(newCenterLatLng,mPositionMarker);
        animateCarMove(mPositionMarker, nearestPointValuesList.get(0), nearestPointValuesList.get(1), 1000);

    }

    public void CalculateNearestViaFakeGPS(){
        Marker gpsMarker=null;
        getLatLngPoints();
        LatLng nearestPosition = null;
        //  nearestPointArray=new ArrayList<LatLng>();
        for(int i=0 ;i<LatLngDataArray.size();i++) {
            currentGpsPosition = LatLngDataArray.get(i);
            gpsMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentGpsPosition)
                    .title("currentLocation")
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.symbol_ht_ss)));

            String nearestPoint = GenerateLinePoint( sourceLng, sourceLat, destLng, destLat,currentGpsPosition.longitude,currentGpsPosition.latitude);
            Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
            String[] nearestDataStr = nearestPoint.split(",");
            double latitude = Double.parseDouble(nearestDataStr[0]);
            double longitude = Double.parseDouble(nearestDataStr[1]);
            nearestPosition=new LatLng(longitude,latitude);
            nearestPointArray.add(nearestPosition);
            mPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(nearestPosition)
                    .title("currentLocation")
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.circle_pink)));
        }


        Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPointArray.size());
        for(int k=0;k<nearestPointArray.size();k++){
            Log.e("NEAREST POINT", "NEAREST POINT List----------" + nearestPointArray.get(k));
        }
        if(nearestPointArray!=null && nearestPointArray.size()>0) {
            animateCarMoveViaFakeGPS(gpsMarker, nearestPointArray.get(0), nearestPointArray.get(1), 1000);
        }


    }


    public void CalculateNearestViaNearestPoint(){
        Marker gpsMarker=null;
        getLatLngPoints();
        LatLng nearestPosition = null;
        //  nearestPointArray=new ArrayList<LatLng>();
        for(int i=0 ;i<LatLngDataArray.size();i++) {
            currentGpsPosition = LatLngDataArray.get(i);
            gpsMarker = mMap.addMarker(new MarkerOptions()
                    .position(currentGpsPosition)
                    .title("currentLocation")
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.symbol_ht_ss)));

            String nearestPoint = GenerateLinePoint( sourceLng, sourceLat, destLng, destLat,currentGpsPosition.longitude,currentGpsPosition.latitude);
            Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
            String[] nearestDataStr = nearestPoint.split(",");
            double latitude = Double.parseDouble(nearestDataStr[0]);
            double longitude = Double.parseDouble(nearestDataStr[1]);
            nearestPosition=new LatLng(longitude,latitude);
            nearestPointArray.add(nearestPosition);
            mPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(nearestPosition)
                    .title("currentLocation")
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.circle_pink)));



        }


        Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPointArray.size());
        for(int k=0;k<nearestPointArray.size();k++){
            Log.e("NEAREST POINT", "NEAREST POINT List----------" + nearestPointArray.get(k));
        }
        if(nearestPointArray!=null && LatLngDataArray.size()>0) {

            animateCarMove(gpsMarker, LatLngDataArray.get(0), LatLngDataArray.get(1), 1000);
        }


    }
    private void animateCarMoveViaFakeGPS(final Marker marker, final LatLng beginLatLng, final LatLng endLatLng, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();
        // set car bearing for current part of path
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);

        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));

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

                // if not end of line segment of path
                if (t < 1.0) {
                    // call next marker position
                    handler.postDelayed(this, 16);
                } else {
                    // call turn animation
                    nextTurnAnimationViaFakeGPS();
                }
            }
        });
    }
    private void nextTurnAnimationViaFakeGPS() {
        mIndexCurrentPoint++;
        Log.e("EdgeListPoints","--------------"+nearestPointArray.size());
        if (mIndexCurrentPoint < LatLngDataArray.size() - 1) {
            LatLng prevLatLng = LatLngDataArray.get(mIndexCurrentPoint - 1);
            LatLng currLatLng = LatLngDataArray.get(mIndexCurrentPoint);
            LatLng nextLatLng = LatLngDataArray.get(mIndexCurrentPoint + 1);

            float beginAngle = (float)(90 * getAngle(prevLatLng, currLatLng) / Math.PI);
            float endAngle = (float)(90 * getAngle(currLatLng, nextLatLng) / Math.PI);

            animateCarTurnViaFakeGPS(mPositionMarker, beginAngle, endAngle, 10);
        }
    }
    private void animateCarTurnViaFakeGPS(final Marker marker, final float startAngle, final float endAngle, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();

        final float dAndgle = endAngle - startAngle;

        handler.post(new Runnable() {
            @Override
            public void run() {

                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                Matrix m = new Matrix();
                float angle=startAngle + dAndgle * t;
                m.postRotate(angle);
                int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
                int height = Resources.getSystem().getDisplayMetrics().heightPixels;
                Bitmap rotatedBitmap = Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), m, true);

                marker.setIcon(BitmapDescriptorFactory.fromBitmap(rotatedBitmap));


                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    nextMoveAnimationViaFakeGPS();
                }
            }
        });
    }
    private void nextMoveAnimationViaFakeGPS() {
        if (mIndexCurrentPoint < LatLngDataArray.size() - 1) {
            animateCarMove(mPositionMarker, LatLngDataArray.get(mIndexCurrentPoint), LatLngDataArray.get(mIndexCurrentPoint+1), 10000);
        }
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
    private List<LatLng> removeDuplicates(List<LatLng> EdgeWithoutDuplicates)
    {
        int count = edgeDataPointsList.size();

        for (int i = 0; i < count; i++)
        {
            for (int j = i + 1; j < count; j++)
            {
                if (edgeDataPointsList.get(i).equals(edgeDataPointsList.get(j)))
                {
                    edgeDataPointsList.remove(j--);
                    count--;
                }
            }
        }
        return EdgeWithoutDuplicates;
    }

}
    /*
    public void MoveWithGpsPointInBetWeenAllPoints(){
        getLatLngPoints();
        edgeDataPointsList = new ArrayList<LatLng>();
        AllPointsList=new ArrayList<>();
        //LatLng srcP1=new LatLng(sourceLat,sourceLng);
        // edgeDataPointsList.add(SourcePosition);
        if (edgeDataList != null && edgeDataList.size() > 0) {
            for (int i = 0; i < edgeDataList.size(); i++) {
                EdgeDataT edge = new EdgeDataT();
                edge = edgeDataList.get(i);
                edge.getEdgeNo();
                String stPoint = edge.getStartPoint();
                String endPoint = edge.getEndPoint();
                String points = edge.getAllPoints();
                //[[55.07252845510704,24.986485718893903], [55.07252691395126,24.986503080465624], [55.07252858393359,24.9865204314153], [55.072533418545014,24.986537282374343], [55.072541282105426,24.9865531573588]]

                String AllPoints = points.replace("[", "");
                AllPoints = AllPoints.replace("]", "");
                String[] AllPointsArray = AllPoints.split(", ");
                Log.e("ALL POINTS", "ALL POINTS" + AllPointsArray.length);

                for (int ap = 0; ap < AllPointsArray.length; ap++) {
                    AllPointsList.add(AllPointsArray[ap]);
                }
            }
        }
        Log.e("ALL POINTS ", "FROM DATABASE ----- " + AllPointsList.size());
        for (int pntCount = 0; pntCount < AllPointsList.size(); pntCount++) {
            String data = String.valueOf(AllPointsList.get(pntCount));
            String dataStr = data.replace("[", "");
            dataStr = dataStr.replace("]", "");
            String ptData[] = dataStr.split(",");
            double Lat = Double.parseDouble(ptData[0]);
            double Lang = Double.parseDouble(ptData[1]);
            PointData = new LatLng(Lat, Lang);
            edgeDataPointsList.add(PointData);
            Log.e("ALL POINTS ", "FROM DATABASE ----- " + edgeDataPointsList.get(pntCount));
        }
        Log.e("ALL POINTS ", "FROM DATABASE ----- " + AllPointsList.size());
        Log.e("ALL POINTS ", "FROM DATABASE ----- " + edgeDataPointsList.size());


        if (enteredMode == 1) {
            nearestPointValuesList=new ArrayList();
            for (int j = 0; j < LatLngDataArray.size(); j++) {
                currentGpsPosition = LatLngDataArray.get(j);
                List<LatLng> EdgeWithoutDuplicates = new ArrayList<>(edgeDataPointsList);

                if (EdgeWithoutDuplicates != null && EdgeWithoutDuplicates.size() > 0) {
                    Log.e("currentGpsPosition ", "currentGpsPosition POINT----------" + currentGpsPosition);
                    String FirstCordinate="",SecondCordinate="";
                    distancesList = new ArrayList();
                    distanceValuesList = new ArrayList();
                    hash_map = new HashMap<String, String>();
                    for (int epList = 0; epList < EdgeWithoutDuplicates.size(); epList++) {
                        LatLng PositionMarkingPoint = EdgeWithoutDuplicates.get(epList);
                        Log.e("currentGpsPosition ", "PositionMarking POINT----------" + PositionMarkingPoint);
                        Log.e("currentGpsPosition ", "currentGpsPosition POINT----------" + currentGpsPosition);
                        double distance = showDistance(PositionMarkingPoint, currentGpsPosition);
                        //distanceValuesList.add("A"+" # "+edgeDataPointsList.get(epList));
                        // Mapping string values to int keys
                        // List<LatLng> deduped = list.stream().distinct().collect(Collectors.toList());
                        ;
                        hash_map.put(String.valueOf(distance), String.valueOf(EdgeWithoutDuplicates.get(epList)));
                        // distanceValuesList.add("A"+" ");
                        //  Log.e("Sorted ArrayList ", "in Ascending order : " + distanceValuesList.get(epList));
                        distancesList.add(distance);
                        Collections.sort(distancesList);
                    }
                    for(int i=0;i<distancesList.size();i++) {
                        Log.e("Sorted ArrayList ", " Distance Values List : " + hash_map.get(i));
                        Log.e("Sorted ArrayList ", "in Ascending order : " + distancesList.get(i));
                    }

                    String FirstShortestDistance = String.valueOf(distancesList.get(0));
                    String SecondShortestDistance = String.valueOf(distancesList.get(1));
                    boolean answerFirst= hash_map.containsKey(FirstShortestDistance);
                    if (answerFirst) {
                        System.out.println("The list contains " + FirstShortestDistance);
                        FirstCordinate = (String)hash_map.get(FirstShortestDistance);
                        Log.e("Sorted ArrayList ", "INDEX----- : " + FirstCordinate);
                    } else {
                        System.out.println("The list does not contains "+ "FALSE");
                    }
                    boolean answerSecond= hash_map.containsKey(SecondShortestDistance);
                    if (answerSecond) {
                        System.out.println("The list contains " + SecondShortestDistance);
                        SecondCordinate = (String)hash_map.get(SecondShortestDistance);
                        Log.e("Sorted ArrayList ", "INDEX----- : " + SecondCordinate);
                    } else {
                        System.out.println("The list does not contains "+ "FALSE");
                    }

                    //  String[] FirstCordinateArray = FirstCordinate.split("#");
                    //  Log.e("Sorted ArrayList ", "in Ascending order ----AT 1---: " + FirstCordinateArray[0]);
                    String First= FirstCordinate.replace("lat/lng: (","");
                    First= First.replace(")","");
                    String[] FirstLatLngsData=First.split(",");

                    Double FirstLatitude= Double.valueOf(FirstLatLngsData[0]);
                    Double FirstLongitude= Double.valueOf(FirstLatLngsData[1]);
                    Log.e("Sorted ArrayList ", "-----FirstLatitude :" + FirstLatitude);
                    Log.e("Sorted ArrayList ", "-----FirstLongitude" + FirstLongitude);



                    // String[] SecondCordinateArray = SecondCordinate.split("#");
                    //  Log.e("Sorted ArrayList ", "in Ascending order ---AT 2--- :" + SecondCordinateArray[0]);
                    String Second= SecondCordinate.replace("lat/lng: (","");

                    Second= Second.replace(")","");
                    String[] SecondLatLngsData=Second.split(",");
                    Double SecondLatitude= Double.valueOf(SecondLatLngsData[0]);
                    Double SecondLongitude= Double.valueOf(SecondLatLngsData[1]);

                    Log.e("Sorted ArrayList ", "-----SecondLatitude :" + SecondLatitude);
                    Log.e("Sorted ArrayList ", "-----SecondLongitude" + SecondLongitude);

                    String nearestPoint = GenerateLinePoint(FirstLatitude, FirstLongitude, SecondLatitude, SecondLongitude, currentGpsPosition.longitude, currentGpsPosition.latitude);
                    Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
                    String[] nearestDataStr = nearestPoint.split(",");
                    double latitude = Double.parseDouble(nearestDataStr[0]);
                    double longitude = Double.parseDouble(nearestDataStr[1]);
                    nearestPositionPoint = new LatLng(longitude, latitude);
                    nearestPointValuesList.add(new LatLng(longitude, latitude));

                }

            }

            Log.e("NEAREST Point", "NEAREST POINT &&&&&&&&&&&&&&&&&&&&&& " + nearestPositionPoint);
            for(int i=0;i<nearestPointValuesList.size();i++) {
                Log.e("Sorted ArrayList ", " NEAREST POINT LIST VALUES : " + nearestPointValuesList.get(i));

            }

            Log.e("EdgeSt Point", "End point" + LatLngDataArray.size());
            // animateLatLngZoom(nearestPositionPoint, 15, 5, 10);
            // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(nearestPositionPoint.latitude,nearestPositionPoint.longitude, 48));
            mPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(nearestPositionPoint)
                    .title("currentLocation")
                    .icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_car_symbol)));
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(new LatLng(sourceLat,sourceLng))
                    .zoom(15)
                    .tilt(45)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

            // verifyRouteDeviation(routeDeviationDistance);
            //  animateMarkerToFinalDestination(mPositionMarker,DestinationPosition,new LatLngInterpolator.Spherical());
            // animateMarker(mMap,mPositionMarker,LatLngDataArray,false);
            // animateMarkerViaVertex(mPositionMarker,new LatLngInterpolator.Spherical());
            // animateMarkerNew(newCenterLatLng,mPositionMarker);
            animateCarMove(mPositionMarker, LatLngDataArray.get(0), LatLngDataArray.get(1), 1000);
        }
    }
    public void MoveWithLatLngEndPoints(){ //move in between LatLng end points
        getLatLngPoints();
        edgeDataPointsList = new ArrayList<LatLng>();
        AllPointsList=new ArrayList<>();
        LatLng srcP1=new LatLng(sourceLat,sourceLng);
        edgeDataPointsList.add(srcP1);
        if (edgeDataList != null && edgeDataList.size() > 0) {
            for (int i = 0; i < edgeDataList.size(); i++) {
                EdgeDataT edge = new EdgeDataT();
                edge = edgeDataList.get(i);
                edge.getEdgeNo();
                String stPoint = edge.getStartPoint();
                String endPoint = edge.getEndPoint();
                String points = edge.getAllPoints();
                //[[55.07252845510704,24.986485718893903], [55.07252691395126,24.986503080465624], [55.07252858393359,24.9865204314153], [55.072533418545014,24.986537282374343], [55.072541282105426,24.9865531573588]]
                Log.e("END POINTS", "END POINTS----" + endPoint);
                String endPointInfo = endPoint.replace("[", "");
                endPointInfo = endPointInfo.replace("]", "");
                String[] endPointInfoArray = endPointInfo.split(",");
                String lat=endPointInfoArray[0];
                String longi=endPointInfoArray[1];
                Log.e("END POINTS", "Lat-----------" + lat);
                Log.e("END POINTS", "longi---------" + longi);

                double endPtLat=Double.valueOf(lat);
                double endPtLongi=Double.valueOf(longi);
                LatLng finalEndPt=new LatLng(endPtLat,endPtLongi);
                edgeDataPointsList.add(finalEndPt);
            }
            Log.e("END POINTS LIST", "END POINTS List" + edgeDataPointsList.size());

            for (int j = 0; j < LatLngDataArray.size(); j++) {
                currentGpsPosition = LatLngDataArray.get(j);
                for(int epData=0;epData<edgeDataPointsList.size()-1;epData++) {
                    String FirstCordinate = String.valueOf(edgeDataPointsList.get(epData));

                    Log.e("FirstCordinate", "FirstCordinate----" + FirstCordinate);
                    String First = FirstCordinate.replace("lat/lng: (", "");
                    First = First.replace(")", "");
                    String[] FirstLatLngsData = First.split(",");

                    Double FirstLatitude = Double.valueOf(FirstLatLngsData[0]);
                    Double FirstLongitude = Double.valueOf(FirstLatLngsData[1]);
                    Log.e("Sorted ArrayList ", "-----FirstLatitude :" + FirstLatitude);
                    Log.e("Sorted ArrayList ", "-----FirstLongitude" + FirstLongitude);


                    // String[] SecondCordinateArray = SecondCordinate.split("#");
                    //  Log.e("Sorted ArrayList ", "in Ascending order ---AT 2--- :" + SecondCordinateArray[0]);
                    String SecondCordinate = String.valueOf(edgeDataPointsList.get(epData + 1));
                    Log.e("SecondCordinate", "SecondCordinate----" + SecondCordinate);
                    String Second = SecondCordinate.replace("lat/lng: (", "");

                    Second = Second.replace(")", "");
                    String[] SecondLatLngsData = Second.split(",");
                    Double SecondLatitude = Double.valueOf(SecondLatLngsData[0]);
                    Double SecondLongitude = Double.valueOf(SecondLatLngsData[1]);


                    String nearestPoint = GenerateLinePoint(FirstLatitude, FirstLongitude, SecondLatitude, SecondLongitude, currentGpsPosition.longitude, currentGpsPosition.latitude);
                    Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
                    String[] nearestDataStr = nearestPoint.split(",");
                    double latitude = Double.parseDouble(nearestDataStr[0]);
                    double longitude = Double.parseDouble(nearestDataStr[1]);
                    nearestPositionPoint = new LatLng(longitude, latitude);
                    mPositionMarker = mMap.addMarker(new MarkerOptions()
                            .position(nearestPositionPoint)
                            .title("currentLocation")
                            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.car_icon_32)));
                }

            }
                /*
                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(sourceLat,sourceLng))
                        .zoom(15)
                        .tilt(45)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

            // verifyRouteDeviation(routeDeviationDistance);
            //  animateMarkerToFinalDestination(mPositionMarker,DestinationPosition,new LatLngInterpolator.Spherical());
            // animateMarker(mMap,mPositionMarker,LatLngDataArray,false);
            // animateMarkerViaVertex(mPositionMarker,new LatLngInterpolator.Spherical());
            // animateMarkerNew(newCenterLatLng,mPositionMarker);
            animateCarMove(mPositionMarker, edgeDataPointsList.get(0), edgeDataPointsList.get(1), 1000);

        }
    }
    */
