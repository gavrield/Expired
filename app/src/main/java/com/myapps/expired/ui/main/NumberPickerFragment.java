package com.myapps.expired.ui.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.myapps.expired.R;

public class NumberPickerFragment extends DialogFragment {

    static public final int AMOUNT = 1;
    static public final int DAYS = 2;

    private NumberPicker picker;
    private int amountOrDays;
    private NumberPickerDialog numPickerDialog;

    public NumberPickerFragment(int amountOrDays){
        this.amountOrDays = amountOrDays;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        numPickerDialog = new NumberPickerDialog(getActivity());
        numPickerDialog.setContentView(R.layout.numberpicker_layout);
        picker = numPickerDialog.findViewById(R.id.numberPicker);
        Button setBtn = numPickerDialog.findViewById(R.id.setBtn);
        Button cnlBtn = numPickerDialog.findViewById(R.id.CancelButton_NumberPicker);

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountOrDays == AMOUNT)
                    ((EnterExpDatePage2) getActivity()).setAmount(picker.getValue());
                else if (amountOrDays == DAYS)
                    ((EnterExpDatePage2) getActivity()).setDaysBefore(picker.getValue());
                numPickerDialog.dismiss();
            }
        });
        cnlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numPickerDialog.dismiss();
            }
        });
        return numPickerDialog;
    }

    public void setPickerValues(int min, int max){
        picker.setMinValue(min);
        picker.setMaxValue(max);
    }
    private class NumberPickerDialog extends Dialog {

        public NumberPickerDialog(@NonNull Context context) {
            super(context);
        }
    }
}
