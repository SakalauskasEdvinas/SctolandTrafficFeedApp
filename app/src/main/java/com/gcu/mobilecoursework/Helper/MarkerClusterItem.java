package com.gcu.mobilecoursework.helper;

import com.gcu.mobilecoursework.model.RoadworkType;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

// By Edvinas Sakalauskas - S1627176
public class MarkerClusterItem implements ClusterItem, Serializable {

    private int itemIndexInArrayList;
    private LatLng latLng;
    private String title;
    private RoadworkType roadworkType;


    public MarkerClusterItem(LatLng latLng, String title) {
        this.latLng = latLng;
        this.title = title;
    }


    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public void setDataType(RoadworkType roadworkType) {
        this.roadworkType = roadworkType;
    }

    public RoadworkType getRoadworkType() {
        return roadworkType;
    }

    public int getItemIndexInArrayList() {
        return itemIndexInArrayList;
    }

    public void setItemIndexInArrayList(int itemIndexInArrayList) {
        this.itemIndexInArrayList = itemIndexInArrayList;
    }
}
