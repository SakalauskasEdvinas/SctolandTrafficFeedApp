package com.gcu.mobilecoursework.asynctasks;

import com.gcu.mobilecoursework.model.TrafficFeedModel;

import java.util.List;
// By Edvinas Sakalauskas - S1627176

public interface OnTrafficDateRetrievedCallback {
    void processData(List<TrafficFeedModel> trafficFeed);
}
