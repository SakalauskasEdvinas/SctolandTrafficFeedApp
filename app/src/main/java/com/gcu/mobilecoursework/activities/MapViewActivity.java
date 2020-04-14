package com.gcu.mobilecoursework.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;

import com.gcu.mobilecoursework.R;
import com.gcu.mobilecoursework.SpinnerFragment;
import com.gcu.mobilecoursework.asynctasks.OnTrafficDateRetrievedCallback;
import com.gcu.mobilecoursework.helper.DateUtility;
import com.gcu.mobilecoursework.helper.MarkerClusterItem;
import com.gcu.mobilecoursework.model.TrafficFeedModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// By Edvinas Sakalauskas - S1627176
public class MapViewActivity extends BaseActivity implements OnMapReadyCallback,
        OnTrafficDateRetrievedCallback, DatePickerDialog.OnDateSetListener, SearchView.OnQueryTextListener {

    private GoogleMap googleMap;
    private ProgressBar progressBar;
    private List<TrafficFeedModel> trafficFeed;

    private SupportMapFragment mapFragment;
    private ClusterManager<MarkerClusterItem> clusterManager;
    private GeoApiContext geoApiContext;
    private SpinnerFragment spinnerFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.progressBar = findViewById(R.id.progressbar);
        this.progressBar.setVisibility(View.VISIBLE);


        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view_map);
        mapFragment.getMapAsync(this);
        getSupportFragmentManager().beginTransaction().hide(mapFragment).commit();

        FragmentManager fragmentManager = getSupportFragmentManager();

        this.spinnerFragment = (SpinnerFragment) fragmentManager.findFragmentById(R.id.spinner_fragment);
        this.spinnerFragment.addItemSelectListenerToSpinner(progressBar, this, null);

        Calendar calendar = Calendar.getInstance();
        dateSelected = calendar.getTime();
        setDefaultDateChoice(calendar);

    }

    private void setDefaultDateChoice(Calendar calendar) {
        Button button = findViewById(R.id.dateSelectionListMapButton);
        button.setText(getString(R.string.date_set_format_journey, DateUtility.dateToString(calendar.getTime())));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.searcher);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);


        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        clusterManager = new ClusterManager<>(this, googleMap);

        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MarkerClusterItem>() {
            @Override
            public boolean onClusterItemClick(MarkerClusterItem markerClusterItem) {
                Intent myIntent = new Intent(getApplicationContext(), SingleRoadworkDetailActivity.class);
                myIntent.putExtra("trafficFeedModel", trafficFeed.get(markerClusterItem.getItemIndexInArrayList()));
                startActivity(myIntent);
                return false;
            }
        });


        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }

    }


    @Override
    public void processData(List<TrafficFeedModel> data) {

        this.trafficFeed = data;
        this.googleMap.clear();
        this.clusterManager.clearItems();
        List<MarkerClusterItem> clusteritems = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            TrafficFeedModel trafficFeedModel = data.get(i);
            if (this.dateSelected != null) {
                if (!trafficFeedModel.checkIfIsDuringADate(dateSelected)) {
                    continue;
                }
            }
            Double[] coordinatesArray = trafficFeedModel.getCoordinates();
            LatLng coordinates = new LatLng(coordinatesArray[0], coordinatesArray[1]);

            MarkerClusterItem markerClusterItem = new MarkerClusterItem(coordinates, trafficFeedModel.getTitle());
            markerClusterItem.setDataType(trafficFeedModel.getRoadworkType());
            markerClusterItem.setItemIndexInArrayList(i);

            clusteritems.add(markerClusterItem);


        }

        clusterManager.addItems(clusteritems);


        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        LatLng scotlandCordinates = new LatLng(56.740674, -4.070435);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(scotlandCordinates, 7));
        setRenderer(this, googleMap, clusterManager);
        clusterManager.cluster();
        getSupportFragmentManager().beginTransaction().show(mapFragment).commit();
        this.progressBar.setVisibility(View.GONE);


    }


    @Override
    protected int getLayoutResource() {
        return R.layout.map_view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
            datePickerDialogFragment.updateDate(calendar);


        this.dateSelected = calendar.getTime();
        setDefaultDateChoice(calendar);
        processData(trafficFeed);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        filterMarkers(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        filterMarkers(query);
        return false;
    }

    private void filterMarkers(String query) {
        List<MarkerClusterItem> markerClusterItems = new ArrayList<>();
        if (!query.isEmpty()) {
            for (TrafficFeedModel tfm : trafficFeed) {
                if (tfm.matchesTitleOrDescription(query) && tfm.checkIfIsDuringADate(dateSelected)) {
                    MarkerClusterItem markerClusterItem =
                            new MarkerClusterItem(new LatLng(tfm.getCoordinates()[0], tfm.getCoordinates()[1]), tfm.getTitle());
                    markerClusterItem.setDataType(tfm.getRoadworkType());
                    markerClusterItems.add(markerClusterItem);

                }
            }
            clusterManager.clearItems();
            clusterManager.addItems(markerClusterItems);
            clusterManager.cluster();
        }
    }
}
