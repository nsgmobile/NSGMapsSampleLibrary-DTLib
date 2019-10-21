package com.nsg.nsgmapslibrary.SupportClasses;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ETACalclator {
    private double distance;
    private double time;
    private double speed;
    private LatLng srcLat;
    private LatLng srcLng;
    private double desLat;
    private double desLng;


    public double cal_speed(double distance, double time) {
       // System.out.print("\n Distance(km) : " + distance);
       // System.out.print("\n Time(hr) : " + time);

        return distance / time;
    }

    // Function to calculate distance traveled
    public double cal_dis(double speed, double time) {
       // System.out.print("\n Time(hr) : " + time);
       // System.out.print("\n Speed(km / hr) : " + speed);
        return speed * time;
    }

    // Function to calculate time taken
    public  double cal_time(double dist, double speed) {
       // System.out.print("\n Distance(km) : " + dist);
      //  System.out.print("\n Speed(km / hr) : " + speed);
        double timeCalculated=dist/speed;
        return timeCalculated;
    }

    public static String convertTime(double time){
        double hrs,min,sec;
        hrs=time*60;
        min=hrs*60;
        sec=min*60;
        BigDecimal bd = new BigDecimal(hrs).setScale(0, RoundingMode.HALF_EVEN);
        hrs = bd.doubleValue();
        BigDecimal bd1 = new BigDecimal(min).setScale(0, RoundingMode.HALF_EVEN);
        min = bd1.doubleValue();
        BigDecimal bd2 = new BigDecimal(sec).setScale(0, RoundingMode.HALF_EVEN);
        sec = bd2.doubleValue();
        String resultTime = hrs +"hrs" +min +"min" +sec+"sec";


        // 0.202hr×60min1hr=12.12min
        // 12.12min=12min+0.12min
        //  0.12min×60s1min=7.2s
        //  7.2s≈7s
        //  Combining: 00:12:07
        return resultTime;
    }
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    double distanceBetweenTwoPoint(double srcLat, double srcLng, double desLat, double desLng) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(desLat - srcLat);
        double dLng = Math.toRadians(desLng - srcLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(srcLat))
                * Math.cos(Math.toRadians(desLat)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        double meterConversion = 1609;

        return (int) (dist * meterConversion);
    }

}
