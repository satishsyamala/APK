package com.example.wms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wms.dailog.CustomDailog;
import com.example.wms.dailog.CustomDailogIntf;
import com.example.wms.databinding.ChangepinBinding;
import com.example.wms.util.AppActivation;
import com.example.wms.util.JSONReadAndWrite;

import org.json.JSONObject;

public class ChangePin extends Fragment implements CustomDailogIntf {

    private ChangepinBinding changepinBinding;
    private SharedPreferences sharedpreferences;
    ;
    private GridView coursesGV;
    private String clinetName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        changepinBinding = ChangepinBinding.inflate(inflater, container, false);

        try {

            changepinBinding.activate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String pin = changepinBinding.pin.getValue();
                        if (pin != null && pin.length() == 4) {
                            String conpin = changepinBinding.conPin.getValue();
                            if (pin != null && pin.length() == 4) {
                                if (pin.equalsIgnoreCase(conpin)) {
                                    String deviceid = AppActivation.getDeviceIMEI(requireContext());
                                    String setst = JSONReadAndWrite.readJSON(deviceid + ".json");
                                    if (setst != null) {
                                        JSONObject set = new JSONObject(setst);
                                        set.put("pin", pin);
                                        JSONReadAndWrite.writeJSONfile(deviceid + ".json", set);
                                        NavHostFragment.findNavController(ChangePin.this)
                                                .navigate(R.id.login);
                                    }
                                } else {
                                    CustomDailog c = new CustomDailog(null, ChangePin.this, "Alert", "Pin and Confirm Pin not matching", -1);
                                    c.show(getFragmentManager(), "NoticeDialogFragment");
                                }
                            } else {
                                CustomDailog c = new CustomDailog(null, ChangePin.this, "Alert", "Please Enter Confirm Pin", -1);
                                c.show(getFragmentManager(), "NoticeDialogFragment");
                            }
                        } else {
                            CustomDailog c = new CustomDailog(null, ChangePin.this, "Alert", "Please Enter Pin", -1);
                            c.show(getFragmentManager(), "NoticeDialogFragment");
                        }

                    } catch (Exception e) {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changepinBinding.getRoot();
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {
        if (actionId == -1) {
            NavHostFragment.findNavController(ChangePin.this)
                    .navigate(R.id.MenuItem);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }
}
