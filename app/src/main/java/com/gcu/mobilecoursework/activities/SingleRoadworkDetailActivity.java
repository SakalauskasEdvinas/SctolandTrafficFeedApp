package com.gcu.mobilecoursework.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gcu.mobilecoursework.R;
import com.gcu.mobilecoursework.helper.MarkerClusterItem;
import com.gcu.mobilecoursework.model.TrafficFeedModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

// By Edvinas Sakalauskas - S1627176
public class SingleRoadworkDetailActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private TrafficFeedModel trafficFeedModel;
    private ClusterManager<MarkerClusterItem> clusterManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.trafficFeedModel = (TrafficFeedModel) getIntent().getSerializableExtra("trafficFeedModel");

        TextView title = findViewById(R.id.title_full);
        TextView type = findViewById(R.id.incident_type);
        TextView description = findViewById(R.id.description_full);
        TextView comments = findViewById(R.id.comments_full);
        TextView author = findViewById(R.id.author_full);
        TextView startDate = findViewById(R.id.startDate_full);
        TextView endDate = findViewById(R.id.endDate_full);


        if (trafficFeedModel != null) {
            title.setText(trafficFeedModel.getTitle());
            type.setText(getString(R.string.incident_type, trafficFeedModel.getRoadworkType().label));
            description.setText(trafficFeedModel.getDescription());
            comments.setText(trafficFeedModel.getComments());
            author.setText(trafficFeedModel.getAuthor());
            if (trafficFeedModel.getStartDate() != null) {
                startDate.setText(getString(R.string.start_date, trafficFeedModel.getStartDateString()));
                endDate.setText(getString(R.string.end_date, trafficFeedModel.getEndDateString()));
            }
        }



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapSingle);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.searcher);
        if (item != null)
            item.setVisible(false);
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        clusterManager = new ClusterManager<>(this, googleMap);
        setRenderer(this, googleMap, clusterManager);

        Double[] coordinatesArray = trafficFeedModel.getCoordinates();
        LatLng coordinates = new LatLng(coordinatesArray[0], coordinatesArray[1]);

        MarkerClusterItem markerClusterItem = new MarkerClusterItem(coordinates, trafficFeedModel.getTitle());
        markerClusterItem.setDataType(trafficFeedModel.getRoadworkType());

        clusterManager.addItem(markerClusterItem);
        clusterManager.cluster();
        gMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates,
                16));


    }

    @Override
    protected int getLayoutResource() {
        return R.layout.traffic_single_item_view_activity;
    }
}
