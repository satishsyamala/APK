package com.example.wms;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wms.dailog.CustomDailog;
import com.example.wms.dailog.CustomDailogIntf;
import com.example.wms.dailog.DatePickerDg;
import com.example.wms.listview.CustomListIntf;
import com.example.wms.listview.SubjectData;
import com.example.wms.util.AppActivation;
import com.example.wms.util.CSVReadAndWrite;
import com.example.wms.util.FileUtils;
import com.example.wms.databinding.RegistrationBinding;
import com.example.wms.util.JSONReadAndWrite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Registration extends Fragment implements CustomDailogIntf {
    private RegistrationBinding salesDataBinding;
    private final int APP_VERSION = 1;
    private String deviceid;
    private int token;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        salesDataBinding = RegistrationBinding.inflate(inflater, container, false);
        boolean actValid = false;
        setHasOptionsMenu(true);
        try {
            deviceid = AppActivation.getDeviceIMEI(requireContext());
            int tk = 0;

            String sec = JSONReadAndWrite.readJSON(deviceid + ".json");
            if (sec != null) {
                JSONObject obj = new JSONObject(sec);
                tk = obj.getInt("token");
                String device = obj.getString("deviceid");
                String code = "";
                if (obj.has("code"))
                    code = obj.getString("code");
                salesDataBinding.actCode.setText(code);
                String newCode = AppActivation.getActivationCode(device, tk, APP_VERSION);
                if (newCode.equalsIgnoreCase(code)) {
                    actValid = true;
                }
            } else {
                tk = AppActivation.appToken();
                JSONObject obj = new JSONObject();
                obj.put("deviceid", deviceid);
                obj.put("token", tk);
                obj.put("version", APP_VERSION);
                JSONReadAndWrite.writeJSONfile(deviceid + ".json", obj);
            }
            token = tk;
            salesDataBinding.appVersion.setText(APP_VERSION + "");
            salesDataBinding.appToken.setText(tk + "");
            salesDataBinding.appDeviceId.setText(deviceid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (actValid) {
            pergeDays();
            NavHostFragment.findNavController(Registration.this)
                    .navigate(R.id.login);
        } else {
            salesDataBinding.message.setText("App is not activated. Please activate");
        }

        salesDataBinding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Version : " + APP_VERSION + "\nToken : " + token + "\nDevice Id : " + deviceid;
                    String shareSub = "";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        salesDataBinding.activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (salesDataBinding.actCode.getText() != null && salesDataBinding.actCode.getText().toString().length() > 0) {
                        String sec = JSONReadAndWrite.readJSON(deviceid + ".json");

                        if (sec != null) {
                            JSONObject obj = new JSONObject(sec);
                            int tk = obj.getInt("token");
                            String device = obj.getString("deviceid");
                            String code = salesDataBinding.actCode.getText().toString();
                            String newCode = AppActivation.getActivationCode(device, tk, APP_VERSION);
                            if (newCode.equalsIgnoreCase(code)) {
                                obj.put("code", code);
                                String pin = salesDataBinding.pin.getValue();
                                if (pin != null && pin.length() == 4) {
                                    String conpin = salesDataBinding.conPin.getValue();
                                    if (pin != null && pin.length() == 4) {
                                        if (pin.equalsIgnoreCase(conpin)) {
                                            obj.put("pin", pin);
                                            JSONReadAndWrite.writeJSONfile(deviceid + ".json", obj);
                                            NavHostFragment.findNavController(Registration.this)
                                                    .navigate(R.id.FirstFragment);
                                        } else {
                                            CustomDailog c = new CustomDailog(null, Registration.this, "Alert", "Pin and Confirm Pin not matching", -1);
                                            c.show(getFragmentManager(), "NoticeDialogFragment");
                                        }
                                    } else {
                                        CustomDailog c = new CustomDailog(null, Registration.this, "Alert", "Please Enter Confirm Pin", -1);
                                        c.show(getFragmentManager(), "NoticeDialogFragment");
                                    }
                                } else {
                                    CustomDailog c = new CustomDailog(null, Registration.this, "Alert", "Please Enter Pin", -1);
                                    c.show(getFragmentManager(), "NoticeDialogFragment");
                                }
                            } else {
                                CustomDailog c = new CustomDailog(null, Registration.this, "Alert", "Invalid  Activation Code", -1);
                                c.show(getFragmentManager(), "NoticeDialogFragment");
                            }

                        }
                    } else {
                        CustomDailog c = new CustomDailog(null, Registration.this, "Alert", "Activation Code Required", -1);
                        c.show(getFragmentManager(), "NoticeDialogFragment");
                    }
                } catch (Exception e) {
                          e.printStackTrace();
                }
            }
        });
        return salesDataBinding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    public void pergeDays() {
        File f = CSVReadAndWrite.getBaseFileWithFolder();
        if (f.isDirectory()) {
            String sj = JSONReadAndWrite.readJSON("/setting.json");
            try {
                if (sj != null) {
                    JSONObject o = new JSONObject(sj);
                    if (o.getString("pergedays") != null) {

                        for (File c : f.listFiles()) {
                            if (c.isDirectory()) {
                                int gd = Integer.parseInt(o.getString("pergedays"));
                                Date d = CSVReadAndWrite.addDays(new Date(), -gd);
                                removeOldData(c.getName(), "Stock Take", d);
                                removeOldData(c.getName(), "Stock In", d);
                                removeOldData(c.getName(), "Stock Out", d);
                                removeOldData(c.getName(), "Sales", d);
                            }
                        }


                    }
                }
            } catch (Exception e) {
            }
        }
    }


    public void removeOldData(String clinetName, String module, Date d) {
        try {
            String clinetJson = JSONReadAndWrite.readJSON(CSVReadAndWrite.jsonFileNames(clinetName, module));
            JSONArray f = new JSONArray();
            SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
            boolean check = false;
            if (clinetJson != null) {
                JSONArray j = JSONReadAndWrite.sortJSONArray(new JSONArray(clinetJson));
                for (int i = 0; i < j.length(); i++) {
                    JSONObject o = j.getJSONObject(i);
                    Date fd = sd.parse(o.getString("date"));
                    if (fd.before(d)) {
                        check = true;
                        CSVReadAndWrite.deleteFile(CSVReadAndWrite.CSVFileNames(clinetName, module, sd.format(fd)));
                    } else
                        f.put(o);

                }
                if (check)
                    JSONReadAndWrite.writeJSONArrayfile(CSVReadAndWrite.jsonFileNames(clinetName, module), f);
            }
        } catch (Exception e) {

        }
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }
}