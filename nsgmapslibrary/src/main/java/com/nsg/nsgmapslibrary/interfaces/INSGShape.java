package com.nsg.nsgmapslibrary.interfaces;

import com.nsg.nsgmapslibrary.enums.ENSGShapes;

public interface INSGShape {
    public void setShape(ENSGShapes shape);
    public ENSGShapes getShare();
    public void setBorder(int borderColor);
    public void setFillColor(int fillColor);
}
