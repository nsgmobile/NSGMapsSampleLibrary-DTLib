package com.nsg.nsgmapslibrary.database.db;

/**
 * Created by sailaja.ch on 03/09/2019
 */
public class DatabaseColumn {
    private String columnName;
    private String attributeName;
    private boolean isNull;
    private String columnType;
    private boolean isInt;
    private boolean isAuto;
    private boolean isPrimary;

    public DatabaseColumn(String columnName, String attributeName, boolean isAuto, boolean isPrimary , boolean isNull, String columnType){
        this.columnName=columnName;
        this.attributeName=attributeName;
        this.isNull=isNull;
        this.isAuto=isAuto;
        this.columnType=columnType;
        this.isInt=columnType.equalsIgnoreCase("int");
        this.isPrimary=isPrimary;

    }

    public boolean isAuto() {
        return isAuto;
    }

    public boolean isInt() {
        return isInt;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isNull() {
        return isNull;
    }

    public String getColumnType() {
        return columnType;
    }

    public boolean isPrimary() {return isPrimary; }
}