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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
import com.example.wms.dailog.DatePickerDg;
import com.example.wms.databinding.ReportViewBinding;
import com.example.wms.listview.CustomListIntf;
import com.example.wms.tableview.TableView;
import com.example.wms.util.CSVReadAndWrite;
import com.example.wms.util.FileUtils;
import com.example.wms.util.JSONReadAndWrite;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportView extends Fragment implements DatePickerDialog.OnDateSetListener {
    private ReportViewBinding stockViewBinding;
    private TableLayout mTableLayout;
    private SharedPreferences sharedpreferences;
    private String clinetName;
    private EditText editTextPath;
    private String seleDate;
    private String fileName;
    private List<List<String>> shareFile;
    private List<List<String>> reportData;
   private CheckBox allItems;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
        seleDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        stockViewBinding = ReportViewBinding.inflate(inflater, container, false);
        mTableLayout = stockViewBinding.tableInvoices;
        allItems=stockViewBinding.allItems;
        stockViewBinding.selectedDate.setText(seleDate);
        String setst= JSONReadAndWrite.readJSON("/setting.json");
        if(setst!=null) {
            try {
                JSONObject set = new JSONObject(setst);
                 if (set.getString("rdv").equalsIgnoreCase("true"))
                     allItems.setChecked(true);
            }catch (Exception e){

            }
        }
        stockViewBinding.selectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please note that use your package name here
                DatePickerDg mDatePickerDialogFragment = new DatePickerDg(ReportView.this);
                mDatePickerDialogFragment.show(getFragmentManager(), "DATE PICK");
            }
         });
        allItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = stockViewBinding.txtSearch.getText().toString();
                tableData(val);
            }
        });


        generateData();
        tableData(null);
        stockViewBinding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CSVReadAndWrite.createClientFolder(clinetName + "/reports");
                    fileName = "RP_" + seleDate + ".csv";
                    CSVReadAndWrite.writeDataAtOnce(shareFile, clinetName + "/reports/RP_" + seleDate + ".csv");
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setType("text/plain");
                    String shareBody = seleDate + "Stock Balance Report";
                    String shareSub = seleDate + "Stock Balance Report";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    String myFilePath = CSVReadAndWrite.getBaseFileWithFolder() + "/" + clinetName + "/reports/" + fileName;
                    File f = new File(myFilePath);
                    Uri apkURI = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName() + ".provider", f);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, apkURI);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


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
    public void generateData() {
        reportData = new ArrayList<>();
        Date cr= CSVReadAndWrite.stringToDate(seleDate);

        List<List<String>> st = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.CSVFileNames(clinetName, "Stock Take", seleDate));
        if(!st.isEmpty()) {
            Date pd = CSVReadAndWrite.previousStockTakeDate(cr, clinetName);
            stockViewBinding.reportInfo.setText(CSVReadAndWrite.dateToString(pd)+" To "+seleDate);
            List<List<String>> allStock = CSVReadAndWrite.readCSVfile(clinetName + "/Stocks.csv");
            List<List<String>> ob = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.CSVFileNames(clinetName, "Stock Take", CSVReadAndWrite.dateToString(pd)));
            Map<String, Integer> si = CSVReadAndWrite.transactions(pd, cr, clinetName, "Purchases");
            Map<String, Integer> so = CSVReadAndWrite.transactions(pd, cr, clinetName, "Stock Out");
            Map<String, Integer> sl = CSVReadAndWrite.transactions(pd, cr, clinetName, "Sales");

            for (List<String> a : allStock) {
                List<String> d = new ArrayList<>();
                d.add(a.get(1));
                int obq = 0, siq = 0, soq = 0, slq = 0, cbq = 0, stq = 0, divq = 0;
                for (List<String> b : ob) {
                    if (b.get(0).equalsIgnoreCase(a.get(0))) {
                        obq = Integer.parseInt(b.get(2));
                        break;
                    }
                }
                siq = si.containsKey(a.get(0)) ? si.get(a.get(0)) : 0;
                slq = sl.containsKey(a.get(0)) ? sl.get(a.get(0)) : 0;
                soq = so.containsKey(a.get(0)) ? so.get(a.get(0)) : 0;
                for (List<String> b : st) {
                    if (b.get(0).equalsIgnoreCase(a.get(0))) {
                        stq = Integer.parseInt(b.get(2));
                        break;
                    }
                }
                obq = obq + siq;
                slq = slq + soq;
                cbq = obq - slq;
                divq = stq - cbq;
                d.add(obq + "");
                d.add(slq + "");
                d.add(cbq + "");
                d.add(stq + "");
                d.add(divq + "");
                reportData.add(d);
            }
        }
        else{
            stockViewBinding.reportInfo.setText("No Stock Take for "+seleDate);
        }
    }

    public void tableData(String searchValue) {
        mTableLayout.removeAllViews();
        List<String> headers = new ArrayList<>();
        headers.add("Name");
        headers.add("OB");
        headers.add("Sales");
        headers.add("CB");
        headers.add("ST");
        headers.add("DIV");

        shareFile = new ArrayList<>();
        shareFile.add(headers);
        List<List<String>> rd = new ArrayList<>();
        if (searchValue != null && searchValue.trim().length() > 0) {
            for (List<String> ls : reportData) {
                if (ls.get(0).toString().toLowerCase().startsWith(searchValue.toLowerCase())) {
                    if (allItems.isChecked())
                        rd.add(ls);
                    else {
                        if (!ls.get(5).equalsIgnoreCase("0"))
                            rd.add(ls);
                    }
                }
            }
        } else {
            if (allItems.isChecked())
                rd.addAll(reportData);
            else
                for (List<String> ls : reportData) {
                    if (!ls.get(5).equalsIgnoreCase("0"))
                        rd.add(ls);
                }
        }
        shareFile.addAll(rd);
        TableView t = new TableView(mTableLayout, requireContext(), null);
        t.setFontSize(32, 28);
        t.setNumberColums("1,2,3,4,5");
        t.setWidthColums("200,70,70,70,70,70");
        t.fullTable(headers, rd);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        seleDate = new SimpleDateFormat("dd-MM-yyyy").format(mCalendar.getTime());
        stockViewBinding.selectedDate.setText(seleDate);
        generateData();
        tableData(null);
    }
}
