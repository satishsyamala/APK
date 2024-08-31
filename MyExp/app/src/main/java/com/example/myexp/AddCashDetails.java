package com.example.myexp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.myexp.dailog.CustomDailogIntf;
import com.example.myexp.databinding.AddCashDetailsBinding;
import com.example.myexp.databinding.ExpChargesDetailsBinding;
import com.example.myexp.databinding.ExpFilterBinding;
import com.example.myexp.listview.CustomListIntf;
import com.example.myexp.listview.SubjectData;
import com.example.myexp.util.CSVReadAndWrite;
import com.example.myexp.util.GeneralUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddCashDetails extends Fragment implements CustomDailogIntf, CustomListIntf {
    private AddCashDetailsBinding addCashDetailsBinding;
    private SharedPreferences sharedpreferences;
    private ExpFilterBinding expFilterBinding;
    private ListView simpleList;
    private String clinetName;
    private String pageName;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    private static final String LOG_TAG = "AndroidExample";
    private EditText editTextPath;
    private String path;
    private List<List<String>> stocks;
    private List<List<String>> shareList;
    private List<String> seletedItem;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
        pageName = sharedpreferences.getString("contact_name", "NA");
        try {
            ((MainActivity) requireContext()).getSupportActionBar().setTitle(pageName);
        } catch (Exception e) {
        }
        addCashDetailsBinding = AddCashDetailsBinding.inflate(inflater, container, false);
        simpleList = addCashDetailsBinding.expseList;
        getStock();
        tableData(null, null);

        return addCashDetailsBinding.getRoot();

    }


    public void getStock() {
        stocks = new ArrayList<>();
        stocks = GeneralUtil.geAddCash(clinetName, pageName);
        ;
    }


    public void tableData(String searchValue, String searchValue1) {
        try {

            ArrayList<SubjectData> subData = new ArrayList<>();
            for (List<String> ls : stocks) {
                subData.add(getSubjectDataForList(ls));
            }
            CustomAdapter customAdapter = new CustomAdapter(requireContext(), subData, AddCashDetails.this, "addcash");
            simpleList.setAdapter(customAdapter);
        } catch (Exception w) {

        }
    }


    public SubjectData getSubjectDataForList(List<String> ls) {
        SubjectData s = new SubjectData(ls.get(1), ls.get(2), "Rs : " + ls.get(3));
        s.setImage(ls.get(4));
        return s;
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }

    @Override
    public void onItemClick(View obj) {

    }
}
