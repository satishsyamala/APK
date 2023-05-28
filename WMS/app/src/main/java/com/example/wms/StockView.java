package com.example.wms;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.wms.dailog.CustomDailog;
import com.example.wms.dailog.CustomDailogIntf;
import com.example.wms.databinding.StockViewBinding;
import com.example.wms.tableview.TableView;
import com.example.wms.util.CSVReadAndWrite;
import com.example.wms.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StockView extends Fragment {
 private StockViewBinding stockViewBinding;
    private TableLayout mTableLayout;
    private SharedPreferences sharedpreferences;
    private String clinetName;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    private static final String LOG_TAG = "AndroidExample";
    private EditText editTextPath;
    private String path;
    private String seleDate;
    private String pageName;
    private List<List<String>> stocks;
    private List<List<String>> shareList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
        seleDate = sharedpreferences.getString("seldate", "NA");
        pageName = sharedpreferences.getString("pagename", "NA");
        stockViewBinding=StockViewBinding.inflate(inflater, container, false);
        stockViewBinding.selectedDate.setText(seleDate);
        mTableLayout=stockViewBinding.tableInvoices;
        try {
            ((MainActivity) requireContext()).getSupportActionBar().setTitle(pageName);
        }catch (Exception e){}
        getStock();
        tableData(null);
        stockViewBinding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CSVReadAndWrite.createClientFolder(clinetName + "/reports");
                    CSVReadAndWrite.writeDataAtOnce(shareList, clinetName + "/reports/"+seleDate+pageName+".csv");
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setType("text/plain");
                    String shareBody = seleDate+" "+pageName+" Report ";
                    String shareSub =  seleDate+" "+pageName+" Report ";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    String myFilePath = CSVReadAndWrite.getBaseFileWithFolder() + "/" + clinetName + "/reports/"+seleDate+pageName+".csv";
                    File f = new File(myFilePath);
                    Uri apkURI = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName() + ".provider", f);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, apkURI);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }  });

        stockViewBinding.txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String val = stockViewBinding.txtSearch.getText().toString();
                System.out.println(val);
                tableData(val);

            }
        });

        return stockViewBinding.getRoot();

    }

    public void getStock() {
        stocks = new ArrayList<>();
        stocks =  CSVReadAndWrite.readCSVfile(CSVReadAndWrite.CSVFileNames(clinetName,pageName,seleDate));
        ;
    }

    public void tableData(String searchValue)
    {
        shareList = new ArrayList<>();
        mTableLayout.removeAllViews();
        List<String> headers=new ArrayList<>();
        TableView t=new TableView(mTableLayout,requireContext(),null);

        if(pageName.equalsIgnoreCase("Invoice"))
        {
            headers.add("Name");
            headers.add("Qty");
            headers.add("Price");
            headers.add("Amount");
            t.setFontSize(32, 28);
            t.setDisplayColums("1,2,3,4");
            t.setNumberColums("2,3,4");
            t.setWidthColums("0,200,60,60,80");
        }
        else{
            headers.add("Code");
            headers.add("Name");
            headers.add("Qty");
            t.setFontSize(32, 28);
            t.setNumberColums("2");
            t.setDisplayColums("0,1,2");
            t.setWidthColums("0,200,0");
        }

        shareList.add(headers);
        List<List<String>> rd = new ArrayList<>();
        if (searchValue != null && searchValue.trim().length() > 0) {
            for (List<String> ls : stocks) {
                if (ls.get(1).toString().toLowerCase().startsWith(searchValue.toLowerCase())) {
                    rd.add(ls);
                }
            }
        } else {
            rd.addAll(stocks);
        }
        shareList.addAll(rd);

        t.fullTable(headers,rd);
    }


}
