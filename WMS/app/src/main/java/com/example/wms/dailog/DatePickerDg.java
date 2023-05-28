package com.example.wms.dailog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerDg extends DialogFragment {

    public DatePickerDialog.OnDateSetListener listener;
    public Date date;

    public DatePickerDg(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    public DatePickerDg(DatePickerDialog.OnDateSetListener listener, Date date) {
        this.listener = listener;
        this.date = date;
    }


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar mCalendar = Calendar.getInstance();
        if (date != null)
            mCalendar.setTime(date);
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), listener, year, month, dayOfMonth);
    }
}
