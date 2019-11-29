package com.nsg.nsgmapslibrary.database.dto;


import com.nsg.nsgmapslibrary.database.db.DatabaseColumn;

import java.util.ArrayList;

/**
 * Created by sailaja.ch on 03/09/2019
 */
public class EdgeDataT {
    private Integer sid;
    private Integer edgeNo;
    private String startPoint;
    private String endPoint;
    private String message;
    private String status;
    private String totaldistance;
    private String latitute;
    private String longitude;
    private String timeDuration;
    private String geometryType;
    private String geometry;
    private String geometryText;
    private String allPoints;
    private String distanceInVertex;
    private String positionMarkingPoint;

    public static ArrayList<DatabaseColumn> MAPPING = new ArrayList<DatabaseColumn>();
    public static String TABLE_NAME = "EdgeDataT";    static{

        MAPPING.add(new DatabaseColumn("sid", "setSid",true,true,false,"int"));
        MAPPING.add(new DatabaseColumn("edgeNo", "setEdgeNo",false,false,true,"int"));
        MAPPING.add(new DatabaseColumn("startPoint", "setStartPoint",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("endPoint", "setEndPoint",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("allPoints", "setAllPoints",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("distanceInVertex", "setDistanceInVertex",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("geometryText", "setGeometryText",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("positionMarkingPoint", "setPositionMarkingPoint",false,false,true,"text"));

    }
    public EdgeDataT(){}
    public EdgeDataT( String startPoint , String endPoint,String positionMarkingPoint,String geometryText,String distanceInVertex ){

        this.startPoint=startPoint;
        this.endPoint=endPoint;
        this.positionMarkingPoint=positionMarkingPoint;
        this.distanceInVertex=distanceInVertex;
        this.geometryText=geometryText;
    }
    public EdgeDataT(Integer edgeNo,String allPoints,String geometryText ){

        this.edgeNo=edgeNo;
        this.allPoints=allPoints;
        this.geometryText=geometryText;
    }

    public EdgeDataT(String distanceInVertex,String positionMarkingPoint,String geometryText ){

        this.distanceInVertex=distanceInVertex;
        this.positionMarkingPoint=positionMarkingPoint;
        this.geometryText=geometryText;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getEdgeNo() {
        return edgeNo;
    }

    public void setEdgeNo(Integer edgeNo) {
        this.edgeNo = edgeNo;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getAllPoints() {
        return allPoints;
    }

    public void setAllPoints(String allPoints) {
        this.allPoints = allPoints;
    }

    public String getDistanceInVertex() {
        return distanceInVertex;
    }

    public void setDistanceInVertex(String distanceInVertex) {
        this.distanceInVertex = distanceInVertex;
    }

    public String getGeometryText() {
        return geometryText;
    }

    public void setGeometryText(String geometryText) {
        this.geometryText = geometryText;
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

    public String getPositionMarkingPoint() {
        return positionMarkingPoint;
    }

    public void setPositionMarkingPoint(String positionMarkingPoint) {
        this.positionMarkingPoint = positionMarkingPoint;
    }
}
