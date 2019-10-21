package com.nsg.nsgmapslibrary.SupportClasses;

import com.google.android.gms.maps.model.LatLng;

public class ConvertGeometry {
    StringBuilder sb=new StringBuilder();
    public String toMarcator (double lat, double longitude){
        double x =longitude * 20037508.34 / 180;
        double y =Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
        y =  y * 20037508.34 / 180;

       sb.append(x).append("").append(y);
        return sb.toString();
    }

    public LatLng inverseMercator (double x, double y){
        double longitude =  (x / 20037508.34) * 180;
        double lat = (y / 20037508.34) * 180;
        lat = 180/Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);
        LatLng geometryInverseMarcator=new LatLng(lat,longitude);
        return geometryInverseMarcator;
    }

}
