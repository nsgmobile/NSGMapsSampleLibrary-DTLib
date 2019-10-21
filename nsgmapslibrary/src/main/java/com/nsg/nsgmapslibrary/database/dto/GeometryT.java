package com.nsg.nsgmapslibrary.database.dto;


import com.nsg.nsgmapslibrary.database.db.DatabaseColumn;

import java.util.ArrayList;

/**
 * Created by sailaja.ch on 18/9/2019.
 */


public class GeometryT {
    private Integer gId;
    private String ID;
    private String message;
    private String status;
    private String totaldistance;
    private String edgeNo;
    private String latitute;
    private String longitude;
    private String timeDuration;
    private String geometryType;
    private String geometry;

    public static ArrayList<DatabaseColumn> MAPPING = new ArrayList<DatabaseColumn>();
    public static String TABLE_NAME = "GEOMETRY_T";    static{

        MAPPING.add(new DatabaseColumn("gId", "setGId",true,true,false,"int"));
        MAPPING.add(new DatabaseColumn("ID", "setID",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("message", "setMessage",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("status", "setStatus",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("totaldistance", "setTotaldistance",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("edgeNo", "setEdgeNo",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("latitute", "setLatitute",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("longitude", "setLongitude",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("timeDuration", "setTimeDuration",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("geometryType", "setGeometryType",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("geometry", "setGeometry",false,false,true,"text"));

    }
    public GeometryT(){}
    public GeometryT(String ID, String message , String status, String totaldistance, String edgeNo, String latitute, String longitude, String timeDuration, String geometryType, String geometry){

        this.ID=ID;
        this.message=message;
        this.status=status;
        this.totaldistance=totaldistance;
        this.edgeNo=edgeNo;
        this.latitute=latitute;
        this.longitude=longitude;
        this.timeDuration=timeDuration;
        this.geometryType=geometryType;
        this.geometry=geometry;

    }

    public Integer getgId() {
        return gId;
    }

    public void setgId(Integer gId) {
        this.gId = gId;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotaldistance() {
        return totaldistance;
    }

    public void setTotaldistance(String totaldistance) {
        this.totaldistance = totaldistance;
    }

    public String getEdgeNo() {
        return edgeNo;
    }

    public void setEdgeNo(String edgeNo) {
        this.edgeNo = edgeNo;
    }

    public String getLatitute() {
        return latitute;
    }

    public void setLatitute(String latitute) {
        this.latitute = latitute;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        this.timeDuration = timeDuration;
    }

    public String getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(String geometryType) {
        this.geometryType = geometryType;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }
}
