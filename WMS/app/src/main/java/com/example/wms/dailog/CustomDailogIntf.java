package com.example.wms.dailog;

import androidx.fragment.app.DialogFragment;

import org.json.JSONException;

public interface CustomDailogIntf {


    public void onDialogPositiveClick(DialogFragment dialog,int actionId) ;
    public void onDialogNegativeClick(DialogFragment dialog,int actionId);
}
