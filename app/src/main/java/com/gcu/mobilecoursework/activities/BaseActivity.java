package com.gcu.mobilecoursework.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.gcu.mobilecoursework.DatePickerDialogFragment;
import com.gcu.mobilecoursework.R;
import com.gcu.mobilecoursework.helper.MarkerClusterItem;
import com.gcu.mobilecoursework.helper.MarkerClusterRenderer;
import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Date;

// By Edvinas Sakalauskas - S1627176
//base activity, defines the toolbar menu for activities that are extending the base activity
public abstract class BaseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    protected DatePickerDialogFragment datePickerDialogFragment;
    protected Date dateSelected;

    protected abstract int getLayoutResource();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResource());
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("MPD2 Coursework - S1627176 ");

        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.listViewMenuItem:
                intent = new Intent(getApplicationContext(), MainListActivity.class);
                startActivity(intent);
                break;
            case R.id.mapViewMenuItem:
                intent = new Intent(getApplicationContext(), MapViewActivity.class);
                startActivity(intent);
                break;
            case R.id.mapJourneyMenuItem:
                intent = new Intent(getApplicationContext(), MapJourneyActivity.class);
                startActivity(intent);
                break;

        }
        return false;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.searcher);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setFocusable(false);
        searchView.setQueryHint("Enter location, road");
        int options = searchView.getImeOptions();

        searchView.setImeOptions(options | EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(getColor(R.color.White));
        editText.setHintTextColor(getColor(R.color.White));
        editText.setBackgroundColor(getColor(R.color.colorPrimarySec));

        return super.onCreateOptionsMenu(menu);

    }


    public void showDatePickerDialog(View view) {
        if (datePickerDialogFragment == null) {
            datePickerDialogFragment = new DatePickerDialogFragment(dateSelected);
        }
        datePickerDialogFragment.show(getSupportFragmentManager(), "datePicker");
    }


    //sets google map cluster rendering
    public void setRenderer(Context context, GoogleMap googleMap, ClusterManager clusterManager) {
        MarkerClusterRenderer<MarkerClusterItem> clusterRenderer = new MarkerClusterRenderer<>(context, googleMap, clusterManager);
        clusterManager.setRenderer(clusterRenderer);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //do nothing, gets overridden, when needed
    }


}
