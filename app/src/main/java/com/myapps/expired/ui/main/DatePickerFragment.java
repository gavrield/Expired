package com.myapps.expired.ui.main;

import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.DatePicker;

import java.sql.Date;
import java.util.Calendar;


public class DatePickerFragment
        extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(
            DatePicker view, int year, int month, int dayOfMonth) {

        long millis = convertToMillis(year, month, dayOfMonth);
        Date date = new Date(millis);
        ((EnterExpDatePage2)getActivity()).setDate(date);
    }

    private long convertToMillis(int year, int month, int day){


        long oneYear = DateUtils.YEAR_IN_MILLIS;
        long oneMonth = DateUtils.MINUTE_IN_MILLIS ;
        long oneDay = DateUtils.DAY_IN_MILLIS;
        long yearInMillis = (year - 1970)*oneYear;
        long monthInMillis = month*oneMonth;
        long dayInMillis = day*oneDay;
        long conversion = yearInMillis + monthInMillis + dayInMillis;
        return conversion;
    }

}
