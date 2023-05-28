package com.example.wms;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SymbolTable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.example.wms.dailog.CustomDailog;
import com.example.wms.dailog.CustomDailogIntf;
import com.example.wms.dailog.DatePickerDg;
import com.example.wms.databinding.StockTakeBinding;
import com.example.wms.databinding.StockTakeDailogBinding;
import com.example.wms.databinding.CustomerInfoBinding;
import com.example.wms.listview.CustomListIntf;
import com.example.wms.listview.SubjectData;
import com.example.wms.tableview.TableView;
import com.example.wms.util.CSVReadAndWrite;
import com.example.wms.util.FileUtils;
import com.example.wms.util.JSONReadAndWrite;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import android.widget.AutoCompleteTextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.example.wms.databinding.StockTakeListBinding;

public class StockTake extends Fragment implements CustomDailogIntf, CustomListIntf, DatePickerDialog.OnDateSetListener {

    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    private StockTakeBinding binding;
    private SurfaceView surfaceView;
    private StockTakeDailogBinding stockTakeDailogBinding;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;
    private boolean disableBarcode;
    private JSONObject stockItems;
    private SharedPreferences sharedpreferences;
    private String clinetName;
    private TableLayout mTableLayout;
    private ArrayList<SubjectData> arrayList;
    private ProgressDialog mProgressBar;
    private TableView tv;
    private AutoCompleteTextView autoCompleteTextView;
    private List<List<String>> addItems;
    private boolean isEdit;
    private Button btnDate;
    private String seldate;
    private String pageName;
    private String path;
    private boolean editData;
    private CustomerInfoBinding customerInfoBinding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
        pageName = sharedpreferences.getString("pagename", "NA");
        editData = new Boolean(sharedpreferences.getString("edit", "false"));
        customerInfoBinding = CustomerInfoBinding.inflate(inflater, container, false);
        System.out.println(editData);
        CSVReadAndWrite.removeKeyFromPS(sharedpreferences, "edit");
        try {
            ((MainActivity) requireContext()).getSupportActionBar().setTitle(pageName);

        } catch (Exception e) {
        }

        binding = StockTakeBinding.inflate(inflater, container, false);
        this.btnDate = binding.btnDate;
        if (!editData) {
            String temJs = JSONReadAndWrite.readJSON(CSVReadAndWrite.tempJSONFileNames(clinetName, pageName));
            if (temJs != null) {
                try {
                    JSONObject tj = new JSONObject(temJs);
                    seldate = tj.getString("seldate");
                } catch (Exception e) {
                }
            } else {
                seldate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                if (!pageName.equalsIgnoreCase("Stock Take"))
                    seldate = CSVReadAndWrite.previousDate(seldate);
            }
        } else {
            seldate = sharedpreferences.getString("editdate", "NA");
            CSVReadAndWrite.removeKeyFromPS(sharedpreferences, "editdate");
        }
        System.out.println("seldate : " + seldate);
        btnDate.setText(seldate);
        mTableLayout = binding.tableInvoices;
        stockTakeDailogBinding = StockTakeDailogBinding.inflate(inflater, container, false);
        disableBarcode = false;
        arrayList = new ArrayList<>();
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = binding.surfaceView;
        barcodeText = binding.barcodeText;
        binding.btnDate.setBackgroundColor(Color.parseColor("#039696"));
        this.btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Please note that use your package name here
                if (!editData) {
                    Date d = new Date();
                    try {
                        d = new SimpleDateFormat("dd-MM-yyyy").parse(seldate);
                    } catch (Exception e) {

                    }
                    DatePickerDg mDatePickerDialogFragment = new DatePickerDg(StockTake.this, d);
                    mDatePickerDialogFragment.show(getFragmentManager(), "DATE PICK");
                }
            }
        });
        binding.fileUpload.setBackgroundColor(Color.parseColor("#FF7F00"));
        binding.fileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermissionAndBrowseFile();
            }
        });

        stockItems = CSVReadAndWrite.getStockDetails(clinetName);
        tv = new TableView(mTableLayout, requireContext(), null);
        if (pageName.equalsIgnoreCase("Invoice")) {
            tv.setFontSize(30, 26);
            tv.setDisplayColums("1,2,3,4");
            tv.setNumberColums("2,3,4");
            tv.setWidthColums("0,200,60,60,80");
        } else {
            tv.setFontSize(40, 36);
            tv.setDisplayColums("1,2");
            tv.setNumberColums("2");
            tv.setWidthColums("0,300,0");
        }


        addItems = new ArrayList<>();
        autoCompleteTextView = binding.autoCompleteTextView;
        String[] items = new String[stockItems.length()];
        try {
            Iterator<String> keys = stockItems.keys();
            int i = 0;
            while (keys.hasNext()) {
                JSONObject jo = stockItems.getJSONObject(keys.next());
                items[i] = jo.getString("name") + "--" + jo.getString("barcode");
                i++;
            }
        } catch (Exception e) {
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (requireContext(), R.layout.item_list, items);

        autoCompleteTextView.setThreshold(1);//will start working from first character
        autoCompleteTextView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        autoCompleteTextView.setTextColor(Color.RED);
        autoCompleteTextView.setTextSize(16);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String s = autoCompleteTextView.getText().toString().split("--")[1];
                openDailogBox(s);
                barcodeData = s;
                barcodeText.setText(s);
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(binding.getRoot().getApplicationWindowToken(), 0);
            }
        });

        binding.btnSave.setBackgroundColor(Color.parseColor("#0794CC"));
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pageName.equalsIgnoreCase("Invoice")) {
                    CustomDailog c = new CustomDailog(customerInfoBinding.getRoot(), StockTake.this, "Customer Info", null, 2);
                    c.show(getFragmentManager(), "NoticeDialogFragment");
                } else {
                    CustomDailog c = new CustomDailog(null, StockTake.this, "Alert", "Do you want to save data for " + seldate + "?", 2);
                    c.show(getFragmentManager(), "NoticeDialogFragment");
                }
            }
        });
        binding.btnClear.setBackgroundColor(Color.RED);
        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDailog c = new CustomDailog(null, StockTake.this, "Alert", "Do you want to clear data " + seldate + "?", 1);
                c.show(getFragmentManager(), "NoticeDialogFragment");

            }
        });


        initialiseDetectorsAndSources();
        tableData();
        return binding.getRoot();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        seldate = new SimpleDateFormat("dd-MM-yyyy").format(mCalendar.getTime());
        btnDate.setText(seldate);
    }

    public void tableData() {
        mTableLayout.removeAllViews();
        List<String> headers = new ArrayList<>();
        headers.add("Name");
        if (pageName.equalsIgnoreCase("Invoice")) {
            headers.add("Qty");
            headers.add("Price");
            headers.add("Amount");
        } else {
            headers.add("Quantity");
        }
        if (addItems.isEmpty()) {
            if (editData)
                addItems = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.CSVFileNames(clinetName, pageName, seldate));
            else
                addItems = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
        }
        System.out.println(addItems);
        tv.fullTable(headers, addItems);
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this.getContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
                    this.doBrowseFile();
                } else {
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
                        String filePath = null;
                        try {
                            filePath = FileUtils.getPath(this.getContext(), fileUri);
                        } catch (Exception e) {

                            Toast.makeText(this.getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                        }
                        path = (filePath);
                        if (!path.endsWith(".csv")) {
                            CustomDailog c = new CustomDailog(null, StockTake.this, "Alert", "Please select only csv file", 0);
                            c.show(getFragmentManager(), "NoticeDialogFragment");
                        } else {
                            List<List<String>> fd = CSVReadAndWrite.readCSVFromFolder(path);
                            if (!fd.isEmpty()) {
                                for (List<String> ls : fd) {
                                    addItems.add(ls);
                                    tv.addRow(ls);
                                }
                                CSVReadAndWrite.writeDataAtOnce(addItems, CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
                            } else {
                                CustomDailog c = new CustomDailog(null, StockTake.this, "Alert", "Empty file or invalid file", -1);
                                c.show(getFragmentManager(), "NoticeDialogFragment");
                            }

                        }

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getPath() {
        return path;
    }


    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(requireContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(requireContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    barcodeText.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText(barcodeData);
                                if (!disableBarcode) {
                                    openDailogBox(barcodeData);
                                    barcodeText.setText(barcodeData);
                                    //   toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                                }

                            } else {
                                barcodeData = barcodes.valueAt(0).displayValue;
                                System.out.println("Hi 1" + barcodeText.getText().toString());
                                if (!disableBarcode) {
                                    barcodeText.setText(barcodeData);
                                    openDailogBox(barcodeData);
                                    // toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                                }

                            }
                        }


                    });

                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void openDailogBox(String barcode) {
        try {
            System.out.println("Barcode : " + barcode);
            System.out.println("stockItems : " + stockItems.length());
            if (stockItems.has(barcode)) {
                disableBarcode = true;
                JSONObject o = (JSONObject) stockItems.get(barcode);
                barcodeText.setText(barcodeData);
                if (pageName.equalsIgnoreCase("Invoice")) {
                    stockTakeDailogBinding.stockName.setText("Price RS : " + o.get("price").toString());
                    stockTakeDailogBinding.stockCode.setText(o.get("name").toString());
                } else {
                    stockTakeDailogBinding.stockName.setText(o.get("name").toString());
                    stockTakeDailogBinding.stockCode.setText(o.get("code").toString());
                }
                stockTakeDailogBinding.quantity.setText(null);
                stockTakeDailogBinding.cartons.setText(null);
                stockTakeDailogBinding.cartons.setEnabled(Integer.parseInt(o.getString("carton")) != 0);
                isEdit = false;
                String msg = null;
                for (List<String> ai : addItems) {
                    if (ai.get(0).equalsIgnoreCase(o.get("code").toString())) {
                        int qty = Integer.parseInt(ai.get(2));
                        if (ai.size() == 4) {
                            qty -= (Integer.parseInt(defaultNumber(ai.get(3))) * Integer.parseInt(o.getString("carton")));
                            stockTakeDailogBinding.cartons.setText(ai.get(3));
                        }
                        stockTakeDailogBinding.quantity.setText(qty + "");
                        msg = "Item Already Added";
                        isEdit = true;
                    }
                }
                CustomDailog c = new CustomDailog(stockTakeDailogBinding.getRoot(), StockTake.this, "Add item", msg, 0);
                c.show(getFragmentManager(), "NoticeDialogFragment");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTempFile() {
        CSVReadAndWrite.deleteFile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
        CSVReadAndWrite.deleteFile(CSVReadAndWrite.tempJSONFileNames(clinetName, pageName));
    }

    public String defaultNumber(Object o) {
        return (o != null && o.toString().length() > 0) ? o.toString() : "0";
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {
        if (actionId == 0) {
            barcodeText.setText(null);
            System.out.println("Hi 4");

            autoCompleteTextView.setText(null);
            if (stockTakeDailogBinding != null) {
                if (stockTakeDailogBinding.quantity.getText() != null && stockTakeDailogBinding.quantity.getText().toString().length() > 0) {
                    disableBarcode = false;
                    try {
                        JSONObject ss = (JSONObject) stockItems.get(barcodeData);
                        if (isEdit) {
                            for (List<String> ai : addItems) {
                                if (ai.get(0).equalsIgnoreCase(ss.getString("code"))) {
                                    ai.remove(2);
                                    int qty = Integer.parseInt(defaultNumber(stockTakeDailogBinding.quantity.getText()));
                                    int carton = Integer.parseInt(defaultNumber(stockTakeDailogBinding.cartons.getText()));
                                    qty += (carton * Integer.parseInt(ss.getString("carton")));
                                    ai.add(2, qty + "");
                                    if(ai.size()==4)
                                      ai.remove(3);
                                    ai.add(3, carton + "");
                                    if (pageName.equalsIgnoreCase("Invoice")) {
                                        ai.remove(5);
                                        ai.remove(4);
                                        double price = Double.parseDouble(ss.getString("price"));
                                        ai.add(4, price + "");
                                        ai.add(5, (qty * price) + "");
                                    }
                                    System.out.println(ai);
                                    break;
                                }
                            }
                            isEdit = false;
                            CSVReadAndWrite.writeDataAtOnce(addItems, CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
                            JSONObject o = new JSONObject();
                            o.put("seldate", seldate);
                            JSONReadAndWrite.writeJSONfile(CSVReadAndWrite.tempJSONFileNames(clinetName, pageName), o);
                            tableData();
                        } else {
                            List<String> ls = new ArrayList<>();
                            ls.add(ss.getString("code"));
                            ls.add(ss.getString("name"));
                            int qty = Integer.parseInt(defaultNumber(stockTakeDailogBinding.quantity.getText()));
                            qty += (Integer.parseInt(defaultNumber(stockTakeDailogBinding.cartons.getText())) * Integer.parseInt(ss.getString("carton")));
                            ls.add(qty + "");
                            ls.add(defaultNumber(stockTakeDailogBinding.cartons.getText()));
                            if (pageName.equalsIgnoreCase("Invoice")) {

                                double price = Double.parseDouble(ss.getString("price"));
                                ls.add(price + "");
                                ls.add((qty * price) + "");
                            }
                            addItems.add(ls);
                            tv.addRow(ls);
                            CSVReadAndWrite.writeDataAtOnce(addItems, CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));

                            JSONObject o = new JSONObject();
                            o.put("seldate", seldate);
                            JSONReadAndWrite.writeJSONfile(CSVReadAndWrite.tempJSONFileNames(clinetName, pageName), o);


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(binding.getRoot().getApplicationWindowToken(), 0);
                } else {
                    CustomDailog c = new CustomDailog(null, StockTake.this, "Alert", "Quantity Required", 3);
                    c.show(getFragmentManager(), "NoticeDialogFragment");
                }


            }
        } else if (actionId == 1) {
            deleteTempFile();
            addItems = new ArrayList<>();
            tableData();

        } else if (actionId == 2) {
            String nf = seldate;
            if (!editData)
                if (pageName.equalsIgnoreCase("Invoice")) {
                    nf = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                }
            String filename = CSVReadAndWrite.CSVFileNames(clinetName, pageName, nf);
            CSVReadAndWrite.writeDataAtOnce(addItems, filename);
            try {
                JSONArray sh = null;
                String shja = JSONReadAndWrite.readJSON(CSVReadAndWrite.jsonFileNames(clinetName, pageName));
                boolean check = true;
                if (shja != null) {
                    sh = new JSONArray(shja);
                    for (int j = 0; j < sh.length(); j++) {
                        JSONObject o = sh.getJSONObject(j);
                        if (o.getString("date").equalsIgnoreCase(nf)) {
                            o.put("datetime", (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())));
                            o.put("filename", filename);
                            o.put("itemcount", addItems.size());
                            if (pageName.equalsIgnoreCase("Invoice")) {
                                if (customerInfoBinding.address.getText() != null)
                                    o.put("moblie", customerInfoBinding.address.getText().toString());
                                else
                                    o.put("moblie", "");
                                if (customerInfoBinding.clientName.getText() != null)
                                    o.put("custname", customerInfoBinding.clientName.getText().toString());
                                else
                                    o.put("custname", "");
                                o.put("amount", totalAmount());

                            }
                            check = false;
                        }
                    }
                } else {
                    sh = new JSONArray();
                }
                if (check) {
                    JSONObject obj = new JSONObject();
                    obj.put("date", nf);
                    obj.put("datetime", (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())));
                    obj.put("filename", filename);
                    obj.put("itemcount", addItems.size());
                    if (pageName.equalsIgnoreCase("Invoice")) {
                        if (customerInfoBinding.address.getText() != null)
                            obj.put("moblie", customerInfoBinding.address.getText().toString());
                        if (customerInfoBinding.clientName.getText() != null)
                            obj.put("custname", customerInfoBinding.clientName.getText().toString());
                        obj.put("amount", totalAmount());
                    }
                    sh.put(obj);
                }
                deleteTempFile();
                JSONReadAndWrite.writeJSONArrayfile(CSVReadAndWrite.jsonFileNames(clinetName, pageName), sh);

            } catch (Exception e) {

            }
            CustomDailog c = new CustomDailog(null, StockTake.this, "Alert", "Data Saved successfully?", -1);
            c.show(getFragmentManager(), "NoticeDialogFragment");

        } else if (actionId == -1) {
            try {
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(binding.getRoot().getApplicationWindowToken(), 0);
            } catch (Exception e) {
            }
            NavHostFragment.findNavController(StockTake.this)
                    .navigate(R.id.StHistory);
        } else if (actionId == 3) {
            openDailogBox(barcodeData);
        }

    }

    public double totalAmount() {
        double amt = 0;
        for (List<String> a : addItems) {
            amt += Double.parseDouble(a.get(4));
        }
        return amt;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {
        disableBarcode = false;
        System.out.println("HI");
        barcodeText.setText(null);
        autoCompleteTextView.setText(null);
    }

    @Override
    public void onItemClick(View obj) {

    }
}

