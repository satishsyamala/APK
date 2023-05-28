package com.example.wms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wms.databinding.FragmentSecondBinding;
import com.example.wms.util.CSVReadAndWrite;

import java.io.File;


public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    SharedPreferences sharedpreferences;
    String clinetName;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        try {

            sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
            clinetName = sharedpreferences.getString("client", "NA");
            ((AppCompatActivity) requireContext()).getSupportActionBar().setTitle(clinetName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnStock.setBackgroundColor(Color.parseColor("#FF03DAC5"));
        binding.btnStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.StockMaster);
            }
        });
        binding.btnStockTake.setBackgroundColor(Color.parseColor("#FF018786"));
        binding.btnStockTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectPage("Stock Take");
            }
        });
        binding.btnStockIn.setBackgroundColor(Color.parseColor("#FFBB86FC"));
        binding.btnStockIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectPage("Stock In");
            }
        });
        binding.btnStockOut.setBackgroundColor(Color.parseColor("#4EC2F0"));
        binding.btnStockOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectPage("Stock Out");
            }
        });

        binding.btnSales.setBackgroundColor(Color.parseColor("#FF7F00"));
        binding.btnSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectPage("Sales");
            }
        });
        binding.btnReports.setBackgroundColor(Color.parseColor("#FF4EC2F0"));
        binding.btnReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.ReportView);
            }
        });
        binding.btnClientLiet.setBackgroundColor(Color.parseColor("#FFF476BC"));
        binding.btnClientLiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.FirstFragment);
            }
        });


    }

    public void redirectPage(String name) {
        try {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("pagename", name);
            editor.commit();
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.StHistory);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}