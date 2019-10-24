package com.nsg.nsgmapslibrary.database.dto;


import com.nsg.nsgmapslibrary.database.db.DatabaseColumn;

import java.util.ArrayList;

/**
 * Created by sailaja.ch on 03/09/2019
 */
public class EdgeDataT {
    private Integer sid;
    private String edgeNo;
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

    public static ArrayList<DatabaseColumn> MAPPING = new ArrayList<DatabaseColumn>();
    public static String TABLE_NAME = "EdgeDataT";    static{

        MAPPING.add(new DatabaseColumn("sid", "setSid",true,true,false,"int"));
        MAPPING.add(new DatabaseColumn("edgeNo", "setEdgeNo",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("startPoint", "setStartPoint",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("endPoint", "setEndPoint",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("allPoints", "setAllPoints",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("distanceInVertex", "setDistanceInVertex",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("geometryText", "setGeometryText",false,false,true,"text"));

    }
    public EdgeDataT(){}
    public EdgeDataT(String edgeNo, String startPoint , String endPoint,String allPoints,String distanceInVertex,String geometryText ){

        this.edgeNo=edgeNo;
        this.startPoint=startPoint;
        this.endPoint=endPoint;
        this.allPoints=allPoints;
        this.distanceInVertex=distanceInVertex;
        this.geometryText=geometryText;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public String getEdgeNo() {
        return edgeNo;
    }

    public void setEdgeNo(String edgeNo) {
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
}
