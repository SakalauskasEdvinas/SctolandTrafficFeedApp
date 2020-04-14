package com.gcu.mobilecoursework;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.gcu.mobilecoursework.activities.BaseActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
// By Edvinas Sakalauskas - S1627176

public class DatePickerDialogFragment extends DialogFragment {

    private Calendar calendar;

   public DatePickerDialogFragment(Date date) {
       this.calendar = new GregorianCalendar();
       if(date == null) {
           calendar = Calendar.getInstance();
       }else {
           this.calendar.setTime(date);

       }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        if(calendar==null) {calendar = Calendar.getInstance();}
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), (BaseActivity) getActivity(), year, month, day);


    }

    public void updateDate(Calendar calendar) {
        this.calendar = calendar;
    }


}