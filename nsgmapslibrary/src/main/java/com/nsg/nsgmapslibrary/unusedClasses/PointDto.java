package com.nsg.nsgmapslibrary.unusedClasses;

import com.nsg.nsgmapslibrary.interfaces.IPoint;

public class PointDto implements IPoint {

    public String geometry;
    public String remarks;
    public String createdDate_TX;
    public String updatedDate_TX;
    public String imageLocation;
    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreatedDate_TX() {
        return createdDate_TX;
    }

    public void setCreatedDate_TX(String createdDate_TX) {
        this.createdDate_TX = createdDate_TX;
    }

    public String getUpdatedDate_TX() {
        return updatedDate_TX;
    }

    public void setUpdatedDate_TX(String updatedDate_TX) {
        this.updatedDate_TX = updatedDate_TX;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public PointDto(){}
    public PointDto(String geometry, String remarks, String createdDate_TX, String updatedDate_TX){
        this.geometry=geometry;
        this.remarks=remarks;
        this.createdDate_TX=createdDate_TX;
        this.updatedDate_TX=updatedDate_TX;
    }

    @Override
    public String addPointFeature(PointDto dto) {
        dto=new PointDto();
        addPointFeature(dto);
        String responseAdd="Point Feature Added";
        System.out.print("Point Feature Added --- "+ dto);
        return responseAdd;
    }

    @Override
    public String showPointFeature(PointDto dto) {
        dto.getGeometry();
        dto.getCreatedDate_TX();
        dto.getRemarks();
        String responseShow="Point Feature Diaplayed";
        System.out.print("Point Feature Diaplayed --- "+ dto);
        return responseShow;
    }


}
