package com.nsg.nsgmapslibrary.SupportClasses;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nsg.nsgmapslibrary.R;
import com.nsg.nsgmapslibrary.database.dto.EdgeDataT;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class GetRoutingDataAPI {
    static LatLng SourcePosition=new LatLng(472233.15880000032,2764734.6520000007);
    static LatLng DestinationPosition=new LatLng(472266.96239999961,2764681.4626);
    public static void drawRoute(){

        DownloadFeaturesFromServer downloadTask = new DownloadFeaturesFromServer();
        downloadTask.execute();

    }
    static class DownloadFeaturesFromServer extends AsyncTask<String, String, String> {

        // Download features from server using URL and get the data and inserted to Respective tables like DT, SS,RMU ect...
        // and process that json data and insert to respective tables ....;
        ProgressDialog dialog ;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... f_url) {

            try {
               String httprequest = "http://202.53.11.74/dtrouting/api/routing/navigate";
                String FeatureResponse = HttpPost(httprequest);
                Log.e("RESPONSE","RESPONSE"+ FeatureResponse);

            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String file_url) {
          //  dialog.setTitle("Downloaded Features ");
        //    dialog.dismiss();

        }
    }


    private static String HttpPost(String myUrl) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String LoginResponse="";
        String result = "";
        URL url = new URL(myUrl);
        Log.v("URL ", " URL: " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "text/plain");
        JSONObject jsonObject = buidJsonObject();
        // Log.e(" Message", " jsonObject: " + jsonObject);
        setPostRequestContent(conn, jsonObject);
        // conn.connect();
        //  Log.e("Response Code", "ResponseCode: " + conn.getResponseCode());
        result = conn.getResponseMessage();
        //  Log.e("Response Message", "Response Message: " + result);

        if(conn.getResponseCode()!=200){

        }else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = null;
            //   System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
               LoginResponse= sb.append(output).append(" ").toString();
                //      Log.e("Login Response "," From server ############ "+LoginResponse);
            }
        }
        conn.disconnect();
        return LoginResponse;
    }
    private static JSONObject buidJsonObject() throws JSONException {
        JSONObject buidJsonObject = new JSONObject();
        buidJsonObject.accumulate("UserData",buidJsonObject1());
        buidJsonObject.accumulate("StartNode",SourcePosition);
        buidJsonObject.accumulate("EndNode",DestinationPosition);
        return buidJsonObject;
    }
    private static JSONObject buidJsonObject1() throws JSONException {
        JSONObject buidJsonObject1 = new JSONObject();
        buidJsonObject1.accumulate("username", "admin");
        buidJsonObject1.accumulate("password","admin");
        return buidJsonObject1;
    }
    private static void setPostRequestContent(HttpURLConnection conn,
                                              JSONObject jsonObject) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
       // Log.i(LoginActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }
    /*
    public String MoveWithGpsPointInBetWeenAllPoints(){
        getLatLngPoints();// get FakeGps points
        getAllEdgesData(); // get All edges data
        edgeDataPointsList = new ArrayList<LatLng>();
        etaList=new ArrayList<>();
        nearestPointValuesList=new ArrayList<LatLng>();
        nearestValuesMap=new HashMap<>();
        nearestPointValuesList.add(new LatLng(sourceLat,sourceLng));
        if (edgeDataList != null && edgeDataList.size() > 0) {
            AllPointsList=new ArrayList();
            AllPointEdgeNo=new HashMap<>();
            for (int i = 0; i < edgeDataList.size(); i++) {
                EdgeDataT edge = new EdgeDataT(); //creating object for EDGETABLE
                edge = edgeDataList.get(i);
                int edgeNo = edge.getEdgeNo(); //Edge Number
                String stPoint = edge.getStartPoint(); //Start Point
                String endPoint = edge.getEndPoint();//End Point
                String points = edge.getAllPoints(); // All points in the edge
                String geometryText=edge.getGeometryText();
                // Geometry Direction text
                Log.e("EdgePoints Data","EdgePoints Data Geometry " + geometryText+" : "+ edgeNo);
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
                        AllPointsList.add(AllPointsArray[ap]);
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
        }
        //Log.e("ALL POINTS ", "FROM DATABASE ----- " + AllPointEdgeNo.size());
        //Log.e("ALL POINTS ", "FROM DATABASE ----- " + AllPointsList.size());
        // Log.e("ALL POINTS ", "FROM DATABASE ----- " + edgeDataPointsList.size());

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
                    Log.e("AllPointEdgeNo ", "AllPointEdgeNo " + AllPointEdgeNo.size());
                    key= String.valueOf(getKeysFromValue(AllPointEdgeNo,FirstCordinate));
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
                Log.e("Sorted ArrayList ", "-----geometryDirectionText :" + geometryDirectionText);

                Log.e("Sorted ArrayList ", "-----FirstLatitude :" + FirstLatitude);
                Log.e("Sorted ArrayList ", "-----FirstLongitude" + FirstLongitude);
                // String[] SecondCordinateArray = SecondCordinate.split("#");
                //  Log.e("Sorted ArrayList ", "in Ascending order ---AT 2--- :" + SecondCordinateArray[0]);
                String Second= SecondCordinate.replace("lat/lng: (","");
                Second= Second.replace(")","");
                String[] SecondLatLngsData=Second.split(",");
                double SecondLatitude= Double.valueOf(SecondLatLngsData[0]);
                double SecondLongitude= Double.valueOf(SecondLatLngsData[1]);

                Log.e("Sorted ArrayList ", "-----SecondLatitude :" + SecondLatitude);
                Log.e("Sorted ArrayList ", "-----SecondLongitude" + SecondLongitude);
                double x= currentGpsPosition.longitude;
                double y= currentGpsPosition.longitude;
                int value = (int)x;
                int value1 = (int)y;
                LatLng source=new LatLng(FirstLongitude,FirstLatitude);
                LatLng destination=new LatLng(SecondLongitude,SecondLatitude);

                Log.e("EdgeSt Point", "End point" + DestinationPosition);
                nearestPositionPoint= findNearestPoint(currentGpsPosition,source,destination);
                nearestValuesMap.put(String.valueOf(nearestPositionPoint),geometryDirectionText);
                nearestPointValuesList.add(nearestPositionPoint);


                if(currentGpsPosition.equals(LatLngDataArray.get(LatLngDataArray.size()-1))){
                    nearestPointValuesList.add(DestinationPosition);
                }
            }

        }
        for(int k=0;k<nearestPointValuesList.size();k++){
            Log.e("NEAREST LIST","NEAREST LIST"+nearestPointValuesList.get(k));
        }
        startTime=System.currentTimeMillis();
        sendTokenRequest();
        Log.e("startTime","StartTime"+startTime);

        Log.e("EdgeSt Point", "End point" + LatLngDataArray.size());
        CameraPosition googlePlex = CameraPosition.builder()
                .target(nearestPositionPoint)
                .zoom(20)
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
            //   animateCarMove(mPositionMarker, nearestPointValuesList.get(0), nearestPointValuesList.get(1), 10000);

        }
        return null;
    }
*/
}
