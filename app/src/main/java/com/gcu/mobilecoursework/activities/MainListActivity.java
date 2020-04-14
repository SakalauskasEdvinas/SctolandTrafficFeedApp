package com.gcu.mobilecoursework.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gcu.mobilecoursework.CustomRecyclerViewAdapter;
import com.gcu.mobilecoursework.R;
import com.gcu.mobilecoursework.SpinnerFragment;
import com.gcu.mobilecoursework.asynctasks.OnPreExecuteCallback;
import com.gcu.mobilecoursework.asynctasks.OnTrafficDateRetrievedCallback;
import com.gcu.mobilecoursework.helper.DateUtility;
import com.gcu.mobilecoursework.model.TrafficFeedModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// By Edvinas Sakalauskas - S1627176
public class MainListActivity extends BaseActivity implements CustomRecyclerViewAdapter.ItemClickListener, OnTrafficDateRetrievedCallback, OnPreExecuteCallback {

    protected CustomRecyclerViewAdapter recyclerViewAdapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SpinnerFragment spinnerFragment;
    private TextView infoText;
    private Button dateSelectButton;
    private String searchQuery = "";
    private SearchView searchView;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.searcher);

        searchView = (SearchView) item.getActionView();
        searchView.setQuery(searchQuery, false);
        if (!searchView.getQuery().toString().equals("")) {
            this.searchView.setIconified(false);
            this.searchView.clearFocus();
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (recyclerViewAdapter != null) {
                    searchQuery = query;
                    recyclerViewAdapter.getFilter().filter(query);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (recyclerViewAdapter != null) {
                    searchQuery = query;
                    recyclerViewAdapter.getFilter().filter(query);

                }
                return false;
            }
        });


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        infoText = findViewById(R.id.infoText);
        recyclerView = findViewById(R.id.trafficFeedList);
        progressBar = findViewById(R.id.progress);
        dateSelectButton = findViewById(R.id.dateSelect);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerViewAdapter = new CustomRecyclerViewAdapter(this, new ArrayList<TrafficFeedModel>(), infoText);
        this.recyclerView.setAdapter(recyclerViewAdapter);
        this.recyclerViewAdapter.setClickListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        this.spinnerFragment = (SpinnerFragment) fragmentManager.findFragmentById(R.id.spinner_fragment);

        spinnerFragment.addItemSelectListenerToSpinner(progressBar, this, this);

        fragmentTransaction.commit();


    }


    @Override
    //on roadwork click
    public void onItemClick(View view, int position) {
        Intent myIntent = new Intent(getApplicationContext(), SingleRoadworkDetailActivity.class);
        myIntent.putExtra("trafficFeedModel", recyclerViewAdapter.getItem(position));
        this.startActivity(myIntent);

    }


    //callback that gets executed after AsyncTask finishes
    public void processData(List<TrafficFeedModel> data) {

        if (spinnerFragment.getSpinner().getSelectedItem() == "Current incidents") {
            this.dateSelectButton.setVisibility(View.GONE);
        } else {
            this.dateSelectButton.setVisibility(View.VISIBLE);
        }

        if (datePickerDialogFragment != null) {
            this.dateSelected = null;
            datePickerDialogFragment.updateDate(null);
        }

        recyclerViewAdapter.setRecyclerViewData(data);
        this.recyclerViewAdapter.notifyDataSetChanged();
        if (searchQuery != null) {
            recyclerViewAdapter.getFilter().filter(searchQuery);
        }
        progressBar.setVisibility(View.GONE);

        if (recyclerViewAdapter.getItemCount() == 0) {
            infoText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);

        }

        recyclerViewAdapter.getFilter().filter(searchQuery);

    }


    @Override
    public void onTrafficDataPreExecute() {

        recyclerView.setVisibility(View.GONE);
        infoText.setVisibility(View.GONE);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        super.onDateSet(view, year, month, day);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        this.dateSelected = calendar.getTime();
        datePickerDialogFragment.updateDate(calendar);

        recyclerViewAdapter.setSelectedDate(this.dateSelected);
        recyclerViewAdapter.getFilter().filter(searchQuery);

        dateSelectButton.setText(DateUtility.dateToString(this.dateSelected) + "\n change");

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedDate", dateSelected);
        outState.putSerializable("searchQuery", searchQuery);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        dateSelected = (Date) savedInstanceState.getSerializable("selectedDate");
        searchQuery = (String) savedInstanceState.getSerializable("searchQuery");
        if (dateSelected != null) {
            dateSelectButton.setText(getString(R.string.date_set_format_journey, DateUtility.dateToString(dateSelected)));
        }
        if (searchQuery == null) {
            searchQuery = "";
        }
        this.recyclerViewAdapter.setSelectedDate(dateSelected);
        this.recyclerViewAdapter.getFilter().filter(searchQuery);

    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }
} // End of MainActivity

