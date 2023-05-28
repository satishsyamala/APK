package com.example.wms;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.wms.dailog.CustomDailog;
import com.example.wms.dailog.CustomDailogIntf;
import com.example.wms.databinding.StockMasterBinding;
import com.example.wms.databinding.AddStockItemBinding;
import com.example.wms.tableview.TableView;
import com.example.wms.tableview.TableViewIntf;
import com.example.wms.util.CSVReadAndWrite;
import com.example.wms.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StockMaster extends Fragment implements CustomDailogIntf, TableViewIntf {
    private StockMasterBinding stockMasterBinding;
    private TableLayout mTableLayout;
    private SharedPreferences sharedpreferences;
    private AddStockItemBinding addStockItemBinding;
    private String clinetName;
    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    private static final String LOG_TAG = "AndroidExample";
    private EditText editTextPath;
    private String path;
    private List<List<String>> stocks;
    private List<List<String>> shareList;
    private TableView tableView;
    private List<String> seletedItem;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
        stockMasterBinding = StockMasterBinding.inflate(inflater, container, false);
        addStockItemBinding= AddStockItemBinding.inflate(inflater, container, false);
        mTableLayout = stockMasterBinding.tableInvoices;
        tableView = new TableView(mTableLayout, requireContext(), StockMaster.this);
        tableView.setWidthColums("120,250,80,80,60");
        tableView.setFontSize(32, 28);
        tableView.setNumberColums("2,4");

        tableView.setEditBtn(true);
        tableView.setDeleteBtn(true);
        tableView.setFragment(StockMaster.this);
        stockMasterBinding.buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermissionAndBrowseFile();
            }
        });
        stockMasterBinding.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStockItemBinding.itemCode.setText(null);
                addStockItemBinding.itemCode.setEnabled(true);
                addStockItemBinding.itemName.setText(null);
                addStockItemBinding.itemPrice.setText(null);
                addStockItemBinding.itemBarCode.setText(null);
                addStockItemBinding.cartonSize.setText(null);
                CustomDailog c = new CustomDailog(addStockItemBinding.getRoot(), StockMaster.this, "Add Item", null,1);
                c.show(getFragmentManager(), "NoticeDialogFragment");
            }
        });
        getStock();
        tableData(null);
        stockMasterBinding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CSVReadAndWrite.createClientFolder(clinetName + "/reports");
                    CSVReadAndWrite.writeDataAtOnce(shareList, clinetName + "/reports/Stocks.csv");
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Stock master";
                    String shareSub = "Stock master";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    String myFilePath = CSVReadAndWrite.getBaseFileWithFolder() + "/" + clinetName + "/reports/Stocks.csv";
                    File f = new File(myFilePath);
                    Uri apkURI = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName() + ".provider", f);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, apkURI);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        stockMasterBinding.txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String val = stockMasterBinding.txtSearch.getText().toString();
                System.out.println(val);
                tableData(val);

            }
        });
        return stockMasterBinding.getRoot();

    }



    public void getStock() {
        stocks = new ArrayList<>();
        stocks = CSVReadAndWrite.csvToList(clinetName);
        ;
    }

    public void tableData(String searchValue) {
        mTableLayout.removeAllViews();
        shareList = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        headers.add("Code");
        headers.add("Name");
        headers.add("Price");
        headers.add("Bar Code");
        headers.add("CT Size");

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
        tableView.fullTable(headers, rd);
    }


    public void askPermissionAndBrowseFile() {
        // With Android Level >= 23, you have to ask the user
        // for permission to access External Storage.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // Level 23

            // Check if we have Call permission
            int permisson = ActivityCompat.checkSelfPermission(this.getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permisson != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_PERMISSION
                );
                return;
            }
        }
        this.doBrowseFile();
    }

    private void doBrowseFile() {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);

        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
        startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (CALL_PHONE).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i(LOG_TAG, "Permission granted!");
                    Toast.makeText(this.getContext(), "Permission granted!", Toast.LENGTH_SHORT).show();

                    this.doBrowseFile();
                }
                // Cancelled or denied.
                else {
                    Log.i(LOG_TAG, "Permission denied!");
                    Toast.makeText(this.getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_RESULT_CODE_FILECHOOSER:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri fileUri = data.getData();
                        Log.i(LOG_TAG, "Uri: " + fileUri);

                        String filePath = null;
                        try {
                            filePath = FileUtils.getPath(this.getContext(), fileUri);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "Error: " + e);
                            Toast.makeText(this.getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                        }
                        path = filePath;
                        if (!path.endsWith(".csv")) {
                            CustomDailog c = new CustomDailog(null, StockMaster.this, "Alert", "Please select only csv file", 0);
                            c.show(getFragmentManager(), "NoticeDialogFragment");
                        } else {
                            System.out.println("Path : "+path);
                            List<List<String>> fd = CSVReadAndWrite.readCSVFromFolder(path);
                            System.out.println("fd : "+fd.size());
                            if(fd.size()>0) {
                                CSVReadAndWrite.writeDataAtOnce(fd, clinetName + "/Stocks.csv");
                                getStock();
                                tableData(null);
                            }
                        }

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath() {
        return this.editTextPath.getText().toString();
    }

    @Override
    public void editRow(List<String> ls)
    {
        seletedItem=ls;
        addStockItemBinding.itemCode.setText(ls.get(0));
        addStockItemBinding.itemCode.setEnabled(false);
        addStockItemBinding.itemName.setText(ls.get(1));
        addStockItemBinding.itemPrice.setText(ls.get(2));
        addStockItemBinding.itemBarCode.setText(ls.get(3));
        if(ls.size()==5)
            addStockItemBinding.cartonSize.setText(ls.get(4));
        CustomDailog c = new CustomDailog(addStockItemBinding.getRoot(), StockMaster.this, "Edit Item", null,2);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }
    @Override
    public void deleteRow(List<String> ls)
    {
        seletedItem=ls;

        CustomDailog c = new CustomDailog(null, StockMaster.this, "Alert", "You want to delete item",3);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }

    public String defaultString(Object o)
    {
        return (o!=null && o.toString().length()>0)?o.toString():"";
    }

    public String defaultNumber(Object o)
    {
        return (o!=null && o.toString().length()>0)?o.toString():"0";
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {
        if(actionId==0) {
            this.doBrowseFile();
        }
        else if(actionId==1){
            List<String> si=new ArrayList<>();
            si.add(defaultString(addStockItemBinding.itemCode.getText()));
            si.add(defaultString(addStockItemBinding.itemName.getText()));
            si.add(defaultNumber(addStockItemBinding.itemPrice.getText()));
            si.add(defaultString(addStockItemBinding.itemBarCode.getText()));
            si.add(defaultNumber(addStockItemBinding.cartonSize.getText()));
            stocks.add(si);
            CSVReadAndWrite.writeDataAtOnce(stocks,clinetName + "/Stocks.csv");
            tableData(null);
        } else if(actionId==2 && seletedItem!=null){
            int i=0;
            for(List<String> s:stocks)
            {
                if(s.get(0).equalsIgnoreCase(seletedItem.get(0)))
                {
                    stocks.remove(i);
                    List<String> si=new ArrayList<>();
                    si.add(defaultString(addStockItemBinding.itemCode.getText()));
                    si.add(defaultString(addStockItemBinding.itemName.getText()));
                    si.add(defaultNumber(addStockItemBinding.itemPrice.getText()));
                    si.add(defaultString(addStockItemBinding.itemBarCode.getText()));
                    si.add(defaultNumber(addStockItemBinding.cartonSize.getText()));
                    stocks.add(i,si);
                    break;
                }
                i++;
            }
            CSVReadAndWrite.writeDataAtOnce(stocks,clinetName + "/Stocks.csv");
            String val = stockMasterBinding.txtSearch.getText().toString();
            tableData(val);
        }else if(actionId==3 && seletedItem!=null){
            int i=0;
            for(List<String> s:stocks)
            {
                if(s.get(0).equalsIgnoreCase(seletedItem.get(0)))
                {
                    stocks.remove(i);
                    break;
                }
                i++;
            }
            CSVReadAndWrite.writeDataAtOnce(stocks,clinetName + "/Stocks.csv");
            String val = stockMasterBinding.txtSearch.getText().toString();
            tableData(val);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }
}
