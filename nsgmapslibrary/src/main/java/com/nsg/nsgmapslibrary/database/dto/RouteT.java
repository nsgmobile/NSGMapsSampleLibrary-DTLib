package com.nsg.nsgmapslibrary.database.dto;

import com.nsg.nsgmapslibrary.database.db.DatabaseColumn;

import java.util.ArrayList;

public class RouteT {
    private Integer sid;
    private String routeID;
    private String startNode;
    private String endNode;
    private String routeData;

    public static ArrayList<DatabaseColumn> MAPPING = new ArrayList<DatabaseColumn>();
    public static String TABLE_NAME = "Route_T";
    static{

        MAPPING.add(new DatabaseColumn("sid", "setSid",true,true,false,"int"));
        MAPPING.add(new DatabaseColumn("routeID", "setRouteID",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("startNode", "setStartNode",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("endPoint", "setEndNode",false,false,true,"text"));
        MAPPING.add(new DatabaseColumn("routeData", "setRouteData",false,false,true,"text"));

    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getStartNode() {
        return startNode;
    }

    public void setStartNode(String startNode) {
        this.startNode = startNode;
    }

    public String getEndNode() {
        return endNode;
    }

    public void setEndNode(String endNode) {
        this.endNode = endNode;
    }

    public String getRouteData() {
        return routeData;
    }

    public void setRouteData(String routeData) {
        this.routeData = routeData;
    }
}
