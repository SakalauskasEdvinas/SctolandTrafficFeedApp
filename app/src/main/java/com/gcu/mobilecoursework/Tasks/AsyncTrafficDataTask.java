package com.gcu.mobilecoursework.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.gcu.mobilecoursework.BuildConfig;
import com.gcu.mobilecoursework.Helper.TrafficXMLParser;
import com.gcu.mobilecoursework.Model.TrafficFeedModel;
import com.gcu.mobilecoursework.MyRecyclerViewAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AsyncTrafficDataTask extends AsyncTask<Void, Integer, List<TrafficFeedModel>> {
    // Traffic Scotland URLs

    private WeakReference<ProgressBar> progressBar;
    private MyRecyclerViewAdapter recyclerViewAdapter;
    private String traffic_url;


    public AsyncTrafficDataTask(MyRecyclerViewAdapter recyclerViewAdapter, ProgressBar progressBar, String traffic_url) {
        super();
        this.traffic_url = traffic_url;
        this.progressBar = new WeakReference<>(progressBar);
        this.recyclerViewAdapter = recyclerViewAdapter;
    }

    @Override
    protected List<TrafficFeedModel> doInBackground(Void... args0) {
        List<TrafficFeedModel> trafficFeedList = new ArrayList<>();
        try {
            URL url = new URL(traffic_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            InputStream stream = connection.getInputStream();

            TrafficXMLParser trafficXMLParser = new TrafficXMLParser();
            trafficFeedList = trafficXMLParser.parseXML(stream);


        }catch (IOException e) {
            Log.e("AsyncTask", "async task failed");

        }

        return trafficFeedList;

    }

    @Override
    protected void onPostExecute(List<TrafficFeedModel> trafficFeed) {
        super.onPostExecute(trafficFeed);
        this.recyclerViewAdapter.setRecyclerViewData(trafficFeed);
        this.recyclerViewAdapter.notifyDataSetChanged();
        System.out.println(trafficFeed);
    }


}

