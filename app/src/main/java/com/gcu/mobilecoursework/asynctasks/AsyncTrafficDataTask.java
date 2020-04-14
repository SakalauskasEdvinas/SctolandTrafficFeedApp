package com.gcu.mobilecoursework.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.gcu.mobilecoursework.BuildConfig;
import com.gcu.mobilecoursework.helper.TrafficXMLParser;
import com.gcu.mobilecoursework.model.RoadworkType;
import com.gcu.mobilecoursework.model.TrafficFeedModel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
// By Edvinas Sakalauskas - S1627176
public class AsyncTrafficDataTask extends AsyncTask<Void, Integer, List<TrafficFeedModel>> {

    private WeakReference<ProgressBar> progressBar;
    private String trafficMode;


    private OnTrafficDateRetrievedCallback callback = null;
    private OnPreExecuteCallback preExecuteCallback = null;


    public AsyncTrafficDataTask(OnTrafficDateRetrievedCallback callback, String trafficMode, ProgressBar progressBar) {
        this.callback = callback;
        this.trafficMode = trafficMode;
        this.progressBar = new WeakReference<>(progressBar);

    }

    public AsyncTrafficDataTask(OnTrafficDateRetrievedCallback callback, OnPreExecuteCallback preExecuteCallback, String trafficMode, ProgressBar progressBar) {
        this.callback = callback;
        this.trafficMode = trafficMode;
        this.progressBar = new WeakReference<>(progressBar);
        this.preExecuteCallback = preExecuteCallback;

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.get().setVisibility(View.VISIBLE);
        if (preExecuteCallback != null) {
            preExecuteCallback.onTrafficDataPreExecute();
        }

    }

    @Override
    protected List<TrafficFeedModel> doInBackground(Void... args0) {

        List<TrafficFeedModel> trafficFeedList = new ArrayList<>();

        if (trafficMode.equals("ALL")) {
            trafficFeedList.addAll(getSingleTrafficFeed(trafficFeedList, BuildConfig.PLANNED_ROADWORKS));
            trafficFeedList.addAll(getSingleTrafficFeed(trafficFeedList, BuildConfig.ROADWORKS));
            trafficFeedList.addAll(getSingleTrafficFeed(trafficFeedList, BuildConfig.CURRENT_INCIDENTS));

        } else {
            trafficFeedList = getSingleTrafficFeed(trafficFeedList, trafficMode);

        }

        return trafficFeedList;

    }

    private List<TrafficFeedModel> getSingleTrafficFeed(List<TrafficFeedModel> trafficFeedList, String trafficType) {
        try {
            URL url = new URL(trafficType);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            InputStream stream = connection.getInputStream();
            TrafficXMLParser trafficXMLParser;

            RoadworkType roadworkType = null;
            switch (trafficType) {
                case BuildConfig.PLANNED_ROADWORKS:
                    roadworkType = RoadworkType.PLANNED_ROADWORK;
                    break;
                case BuildConfig.ROADWORKS:
                    roadworkType = RoadworkType.ROADWORK;
                    break;
                case BuildConfig.CURRENT_INCIDENTS:
                    roadworkType = RoadworkType.INCIDENT;
                    break;
            }

            trafficXMLParser = new TrafficXMLParser(roadworkType);


            trafficFeedList = trafficXMLParser.parseXML(stream);


        } catch (IOException e) {
            Log.e("AsyncTask", "async task failed");

        }
        return trafficFeedList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.get().setProgress(values[0]);
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(List<TrafficFeedModel> trafficFeed) {
        super.onPostExecute(trafficFeed);
        callback.processData(trafficFeed);


    }


}

