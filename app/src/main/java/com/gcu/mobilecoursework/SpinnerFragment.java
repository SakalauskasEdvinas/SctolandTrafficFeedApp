package com.gcu.mobilecoursework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gcu.mobilecoursework.asynctasks.AsyncTrafficDataTask;
import com.gcu.mobilecoursework.asynctasks.OnPreExecuteCallback;
import com.gcu.mobilecoursework.asynctasks.OnTrafficDateRetrievedCallback;

// By Edvinas Sakalauskas - S1627176
public class SpinnerFragment extends Fragment {
    private Spinner spinner;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spinner_fragment, container, false);
        this.spinner = view.findViewById(R.id.spinner_fragment);

        String[] strings = {"Roadworks", "Planned roadworks", "Current incidents", "All"};
        final ArrayAdapter<CharSequence> spinnerArrayAdapter = new ArrayAdapter<CharSequence>
                (getActivity(), android.R.layout.simple_spinner_item, strings); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(spinnerArrayAdapter);


        return view;

    }

    public Spinner getSpinner() {
        return this.spinner;
    }


    //adds event listener on spinner changes, calls async data retrieval task for selected option
    public void addItemSelectListenerToSpinner(ProgressBar progressBar, OnTrafficDateRetrievedCallback cb, OnPreExecuteCallback precb) {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {

                int item = spinner.getSelectedItemPosition();
                switch (item) {
                    case 0:
                        new AsyncTrafficDataTask(cb, precb, BuildConfig.ROADWORKS, progressBar).execute();
                        break;
                    case 1:
                        new AsyncTrafficDataTask(cb, precb, BuildConfig.PLANNED_ROADWORKS, progressBar).execute();
                        break;
                    case 2:
                        new AsyncTrafficDataTask(cb, precb, BuildConfig.CURRENT_INCIDENTS, progressBar).execute();
                        break;
                    case 3:
                        new AsyncTrafficDataTask(cb, precb, "ALL", progressBar).execute();


                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


}
