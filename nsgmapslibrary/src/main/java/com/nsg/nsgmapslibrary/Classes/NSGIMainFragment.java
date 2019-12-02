package com.nsg.nsgmapslibrary.Classes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.SphericalUtil;
import com.nsg.nsgmapslibrary.R;
import com.nsg.nsgmapslibrary.database.db.SqlHandler;
import com.nsg.nsgmapslibrary.database.dto.EdgeDataT;
import com.nsg.nsgmapslibrary.database.dto.GeometryT;
import com.nsg.nsgmapslibrary.database.dto.RouteT;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.STORAGE;
import static android.content.Context.SENSOR_SERVICE;
import static android.graphics.BitmapFactory.decodeFile;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class NSGIMainFragment extends Fragment implements View.OnClickListener, SensorEventListener {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int SENSOR_DELAY_NORMAL =5000;
    private ProgressDialog dialog;
    private TextToSpeech textToSpeech;
    LatLng SourcePosition, DestinationPosition;
    //LatLng convertedSrcPosition,convertedDestinationPoisition;
    double sourceLat, sourceLng, destLat, destLng;
    LatLng dubai;
    String SourcePoint="55.067291 24.978782";
    String DestinationPoint="55.067205 24.979878",tokenResponse,etaResponse;
    Marker markerSource, markerDestination,mPositionMarker;
    private Polyline mPolyline;
    private GoogleMap mMap;
    private SqlHandler sqlHandler;
    GoogleMap.CancelableCallback callback;
    private double userLocatedLat, userLocatedLongi;
    private List points;
    private List<LatLng> convertedPoints;
   // LatLng currentGpsPosition,lastKnownLocation;
    StringBuilder sb = new StringBuilder();
    private List LocationPerpedicularPoints=new ArrayList();
    private ArrayList<LatLng> currentLocationList=new ArrayList<LatLng>();
    private Marker sourceMarker,destinationMarker;
    private List<EdgeDataT> edgeDataList;
    private List<RouteT> RouteDataList;
    private Handler handler = new Handler();
    // private int index=0;
    // private int next=0;
    private int enteredMode;
    private Marker carMarker;
    private int routeDeviationDistance;
    List<LatLng> LatLngDataArray=new ArrayList<LatLng>();
    private String currentGpsPoint;
    private Polyline line;
    private List polyLines;
    private Circle mCircle;
    private List<LatLng>lastKnownPosition;
    private LatLng nearestPositionPoint;
     // BitmapDescriptor mMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.car_icon_32);
    Bitmap mMarkerIcon;
    int mIndexCurrentPoint=0;
    private List<LatLng> edgeDataPointsList ;
    Map<String, List> mapOfLists = new HashMap<String, List>();
    private List AllPointsList ;
    HashMap<String,String> AllPointEdgeNo;
    HashMap<String,String> AllPointEdgeDistaces;
    private LatLng newCenterLatLng,PointData;
    private List distancesList;
    private List distanceValuesList;
    HashMap<String, String> hash_map;
     private List<LatLng> nearestPointValuesList;
    private Marker gpsMarker;
    private TextView tv,tv1,tv2,tv3,tv4,tv5;
    private String routeIDName;
    LatLng centerFromPoint;
    LatLng point;
    private ImageButton etaListener;
    private ToggleButton fakeGpsListener;
    Marker fakeGpsMarker;
    List<Marker> markerlist;
    ArrayList<String> etaList;
    private ArrayList lastDistancesList;
    private double lastDistance;
   // private String geometryText;
    private LocationManager locationManager;
    private Location lastLocation;
    Bitmap tileBitmap;
   // MultiMap multiMap = new MultiValueMap();
    private ImageButton change_map_options;
    String tokenNumber,updaterServiceResponse;
    private long startTime,presentTime,previousTime,TimeDelay;
    private  List<LatLng>listOfLatLng;
    HashMap<LatLng,String>edgeDataPointsListData;
    private String geometryDirectionText="",key="",distanceKey="",geometryDirectionDistance="";
    HashMap<String,String>nearestValuesMap;
    private List<LatLng> OldNearestGpsList;
    private int locationFakeGpsListener=0;
    String GeometryDirectionText="";
    ImageView water_ball;
    private double vehicleSpeed;
    private double maxSpeed=30;
    private boolean isMarkerRotating=false;
    private String BASE_MAP_URL_FORMAT,DBCSV_PATH,jobId;
    private LatLng SourceNode,DestinationNode;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    float azimuthInRadians;
    float azimuthInDegress;
    float degree,lastUpdate;
    private String TotalDistance;
    private List<EdgeDataT> EdgeContainsDataList;
    StringBuilder time= new StringBuilder();
    public interface FragmentToActivity {
        String communicate(String comm);
    }
    private NSGIMainFragment.FragmentToActivity Callback;
    public NSGIMainFragment(){ }
    @SuppressLint("ValidFragment")
    public NSGIMainFragment(String BASE_MAP_URL_FORMAT, String DBCSV_PATH, String jobId, String routeId, int mode, int radius ) {
        enteredMode = mode;
        routeDeviationDistance=radius;
        NSGIMainFragment.this.BASE_MAP_URL_FORMAT = BASE_MAP_URL_FORMAT;
        NSGIMainFragment.this.DBCSV_PATH = DBCSV_PATH;
        NSGIMainFragment.this.routeIDName=routeId;
        NSGIMainFragment.this.jobId=jobId;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sqlHandler = new SqlHandler(getContext());// Sqlite handler
            Callback = (FragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentToActivity");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMarkerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.gps_transperent_98);
        tileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water_image);
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        tv = (TextView) rootView.findViewById(R.id.tv);
        tv1 = (TextView) rootView.findViewById(R.id.tv1);
        tv2 = (TextView) rootView.findViewById(R.id.tv2);
        tv3 = (TextView) rootView.findViewById(R.id.tv3);
        mSensorManager = (SensorManager)getContext().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        checkPermission();
        requestPermission();
        String delQuery = "DELETE  FROM " + RouteT.TABLE_NAME;
        InsertAllRouteData(DBCSV_PATH);
        change_map_options = (ImageButton)rootView.findViewById(R.id.change_map_options);
        change_map_options.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment1 = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googlemap) {

                mMap = googlemap;
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.stle_map_json));
                TileProvider tileProvider = new ExpandedMBTilesTileProvider(new File(BASE_MAP_URL_FORMAT.toString()), 256, 256);
                TileOverlay tileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
                tileOverlay.setTransparency(0.5f - tileOverlay.getTransparency());
                tileOverlay.setVisible(true);
                getRouteAccordingToRouteID(routeIDName);
                if(RouteDataList!=null && RouteDataList.size()>0) {
                    dialog = new ProgressDialog(getActivity(), R.style.ProgressDialog);
                    dialog.setMessage("Fetching Route");
                    dialog.setMax(100);
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            RouteT route = RouteDataList.get(0);
                            String routeData = route.getRouteData();
                            String sourceText=route.getStartNode();
                            String[]  text =sourceText.split(" ");
                            sourceLat= Double.parseDouble(text[1]);
                            sourceLng= Double.parseDouble(text[0]);
                            String destinationText=route.getEndNode();
                            String[]  text1 =destinationText.split(" ");
                            destLat= Double.parseDouble(text1[1]);
                            destLng= Double.parseDouble(text1[0]);
                            SourceNode=new LatLng(sourceLat,sourceLng);
                            DestinationNode=new LatLng(destLat,destLng);
                            if(routeData!=null) {
                                GetRouteFromDBPlotOnMap(routeData);
                               // GetRouteDetails(SourcePoint,DestinationPoint);
                            }
                            //
                            StringBuilder routeAlert=new StringBuilder();
                            routeAlert.append("src");
                            sendData(routeAlert.toString());
                            // sendTokenRequest();
                            getAllEdgesData();
                            addMarkers();
                            getValidRouteData();
                            // dialog.dismiss();
                            nearestPointValuesList=new ArrayList<LatLng>();
                            nearestPointValuesList.add(new LatLng(sourceLat,sourceLng));
                            OldNearestGpsList=new ArrayList<>();
                            OldNearestGpsList.add(new LatLng(sourceLat,sourceLng));
                            if(enteredMode==1 &&edgeDataList!=null && edgeDataList.size()>0){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    // MoveWithGPSMARKER();
                                    if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        return;
                                    }
                                    mMap.setMyLocationEnabled(true);
                                    mMap.setBuildingsEnabled(true);
                                    mMap.getUiSettings().setZoomControlsEnabled(true);
                                    mMap.getUiSettings().setCompassEnabled(true);
                                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                    mMap.getUiSettings().setMapToolbarEnabled(true);
                                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                                    mMap.getUiSettings().setScrollGesturesEnabled(true);
                                    mMap.getUiSettings().setTiltGesturesEnabled(true);
                                    mMap.getUiSettings().setRotateGesturesEnabled(true);
                                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                                @Override
                                                public void onMyLocationChange(Location location) {
                                                    if (mPositionMarker != null) {
                                                        mPositionMarker.remove();

                                                    }
                                                    vehicleSpeed=location.getSpeed();
                                                    getLatLngPoints();
                                                    // LatLng currentGpsPosition = new LatLng(24.978305, 55.066644);
                                                    // NavigationDirection(currentGpsPosition,DestinationPosition);
                                                    LatLng currentGpsPosition = LatLngDataArray.get(locationFakeGpsListener);
                                                    MoveWithGpsPointInBetWeenAllPoints(currentGpsPosition);
                                                    locationFakeGpsListener = locationFakeGpsListener + 1;
                                                }
                                            });
                                        }
                                    }, 0);
                                }
                            }else if(enteredMode==2){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        return;
                                    }
                                    mMap.setMyLocationEnabled(true);
                                    mMap.setBuildingsEnabled(true);
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
                                            LatLng currentGpsPosition=new LatLng(location.getLatitude(),location.getLongitude());
                                                /*
                                               LatLng currentGpsPosition=new LatLng(location.getLatitude(),location.getLongitude());
                                                mPositionMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(currentGpsPosition)
                                                        .title("currentLocation")
                                                        .anchor(0.5f, 0.5f)
                                                        .rotation(location.bearingTo(location))
                                                        .flat(true)
                                                        .icon(bitmapDescriptorFromVector(getContext(), R.drawable.gps_transperent)));
                                                //changing direction to NORTH as Shown in vedio by DT Team 65.5f

                                                CameraPosition currentPlace = new CameraPosition.Builder()
                                                        .target(new LatLng(currentGpsPosition.latitude, currentGpsPosition.longitude))
                                                        .bearing(location.bearingTo(location)).tilt(65.5f).zoom(20)
                                                        .build();

                                                CameraUpdate center = CameraUpdateFactory.newLatLng(currentGpsPosition);
                                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);
                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 2000, null);
                                                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                                                    @Override            public void onCameraChange(CameraPosition cameraPosition) {

                                                        //http://androidalc.blogspot.com/2016/03/move-google-map-under-marker-and-get.html

                                                    }
                                                });
                                                if (lastLocation == null) {
                                                    // save last location
                                                    lastLocation = location;
                                                    // initial camera
                                                    CameraPosition.Builder b = CameraPosition.builder().
                                                            zoom(15.0F).
                                                            target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                                                    CameraUpdate cu = CameraUpdateFactory.newCameraPosition(b.build());
                                                    mMap.animateCamera(cu);
                                                    return;
                                                }
                                                // subsequent updates

                                                LatLng oldPos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                                                LatLng newPos = new LatLng(location.getLatitude(), location.getLongitude());

                                                // ignore very small position deviations (prevents wild swinging)
                                                double d = SphericalUtil.computeDistanceBetween(oldPos, newPos);
                                                if (d < 10) {
                                                    return;
                                                }

                                                double bearing = SphericalUtil.computeHeading(oldPos, newPos);
                                                Projection p = mMap.getProjection();
                                                Point  bottomRightPoint = p.toScreenLocation(p.getVisibleRegion().nearRight);
                                                Point center = new Point(bottomRightPoint.x/2,bottomRightPoint.y/2);
                                                Point offset = new Point(center.x, (center.y + 300));

                                                LatLng centerLoc = p.fromScreenLocation(center);
                                                LatLng offsetNewLoc = p.fromScreenLocation(offset);

                                                double offsetDistance = SphericalUtil.computeDistanceBetween(centerLoc, offsetNewLoc);
                                                LatLng shadowTgt = SphericalUtil.computeOffset(newPos,offsetDistance,bearing);
                                                if (mPositionMarker != null) {
                                                 //   mMap.setCenter(newPos);
                                                } else {
                                                    mMap.addCircle(new CircleOptions().strokeColor(Color.GREEN).center(newPos).radius(50));
                                                }
                                                // update camera
                                                CameraPosition.Builder b = CameraPosition.builder();
                                                b.zoom(15.0F);
                                                b.bearing((float)(bearing));
                                                b.target(shadowTgt);
                                                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(b.build());
                                                mMap.animateCamera(cu);

                                                // save location as last for next update
                                                lastLocation = location;
                                                */
                                            Projection p = mMap.getProjection();
                                            Point  bottomRightPoint = p.toScreenLocation(p.getVisibleRegion().nearRight);
                                            Point center = new Point(bottomRightPoint.x/2,bottomRightPoint.y/2);
                                            Point offset = new Point(center.x, (center.y + 350));
                                            LatLng centerLoc = p.fromScreenLocation(center);
                                            LatLng offsetNewLoc = p.fromScreenLocation(offset);
                                            double offsetDistance = SphericalUtil.computeDistanceBetween(centerLoc, offsetNewLoc);
                                            LatLng shadowTgt = SphericalUtil.computeOffset(currentGpsPosition,offsetDistance,location.getBearing());

                                            CameraPosition currentPlace = new CameraPosition.Builder()
                                                    .target(shadowTgt)
                                                    .bearing(location.getBearing()).tilt(65.5f).zoom(20)
                                                    .build();
                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 5000, null);
                                        }
                                    });
                                }

                            }
                            dialog.dismiss();

                        }
                    }, 30);
                }else{
                    Log.e("SendData","SendData ------- "+ "internet does not exist");
                }
            }
        });
        return rootView;
    }
    @Override
    public void onDetach() {
        Callback = null;
        super.onDetach();
    }
    private void sendData(String comm)
    {
        //comm=time.toString();
        Log.e("SendData","SendData ------- "+ comm);
        Callback.communicate(comm);

    }
    private  List<RouteT> getRouteAccordingToRouteID(String routeIDName) {
        String query = "SELECT * FROM " + RouteT.TABLE_NAME +" WHERE routeID = "+"'"+routeIDName+"'";
        Log.e("Select","Select Query ------- "+ query);
        Cursor c1 = sqlHandler.selectQuery(query);
        RouteDataList = (List<RouteT>) SqlHandler.getDataRows(RouteT.MAPPING, RouteT.class, c1);
        sqlHandler.closeDataBaseConnection();
        return RouteDataList;
    }

    private  List<EdgeDataT> getAllEdgesData() {
        String query = "SELECT * FROM " + EdgeDataT.TABLE_NAME;
        Cursor c1 = sqlHandler.selectQuery(query);
        edgeDataList = (List<EdgeDataT>) SqlHandler.getDataRows(EdgeDataT.MAPPING, EdgeDataT.class, c1);
        sqlHandler.closeDataBaseConnection();
        return edgeDataList;
    }
    private String getAddress(Double latitude, Double longitude) {
        String strAdd="";

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current  address", strReturnedAddress.toString());
            } else {
                Log.w("My Current  address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current  address", "Canont get Address!");
        }
        return strAdd;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(10, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void addMarkers(){
       // LatLng position1= new LatLng(SourceNode);
        // Log.e("URL FORMAT","Uposition2 T ****************** "+ position1);
        sourceMarker = mMap.addMarker(new MarkerOptions()
                .position(SourceNode)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.source_red)));
        CameraPosition googlePlex = CameraPosition.builder()
                .target(SourceNode)
                .zoom(18)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
       // LatLng position2= new LatLng(destLat,destLng);
        destinationMarker= mMap.addMarker(new MarkerOptions()
                .position(DestinationNode)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.destination_green)));
        CameraPosition googlePlex1 = CameraPosition.builder()
                .target(DestinationNode)
                .zoom(18)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex1), 1000, null);
    }
    public void getValidRouteData(){
        if (edgeDataList != null && edgeDataList.size() > 0) {
            edgeDataPointsList = new ArrayList<LatLng>();
            AllPointsList=new ArrayList();
            AllPointEdgeNo=new HashMap<>();
            AllPointEdgeDistaces=new HashMap<>();
            EdgeContainsDataList=new ArrayList<EdgeDataT>();

            for (int i = 0; i < edgeDataList.size(); i++) {
                EdgeDataT edge = new EdgeDataT(); //creating object for EDGETABLE
                edge = edgeDataList.get(i);
                int edgeNo = edge.getEdgeNo(); //Edge Number
                String stPoint = edge.getStartPoint(); //Start Point
                String endPoint = edge.getEndPoint();//End Point
                String points = edge.getAllPoints(); // All points in the edge
                String geometryText=edge.getGeometryText();
                String distanceInEdge = edge.getDistanceInVertex();
                TotalDistance = edge.getTotaldistance();
                // Geometry Direction text
                Log.e("EdgePoints Data","EdgePoints Data Geometry FROM VALID ROUTE DATA " + geometryText+" : "+ edgeNo);
                //[[55.07252845510704,24.986485718893903], [55.07252691395126,24.986503080465624], [55.07252858393359,24.9865204314153], [55.072533418545014,24.986537282374343], [55.072541282105426,24.9865531573588]]
                if(points!=null){
                    String AllPoints = points.replace("[", "");
                    AllPoints = AllPoints.replace("]", "");
                    String[] AllPointsArray = AllPoints.split(", ");
                    Log.e("ALL POINTS", "ALL POINTS" + AllPointsArray.length);
                    for (int ap = 0; ap < AllPointsArray.length; ap++) {

                        String data = String.valueOf(AllPointsArray[ap]);
                        String dataStr = data.replace("[", "");
                        dataStr = dataStr.replace("]", "");
                        String ptData[] = dataStr.split(",");
                        double Lat = Double.parseDouble(ptData[0]);
                        double Lang = Double.parseDouble(ptData[1]);
                        PointData = new LatLng(Lat, Lang);
                        AllPointEdgeNo.put(String.valueOf(PointData),geometryText);
                        AllPointEdgeDistaces.put(String.valueOf(PointData),distanceInEdge);
                        AllPointsList.add(AllPointsArray[ap]);
                        PointData = new LatLng(Lang,Lat);
                        EdgeDataT edgePointData = new EdgeDataT(stPoint,endPoint,String.valueOf(PointData),geometryText,distanceInEdge);
                        EdgeContainsDataList.add(edgePointData);

                    }
                }


                for (int pntCount = 0; pntCount < AllPointsList.size(); pntCount++) {
                    Log.e("ALL POINTS ", "FROM DATABASE with Edge no----- " + AllPointsList.get(pntCount));
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
            }


            for(int k=0;k<EdgeContainsDataList.size();k++){
                EdgeDataT edgeK=EdgeContainsDataList.get(k);
                StringBuilder sb=new StringBuilder();
                sb.append("STPOINT :"+edgeK.getStartPoint()+"EndPt:"+edgeK.getEndPoint()+"Points:"+edgeK.getPositionMarkingPoint()+"Geometry TEXT:"+ edgeK.getGeometryText());
                Log.e("EDGE CONTAINS","EDGE CONTAINS"+sb.toString());
            }
            Log.e("EDGE CONTAINS","EDGE CONTAINS"+sb.toString());

        }

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void MoveWithGpsPointInBetWeenAllPoints(final LatLng currentGpsPosition){
        LatLng OldGps,nayaGps;
        List<LatLng> EdgeWithoutDuplicates = removeDuplicates(edgeDataPointsList);
        nearestValuesMap=new HashMap<>();
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
                Log.e("AllPointEdgeNo ", "AllPointEdgeNo " + AllPointEdgeNo.size());
                key= String.valueOf(getKeysFromValue(AllPointEdgeNo,FirstCordinate));
                distanceKey= String.valueOf(getKeysFromValue(AllPointEdgeDistaces,FirstCordinate));
                Log.e("KEY ", "KEY " + key);
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
            String First= FirstCordinate.replace("lat/lng: (","");
            First= First.replace(")","");
            String[] FirstLatLngsData=First.split(",");
            double FirstLatitude= Double.valueOf(FirstLatLngsData[0]);
            double FirstLongitude= Double.valueOf(FirstLatLngsData[1]);

            geometryDirectionText=key;
            geometryDirectionDistance=distanceKey;
             Log.e("Sorted ArrayList ", "-----geometryDirectionText :" + geometryDirectionText);
            Log.e("Sorted ArrayList ", "-----geometryDirectionText :" + geometryDirectionText);

            // Log.e("Sorted ArrayList ", "-----FirstLatitude :" + FirstLatitude);
            // Log.e("Sorted ArrayList ", "-----FirstLongitude" + FirstLongitude);
            // String[] SecondCordinateArray = SecondCordinate.split("#");
            //  Log.e("Sorted ArrayList ", "in Ascending order ---AT 2--- :" + SecondCordinateArray[0]);
            String Second= SecondCordinate.replace("lat/lng: (","");
            Second= Second.replace(")","");
            String[] SecondLatLngsData=Second.split(",");
            double SecondLatitude= Double.valueOf(SecondLatLngsData[0]);
            double SecondLongitude= Double.valueOf(SecondLatLngsData[1]);

            // Log.e("Sorted ArrayList ", "-----SecondLatitude :" + SecondLatitude);
            // Log.e("Sorted ArrayList ", "-----SecondLongitude" + SecondLongitude);
            double x= currentGpsPosition.longitude;
            double y= currentGpsPosition.longitude;
            int value = (int)x;
            int value1 = (int)y;
            LatLng source=new LatLng(FirstLongitude,FirstLatitude);
            LatLng destination=new LatLng(SecondLongitude,SecondLatitude);

             Log.e("EdgeSt Point", "End point " + DestinationPosition);
            nearestPositionPoint= findNearestPoint(currentGpsPosition,source,destination);
            OldNearestGpsList.add(nearestPositionPoint);

        }
        Log.e("EdgeSt Point", "End point" + OldNearestGpsList.size());
        for(int i=0;i<OldNearestGpsList.size();i++){
            Log.e("Old Nearest GpsList", "Old Nearest GpsList POINTS" + OldNearestGpsList.get(i));
        }

        if(OldNearestGpsList.isEmpty() && OldNearestGpsList.size()==0){
            OldGps=OldNearestGpsList.get(0);
            int indexVal=OldNearestGpsList.indexOf(nearestPositionPoint);
            nayaGps=OldNearestGpsList.get(indexVal);
        }else{
            int indexVal=OldNearestGpsList.indexOf(nearestPositionPoint);
            OldGps=OldNearestGpsList.get(indexVal-1);
            nayaGps=OldNearestGpsList.get(indexVal);
        }
        nearestValuesMap.put(String.valueOf(nearestPositionPoint),geometryDirectionText);
        nearestPointValuesList.add(nearestPositionPoint);
        if(currentGpsPosition.equals(LatLngDataArray.get(LatLngDataArray.size()-1))){
            nearestPointValuesList.add(DestinationPosition);
        }
        float bearing = (float) bearingBetweenLocations(OldGps,nayaGps); //correct method to change orientation of map
        mPositionMarker = mMap.addMarker(new MarkerOptions()
                .position(nearestPositionPoint)
                .title("currentLocation")
                .anchor(0.5f, 0.5f)
                .rotation(bearing)
                .flat(true)
                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.gps_transperent)));

        Projection p = mMap.getProjection();
        Point  bottomRightPoint = p.toScreenLocation(p.getVisibleRegion().nearRight);
        Point center = new Point(bottomRightPoint.x/2,bottomRightPoint.y/2);
        Point offset = new Point(center.x, (center.y + 350));
        LatLng centerLoc = p.fromScreenLocation(center);
        LatLng offsetNewLoc = p.fromScreenLocation(offset);
        double offsetDistance = SphericalUtil.computeDistanceBetween(centerLoc, offsetNewLoc);
        LatLng shadowTgt = SphericalUtil.computeOffset(nearestPositionPoint,offsetDistance,bearing);

        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(shadowTgt)
                .bearing(bearing).tilt(65.5f).zoom(20)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 10000, null);

        if( OldGps .equals(nearestPositionPoint)){

        }else{
            animateCarMove(mPositionMarker, OldGps, nearestPositionPoint, 10000,currentGpsPosition);
        }
        NavigationDirection(currentGpsPosition,DestinationNode);
        AlertDestination(currentGpsPosition);


/*
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override            public void onCameraChange(CameraPosition cameraPosition) {
                Log.e("Destination points","Destination points "+destLat+destLng);
                getTextImplementation(currentGpsPosition,DestinationNode);
               // verifyRouteDeviation(currentGpsPosition,10);
            }
        });
        */

    }
    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;
        double dLon = (long2 - long1);
        double y = sin(dLon) * cos(lat2);
        double x = cos(lat1) * sin(lat2) - sin(lat1)
                * cos(lat2) * cos(dLon);
        double brng = atan2(y, x);
        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return brng;
    }


    private LatLng animateLatLngZoom(LatLng latlng, int reqZoom, int offsetX, int offsetY) {
        // Save current zoom
        float originalZoom = mMap.getCameraPosition().zoom;
        // Move temporarily camera zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(reqZoom));
        Point pointInScreen = mMap.getProjection().toScreenLocation(latlng);
        Point newPoint = new Point();
        newPoint.x = pointInScreen.x - offsetX;
        newPoint.y = pointInScreen.y + offsetY;
        newCenterLatLng = mMap.getProjection().fromScreenLocation(newPoint);
        // Restore original zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(originalZoom));
        // Animate a camera with new latlng center and required zoom.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCenterLatLng, reqZoom));
        return newCenterLatLng;
    }

    public void AlertDestination(LatLng currentGpsPosition){
        int GpsIndex=OldNearestGpsList.indexOf(nearestPositionPoint);
        LatLng cameraPosition=OldNearestGpsList.get(GpsIndex);
        if (currentGpsPosition.equals(DestinationNode)) {
            Log.e("last Point","last Point--------- "+ "TRUE");
            lastDistance= showDistance(cameraPosition,DestinationNode);
            Log.e("lastDistance","lastDistance--------- "+ lastDistance );
            if (lastDistance <5) {
                if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                mMap.setMyLocationEnabled(false);
                //Speech implementation
                String data1=" Your Destination Reached ";
                int speechStatus1 = textToSpeech.speak(data1, TextToSpeech.QUEUE_FLUSH, null);
                if (speechStatus1 == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.yourDialog);
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.car_icon_32);
                builder.setMessage("Destination Reached")
                        .setCancelable(false)
                        .setPositiveButton(" Finish ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i=new Intent(getActivity(), NSGIMainFragment.class);
                                startActivity(i);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }else{
            Log.e("last Point","last Point--------- "+ "FALSE");
        }
    }
    double mphTOkmph(double mph)
    {
        return mph * 1.60934;
    }
    public Bitmap fromDrawable(final Drawable drawable) {
        final Bitmap bitmap = Bitmap.createBitmap(25, 25, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Set<Object> getKeysFromValue(Map<String, String> map, String key) {
        Set<Object> keys = new HashSet<Object>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            //if value != null
            if (entry.getKey().equals(key)){
                keys.add(entry.getValue());
            }
        }
        return keys;
    }

    private void GetRouteDetails(final String currentGpsPosition, final String DestinationPoint){

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
                                    Log.e("DEL QUERY","DEL QUERY " + delQuery);
                                    sqlHandler.executeQuery(delQuery.toString());
                                    jsonObject = new JSONObject(FeatureResponse);
                                    String ID = String.valueOf(jsonObject.get("$id"));
                                   // MESSAGE = jsonObject.getString("Message");
                                    String Status = jsonObject.getString("Status");
                                    double TotalDistance = jsonObject.getDouble("TotalDistance");
                                    JSONArray jSonRoutes = new JSONArray(jsonObject.getString("Route"));
                                    // Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes);
                                    PolylineOptions polylineOptions = new PolylineOptions();
                                    Polyline polyline = null;
                                    convertedPoints=new ArrayList<LatLng>();
                                    for (int i = 0; i < jSonRoutes.length(); i++) {
                                        points=new ArrayList();
                                        // Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes.get(i));

                                        // List Routes=new ArrayList();
                                        // Routes.add(jSonRoutes.get(i));
                                        JSONObject Routes = new JSONObject(jSonRoutes.get(i).toString());
                                        String $id = Routes.getString("$id");
                                        String EdgeNo = Routes.getString("EdgeNo");
                                        String GeometryText = Routes.getString("GeometryText");
                                        // Log.e("GeometryText", "GeometryText" + GeometryText);
                                        String Geometry = Routes.getString("Geometry");
                                        // Log.e("Geometry", "Geometry----" + Geometry);
                                        JSONObject geometryObject = new JSONObject(Routes.getString("Geometry"));
                                        String $id1 = geometryObject.getString("$id");
                                        String type = geometryObject.getString("type");
                                        // Log.e("type", "type----" + type);
                                        String coordinates = geometryObject.getString("coordinates");
                                        // Log.e("coordinates", "coordinates----" + coordinates);
                                        JSONArray jSonLegs = new JSONArray(geometryObject.getString("coordinates"));
                                        // Log.e("jSonLegs", "jSonLegs----" + jSonLegs);
                                        for (int j = 0; j < jSonLegs.length(); j++) {
                                            //   Log.e("JSON LEGS", "JSON CORDINATES" + jSonLegs.get(j));
                                            points.add(jSonLegs.get(j));
                                            //    Log.e("JSON LEGS", " LATLNG RESULT------ " + points.size());
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
                                            //    Log.e("JSON LEGS", "JSON POINTS LIST ---- " + points.get(p));
                                             String listItem = points.get(p).toString();
                                            listItem = listItem.replace("[", "");
                                            listItem = listItem.replace("]", "");
                                            //   Log.e("JSON LEGS", "JSON POINTS LIST ---- " + listItem);
                                            String[] subListItem = listItem.split(",");
                                            //  Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem.length);
                                            //  Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[0]);
                                            //  Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[1]);
                                            Double y = Double.valueOf(subListItem[0]);
                                            Double x = Double.valueOf(subListItem[1]);
                                            StringBuilder sb=new StringBuilder();
                                            //  sb.append(x).append(",").append(y).append(":");
                                            //  LocationPerpedicularPoints.add(sb.toString());
                                            LatLng latLng = new LatLng(x, y);
                                            //   Log.e("JSON LEGS", " LATLNG RESULT------ " + latLng);
                                            convertedPoints.add(latLng);
                                        }
                                        Log.e("convertedPoints", " convertedPoints------ " +  convertedPoints.size());
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        for (int k = 0; k < convertedPoints.size(); k++) {
                                            if(polylineOptions!=null && mMap!=null) {
                                                markerOptions.position(convertedPoints.get(k));
                                                markerOptions.title("Position");
                                            }
                                           // polyline.setGeodesic(true);
                                        }
                                    }
                                    polylineOptions.addAll(convertedPoints);
                                    polyline = mMap.addPolyline(polylineOptions);
                                    polylineOptions.color(Color.CYAN).width(30);
                                    mMap.addPolyline(polylineOptions);
                                    polyline.setJointType(JointType.ROUND);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void verifyRouteDeviation(final LatLng currentGpsPosition, int markDistance){
        PolylineOptions polylineOptions = new PolylineOptions();
        //To Verify Route Deviation
        //currentLocationList.add(currentGpsPosition);
        String nearestPoint = GenerateLinePoint( sourceLng,sourceLat,destLng,destLat,currentGpsPosition.longitude,currentGpsPosition.latitude);
        Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
        String[] nearestDataStr = nearestPoint.split(",");
        double latitude = Double.parseDouble(nearestDataStr[0]);
        double longitude = Double.parseDouble(nearestDataStr[1]);
        LatLng nearestPosition=new LatLng(longitude,latitude);
        double returnedDistance= showDistance(currentGpsPosition,nearestPosition);
        Log.e("returnedDistance", "returnedDistance --------- "+ returnedDistance);
       // drawMarkerWithCircle(nearestPosition,markDistance);
        if(returnedDistance > markDistance){
            Toast toast = Toast.makeText(getContext(), " ROUTE DEVIATED ", Toast.LENGTH_LONG);
            toast.setMargin(100, 100);
            toast.show();
            Log.e("Route Deviation","Route deviation"+" Route Deviated");
            //drawDeviatedRoute(currentGpsPosition, DestinationPosition);
            String cgpsLat= String.valueOf(currentGpsPosition.latitude);
            String cgpsLongi= String.valueOf(currentGpsPosition.longitude);
            currentGpsPoint=cgpsLongi.concat(" ").concat(cgpsLat);
            Log.e("returnedDistance", "nearest Position--------- "+ nearestPosition);
            Log.e("returnedDistance", "Destination Position --------- "+ DestinationPosition);
            DestinationPosition=new LatLng(destLat,destLng);
            Log.e("returnedDistance", "DestinationPosition --------- "+ DestinationPosition);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentGpsPosition);
            markerOptions.position(DestinationPosition);
            markerOptions.title("Position");
            dialog = new ProgressDialog(getActivity(), R.style.ProgressDialog);
            dialog.setMessage("Fetching new Route");
            dialog.setMax(100);
            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    dialog.dismiss();
                    Log.e("Route Deviated Point", "Route Deviated Point--------- "+ currentGpsPosition);
                    Log.e("Route Deviated Point", "Route Destination Point--------- "+ DestinationPosition);

                   // GetRouteDetails(currentGpsPosition,DestinationPosition);
                }
            }, 30);


        /*
            polylineOptions.color(Color.RED);
            polylineOptions.width(6);
            points.add(nearestPosition);
            points.add(new LatLng(24.987665, 55.060701));
            points.add(new LatLng(24.988843, 55.062091));
            points.add(new LatLng(24.989472, 55.061488));
            points.add(DestinationPosition);
            if(points.size()>0) {
                polylineOptions.addAll(points);
                line = mMap.addPolyline(polylineOptions);
                if (polylineOptions != null) {
                    if (line != null) {
                        line.remove();
                    }
                    line = mMap.addPolyline(polylineOptions);
                } else
                    Toast.makeText(getContext(), "No route is found", Toast.LENGTH_LONG).show();
            }
            */

        }else{

        }
    }
    private void drawMarkerWithCircle(LatLng gpsPosition,double radius){
        // double radiusInMeters = 400.0;
        CircleOptions circleOptions = new CircleOptions().center(gpsPosition).radius(radius).fillColor(Color.parseColor("#2271cce7")).strokeColor(Color.parseColor("#2271cce7")).strokeWidth(3);
        mCircle = mMap.addCircle(circleOptions);

    }
    private double showDistance(LatLng latlng1,LatLng latLng2) {
        double distance = SphericalUtil.computeDistanceBetween(latlng1,latLng2);
        return distance;
    }
    public int getLatLngPoints(){
        LatLngDataArray.add(new LatLng(24.978782,55.067291));
        LatLngDataArray.add(new LatLng(24.978792,55.067279));
        LatLngDataArray.add(new LatLng(24.978762,55.067241));
        LatLngDataArray.add(new LatLng(24.978765,55.067237));
        LatLngDataArray.add(new LatLng(24.978755,55.067218));
        LatLngDataArray.add(new LatLng(24.978449,55.067310));
        LatLngDataArray.add(new LatLng(24.978656,55.066997));
        LatLngDataArray.add(new LatLng(24.978408,55.066897));
        LatLngDataArray.add(new LatLng(24.978025,55.066462));
        LatLngDataArray.add(new LatLng(24.977993,55.066226));
        LatLngDataArray.add(new LatLng(24.97761,55.065815));
        LatLngDataArray.add(new LatLng(24.977358,55.065692));
        LatLngDataArray.add(new LatLng(24.977132,55.065436));

        LatLngDataArray.add(new LatLng(24.977126,55.065249));
        LatLngDataArray.add(new LatLng(24.977164,55.065171));
        LatLngDataArray.add(new LatLng(24.977257,55.064874));
        LatLngDataArray.add(new LatLng(24.977631,55.06466));
        LatLngDataArray.add(new LatLng(24.977819,55.064294));//ok  //Route Deviation point


/*
        LatLngDataArray.add(new LatLng(24.978292,55.064001));//ok
        LatLngDataArray.add(new LatLng(24.97839,55.063665));//ok
        LatLngDataArray.add(new LatLng(24.978536,55.063522));//ok
        LatLngDataArray.add(new LatLng(24.978702,55.063579));//ok
        LatLngDataArray.add(new LatLng(24.978885,55.063587));//ok
        LatLngDataArray.add(new LatLng(24.979201,55.063928));//ok
        LatLngDataArray.add(new LatLng(24.979542,55.064338));//ok
        LatLngDataArray.add(new LatLng(24.979851,55.064687));//ok
        LatLngDataArray.add(new LatLng(24.980139,55.065028));//ok
        LatLngDataArray.add(new LatLng(24.980285,55.065195));//ok
        LatLngDataArray.add(new LatLng(24.980427,55.065333));//ok
        LatLngDataArray.add(new LatLng(24.980586,55.065491));//ok
        LatLngDataArray.add(new LatLng(24.980833,55.0658));//ok
        LatLngDataArray.add(new LatLng(24.981081,55.066064));//ok
        LatLngDataArray.add(new LatLng(24.980886,55.066323));//ok
        LatLngDataArray.add(new LatLng(24.980614,55.066624));//ok
        LatLngDataArray.add(new LatLng(24.980146,55.066946)); //ok
        LatLngDataArray.add(new LatLng(24.980072,55.067073)); //ok
        LatLngDataArray.add(new LatLng(24.979965,55.067191));// ok
        LatLngDataArray.add(new LatLng(24.979878,55.067205));// ok
        */




        LatLngDataArray.add(new LatLng(24.978002, 55.064153));
        LatLngDataArray.add(new LatLng(24.978070, 55.064231));
        LatLngDataArray.add(new LatLng(24.978175, 55.064343));
        LatLngDataArray.add(new LatLng(24.978317, 55.064500));
        LatLngDataArray.add(new LatLng(24.978417, 55.064630));
        LatLngDataArray.add(new LatLng(24.978536, 55.064755));
        LatLngDataArray.add(new LatLng(24.978645, 55.064879));
        LatLngDataArray.add(new LatLng(24.978688, 55.064914));
        LatLngDataArray.add(new LatLng(24.978787, 55.065017));
        LatLngDataArray.add(new LatLng(24.978896, 55.065129));
        LatLngDataArray.add(new LatLng(24.978989, 55.065254));
        LatLngDataArray.add(new LatLng(24.979085, 55.065356));
        LatLngDataArray.add(new LatLng(24.979809, 55.066199));
        LatLngDataArray.add(new LatLng(24.980273, 55.066664));
        LatLngDataArray.add(new LatLng(24.980335, 55.066770));
        LatLngDataArray.add(new LatLng(24.980174, 55.066937));
        LatLngDataArray.add(new LatLng(24.979878,55.067205));  //Destinationm point


        return LatLngDataArray.size();

    }
    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return atan2(sin(dl) * cos(f2) , cos(f1) * sin(f2) - sin(f1) * cos(f2) * cos(dl));
    }
    private static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean isNecessaryToKeepOrig) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        if (!isNecessaryToKeepOrig) {
            bm.recycle();
        }
        return resizedBitmap;
    }
    public Bitmap addPaddingLeftForBitmap(Bitmap bitmap, int paddingLeft) {

        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth() + paddingLeft, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, paddingLeft, 0, null);
        return outputBitmap;
    }

    public Bitmap addPaddingRightForBitmap(Bitmap bitmap, int paddingRight) {

        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth() + paddingRight, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawColor(Color.RED);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }
    public Bitmap setBounds(Bitmap bitmap,int paddingRight,int paddingLeft){
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth() + paddingLeft - paddingRight, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        // canvas.drawColor(Color.RED);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap ;
    }

    private void animateCarMove(final Marker marker, final LatLng beginLatLng, final LatLng endLatLng, final long duration,final LatLng currentGpsPosition) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();
        // set car bearing for current part of path
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);
        // Bitmap opBitMap= addPaddingLeftForBitmap(mMarkerIcon,60);
        Bitmap opBitMap= setBounds(mMarkerIcon,15,15);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0,mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));
        //marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, centerX,centerY, matrix, true)));
        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
                Location location= new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(endLatLng.latitude);
                location.setLongitude(endLatLng.longitude);
                float bearingMap= location.getBearing();
              //  float bearingMap= mMap.getCameraPosition().bearing;
                float bearing = (float) bearingBetweenLocations(beginLatLng,endLatLng);
                float angle = -azimuthInDegress+bearing;
                float rotation = -azimuthInDegress * 360 / (2 * 3.14159f) ;
                double lng = lngDelta * t + beginLatLng.longitude;
                marker.setPosition(new LatLng(lat, lng));
                        marker.setAnchor(0.5f, 0.5f);
                        marker.setFlat(true);
                        marker.setRotation(rotation);
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                      float beginAngle = (float)(90 * getAngle(beginLatLng, endLatLng) / Math.PI);
                      float endAngle = (float)(90 * getAngle(currentGpsPosition, endLatLng) / Math.PI);
                      computeRotation(10,beginAngle,endAngle);
                }
            }
        });
    }


    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = sin(dLat/2) * sin(dLat/2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                        sin(dLng/2) * sin(dLng/2);
        double c = 2 * atan2(Math.sqrt(a), Math.sqrt(1-a));
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


    public void addFakeGPSMarkers(){
        getLatLngPoints();
        for(int p=0;p<LatLngDataArray.size();p++){
            fakeGpsMarker =mMap.addMarker(new MarkerOptions()
                    .position(LatLngDataArray.get(p))
                    .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.symbol_shackel_point)));
            markerlist= new ArrayList<Marker>();
            markerlist.add(fakeGpsMarker);
        }
        Log.e("MarkerList :", " MarkerList ----- " + markerlist.size());
    }
    public void removeFakeGPSMarkers(){
        getLatLngPoints();
        for(int p=0;p<LatLngDataArray.size();p++) {
            if (markerlist != null && !markerlist.isEmpty()) {
                //  markerlist.get(p).remove(); // Add this line
                markerlist.remove(p);
                if(  fakeGpsMarker.getPosition().equals(LatLngDataArray.get(p))){
                    fakeGpsMarker.remove();
                }
            }
        }
        Log.e("MarkerList :", " MarkerList ----- " + markerlist.size());
    }

    @Override
    public void onClick(View v) {
        if(v==fakeGpsListener){
            String fakeGpsText=fakeGpsListener.getText().toString();
            if(fakeGpsText.equals("Off")){
                fakeGpsListener.setBackgroundColor(Color.RED);
                Log.e("Fake Gps Text :", " Fake Gps Text ----- " + fakeGpsText);
                if(fakeGpsMarker!=null) {
                    removeFakeGPSMarkers();
                }
            }else if(fakeGpsText.equals("On")){
                fakeGpsListener.setBackgroundColor(Color.GREEN);
                Log.e("Fake Gps Text :", " Fake Gps Text ------" + fakeGpsText);
                addFakeGPSMarkers();
            }
        } else if(v==etaListener){

        }else if(v==change_map_options){
            PopupMenu popup = new PopupMenu(getContext(), change_map_options);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.popup_menu, popup.getMenu());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.slot1) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            Toast.makeText(getContext(), "NORMAL MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } else if (itemId == R.id.slot2) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            Toast.makeText(getContext(), "SATELLITE MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } else if (itemId == R.id.slot3) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            Toast.makeText(getContext(), "TERRAIN MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }else if (itemId == R.id.slot4) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            Toast.makeText(getContext(), "HYBRID MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                    return true;
                }
            });
            popup.show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
    public void sendTokenRequest(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String url1 = "http://86.96.196.245/ROROAPI/Login/GetToken";
                    Log.e("SAVING DATA ", "SAVING DATA " + url1);
                    tokenResponse = HttpPost1(url1);
                    Log.e("SAVING DATA ", "RESPONSE DATA " + tokenResponse);
                    JSONObject obj = new JSONObject(tokenResponse);
                    tokenNumber = obj.getString("tokenNumber");

                    if(tokenNumber!=null && !tokenNumber.isEmpty()){
                        String url = "http://86.96.196.245/ROROAPI/NSGMap/AlertDataProcess";
                        Log.e("SAVING DATA ", "SAVING DATA " + url);
                        updaterServiceResponse = HttpPost(url);
                        Log.e("SAVING DATA ", "SAVING DATA " + updaterServiceResponse);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 10);//just mention the time when you want to launch your action

    }
    private String HttpPost(String myUrl) throws IOException, JSONException {
        StringBuilder sbResponse=new StringBuilder();
        String result = "";
        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String basicAuth = "Bearer "+tokenNumber;
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization",basicAuth);
        //; charset=utf-8
        // 2. build JSON object
        JSONObject jsonObject = buidJsonObject();
        Log.e("jsonObject", "jsonObject: " + jsonObject);
        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);
        conn.connect();
        result = conn.getResponseMessage();
        Log.e("Response Message", "Response Message: " + conn.getResponseMessage());
        // 5. return response message
        Log.e("Response Code", "ResponseCode: " + conn.getResponseCode());
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output=null;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            sbResponse.append(output).append(" ");
        }
        return sbResponse.toString();
    }
    private JSONObject buidJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("DriverID", "15022");
        jsonObject.accumulate("AlertType", "ETA");
        jsonObject.accumulate("AlertCode",  "ETACroess");
        jsonObject.accumulate("AlertName", "ETA Croessed");
        jsonObject.accumulate("AlertValue", "true");
        jsonObject.accumulate("OptionalString1",  "mobile---Application Testing From NSGI");
        jsonObject.accumulate("OptionalString1", "24.978782,55.067291");
        jsonObject.accumulate("OptionalInt1",  "24.979745, 55.067548");
        jsonObject.accumulate("OptionalInt2", "");
        jsonObject.accumulate("UserID", "nsgadmin");
        jsonObject.accumulate("ApplicationID",  "10");
        jsonObject.accumulate("CompanyUno", "2");
        jsonObject.accumulate("LanguageUno",  "1033");
        jsonObject.accumulate("Condition",  1);
        return jsonObject;
    }
    private String HttpPost1(String myUrl) throws IOException, JSONException {
        StringBuilder sbResponse=new StringBuilder();
        String result = "";
        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        //  conn.setRequestProperty("Accept", "application/json");
        //; charset=utf-8
        // 2. build JSON object
        JSONObject jsonObject = buidJsonObjectTokenService();
        //Log.e("jsonObject", "jsonObject: " + jsonObject);
        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);
        // 4. make POST request to the given URL
        conn.connect();
        result = conn.getResponseMessage();
        Log.e("Response Message", "Response Message: " + result);
        // 5. return response message
        // Log.e("RequestMethod Code", "RequestMethod: " + conn.getRequestMethod());
        // Log.e("ContentEncoding Code", "ContentEncoding: " + conn.getContentEncoding());
        Log.e("Response Code", "ResponseCode: " + conn.getResponseCode());
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output=null;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            sbResponse.append(output).append(" ");
        }
        return sbResponse.toString();
    }
    private JSONObject buidJsonObjectTokenService() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("Username","nsgadmin");
        jsonObject.accumulate("Password","nsgadmin");
        return jsonObject;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void InsertAllRouteData(String DBCSV_PATH){
        File file = new File(DBCSV_PATH);
        Log.e(" Route CSV File From ", " generateCsvFile Method ---" + file);
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        File outputFile = new File(file, "RouteSample.csv");
        if(outputFile.exists()) {
            try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
                CsvRow row;
                while ((row = csvParser.nextRow()) != null) {
                    System.out.println("Read line: " + row);
                    String ID = row.getField("ID");
                    String RouteID = row.getField("RouteID");
                    String startNode = row.getField("StartPoint");
                    String endNode = row.getField("EndPoint");
                    String routeData = row.getField("Route");

                    StringBuilder query = new StringBuilder("INSERT INTO ");
                    query.append(RouteT.TABLE_NAME).append("(routeID,startNode,endNode,routeData) values (")
                            .append("'").append(RouteID).append("',")
                            .append("'").append(startNode).append("',")
                            .append("'").append(endNode).append("',")
                            .append("'").append(routeData).append("')");
                    Log.e("INSERT QUERY", "INSERT QUERY ------ " + query);
                    sqlHandler.executeQuery(query.toString());
                    sqlHandler.closeDataBaseConnection();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.e("INSERT QUERY", "INSERT QUERY ------ " + "FILE DOESNOT EXISTS");
        }

    }
    public void GetRouteFromDBPlotOnMap(String FeatureResponse){
        JSONObject jsonObject = null;
        try {
            if(FeatureResponse!=null){
              //  String delQuery = "DELETE  FROM " + EdgeDataT.TABLE_NAME;
             //   Log.e("DEL QUERY","DEL QUERY " + delQuery);
             //   sqlHandler.executeQuery(delQuery.toString());
                jsonObject = new JSONObject(FeatureResponse);
                String ID = String.valueOf(jsonObject.get("$id"));
               // MESSAGE = jsonObject.getString("Message");
                String Status = jsonObject.getString("Status");
                double TotalDistance = jsonObject.getDouble("TotalDistance");
                double TotalDistanceInMTS= TotalDistance*1000;
                JSONArray jSonRoutes = new JSONArray(jsonObject.getString("Route"));
                // Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes);
                PolylineOptions polylineOptions = new PolylineOptions();
                Polyline polyline = null;
                convertedPoints=new ArrayList<LatLng>();
                for (int i = 0; i < jSonRoutes.length(); i++) {
                    points=new ArrayList();
                    // Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes.get(i));
                    // List Routes=new ArrayList();
                    // Routes.add(jSonRoutes.get(i));
                    JSONObject Routes = new JSONObject(jSonRoutes.get(i).toString());
                    String $id = Routes.getString("$id");
                    String EdgeNo = Routes.getString("EdgeNo");
                    String GeometryText = Routes.getString("GeometryText");
                    // Log.e("GeometryText", "GeometryText" + GeometryText);
                    String Geometry = Routes.getString("Geometry");
                    // Log.e("Geometry", "Geometry----" + Geometry);
                    JSONObject geometryObject = new JSONObject(Routes.getString("Geometry"));
                    String $id1 = geometryObject.getString("$id");
                    String type = geometryObject.getString("type");
                    // Log.e("type", "type----" + type);
                    String coordinates = geometryObject.getString("coordinates");
                    // Log.e("coordinates", "coordinates----" + coordinates);
                    JSONArray jSonLegs = new JSONArray(geometryObject.getString("coordinates"));
                    // Log.e("jSonLegs", "jSonLegs----" + jSonLegs);
                    for (int j = 0; j < jSonLegs.length(); j++) {
                        //   Log.e("JSON LEGS", "JSON CORDINATES" + jSonLegs.get(j));
                        points.add(jSonLegs.get(j));
                        //    Log.e("JSON LEGS", " LATLNG RESULT------ " + points.size());
                    }
                    // Log.e("JSON LEGS", " LATLNG RESULT------ " + points.size());
                    String  stPoint=String.valueOf(jSonLegs.get(0));
                    String  endPoint=String.valueOf(jSonLegs.get(jSonLegs.length()-1));

                    stPoint=stPoint.replace("[","");
                    stPoint=stPoint.replace("]","");
                    String [] firstPoint=stPoint.split(",");
                    Double stPointLat= Double.valueOf(firstPoint[0]);
                    Double stPointLongi= Double.valueOf(firstPoint[1]);
                    LatLng stVertex=new LatLng(stPointLongi,stPointLat);

                        endPoint=endPoint.replace("[","");
                        endPoint=endPoint.replace("]","");
                        String [] secondPoint=endPoint.split(",");
                        Double endPointLat= Double.valueOf(secondPoint[0]);
                        Double endPointLongi= Double.valueOf(secondPoint[1]);
                        LatLng endVertex=new LatLng(endPointLongi,endPointLat);

                        double distance=showDistance(stVertex,endVertex);
                        String distanceInKM = String.valueOf(distance/1000);
                            Log.e("Distance -----","Distance in KM-------- "+ distanceInKM);
                    StringBuilder query = new StringBuilder("INSERT INTO ");
                    query.append(EdgeDataT.TABLE_NAME).append("(edgeNo,distanceInVertex,totaldistance,startPoint,allPoints,geometryText,endPoint) values (")
                            .append("'").append(EdgeNo).append("',")
                            .append("'").append(distanceInKM).append("',")
                            .append("'").append(String.valueOf(TotalDistanceInMTS)).append("',")
                            .append("'").append(jSonLegs.get(0)).append("',")
                            .append("'").append(points).append("',")
                            .append("'").append(GeometryText).append("',")
                            .append("'").append(jSonLegs.get(jSonLegs.length()-1)).append("')");
                    sqlHandler.executeQuery(query.toString());
                    sqlHandler.closeDataBaseConnection();
                    for (int p = 0; p < points.size(); p++) {
                        //    Log.e("JSON LEGS", "JSON POINTS LIST ---- " + points.get(p));
                        String listItem = points.get(p).toString();
                        listItem = listItem.replace("[", "");
                        listItem = listItem.replace("]", "");
                        //   Log.e("JSON LEGS", "JSON POINTS LIST ---- " + listItem);
                        String[] subListItem = listItem.split(",");
                        //  Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem.length);
                        //  Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[0]);
                        //  Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[1]);
                        Double y = Double.valueOf(subListItem[0]);
                        Double x = Double.valueOf(subListItem[1]);
                        StringBuilder sb=new StringBuilder();
                        //  sb.append(x).append(",").append(y).append(":");
                        //  LocationPerpedicularPoints.add(sb.toString());
                        LatLng latLng = new LatLng(x, y);
                        //   Log.e("JSON LEGS", " LATLNG RESULT------ " + latLng);
                        convertedPoints.add(latLng);
                    }
                    Log.e("convertedPoints", " convertedPoints------ " +  convertedPoints.size());
                    MarkerOptions markerOptions = new MarkerOptions();
                    for (int k = 0; k < convertedPoints.size(); k++) {
                        if(polylineOptions!=null && mMap!=null) {
                            markerOptions.position(convertedPoints.get(k));
                            markerOptions.title("Position");
                        }
                        // polyline.setGeodesic(true);
                    }
                }
                polylineOptions.addAll(convertedPoints);
                polyline = mMap.addPolyline(polylineOptions);
                polylineOptions.color(Color.CYAN).width(30);
                mMap.addPolyline(polylineOptions);
                polyline.setJointType(JointType.ROUND);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getContext(), STORAGE);
        return true;//result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION, STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
                case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted) {
                        // Toast.makeText(this, "Permission Granted,.", Toast.LENGTH_LONG).show();
                    }else {
                        // Toast.makeText(this, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Look at this dialog!")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                                return;
                            }
                        }

                    }
                }
                break;
        }
    }
    @Override
    public void onSensorChanged(final SensorEvent event) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                degree = Math.round(event.values[0]);

            }
        }, 10000);



    }

    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

    }

    public static Bitmap RotateBitmap(Bitmap source, float azimuth) {
        Bitmap outputBitmap;
        Matrix matrix=new Matrix();
        matrix.postRotate(azimuth);
        outputBitmap=Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return outputBitmap;
    }
    public void RotateBitmap(Marker marker,float mCurrentDegree,float azimuthInDegress){
        Matrix matrix = new Matrix();
        matrix.postRotate(azimuthInDegress);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0,mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void onResume() {
        super.onResume();
        if (mSensorManager != null)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
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
    public String NavigationDirection(final LatLng currentGpsPosition, LatLng DestinationPosition) {
        final String shortestDistancePoint = "";
       // Handler handler = new Handler();
       // handler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
                ArrayList<Double> EdgeDistancesList=new ArrayList<Double>();
                HashMap EdgeDistancesMap=new HashMap<String,String>();
                String stPoint = "", endPoint = "", geometryTextimpValue = "", distanceInEdge = "";
                String position="";
                for(int k=0;k<EdgeContainsDataList.size();k++){
                    EdgeDataT edgeK=EdgeContainsDataList.get(k);
                    StringBuilder sb=new StringBuilder();
                    sb.append("STPOINT :"+edgeK.getStartPoint()+"EndPt:"+edgeK.getEndPoint()+"Points:"+edgeK.getPositionMarkingPoint()+"Geometry TEXT:"+ edgeK.getGeometryText());
                    Log.e("EDGE CONTAINS","EDGE CONTAINS"+sb.toString());
                    String pointDataText=edgeK.getPositionMarkingPoint();
                    String stPoint_data=pointDataText.replace("lat/lng: (","");
                    String stPoint_data1=stPoint_data.replace(")","");
                    String[] st_point=stPoint_data1.split(",");
                    double st_point_lat= Double.parseDouble(st_point[1]);
                    double st_point_lnag= Double.parseDouble(st_point[0]);
                    LatLng st_Point_vertex_main=new LatLng(st_point_lnag,st_point_lat);
                    double distanceOfCurrentPosition=showDistance(st_Point_vertex_main,currentGpsPosition);
                    EdgeDistancesList.add(distanceOfCurrentPosition);
                    EdgeDistancesMap.put(String.valueOf(distanceOfCurrentPosition).trim(),String.valueOf(pointDataText));
                  //  Collections.addAll(EdgeDistancesList);
                   // Collections.sort(EdgeDistancesList);
                    Collections.sort(EdgeDistancesList);
                }

                     GetSortetPoint(EdgeDistancesMap,EdgeDistancesList, currentGpsPosition );

        //    }
       // }, 10000);
        return shortestDistancePoint;

    }
    public String  GetSortetPoint(HashMap EdgeDistancesMap, ArrayList<Double>  EdgeDistancesList, LatLng currentGpsPosition ){
        String vfinalValue = "";
        String FirstShortestDistance = String.valueOf(EdgeDistancesList.get(0));
        boolean verify=EdgeDistancesMap.containsKey(FirstShortestDistance.trim());
        if(verify){
            Log.e("shortestDistancePoint ", "shortestDistancePoint----- : " + "TRUE");
            Object Value =String.valueOf( EdgeDistancesMap.get(FirstShortestDistance));
            vfinalValue= String.valueOf(EdgeDistancesMap.get(FirstShortestDistance));
            Log.e("shortestDistancePoint ", "shortestDistancePoint  VALUE----- : " + vfinalValue);


        } else{
            Log.e("shortestDistancePoint ", "shortestDistancePoint----- : " + "FALSE");
            System.out.println("Key not matched with ID");
        }
        EdgesEndContaingData(currentGpsPosition,vfinalValue);

        return vfinalValue;
    }
    public void EdgesEndContaingData(LatLng currentGpsPosition,String shortestDistancePoint){
        String stPoint = "", endPoint = "", geometryTextimpValue = "", distanceInEdge = "";
        String position="";
        int indexPosition=0;
        EdgeDataT edgeCurrentPoint= null;
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<EdgeContainsDataList.size();i++){
             edgeCurrentPoint=EdgeContainsDataList.get(i);
            Log.e("Psoition","Position"+position);
             position=edgeCurrentPoint.getPositionMarkingPoint();
            Log.e("Psoition","Position"+position);
            if(position.equals(shortestDistancePoint)){
                distanceInEdge=EdgeContainsDataList.get(i).getGeometryText();
                stPoint=  EdgeContainsDataList.get(i).getStartPoint();
                endPoint= EdgeContainsDataList.get(i).getEndPoint();
                geometryTextimpValue=EdgeContainsDataList.get(i).getGeometryText();
                Log.e("VertexCordinate ", "INDEX POSITION ----- : " + indexPosition);
            }else{
                // Log.e("VertexCordinate ", "StrtPoint----- : " + "NOT MATCHED");
            }
        }

        String stPoint_data=stPoint.replace("[","");
        String stPoint_data1=stPoint_data.replace("]","");
        String[] st_point=stPoint_data1.split(",");
        double st_point_lat= Double.parseDouble(st_point[1]);
        double st_point_lnag= Double.parseDouble(st_point[0]);
        LatLng st_Point_vertex=new LatLng(st_point_lat,st_point_lnag);

        String endPoint_data=endPoint.replace("[","");
        String endPoint_data1=endPoint_data.replace("]","");
        String[] end_point=endPoint_data1.split(",");
        double end_point_lat= Double.parseDouble(end_point[1]);
        double end_point_lnag= Double.parseDouble(end_point[0]);
        LatLng end_Point_vertex=new LatLng(end_point_lat,end_point_lnag);

        Log.e("GEOMETRY TEXT ", "GEOMETRY TEXT  @@@@@@@@@@@@@@@@@@@: " + st_Point_vertex);
        Log.e("GEOMETRY TEXT ", "GEOMETRY TEXT  @@@@@@@@@@@@@@@@@@@: " + end_Point_vertex);
        Log.e("GEOMETRY TEXT ", "GEOMETRY TEXT  @@@@@@@@@@@@@@@@@@@: " + geometryTextimpValue);
        double Distance_To_travelIn_Vertex=showDistance(currentGpsPosition,end_Point_vertex);
        String Distance_To_travelIn_Vertex_Convetred=String.format("%.0f", Distance_To_travelIn_Vertex);

        Log.e("VertexCordinate ", " Distance in EDGE NEED TO  TRAVEL DISTANCE " + Distance_To_travelIn_Vertex_Convetred);
        String data= geometryTextimpValue +" " + Distance_To_travelIn_Vertex_Convetred +"Meters";
        //String data=" in "+ DitrectionDistance +" Meters "+ directionTextFinal;
        int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
        Toast.makeText(getActivity(), ""+ geometryTextimpValue+" "+ Distance_To_travelIn_Vertex_Convetred +"Meters", Toast.LENGTH_SHORT).show();
        LayoutInflater inflater1 = getActivity().getLayoutInflater();
        @SuppressLint("WrongViewCast") View layout = inflater1.inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.textView_toast));
        TextView text = (TextView) layout.findViewById(R.id.textView_toast);

        text.setText(""+ geometryTextimpValue+" "+ Distance_To_travelIn_Vertex_Convetred +"Meters");
        ImageView image = (ImageView) layout.findViewById(R.id.image_toast);
        if(geometryTextimpValue.contains("Take Right")){
            image.setImageResource(R.drawable.direction_right);
        }else if(geometryTextimpValue.contains("Take Left")){
            image.setImageResource(R.drawable.direction_left);
        }

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setGravity(Gravity.TOP,0,150);
        toast.setView(layout);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, 1500);  // 1500 seconds

    }

    public void getTextImplementation(final LatLng currentGpsPosition, LatLng DestinationPosition){
        String stPoint="",endPoint="",geometryTextimpValue="",distanceInEdge="";
        final String shortestDistancePoint="";
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                List  EdgeDistancesList=new ArrayList<>();
                HashMap EdgeDistancesMap=new HashMap<String,String>();

                    AlertDestination(currentGpsPosition);
                    for (int i = 0; i < edgeDataList.size(); i++) {
                        EdgeDataT edge = new EdgeDataT(); //creating object for EDGETABLE
                        edge = edgeDataList.get(i);
                        int edgeNo = edge.getEdgeNo(); //Edge Number
                        String points = edge.getAllPoints(); // All points in the edge
                        if(points!=null){
                            String AllPoints = points.replace("[", "");
                            AllPoints = AllPoints.replace("]", "");
                            String[] AllPointsArray = AllPoints.split(", ");
                            Log.e("ALL POINTS", "ALL POINTS" + AllPointsArray.length);
                            for (int ap = 0; ap < AllPointsArray.length; ap++) {

                                String data = String.valueOf(AllPointsArray[ap]);
                                String dataStr = data.replace("[", "");
                                dataStr = dataStr.replace("]", "");
                                String ptData[] = dataStr.split(",");
                                double Lat = Double.parseDouble(ptData[0]);
                                double Lang = Double.parseDouble(ptData[1]);
                                PointData = new LatLng(Lang,Lat);
                                double distanceOfCurrentPosition=showDistance(PointData,currentGpsPosition);
                                EdgeDistancesList.add(distanceOfCurrentPosition);
                                EdgeDistancesMap.put(String.valueOf(distanceOfCurrentPosition).trim(),String.valueOf(PointData));
                                Collections.addAll(EdgeDistancesList);
                            }
                            String FirstShortestDistance = String.valueOf(EdgeDistancesList.get(0));
                            boolean verify=EdgeDistancesMap.containsKey(FirstShortestDistance.trim());
                            if(verify){
                               Log.e("shortestDistancePoint ", "shortestDistancePoint----- : " + "TRUE");
                               Object Value = EdgeDistancesMap.get(FirstShortestDistance);
                               key= String.valueOf(Value);
                               Log.e("shortestDistancePoint ", "shortestDistancePoint----- : " + key);
                            } else{
                                Log.e("shortestDistancePoint ", "shortestDistancePoint----- : " + "FALSE");
                                System.out.println("Key not matched with ID");
                            }
                        }


                    }

            }
        }, 10000);

        int GpsIndex=OldNearestGpsList.indexOf(nearestPositionPoint);
        LatLng cameraPosition=OldNearestGpsList.get(GpsIndex);
        LatLng OldcameraPosition=OldNearestGpsList.get(GpsIndex-1);
        double DistanceInVertex=showDistance(OldcameraPosition,cameraPosition);
        String DitrectionDistance=String.format("%.0f", DistanceInVertex);

        for(int i=0;i<EdgeContainsDataList.size();i++){
            EdgeDataT edgeCurrentPoint=EdgeContainsDataList.get(i);
            String position=edgeCurrentPoint.getPositionMarkingPoint();
            //  Log.e("VertexCordinate ", "StrtPoint----- : " + position);
            if(position.equals(shortestDistancePoint)){
                distanceInEdge= edgeCurrentPoint.getDistanceInVertex();
                stPoint=  edgeCurrentPoint.getStartPoint();
                endPoint= edgeCurrentPoint.getEndPoint();
                geometryTextimpValue=edgeCurrentPoint.getGeometryText();

            }else{
                // Log.e("VertexCordinate ", "StrtPoint----- : " + "NOT MATCHED");
            }

        }

        String stPoint_data=stPoint.replace("[","");
        String stPoint_data1=stPoint_data.replace("]","");
        String[] st_point=stPoint_data1.split(",");
        double st_point_lat= Double.parseDouble(st_point[1]);
        double st_point_lnag= Double.parseDouble(st_point[0]);
        LatLng st_Point_vertex=new LatLng(st_point_lat,st_point_lnag);



        String endPoint_data=endPoint.replace("[","");
        String endPoint_data1=endPoint_data.replace("]","");
        String[] end_point=endPoint_data1.split(",");
        double end_point_lat= Double.parseDouble(end_point[1]);
        double end_point_lnag= Double.parseDouble(end_point[0]);
        LatLng end_Point_vertex=new LatLng(end_point_lat,end_point_lnag);
        Log.e("GEOMETRY TEXT ", "GEOMETRY TEXT  @@@@@@@@@@@@@@@@@@@: " + geometryTextimpValue);

        Log.e(" EDGE CONVERSION ", "Strat Point from API" + st_Point_vertex);
        Log.e(" EDGE CONVERSION ", "End Point from API" + end_Point_vertex);
        double resultTotalDistance=showDistance(new LatLng(sourceLat,sourceLng),new LatLng(destLat,destLng));


        Log.e("Total Distance ", "CurrentGPS POSITION in EDGE from API" + currentGpsPosition);
        Log.e("Total Distance ", "Distance in EDGE from API" + resultTotalDistance);



        double Distance_To_travelIn_Vertex=showDistance(currentGpsPosition,end_Point_vertex);
        String Distance_To_travelIn_Vertex_Convetred=String.format("%.0f", Distance_To_travelIn_Vertex);
        Log.e("VertexCordinate ", " Distance in EDGE NEED TO  TRAVEL DISTANCE " + Distance_To_travelIn_Vertex_Convetred);

        String data= geometryTextimpValue +" " + Distance_To_travelIn_Vertex_Convetred +"Meters";
        //String data=" in "+ DitrectionDistance +" Meters "+ directionTextFinal;
        int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }



      //  double  DitrectionDistance_InEDGE= showDistance(st_Point_vertex,end_Point_vertex);
      //  String directionDistanceFinal_inEdge=String.format("%.0f", DitrectionDistance_InEDGE);
      //  Log.e(" EDGE CONVERSION ", "Distance in EDGE TOTAL DISTANCE " + directionDistanceFinal_inEdge);

      //  double  TravelledDitrectionDistance_InEDGE= showDistance(st_Point_vertex,currentGpsPosition);
      //  String TravelledDitrectionDistance_inEdge_Convetred=String.format("%.0f", TravelledDitrectionDistance_InEDGE);
      //  Log.e("VertexCordinate ", "Distance in EDGE TRAVELLED DISTANCE  " + TravelledDitrectionDistance_inEdge_Convetred);






        //String geometryTextimp= String.valueOf(getKeysFromValue(nearestValuesMap,cameraPosition.toString()));
        String directionText1=geometryTextimpValue.replace("[[","");
        String directionTextFinal=directionText1.replace("]]","");
        Log.e("Direction Text","Direction Text " + directionTextFinal);
/*
        //Total Distance & Time Calculations
       // double resultTotalDistance=showDistance(new LatLng(sourceLat,sourceLng),new LatLng(destLat,destLng));
       // String totalDistanceInMts=String.format("%.0f", TotalDistance);
        ETACalclator calculator=new ETACalclator();
        double resultTotalTime=calculator.cal_time(Double.valueOf(TotalDistance), maxSpeed);
        double resultTotalTimeConverted =DecimalUtils.round(resultTotalTime,0);
        int seconds = (int) ((resultTotalTime / 1000) % 60);
        int minutes = (int) ((resultTotalTime / 1000) / 60);

        //Travelled Distance Calculation
        double resultTravelledTimeConverted=0.0;
        double resultTravelledDistance=showDistance(new LatLng(sourceLat,sourceLng),currentGpsPosition);
        String resultTravelledDistanceInMts=String.format("%.0f", resultTravelledDistance);
        ETACalclator etaCalculator=new ETACalclator();
        double speedKMPH= mphTOkmph(vehicleSpeed);
        double resultTravelledTime=etaCalculator.cal_time(resultTravelledDistance, vehicleSpeed);
        if((String.valueOf(resultTravelledTime).equals("Infinity"))||(String.valueOf(resultTravelledTime).equals("NAN"))){

        }else {
            //resultTravelledTimeConverted = DecimalUtils.round(resultTravelledTime, 0);
        }
        //  double resultTravelledTimeConverted =DecimalUtils.round(resultTravelledTime,0);
        String resultTravelledDistanceMts=String.format("%.0f", resultTravelledDistance);


        double RemaininedTime=0.0, DistanceToTravel=0.0,RemaininedTimeConverted=0.0;
        if(resultTravelledDistanceInMts!=null) {
            DistanceToTravel = showDistance(currentGpsPosition,new LatLng(destLat,destLng));
            String resultDistanceToTravel = String.format("%.0f", DistanceToTravel);
            ETACalclator calculator1 = new ETACalclator();
            RemaininedTime=calculator1.cal_time(DistanceToTravel, vehicleSpeed);
            if((String.valueOf(RemaininedTime).equals("Infinity"))||(String.valueOf(RemaininedTime).equals("NAN"))){

            }else {
                RemaininedTimeConverted = DecimalUtils.round(RemaininedTime, 0);
            }
        }

        double presentETATime = resultTravelledTime+RemaininedTime;
        double EtaCrossedTime=0.0;
        String etaCrossedFlag="NO";
        double EtaElapsed=0.0;

        Log.e("resultTotalTime ","resultTotalTime " + resultTotalTimeConverted);
        Log.e("resultTravelledTime ","resultTravelledTime " + resultTravelledTime);
        Log.e("RemaininedTime ","RemaininedTime " + resultTravelledTimeConverted);
        Log.e("present ETATime ","present ETATime " + RemaininedTimeConverted);


        if(presentETATime > resultTotalTime) {
            EtaCrossedTime= presentETATime-resultTotalTime;

        }
        if(EtaCrossedTime > 0.0){
            etaCrossedFlag="YES";

        }else{
            etaCrossedFlag="NO";
        }
        if(String.valueOf(EtaCrossedTime).equals("Infinity")){

        }else{
            EtaElapsed =DecimalUtils.round(EtaCrossedTime,0);
        }
        time.append("Distance").append(TotalDistance +" Meters ").append("\n").append("Total ETA ").append(resultTotalTime +" SEC ").append("\n").append(" Distance To Travel").append(RemaininedTime +"Sec").append("Elapsed Time").append(EtaCrossedTime).append("\n");
        sendData(time.toString());
        tv.setText("Total Time: "+ resultTotalTimeConverted +" SEC" );
        tv1.setText("Time  Traveled: "+ resultTravelledTime +" SEC ");
        tv2.setText("ETA : " + RemaininedTimeConverted +"SEC" );
        tv3.setText("ETA Crossed Alert: "+ etaCrossedFlag );
        //Real ETA Distance
        double ETADIstance=showDistance(currentGpsPosition,new LatLng(destLat,destLng));
        String ETADIstanceInMts=String.format("%.0f", ETADIstance);
        ETACalclator calculator2=new ETACalclator();
        double ETA=calculator2.cal_time(ETADIstance, vehicleSpeed);
        ETA=DecimalUtils.round(resultTotalTime,0);
        //tv2.setText("Distance To Travel: "+ ETADIstanceInMts +"  ");
        //tv3.setText("ETA: "+ ETA +" SEC ");
        //tv4.setText("Direction : "+ directionText );
        //Speech implementation

        String data= directionTextFinal +" " + Distance_To_travelIn_Vertex_Convetred +"Meters";
        //String data=" in "+ DitrectionDistance +" Meters "+ directionTextFinal;
        int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
 */

        Toast.makeText(getActivity(), ""+ DitrectionDistance+" "+ directionTextFinal, Toast.LENGTH_SHORT).show();
        LayoutInflater inflater1 = getActivity().getLayoutInflater();
        @SuppressLint("WrongViewCast") View layout = inflater1.inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.textView_toast));
        TextView text = (TextView) layout.findViewById(R.id.textView_toast);

        text.setText(" "+ directionTextFinal +" " + Distance_To_travelIn_Vertex_Convetred +"Meters");
        ImageView image = (ImageView) layout.findViewById(R.id.image_toast);
        if(directionTextFinal.contains("Take Right")){
            image.setImageResource(R.drawable.direction_right);
        }else if(directionTextFinal.contains("Take Left")){
            image.setImageResource(R.drawable.direction_left);
        }

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setGravity(Gravity.TOP,0,150);
        toast.setView(layout);
        toast.show();
        Log.e("Destination points","currentGpsPosition LastLoop "+currentGpsPosition);
        Log.e("Destination points","DestinationPosition  "+DestinationPosition);

    }


}

