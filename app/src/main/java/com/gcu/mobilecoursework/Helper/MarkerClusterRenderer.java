package com.gcu.mobilecoursework.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gcu.mobilecoursework.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
// By Edvinas Sakalauskas - S1627176

public class MarkerClusterRenderer<T extends MarkerClusterItem> extends DefaultClusterRenderer<T> {

    private static final int MARKER_DIMENSION = 100;  // 2
    private final IconGenerator iconGenerator;
    private final ImageView markerImageView;

    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context);  // 3
        markerImageView = new ImageView(context);
        markerImageView.setLayoutParams(new ViewGroup.LayoutParams(MARKER_DIMENSION, MARKER_DIMENSION));
        iconGenerator.setContentView(markerImageView);  // 4
        iconGenerator.setBackground(null);
    }

    @Override
    protected void onBeforeClusterItemRendered(T item, MarkerOptions markerOptions) { // 5
        switch (item.getRoadworkType()) {
            case ROADWORK:
                markerImageView.setImageResource(R.drawable.ic_road_work_current);  // 6
                break;
            case PLANNED_ROADWORK:
                markerImageView.setImageResource(R.drawable.ic_road_work);  // 6
                break;
            case INCIDENT:
                markerImageView.setImageResource(R.drawable.ic_accident);  // 6
                break;
        }
        Bitmap icon = iconGenerator.makeIcon();  // 7

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));  // 8
        markerOptions.title(item.getTitle());
    }

    @Override
    protected void onClusterRendered(Cluster<T> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);

    }
}