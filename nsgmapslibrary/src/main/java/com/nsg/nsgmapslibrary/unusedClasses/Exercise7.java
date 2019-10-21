package com.nsg.nsgmapslibrary.unusedClasses;

import java.util.Scanner;

public class Exercise7 {
    public static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);

        float timeSeconds;
        float mps,kph, mph;

        System.out.print("Input distance in meters: ");
        float distance = scanner.nextFloat();

        System.out.print("Input hour: ");
        float hr = scanner.nextFloat();

        System.out.print("Input minutes: ");
        float min = scanner.nextFloat();

        System.out.print("Input seconds: ");
        float sec = scanner.nextFloat();

        timeSeconds = (hr*3600) + (min*60) + sec;
        mps = distance / timeSeconds;
        kph = ( distance/1000.0f ) / ( timeSeconds/3600.0f );
        mph = kph / 1.609f;

        System.out.println("Your speed in meters/second is "+mps);
        System.out.println("Your speed in km/h is "+kph);
        System.out.println("Your speed in miles/h is "+mph);


        scanner.close();
    }
}
//Get Routing data Correctly
// CarMoveAnim.startcarAnimation(marker,mMap, SourcePosition,DestinationPosition,5000,callback);

//  DownloadFeaturesFromServer downloadTask = new DownloadFeaturesFromServer();
//  downloadTask.execute();
                /*
                if(enteredMode==1){
                    addGPSMarkers();
                  // addEnteredModePath();
                    /*

                    getLatLngPoints();
                    for(int i=0 ;i<LatLngDataArray.size();i++) {
                        currentGpsPosition = LatLngDataArray.get(i);
                        points=new ArrayList();
                        Log.e(" currentGpsPosition ","currentGpsPosition -----------------"+ currentGpsPosition);
                        points.add(LatLngDataArray.get(i));
                        //currentGpsPosition = new LatLng(24.988851, 55.062080);
                        verifyRouteDeviation(routeDeviationDistance);
                    }
                    */

//55.061301,24.989313,55.059944,24.988134
                /* NEED TO VERIFY WITH CORRECT ROUTE..
                  if(edgeDataList!=null && edgeDataList.size()>0) {
                    // Log.e("Perpedicular PointsList","Perpedicular PointsList -----------------"+ edgeDataList.size());
                    for (int ep = 0; ep < edgeDataList.size(); ep++) {
                        EdgeDataT edge = new EdgeDataT();
                        edge = edgeDataList.get(ep);

                        String edges = edge.getEdgeNo();
                        String srtPosition = edge.getStartPoint();
                        String endPt = edge.getEndPoint();
                        srtPosition = srtPosition.replace("[", "");
                        srtPosition = srtPosition.replace("]", "");
                        endPt = endPt.replace("[", "");
                        endPt = endPt.replace("]", "");
                        sb.append(srtPosition).append(";").append(endPt).append(":");
                    }
                }
                  getLatLngPoints();
                    for (int dp = 0; dp < LatLngDataArray.size(); dp++) {
                        String srtPosition = "", endPt = "";
                        getAllEdgesData();
                        Log.e("Perpedicular PointsList", "Perpedicular PointsList -----------------" + edgeDataList.size());
                        EdgeDataT edge = new EdgeDataT();
                        edge = edgeDataList.get(dp);
                        String edges = edge.getEdgeNo();
                        srtPosition = edge.getStartPoint();
                        endPt = edge.getEndPoint();
                        srtPosition = srtPosition.replace("[", "");
                        String srtPositionFinal = srtPosition.replace("]", "");
                        String[] firstPoint = srtPositionFinal.split(",");
                        double Lat1 = Double.parseDouble(firstPoint[0]);
                        double Longi1 = Double.parseDouble(firstPoint[1]);

                        endPt = endPt.replace("[", "");
                        String endPtFinal = endPt.replace("]", "");
                        String[] EndPoint = endPtFinal.split(",");
                        double Lat2 = Double.parseDouble(EndPoint[0]);
                        double Longi2 = Double.parseDouble(EndPoint[1]);

                        String nearestPoint = GenerateLinePoint( Lat1,Longi1,Lat2,Longi2,LatLngDataArray.get(dp).longitude, LatLngDataArray.get(dp).latitude);
                        Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
                        String[] nearestDataStr = nearestPoint.split(",");
                        double latitude = Double.parseDouble(nearestDataStr[0]);
                        double longitude = Double.parseDouble(nearestDataStr[1]);
                        mPositionMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(longitude, latitude))
                                .title("currentLocation")
                                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.purple_car)));
                        // CarMoveAnim animCar=new CarMoveAnim();
                        // animCar.startcarAnimation(mPositionMarker,mMap,SourcePosition,DestinationPosition,1000,callback);
                    }


                }else if(enteredMode==2) {
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
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onMyLocationChange(Location location) {
                            if (mPositionMarker != null) {
                                mPositionMarker.remove();
                            }
                            double lat = location.getLatitude();
                            double longi = location.getLongitude();

                            currentGpsPosition = new LatLng(location.getLatitude(), location.getLongitude());
                            mPositionMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .title("currentLocation"));
                           // .icon(bitmapDescriptorFromVector(getContext(), R.drawable.purple_car))

                            CameraPosition googlePlex = CameraPosition.builder()
                                    .target(currentGpsPosition)
                                    .zoom(18)
                                    .tilt(45)
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
                              if(mMap!=null) {
                               //   verifyRouteDeviation(routeDeviationDistance);
                              }


                        }
                    });
                }
                */
