package com.example.wms;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wms.dailog.CustomDailog;
import com.example.wms.dailog.CustomDailogIntf;
import com.example.wms.dailog.DatePickerDg;
import com.example.wms.databinding.LoginBinding;
import com.example.wms.databinding.RegistrationBinding;
import com.example.wms.listview.CustomListIntf;
import com.example.wms.listview.SubjectData;
import com.example.wms.util.AppActivation;
import com.example.wms.util.CSVReadAndWrite;
import com.example.wms.util.FileUtils;
import com.example.wms.util.JSONReadAndWrite;
import com.goodiebag.pinview.Pinview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Login extends Fragment implements CustomDailogIntf {


    private LoginBinding loginBinding;
    private CancellationSignal cancellationSignal = null;
    private BiometricPrompt.AuthenticationCallback authenticationCallback;
    private BiometricPrompt biometricPrompt;

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        loginBinding = LoginBinding.inflate(inflater, container, false);
        try{
            ((AppCompatActivity) requireContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }catch (Exception e){}
        try {

            authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(
                        int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    notifyUser("Authentication Error : " + errString);
                }

                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    NavHostFragment.findNavController(Login.this)
                            .navigate(R.id.FirstFragment);
                }
            };
            biometricPrompt = new BiometricPrompt
                    .Builder(getContext())
                    .setTitle("Login")
                    .setDescription("Use your fingerprint to login")
                    .setNegativeButton("Cancel", getActivity().getMainExecutor(), new DialogInterface.OnClickListener() {
                        @Override
                        public void
                        onClick(DialogInterface dialogInterface, int i) {
                            notifyUser("Authentication Cancelled");
                        }
                    }).build();

            String setst = JSONReadAndWrite.readJSON("/setting.json");
            if (setst != null) {
                System.out.println(setst);
                JSONObject s = new JSONObject(setst);
                boolean a = s.has("biologin") ? s.getBoolean("biologin") : false;
                if (a) {
                    System.out.println("a :  "+a);
                    biometricPrompt.authenticate(
                            getCancellationSignal(),
                            getActivity().getMainExecutor(),
                            authenticationCallback);
                    loginBinding.imageView2.setEnabled(true);

                }
                else{
                    loginBinding.imageView2.setEnabled(false);
                }

            }




        } catch (Exception e) {
            e.printStackTrace();
        }
        loginBinding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("a :  ");
                biometricPrompt.authenticate(
                        getCancellationSignal(),
                        getActivity().getMainExecutor(),
                        authenticationCallback);

            }
        });

        loginBinding.pin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                String deviceid = AppActivation.getDeviceIMEI(requireContext());
                String sec = JSONReadAndWrite.readJSON(deviceid + ".json");
                if (sec != null) {
                    try {
                        JSONObject obj = new JSONObject(sec);
                        String pin = obj.getString("pin");
                        if (pin.equalsIgnoreCase(loginBinding.pin.getValue())) {
                            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(loginBinding.getRoot().getApplicationWindowToken(), 0);
                            NavHostFragment.findNavController(Login.this)
                                    .navigate(R.id.FirstFragment);
                        } else {
                            loginBinding.pin.setValue("");
                            CustomDailog c = new CustomDailog(null, Login.this, "Alert", "Invalid Pin", -1);
                            c.show(getFragmentManager(), "NoticeDialogFragment");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println();
            }
        });
        return loginBinding.getRoot();
    }


    // it will be called when
    // authentication is cancelled by
    // the user
    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(
                new CancellationSignal.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        notifyUser("Authentication was Cancelled by the user");
                    }
                });
        return cancellationSignal;
    }


    // this is a toast method which is responsible for
    // showing toast it takes a string as parameter
    private void notifyUser(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }
}