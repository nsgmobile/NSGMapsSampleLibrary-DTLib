package com.nsg.nsgmapslibrary.unusedClasses;

import android.graphics.drawable.GradientDrawable;

import com.nsg.nsgmapslibrary.enums.ENSGShapes;
import com.nsg.nsgmapslibrary.interfaces.INSGShape;

public class NSGShape extends GradientDrawable implements INSGShape {
    private ENSGShapes shape;
    private int width;
    private int height;
    private int borderColor;
    private int fillColor;
    private int stroke;


    @Override
    public void setShape(ENSGShapes shape) {
        this.shape=shape;
        if(this.shape.equals(ENSGShapes.CIRCLE)){
            super.setShape(OVAL);
        }else if(this.shape.equals(ENSGShapes.POLYLINE)){
            super.setShape(LINE);
        }else if(this.shape.equals(ENSGShapes.TRIANGLE)){
            super.setShape(RING);
        }
    }

    @Override
    public ENSGShapes getShare() {
        return  this.shape;
    }

    @Override
    public void setBorder(int borderColor) {
        this.borderColor=borderColor;

    }

    @Override
    public void setFillColor(int fillColor) {
        this.fillColor=fillColor;

    }
    public NSGShape(ENSGShapes shape,int width,int height,int borderColor,int fillColor,int stroke){
        this.shape=shape;
        this.height=height;
        this.width=width;
        this.borderColor=borderColor;
        this.fillColor=fillColor;
        this.stroke=stroke;
    }
    public NSGShape(ENSGShapes shapes, int width, int heght, int borderColor, int fillColor){
        this(shapes,width,heght,borderColor,fillColor,2);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public int getFillColor() {
        return fillColor;
    }

    public int getStroke() {
        return stroke;
    }

    public void setStroke(int stroke) {
        this.stroke = stroke;
    }
}
