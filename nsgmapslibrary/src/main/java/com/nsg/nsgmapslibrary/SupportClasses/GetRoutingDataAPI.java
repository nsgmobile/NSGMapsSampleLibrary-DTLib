package com.nsg.nsgmapslibrary.SupportClasses;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.nsg.nsgmapslibrary.Classes.NSGGetRouteOnMap;

import org.json.JSONArray;
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
                /*
                if (FeatureResponse != null ) {
                    //Log.e("Insertion query", "Insertion query" + query);
                    JSONArray a = new JSONArray(FeatureResponse.toString());
                    for (int i = 0; i < a.length(); i++) {
                        JSONArray b = new JSONArray(a.getJSONArray(i).toString());
                        for (int j = 0; j < b.length(); j++) {
                            JSONObject mJsonObject = b.getJSONObject(j);
                            if (mJsonObject.getString("featureName").startsWith("POINT")) {

                                String id = mJsonObject.getString("id");
                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String createdon = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("log");
                                String sectionId = mJsonObject.getString("log");
                                String gisid = mJsonObject.getString("GISRefID");
                                String featureName = mJsonObject.getString("featureName");

                                String jsonFormatString1 = jsondata.replace(" ", ",");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatString1);
                                String jsonFormatStringFinal = jsonFormatString1.replace("POINT", "lat/lng: ");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatStringFinal);

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(RedPoints_T.TABLE_NAME).append("(geojson,remarks,status,createdOn,createdBy,flag,GISRefID,latitude,longitude,imageLocation,sectionId,featureName) values (").append("'").append(jsonFormatStringFinal).append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(createdon).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(gisid).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                sqlHandler.closeDataBaseConnection();

                            } else if (mJsonObject.getString("featureName").startsWith("LINESTRING")) {
                                //MULTILINESTRING((75.0401408225298 14.1654583698025,75.0415141135454 14.1697305494368))
                                //[lat/lng: (14.173900901974315,75.02764876931906), lat/lng: (14.178127738865138,75.0337353721261)]

                                String id = mJsonObject.getString("id");
                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String createdon = mJsonObject.getString("createdon");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");
                                String gisid = mJsonObject.getString("GISRefID");

                                Log.e("Point data", "LINE DAATA " + jsondata);
                                LatLng latLng1 = null, latLng2 = null;
                                List<LatLng> points=new ArrayList<LatLng>();
                                StringBuilder sb = new StringBuilder();
                                String finalJsonString="";
                                String jsondata1 = jsondata.replace("MULTILINESTRING((", "");
                                String jsondata2 = jsondata1.replace("))", "");
                                String jsondata3 = jsondata2.replace(" ", ",");
                                Log.e("jsonCordinates", "jsondata3----- " + jsondata3);
                                String[] jsondataFinal = jsondata3.split(",");
                                Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal.length);
                                for(int p=0;p<jsondataFinal.length;p+=2){
                                    Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal[p]);

                                    double latitude = Double.parseDouble(jsondataFinal[p]);
                                    double longitude = Double.parseDouble(jsondataFinal[p+1]);
                                    LatLng point = new LatLng(longitude,latitude);
                                    Log.e("jsonCordinates", "LineCordinates ############" + point);
                                    points.add(point);
                                    finalJsonString = points.toString();
                                }
                                Log.e("jsonCordinates", "Points Size ############" + points.size());
                                System.out.println("Step-1 ----  ############ : " + finalJsonString);

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(RedLines_T.TABLE_NAME).append("(geojson,remarks,status,createdOn,createdBy,flag,GISRefID,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(finalJsonString).append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(createdon).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(gisid).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                // Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.closeDataBaseConnection();

                            } else if (mJsonObject.getString("featureName").startsWith("POLYGON")) {
                                String id = mJsonObject.getString("id");
                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String createdon = mJsonObject.getString("createdon");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");
                                String gisid = mJsonObject.getString("GISRefID");


                                Log.e("Polygon  data", "POLYGON DAATA -------" + jsondata);
                                LatLng latLng1 = null, latLng2 = null;
                                List<LatLng> points=new ArrayList<LatLng>();
                                StringBuilder sb = new StringBuilder();
                                String finalJsonString="";
                                String jsondata1 = jsondata.replace("MULTIPOLYGON(((", "");
                                String jsondata2 = jsondata1.replace(")))", "");
                                String jsondata3 = jsondata2.replace(" ", ",");
                                Log.e("jsonCordinates", "jsondata3----- " + jsondata3);
                                String[] jsondataFinal = jsondata3.split(",");
                                Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal.length);
                                for(int p=0;p<jsondataFinal.length;p+=2){
                                    Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal[p]);

                                    double latitude = Double.parseDouble(jsondataFinal[p]);
                                    double longitude = Double.parseDouble(jsondataFinal[p+1]);
                                    LatLng point = new LatLng(longitude,latitude);
                                    Log.e("jsonCordinates", "LineCordinates ############" + point);
                                    points.add(point);
                                    finalJsonString = points.toString();
                                }
                                Log.e("jsonCordinates", "Points Size ############" + points.size());
                                System.out.println("Step-1 ----  ############ : " + finalJsonString);

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(RedPolygons_T.TABLE_NAME).append("(geojson,remarks,status,createdOn,createdBy,flag,GISRefID,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(finalJsonString).append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(createdon).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append("gisid").append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                // Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.closeDataBaseConnection();
                            } else if (mJsonObject.getString("featureName").equals("DT")) {

                                //                              String id = mJsonObject.getString("id");
                                String ASSETID = mJsonObject.getString("ASSETID");
                                //                                String NAME = mJsonObject.getString("NAME");
                                String LOCATION = mJsonObject.getString("LOCATION");
                                String FEEDER_NO = mJsonObject.getString("FEEDER_NO");
                                String SUBSTATION_NAME = mJsonObject.getString("SUBSTATION_NAME");
                                String MAKE = mJsonObject.getString("MAKE");
                                String CREATED_ON = mJsonObject.getString("CREATED_ON");
                                String DTC_NO = mJsonObject.getString("DTC_NO");
                                String MFG_YEAR = mJsonObject.getString("MFG_YEAR");
                                String GISRefID = mJsonObject.getString("GISRefID");
                                String CAPACITY = mJsonObject.getString("CAPACITY");
                                String redlineaction = mJsonObject.getString("redlineaction");
                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");
                                Log.e("Point data", "pointdata" + jsondata);
                                String jsonFormatString1 = jsondata.replace(" ", ",");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatString1);
                                String jsonFormatStringFinal = jsonFormatString1.replace("POINT", "lat/lng: ");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatStringFinal);

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(DT_T.TABLE_NAME).append("(geojson,flag,remarks,status,createdOn,assetId,GISRefID,assetName,locationTX,FEEDER_NO,SUBSTATION_NAME,MFG_YEAR,MAKE,DTC_NO,CAPACITY,redlineAction,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(jsonFormatStringFinal).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(CREATED_ON).append("',")
                                        .append("'").append(ASSETID).append("',")
                                        .append("'").append(GISRefID).append("',")
                                        .append("'").append("NAME").append("',")
                                        .append("'").append(LOCATION).append("',")
                                        .append("'").append(FEEDER_NO).append("',")
                                        .append("'").append(SUBSTATION_NAME).append("',")
                                        .append("'").append(MFG_YEAR).append("',")
                                        .append("'").append(MAKE).append("',")
                                        .append("'").append(DTC_NO).append("',")
                                        .append("'").append(CAPACITY).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(redlineaction).append("',")
                                        .append("'").append(featureName).append("')");
                                // Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                // Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.closeDataBaseConnection();
                            } else if (mJsonObject.getString("featureName").equals("SS")) {
                                String ASSETID = mJsonObject.getString("ASSETID");
                                String GISRefID = mJsonObject.getString("GISRefID");
                                String SUBTYPECD = mJsonObject.getString("SUBTYPECD");
                                String HEIGHT = mJsonObject.getString("HEIGHT");
                                String TYPE = mJsonObject.getString("TYPE");
                                String FEEDER_NO = mJsonObject.getString("FEEDER_NO");
                                String SUBSTATION_NAME = mJsonObject.getString("SUBSTATION_NAME");
                                String redlineaction = mJsonObject.getString("redlineaction");
                                String NO_OF_POLES = mJsonObject.getString("FEEDER_NO");
                                String CREATED_ON = mJsonObject.getString("SUBSTATION_NAME");
                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");

                                Log.e("Point data", "pointdata" + jsondata);
                                String jsonFormatString1 = jsondata.replace(" ", ",");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatString1);
                                String jsonFormatStringFinal = jsonFormatString1.replace("POINT", "lat/lng: ");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatStringFinal);

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(SupportStructure_T.TABLE_NAME).append("(geojson,flag,remarks,status,createdOn,assetId,GISRefID,assetName,locationTX,FEEDER_NO,SUBSTATION_NAME,SUBTYPECD,TYPE,NO_OF_POLES,redlineAction,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(jsonFormatStringFinal).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(CREATED_ON).append("',")
                                        .append("'").append(ASSETID).append("',")
                                        .append("'").append(GISRefID).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(FEEDER_NO).append("',")
                                        .append("'").append(SUBSTATION_NAME).append("',")
                                        .append("'").append(SUBTYPECD).append("',")
                                        .append("'").append(TYPE).append("',")
                                        .append("'").append(NO_OF_POLES).append("',")
                                        .append("'").append(redlineaction).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");
                                //  Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                //  Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.closeDataBaseConnection();
                            } else if (mJsonObject.getString("featureName").equals("RMU")) {

                                String ID = mJsonObject.getString("ID");
                                String SUBTYPECD = mJsonObject.getString("SUBTYPECD");
                                String GISRefID = mJsonObject.getString("GISRefID");
                                String NAME = mJsonObject.getString("NAME");
                                String LOCATION = mJsonObject.getString("LOCATION");
                                String MAKE = mJsonObject.getString("MAKE");
                                String ASSETID = mJsonObject.getString("ASSETID");
                                String FEEDER_NO = mJsonObject.getString("FEEDER_NO");
                                String SUBSTATION_NAME = mJsonObject.getString("SUBSTATION_NAME");
                                String MFG_YEAR = mJsonObject.getString("MFG_YEAR");
                                String INSTALL_TYPE = mJsonObject.getString("INSTALL_TYPE");
                                String redlineaction = mJsonObject.getString("redlineaction");
                                String CREATED_ON = mJsonObject.getString("CREATED_ON");

                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");

                                Log.e("Point data", "pointdata" + jsondata);
                                String jsonFormatString1 = jsondata.replace(" ", ",");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatString1);
                                String jsonFormatStringFinal = jsonFormatString1.replace("POINT", "lat/lng: ");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatStringFinal);

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(RMU_T.TABLE_NAME).append("(geojson,flag,remarks,status,createdOn,assetId,GISRefID,assetName,locationTX,FEEDER_NO,SUBSTATION_NAME,MFG_YEAR,MAKE,SUBTYPECD,INSTALL_TYPE,redlineAction,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(jsonFormatStringFinal).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(CREATED_ON).append("',")
                                        .append("'").append(ASSETID).append("',")
                                        .append("'").append(GISRefID).append("',")
                                        .append("'").append(NAME).append("',")
                                        .append("'").append(LOCATION).append("',")
                                        .append("'").append(FEEDER_NO).append("',")
                                        .append("'").append(SUBSTATION_NAME).append("',")
                                        .append("'").append(MFG_YEAR).append("',")
                                        .append("'").append(MAKE).append("',")
                                        .append("'").append(SUBTYPECD).append("',")
                                        .append("'").append(INSTALL_TYPE).append("',")
                                        .append("'").append(redlineaction).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                // Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.closeDataBaseConnection();
                            } else if (mJsonObject.getString("featureName").equals("DTC")) {

                                String ID = mJsonObject.getString("ID");
                                String FEEDER_NO = mJsonObject.getString("FEEDER_NO");
                                String GISRefID = mJsonObject.getString("GISRefID");
                                String NO_OF_GOS = mJsonObject.getString("NO_OF_GOS");
                                String TRANSFORMER_MOUNTING = mJsonObject.getString("TRANSFORMER_MOUNTING");
                                String ASSETID = mJsonObject.getString("ASSETID");
                                String SUBSTATION_NAME = mJsonObject.getString("SUBSTATION_NAME");
                                String redlineaction = mJsonObject.getString("redlineaction");
                                String CREATED_ON = mJsonObject.getString("CREATED_ON");
                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");

                                Log.e("Point data", "pointdata" + jsondata);
                                String jsonFormatString1 = jsondata.replace(" ", ",");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatString1);
                                String jsonFormatStringFinal = jsonFormatString1.replace("POINT", "lat/lng: ");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatStringFinal);

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(DTCStructure_T.TABLE_NAME).append("(geojson,flag,remarks,status,createdOn,assetId,GISRefID,locationTX,SUBSTATION_NAME,FEEDER_NO,NO_OF_GOS,TRANSFORMER_MOUNTING,redlineAction,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(jsonFormatStringFinal).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(CREATED_ON).append("',")
                                        .append("'").append(ASSETID).append("',")
                                        .append("'").append(GISRefID).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(SUBSTATION_NAME).append("',")
                                        .append("'").append(FEEDER_NO).append("',")
                                        .append("'").append(NO_OF_GOS).append("',")
                                        .append("'").append(TRANSFORMER_MOUNTING).append("',")
                                        .append("'").append(redlineaction).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");

                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.closeDataBaseConnection();
                            } else if (mJsonObject.getString("featureName").equals("SWITCH")) {

                                String ID = mJsonObject.getString("ID");
                                String SUBTYPECD = mJsonObject.getString("SUBTYPECD");
                                String GISRefID = mJsonObject.getString("GISRefID");
                                String ASSETID = mJsonObject.getString("ASSETID");
                                String TYPE = mJsonObject.getString("TYPE");
                                String DEVICE_TYPE = mJsonObject.getString("DEVICE_TYPE");
                                String CREATED_ON = mJsonObject.getString("CREATED_ON");
                                String redlineaction = mJsonObject.getString("redlineaction");
                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");

                                Log.e("Point data", "pointdata" + jsondata);
                                String jsonFormatString1 = jsondata.replace(" ", ",");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatString1);
                                String jsonFormatStringFinal = jsonFormatString1.replace("POINT", "lat/lng: ");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatStringFinal);

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(Switch_T.TABLE_NAME).append("(geojson,flag,remarks,status,createdOn,assetId,GISRefID,locationTX,SUBSTATION_NAME,SUBTYPECD,TYPE,DEVICE_TYPE,redlineAction,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(jsonFormatStringFinal).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(CREATED_ON).append("',")
                                        .append("'").append(ASSETID).append("',")
                                        .append("'").append(GISRefID).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(SUBTYPECD).append("',")
                                        .append("'").append(TYPE).append("',")
                                        .append("'").append(DEVICE_TYPE).append("',")
                                        .append("'").append(redlineaction).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.closeDataBaseConnection();

                            } else if (mJsonObject.getString("featureName").equals("FPB")) {
                                String ID = mJsonObject.getString("ID");
                                String SUBTYPECD = mJsonObject.getString("SUBTYPECD");
                                String GISRefID = mJsonObject.getString("GISRefID");
                                String FEEDER_NO = mJsonObject.getString("FEEDER_NO");
                                String SUBSTATION_NAME = mJsonObject.getString("SUBSTATION_NAME");
                                String ASSETID = mJsonObject.getString("ASSETID");
                                String TYPE = mJsonObject.getString("TYPE");
                                String CREATED_ON = mJsonObject.getString("CREATED_ON");
                                String redlineaction = mJsonObject.getString("redlineaction");
                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");

                                Log.e("Point data", "pointdata" + jsondata);
                                String jsonFormatString1 = jsondata.replace(" ", ",");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatString1);
                                String jsonFormatStringFinal = jsonFormatString1.replace("POINT", "lat/lng: ");
                                Log.e("Point data", "ReplacedString ###### " + jsonFormatStringFinal);


                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(FeederPillerBox_T.TABLE_NAME).append("(geojson,flag,remarks,status,createdOn,assetId,GISRefID,locationTX,SUBSTATION_NAME,FEEDER_NO,SUBTYPECD,TYPE,redlineAction,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(jsonFormatStringFinal).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(CREATED_ON).append("',")
                                        .append("'").append(ASSETID).append("',")
                                        .append("'").append(GISRefID).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(SUBSTATION_NAME).append("',")
                                        .append("'").append(FEEDER_NO).append("',")
                                        .append("'").append(SUBTYPECD).append("',")
                                        .append("'").append(TYPE).append("',")
                                        .append("'").append(redlineaction).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");


                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.closeDataBaseConnection();

                            } else if (mJsonObject.getString("featureName").equals("OHH")) {
                                //Get response from service and insert to database

                                String ID = mJsonObject.getString("ID");
                                String SUBTYPECD = mJsonObject.getString("SUBTYPECD");
                                String GISRefID = mJsonObject.getString("GISRefID");
                                String FEEDER_NO = mJsonObject.getString("FEEDER_NO");
                                String SUBSTATION_NAME = mJsonObject.getString("SUBSTATION_NAME");
                                String CONDUCTOR_SIZE = mJsonObject.getString("CONDUCTOR_SIZE");
                                String MATERIAL = mJsonObject.getString("MATERIAL");
                                String redlineaction = mJsonObject.getString("redlineaction");
                                String TYPE = mJsonObject.getString("TYPE");
                                String CREATED_ON = mJsonObject.getString("CREATED_ON");

                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");



                                Log.e("Point data", "LINE DAATA " + jsondata);
                                LatLng latLng1 = null, latLng2 = null;
                                List<LatLng> points=new ArrayList<LatLng>();
                                StringBuilder sb = new StringBuilder();
                                String finalJsonString="";
                                String jsondata1 = jsondata.replace("MULTILINESTRING((", "");
                                String jsondata2 = jsondata1.replace("))", "");
                                String jsondata3 = jsondata2.replace(" ", ",");
                                Log.e("jsonCordinates", "jsondata3----- " + jsondata3);
                                String[] jsondataFinal = jsondata3.split(",");
                                Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal.length);
                                for(int p=0;p<jsondataFinal.length;p+=2){
                                    Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal[p]);

                                    double latitude = Double.parseDouble(jsondataFinal[p]);
                                    double longitude = Double.parseDouble(jsondataFinal[p+1]);
                                    LatLng point = new LatLng(longitude,latitude);
                                    Log.e("jsonCordinates", "LineCordinates ############" + point);
                                    points.add(point);
                                    finalJsonString = points.toString();
                                }
                                Log.e("jsonCordinates", "Points Size ############" + points.size());
                                System.out.println("Step-1 ----  ############ : " + finalJsonString);

                                Log.e("jsonCordinates", "LineCordinates" + sb.toString());
                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(OHHT_T.TABLE_NAME).append("(geojson,flag,remarks,status,createdOn,assetId,GISRefID,locationTX,SUBSTATION_NAME,FEEDER_NO,SUBTYPECD,TYPE,CONDUCTOR_SIZE,MATERIAL,redlineAction,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(finalJsonString).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(CREATED_ON).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(GISRefID).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(SUBSTATION_NAME).append("',")
                                        .append("'").append(FEEDER_NO).append("',")
                                        .append("'").append(SUBTYPECD).append("',")
                                        .append("'").append(TYPE).append("',")
                                        .append("'").append(CONDUCTOR_SIZE).append("',")
                                        .append("'").append(MATERIAL).append("',")
                                        .append("'").append(redlineaction).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                // Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.closeDataBaseConnection();

                            } else if (mJsonObject.getString("featureName").equals("OHL")) {


                                String ID = mJsonObject.getString("ID");

                                String SUBTYPECD = mJsonObject.getString("SUBTYPECD");
                                String GISRefID = mJsonObject.getString("GISRefID");
                                String PHASEDESIGNATION = mJsonObject.getString("PHASEDESIGNATION");
                                String FEEDER_NO = mJsonObject.getString("FEEDER_NO");
                                String SUBSTATION_NAME = mJsonObject.getString("SUBSTATION_NAME");
                                String CONDUCTOR_SIZE = mJsonObject.getString("CONDUCTOR_SIZE");
                                String MATERIAL = mJsonObject.getString("MATERIAL");
                                String TYPE = mJsonObject.getString("TYPE");
                                String NO_OF_CONDUCTOR = mJsonObject.getString("NO_OF_CONDUCTOR");
                                String redlineaction = mJsonObject.getString("redlineaction");
                                String CREATED_ON = mJsonObject.getString("CREATED_ON");

                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");


                                Log.e("Point data", "LINE DAATA " + jsondata);
                                LatLng latLng1 = null, latLng2 = null;
                                List<LatLng> points=new ArrayList<LatLng>();
                                StringBuilder sb = new StringBuilder();
                                String finalJsonString="";
                                String jsondata1 = jsondata.replace("MULTILINESTRING((", "");
                                String jsondata2 = jsondata1.replace("))", "");
                                String jsondata3 = jsondata2.replace(" ", ",");
                                Log.e("jsonCordinates", "jsondata3----- " + jsondata3);
                                String[] jsondataFinal = jsondata3.split(",");
                                Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal.length);
                                for(int p=0;p<jsondataFinal.length;p+=2){
                                    Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal[p]);

                                    double latitude = Double.parseDouble(jsondataFinal[p]);
                                    double longitude = Double.parseDouble(jsondataFinal[p+1]);
                                    LatLng point = new LatLng(longitude,latitude);
                                    Log.e("jsonCordinates", "LineCordinates ############" + point);
                                    points.add(point);
                                    finalJsonString = points.toString();
                                }
                                Log.e("jsonCordinates", "Points Size ############" + points.size());
                                System.out.println("Step-1 ----  ############ : " + finalJsonString);

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(OHLT_T.TABLE_NAME).append("(geojson,flag,remarks,status,createdOn,assetId,GISRefID,locationTX,SUBSTATION_NAME,FEEDER_NO,SUBTYPECD,TYPE,CONDUCTOR_SIZE,MATERIAL,PHASEDESIGNATION,NO_OF_CONDUCTOR,redlineAction,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(finalJsonString).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(CREATED_ON).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(GISRefID).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(SUBSTATION_NAME).append("',")
                                        .append("'").append(FEEDER_NO).append("',")
                                        .append("'").append(SUBTYPECD).append("',")
                                        .append("'").append(TYPE).append("',")
                                        .append("'").append(CONDUCTOR_SIZE).append("',")
                                        .append("'").append(MATERIAL).append("',")
                                        .append("'").append(PHASEDESIGNATION).append("',")
                                        .append("'").append(NO_OF_CONDUCTOR).append("',")
                                        .append("'").append(redlineaction).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                sqlHandler.closeDataBaseConnection();

                            } else if (mJsonObject.getString("featureName").equals("UGH")) {


                                String ID = mJsonObject.getString("id");

                                String SUBTYPECD = mJsonObject.getString("SUBTYPECD");
                                String GISRefID = mJsonObject.getString("GISRefID");
                                String FEEDER_NO = mJsonObject.getString("FEEDER_NO");
                                String SUBSTATION_NAME = mJsonObject.getString("SUBSTATION_NAME");
                                String CABLE_SIZE = mJsonObject.getString("CABLE_SIZE");
                                String TYPE = mJsonObject.getString("TYPE");
                                String redlineaction = mJsonObject.getString("redlineaction");
                                String CREATED_ON = mJsonObject.getString("CREATED_ON");
                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");

                                Log.e("Point data", "LINE DAATA " + jsondata);
                                LatLng latLng1 = null, latLng2 = null;
                                List<LatLng> points=new ArrayList<LatLng>();
                                StringBuilder sb = new StringBuilder();
                                String finalJsonString="";
                                String jsondata1 = jsondata.replace("MULTILINESTRING((", "");
                                String jsondata2 = jsondata1.replace("))", "");
                                String jsondata3 = jsondata2.replace(" ", ",");
                                Log.e("jsonCordinates", "jsondata3----- " + jsondata3);
                                String[] jsondataFinal = jsondata3.split(",");
                                Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal.length);
                                for(int p=0;p<jsondataFinal.length;p+=2){
                                    Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal[p]);

                                    double latitude = Double.parseDouble(jsondataFinal[p]);
                                    double longitude = Double.parseDouble(jsondataFinal[p+1]);
                                    LatLng point = new LatLng(longitude,latitude);
                                    Log.e("jsonCordinates", "LineCordinates ############" + point);
                                    points.add(point);
                                    finalJsonString = points.toString();
                                }
                                Log.e("jsonCordinates", "Points Size ############" + points.size());
                                System.out.println("Step-1 ----  ############ : " + finalJsonString);

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(UGHT_T.TABLE_NAME).append("(geojson,flag,remarks,status,createdOn,assetId,GISRefID,locationTX,SUBSTATION_NAME,FEEDER_NO,SUBTYPECD,TYPE,MATERIAL,CABLE_SIZE,redlineAction,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(finalJsonString).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(CREATED_ON).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(GISRefID).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(SUBSTATION_NAME).append("',")
                                        .append("'").append(FEEDER_NO).append("',")
                                        .append("'").append(SUBTYPECD).append("',")
                                        .append("'").append(TYPE).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(CABLE_SIZE).append("',")
                                        .append("'").append(redlineaction).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                Log.e("Insertion query", "Insertion query" + query);


                                sqlHandler.closeDataBaseConnection();
                                getListofUsers();
                                Log.e("UserList", "UserList %%%%%" + userList.size());
                            } else if (mJsonObject.getString("featureName").equals("UGL")) {

                                String ID = mJsonObject.getString("ID");
                                String SUBTYPECD = mJsonObject.getString("SUBTYPECD");
                                String GISRefID = mJsonObject.getString("GISRefID");
                                String PHASEDESIGNATION = mJsonObject.getString("PHASEDESIGNATION");
                                String FEEDER_NO = mJsonObject.getString("FEEDER_NO");
                                String SUBSTATION_NAME = mJsonObject.getString("SUBSTATION_NAME");
                                String CABLE_SIZE = mJsonObject.getString("CABLE_SIZE");
                                String NO_OF_CORE = mJsonObject.getString("NO_OF_CORE");
                                String TYPE = mJsonObject.getString("TYPE");
                                String redlineaction = mJsonObject.getString("redlineaction");
                                String CREATED_ON = mJsonObject.getString("CREATED_ON");
                                String jsondata = mJsonObject.getString("jsondata");
                                String remarks = mJsonObject.getString("remarks");
                                String status = mJsonObject.getString("status");
                                String imgText = mJsonObject.getString("imagetxt");
                                String lat = mJsonObject.getString("lat");
                                String log = mJsonObject.getString("log");
                                String userId = mJsonObject.getString("userId");
                                String sectionId = mJsonObject.getString("sectionId");
                                String featureName = mJsonObject.getString("featureName");

                                Log.e("Point data", "LINE DAATA " + jsondata);
                                LatLng latLng1 = null, latLng2 = null;
                                List<LatLng> points=new ArrayList<LatLng>();
                                StringBuilder sb = new StringBuilder();
                                String finalJsonString="";
                                String jsondata1 = jsondata.replace("MULTILINESTRING((", "");
                                String jsondata2 = jsondata1.replace("))", "");
                                String jsondata3 = jsondata2.replace(" ", ",");
                                Log.e("jsonCordinates", "jsondata3----- " + jsondata3);
                                String[] jsondataFinal = jsondata3.split(",");
                                Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal.length);
                                for(int p=0;p<jsondataFinal.length;p+=2){
                                    Log.e("jsonCordinates", "jsonCordinates from PointsList--------  " + jsondataFinal[p]);

                                    double latitude = Double.parseDouble(jsondataFinal[p]);
                                    double longitude = Double.parseDouble(jsondataFinal[p+1]);
                                    LatLng point = new LatLng(longitude,latitude);
                                    Log.e("jsonCordinates", "LineCordinates ############" + point);
                                    points.add(point);
                                    finalJsonString = points.toString();
                                }
                                Log.e("jsonCordinates", "Points Size ############" + points.size());
                                System.out.println("Step-1 ----  ############ : " + finalJsonString);
                                /*

                                Log.e("Point data", "LINE DAATA " + jsondata);
                                //MULTILINESTRING((75.0401408225298 14.1654583698025,75.0415141135454 14.1697305494368))
                                String jsondata1 = jsondata.replace("MULTILINESTRING((", "");
                                String jsondata2 = jsondata1.replace("))", "");
                                String[] jsondata3 = jsondata2.split(",");
                                StringBuilder sb = new StringBuilder();
                                LatLng latLng1 = null, latLng2 = null;
                                for (int p = 0; p < jsondata3.length; p++) {
                                    Log.e("jsonCordinates", "jsonCordinates " + jsondata3[0]);
                                    Log.e("jsonCordinates", "jsonCordinates " + jsondata3[1]);
                                    int count1 = jsondata3[0].length();
                                    Log.e("jsonCordinates", "count1 " + count1);
                                    Double lineStr1 = Double.valueOf(jsondata3[0].substring(0, count1 / 2));
                                    Log.e("jsonCordinates", "lineStr1 " + lineStr1);

                                    Double lineStr2 = Double.valueOf(jsondata3[0].substring(count1 / 2, count1));
                                    Log.e("jsonCordinates", "lineStr2 " + lineStr2);

                                    int count2 = jsondata3[1].length();
                                    Log.e("jsonCordinates", "count2 " + count2);

                                    Double lineStr3 = Double.valueOf(jsondata3[1].substring(0, count2 / 2));
                                    Log.e("jsonCordinates", "lineStr1 " + lineStr1);

                                    Double lineStr4 = Double.valueOf(jsondata3[1].substring(count2 / 2, count2));
                                    Log.e("jsonCordinates", "lineStr2 " + lineStr2);

                                    latLng1 = new LatLng(lineStr2, lineStr1);
                                    Log.e("jsonCordinates", "First Latlng  " + latLng1);

                                    latLng2 = new LatLng(lineStr4, lineStr3);
                                    Log.e("jsonCordinates", "First Latlng  " + latLng1);
                                }

                                sb.append("[").append(latLng1).append(", ").append(latLng2).append("]");
                                Log.e("jsonCordinates", "LineCordinates" + sb.toString());

                                StringBuilder query = new StringBuilder("INSERT INTO ");
                                query.append(UGLT_T.TABLE_NAME).append("(geojson,flag,remarks,status,createdOn,GISRefID,locationTX,SUBSTATION_NAME,FEEDER_NO,SUBTYPECD,TYPE,CABLE_SIZE,PHASEDESIGNATION,redlineAction,latitude,longitude,imageLocation,userId,sectionId,featureName) values (")
                                        .append("'").append(finalJsonString).append("',")
                                        .append("'").append("1").append("',")
                                        .append("'").append(remarks).append("',")
                                        .append("'").append(status).append("',")
                                        .append("'").append(CREATED_ON).append("',")
                                        .append("'").append(GISRefID).append("',")
                                        .append("'").append("").append("',")
                                        .append("'").append(SUBSTATION_NAME).append("',")
                                        .append("'").append(FEEDER_NO).append("',")
                                        .append("'").append(SUBTYPECD).append("',")
                                        .append("'").append(TYPE).append("',")
                                        .append("'").append(CABLE_SIZE).append("',")
                                        .append("'").append(PHASEDESIGNATION).append("',")
                                        .append("'").append(redlineaction).append("',")
                                        .append("'").append(lat).append("',")
                                        .append("'").append(log).append("',")
                                        .append("'").append(imgText).append("',")
                                        .append("'").append(userId).append("',")
                                        .append("'").append(sectionId).append("',")
                                        .append("'").append(featureName).append("')");
                                Log.e("Insertion query", "Insertion query" + query);
                                sqlHandler.executeQuery(query.toString());
                                sqlHandler.closeDataBaseConnection();
                            }

                        }
                    }
                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.alert);
                    builder.setMessage("Internal Server error ,failed to get features  ")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things
                                    }
                                });
                    AlertDialog alert = builder.create();
                    alert.show();

                    dialog.dismiss();
                    dialog.setMessage("Internal server error");

                }
                */

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

}
