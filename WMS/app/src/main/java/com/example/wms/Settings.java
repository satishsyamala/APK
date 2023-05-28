package com.example.wms;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.example.wms.dailog.DatePickerDg;
import com.example.wms.databinding.SettingsBinding;
import com.example.wms.listview.CustomListIntf;
import com.example.wms.listview.SubjectData;
import com.example.wms.util.JSONReadAndWrite;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Settings extends Fragment implements CustomDailogIntf {

    private SettingsBinding settingsBinding;
    private SharedPreferences sharedpreferences;;
    private GridView coursesGV;
    private String clinetName;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsBinding=SettingsBinding.inflate(inflater, container, false);
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
        try {
            String setst=JSONReadAndWrite.readJSON("/setting.json");
            if(setst!=null) {
                JSONObject set = new JSONObject(setst);
                settingsBinding.pergeDays.setText(set.getString("pergedays"));
                if (set.getString("rdv").equalsIgnoreCase("true"))
                    settingsBinding.reportDefault.setChecked(true);
                if (set.getString("biologin").equalsIgnoreCase("true"))
                    settingsBinding.biometric.setChecked(true);
            }
           settingsBinding.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject a = new JSONObject();
                        if (settingsBinding.pergeDays.getText() != null && settingsBinding.pergeDays.getText().toString().length() > 0)
                            a.put("pergedays", settingsBinding.pergeDays.getText().toString());
                        else
                            a.put("pergedays", "15");
                        a.put("rdv", settingsBinding.reportDefault.isChecked());
                        a.put("biologin", settingsBinding.biometric.isChecked());
                        JSONReadAndWrite.writeJSONfile("/setting.json",a);
                        CustomDailog c = new CustomDailog(null, Settings.this, "Success", "Saved successfully", -1);
                        c.show(getFragmentManager(), "NoticeDialogFragment");
                    }catch (Exception e){

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return settingsBinding.getRoot();
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {
        if(actionId==-1)
        {
            NavHostFragment.findNavController(Settings.this)
                    .navigate(R.id.MenuItem);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }
}
