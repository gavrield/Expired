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

    private long oneDay = DateUtils.DAY_IN_MILLIS;
    private long oneYear = DateUtils.YEAR_IN_MILLIS + oneDay; // DateUtils.YEAR_IN_MILLIS is only 364 days

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

        int leap = year%4 == 0 ? 1 : 0;
        long yearInMillis = convertYearToMillis(year);
        long monthInMillis = convertMonthToMillis(month + 1, leap); //months start in 0
        long dayInMillis = day*oneDay;
        long conversion = yearInMillis + monthInMillis + dayInMillis - 1;
        return conversion;
    }

    private long convertYearToMillis(int year) {
        int normaliseYear = year - 1970;
        int leapYears = normaliseYear/4;
        return normaliseYear*oneYear + leapYears*oneDay;
    }

    private long convertMonthToMillis(int month, int leap) {

        switch (month) {
            case 1:
                return 0;
            case 2:
                return 31*oneDay;
            case 3:
                return convertMonthToMillis(2, leap) + 28*oneDay + leap*oneDay;
            case 4:
                return convertMonthToMillis(3, leap) + 31*oneDay;
            case 5:
                return convertMonthToMillis(4, leap) + 30*oneDay;
            case 6:
                return convertMonthToMillis(5, leap) + 31*oneDay;
            case 7:
                return convertMonthToMillis(6, leap) + 30*oneDay;
            case 8:
                return convertMonthToMillis(7, leap) + 31*oneDay;
            case 9:
                return convertMonthToMillis(8, leap) + 31*oneDay;
            case 10:
                return convertMonthToMillis(9, leap) + 30*oneDay;
            case 11:
                return convertMonthToMillis(10, leap) + 31*oneDay;
            case 12:
                return convertMonthToMillis(11, leap) + 30*oneDay;
            default: return convertMonthToMillis(1, leap);
        }
    }

}
