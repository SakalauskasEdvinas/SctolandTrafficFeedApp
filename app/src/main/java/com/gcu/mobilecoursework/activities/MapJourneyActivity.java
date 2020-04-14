package com.gcu.mobilecoursework.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.gcu.mobilecoursework.R;
import com.gcu.mobilecoursework.asynctasks.AsyncTrafficDataTask;
import com.gcu.mobilecoursework.asynctasks.OnTrafficDateRetrievedCallback;
import com.gcu.mobilecoursework.helper.DateUtility;
import com.gcu.mobilecoursework.helper.MarkerClusterItem;
import com.gcu.mobilecoursework.model.TrafficFeedModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

// By Edvinas Sakalauskas - S1627176
public class MapJourneyActivity extends BaseActivity implements OnMapReadyCallback,
        OnTrafficDateRetrievedCallback, View.OnClickListener, GoogleMap.OnPolylineClickListener,
        DatePickerDialog.OnDateSetListener {

    List<LatLng> coordinatesList;
    private GoogleMap gMap;
    private ProgressBar progressBar;
    private List<TrafficFeedModel> trafficFeed;
    private ClusterManager<MarkerClusterItem> clusterManager;
    private GeoApiContext geoApiContext;
    private LatLng origin;
    private LatLng destination;
    private Polyline selectedPolyline;
    private TextView selectedTripDuration;
    private TextView disruptionCount;
    private Date dateSelected;
    private CardView journeyInfoView;
    private RelativeLayout journeySearchInputView;
    private FrameLayout mapFame;
    private Button dateSelectionButton;
    private DirectionsResult directionResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         dateSelectionButton = findViewById(R.id.dateSelection);



        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        this.mapFame = findViewById(R.id.mapFrame);
        this.journeySearchInputView = findViewById(R.id.inputArea);
        this.journeyInfoView = findViewById(R.id.journeyInfo);
        this.progressBar = findViewById(R.id.progressbar);
        this.selectedTripDuration = findViewById(R.id.tripDuration);
        this.disruptionCount = findViewById(R.id.numberOfDisruptions);
        Button planJourneyButton = findViewById(R.id.planJourneyButton);


        selectedTripDuration.setVisibility(View.GONE);
        disruptionCount.setVisibility(View.GONE);


        planJourneyButton.setOnClickListener(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view_map_2);
        mapFragment.getMapAsync(this);


        new AsyncTrafficDataTask(this, "ALL", progressBar).execute();

        //set up location search fields
        AutocompleteSupportFragment fromLocationFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.fromLocation);

        AutocompleteSupportFragment toLocationFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.toLocation);


        toLocationFragment.setHint("Travelling to?");
        fromLocationFragment.setHint("Where from?");
        addPlaceSelectedListener(toLocationFragment, true);
        addPlaceSelectedListener(fromLocationFragment, false);

        toLocationFragment.getView().findViewById(R.id.places_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toLocationFragment.setText(null);


                    }
                });

        fromLocationFragment.getView().findViewById(R.id.places_autocomplete_clear_button)
                .setOnClickListener(view -> {
                    fromLocationFragment.setText(null);

                });


    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("origin", origin);
        outState.putParcelable("destination", destination);
        outState.putSerializable("selectedDate", dateSelected);
        outState.putSerializable("directionResult", directionResult);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        origin = (LatLng) savedInstanceState.getParcelable("origin");
        destination = (LatLng) savedInstanceState.getParcelable("destination");
        dateSelected = (Date) savedInstanceState.getSerializable("selectedDate");

        if(dateSelected !=null) {
            dateSelectionButton.setText(getString(R.string.date_set_format_journey, DateUtility.dateToString(dateSelected)));

        }
        this.directionResult = (DirectionsResult) savedInstanceState.getSerializable("directionResult");
        if (directionResult !=null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            this.mapFame.setVisibility(View.VISIBLE);
            this.journeySearchInputView.setVisibility(View.GONE);
            this.journeyInfoView.setVisibility(View.VISIBLE);
        }
    }

    private void addPlaceSelectedListener(AutocompleteSupportFragment placeSearchFragment, Boolean isOrigin) {

        placeSearchFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        placeSearchFragment.setCountries("uk");
        placeSearchFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (isOrigin) {
                    origin = place.getLatLng();
                } else {
                    destination = place.getLatLng();
                }
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());



            }
            @Override
            public void onError(Status status) {

                Log.e("TAG", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        dateSelected = calendar.getTime();
        datePickerDialogFragment.updateDate(calendar);

        dateSelectionButton.setText(getString(R.string.date_set_format_journey, DateUtility.dateToString(calendar.getTime())));
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
        this.progressBar.setVisibility(View.GONE);

        this.gMap = googleMap;

        LatLng scotlandCoordinates = new LatLng(56.740674, -4.070435);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(scotlandCoordinates, 7));

        clusterManager = new ClusterManager<>(this, googleMap);
        setRenderer(this, googleMap, clusterManager);



        clusterManager.setOnClusterClickListener(null);
        clusterManager.setOnClusterItemClickListener(markerClusterItem -> {
            Intent myIntent = new Intent(getApplicationContext(), SingleRoadworkDetailActivity.class);
            myIntent.putExtra("trafficFeedModel", trafficFeed.get(markerClusterItem.getItemIndexInArrayList()));
            startActivity(myIntent);
            return false;
        });

        googleMap.setOnPolylineClickListener(this);
    }


    @Override
    public void processData(List<TrafficFeedModel> data) {

        this.trafficFeed = data;

        coordinatesList = new ArrayList<>();


        gMap.setOnCameraIdleListener(clusterManager);
        gMap.setOnMarkerClickListener(clusterManager);
        if(directionResult !=null) {
            addPolyLinesToMap(this.directionResult);

        }


    }


    //retrieves directions from point a to b
    private void getDirections() {
        if (origin == null || destination == null || dateSelected == null) {

            Toast toast = Toast.makeText(getApplicationContext(), "You must fill in the fields above, to plan a journey ", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);

            toast.show();
            return;
        }

        //clear map
        gMap.clear();
        clusterManager.clearItems();

        //calculate directions
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);
        directions.mode(TravelMode.DRIVING);
        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        origin.latitude,
                        origin.longitude
                )
        );

        directions.destination(
                new com.google.maps.model.LatLng(
                        destination.latitude,
                        destination.longitude
                )
        );
        directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                //add routes to map when finished
                addPolyLinesToMap(result);

            }

            @Override
            public void onFailure(Throwable e) {
                throw new UnsupportedOperationException(e);
            }

        });
    }


    private void addPolyLinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d("TAG", "run: result routes: " + result.routes.length);
            directionResult = result;

            for (DirectionsRoute route : result.routes) {
                Log.d("TAG", "run: leg: " + route.legs[0].toString());
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                List<LatLng> newDecodedPath = new ArrayList<>();

                for (com.google.maps.model.LatLng latLng : decodedPath) {

                    newDecodedPath.add(new LatLng(
                            latLng.lat,
                            latLng.lng
                    ));
                }

                //create polyline ( route on map)
                Polyline polyline = gMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                polyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.DarkGray));
                polyline.setClickable(true);


                HashMap<String, String> polyInfo = new HashMap<>();
                int disruptionCounter = 0;

                disruptionCounter = createMarkers(newDecodedPath);

                polyInfo.put("disruptionCount", Integer.toString(disruptionCounter));
                polyInfo.put("duration", route.legs[0].duration.humanReadable);
                polyline.setTag(polyInfo);

                onPolylineClick(polyline);


            }
            clusterManager.cluster();


        });
    }

    //creates markers if a roadwork is on the directions path
    private int createMarkers(List<LatLng> newDecodedPath) {
        int count = 0;
        ArrayList<MarkerClusterItem> markerClusterItems = new ArrayList<>();
        for (int i = 0; i < trafficFeed.size(); i++) {
            TrafficFeedModel trafficFeedModel = trafficFeed.get(i);
            LatLng cords = new LatLng(trafficFeedModel.getCoordinates()[0], trafficFeedModel.getCoordinates()[1]);
            MarkerClusterItem markerClusterItem = new MarkerClusterItem(cords, trafficFeedModel.getTitle());
            markerClusterItem.setDataType(trafficFeedModel.getRoadworkType());
            markerClusterItem.setItemIndexInArrayList(i);
            if (trafficFeedModel.checkIfIsDuringADate(this.dateSelected)) {
                if (PolyUtil.isLocationOnPath(cords, newDecodedPath, false, 100)) {
                    markerClusterItems.add(markerClusterItem);
                    count++;

                }
            }


        }
        clusterManager.addItems(markerClusterItems);
        return count;
    }

    @Override
    public void onClick(View v) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && origin !=null && destination !=null) {
            this.mapFame.setVisibility(View.VISIBLE);
            this.journeySearchInputView.setVisibility(View.GONE);
            this.journeyInfoView.setVisibility(View.VISIBLE);
        }

        this.getDirections();

    }

    public void onBackClick(View view) {
        this.mapFame.setVisibility(View.GONE);
        this.journeySearchInputView.setVisibility(View.VISIBLE);
        this.journeyInfoView.setVisibility(View.GONE);


    }


    @Override
    public void onPolylineClick(Polyline polyline) {
        //clear previous selected route if exists
        if (selectedPolyline != null) {
            selectedPolyline.setZIndex(0);
            selectedPolyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.DarkGray));
        }

        selectedPolyline = polyline;
        selectedPolyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.DarkBlue));
        selectedPolyline.setZIndex(1);
        selectedTripDuration.setVisibility(View.VISIBLE);
        disruptionCount.setVisibility(View.VISIBLE);

        HashMap<String, String> polyInfo = (HashMap<String, String>) selectedPolyline.getTag();
        selectedTripDuration.setText(getString(R.string.route_duration, polyInfo.get("duration")));
        disruptionCount.setText(getString(R.string.number_of_disruptions, polyInfo.get("disruptionCount")));
        zoomOnRoute(selectedPolyline.getPoints());

    }

    public void zoomOnRoute(List<LatLng> latLngs) {
        if (gMap == null || latLngs == null || latLngs.size() <= 0) {

            Log.e("LatLngs empty", "The coordinate array is null or empty");
            return;
        }
        Log.i("LatLngs not empty", "The coordinate array has " + latLngs.size());
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLngs) {
            builder.include(latLng);
        }

        LatLngBounds latLngBounds = builder.build();


        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 120),
                600, null);

    }


    @Override
    protected int getLayoutResource() {
        return R.layout.map_view_journey;
    }


}
