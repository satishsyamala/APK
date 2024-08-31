package com.example.myexp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.myexp.dailog.CustomDailog;
import com.example.myexp.dailog.CustomDailogIntf;
import com.example.myexp.dailog.DatePickerDg;
import com.example.myexp.databinding.AddExpTypeBinding;
import com.example.myexp.databinding.AddExpenseBinding;
import com.example.myexp.databinding.ExpFilterBinding;
import com.example.myexp.databinding.ExpTypesBinding;
import com.example.myexp.databinding.ExpensesBinding;
import com.example.myexp.listview.CustomListIntf;
import com.example.myexp.listview.SubjectData;
import com.example.myexp.tableview.TableView;
import com.example.myexp.tableview.TableViewIntf;
import com.example.myexp.util.CSVReadAndWrite;
import com.example.myexp.util.DatePickerFragment;
import com.example.myexp.util.GeneralUtil;
import com.example.myexp.util.PDFUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Expenses extends Fragment implements CustomDailogIntf, CustomListIntf, DatePickerDialog.OnDateSetListener {
    private ExpensesBinding expensesBinding;
    private SharedPreferences sharedpreferences;
    private AddExpenseBinding addExpenseBinding;
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
    private List<String> exponls;
    private List<String> typesls;
    private List<String> chargels;
    private List<String> seletedItem;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
        pageName = sharedpreferences.getString("pagename", "NA");
        try {
            ((MainActivity) requireContext()).getSupportActionBar().setTitle(pageName);
        } catch (Exception e) {
        }
        expensesBinding = ExpensesBinding.inflate(inflater, container, false);
        simpleList = expensesBinding.expseList;
        addExpenseBinding = AddExpenseBinding.inflate(inflater, container, false);
        expFilterBinding = ExpFilterBinding.inflate(inflater, container, false);
        typesls = CSVReadAndWrite.getDropdownValue(clinetName, "Exp Types", "Select");
        ArrayAdapter<String> adp = new ArrayAdapter<String>(requireContext(), R.layout.item_list, typesls);
        addExpenseBinding.expType.setAdapter(adp);

        exponls = CSVReadAndWrite.getDropdownValue(clinetName, "Exp On", "Select");
        ArrayAdapter<String> expona = new ArrayAdapter<String>(requireContext(), R.layout.item_list, exponls);
        addExpenseBinding.expOn.setAdapter(expona);

        chargels = CSVReadAndWrite.getDropdownValue(clinetName, "Charges", "Select");
        ArrayAdapter<String> cha = new ArrayAdapter<String>(requireContext(), R.layout.item_list, chargels);
        addExpenseBinding.chargeName.setAdapter(cha);

        expensesBinding.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String seldate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                addExpenseBinding.expDate.setText(seldate);
                addExpenseBinding.chargeName.setSelection(0);
                addExpenseBinding.expDes.setText(null);
                addExpenseBinding.expQty.setText(null);
                addExpenseBinding.expAmount.setText(null);
                addExpenseBinding.expDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Date d = new Date();
                        DatePickerDg mDatePickerDialogFragment = new DatePickerDg(Expenses.this, d);
                        mDatePickerDialogFragment.show(getFragmentManager(), "DATE PICK");
                    }
                });

                CustomDailog c = new CustomDailog(addExpenseBinding.getRoot(), Expenses.this, "Add Expense", null, 1);
                c.show(getFragmentManager(), "NoticeDialogFragment");
            }
        });

        expensesBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String seldate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                expFilterBinding.fromDate.setText(null);
                expFilterBinding.fromDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new DatePickerFragment(expFilterBinding.fromDate, null);
                        newFragment.show(getFragmentManager(), "datePicker");
                    }
                });
                expFilterBinding.toDate.setText(null);
                expFilterBinding.toDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new DatePickerFragment(expFilterBinding.toDate, null);
                        newFragment.show(getFragmentManager(), "datePicker");
                    }
                });
                expFilterBinding.expType.setAdapter(adp);
                expFilterBinding.expOn.setAdapter(expona);
                CustomDailog c = new CustomDailog(expFilterBinding.getRoot(), Expenses.this, "Search", null, 15);
                c.show(getFragmentManager(), "NoticeDialogFragment");
            }
        });
        getStock();
        tableData(null, null);
        expensesBinding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String seldate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    String file = clinetName + "/share/" + seldate + "/" + (new SimpleDateFormat("HH-mm-ss").format(new Date())) + ".csv";
                    CSVReadAndWrite.createClientFolder(clinetName + "/share/" + seldate);
                    List<List<String>> reportData = new ArrayList<>();
                    List<List<String>> ls = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
                    GeneralUtil.sortExpenses(ls);
                    List<String> headers = new ArrayList<>();
                    headers.add("Date");
                    headers.add("Expense Type");
                    headers.add("Expense On");
                    headers.add("Description");
                    headers.add("Qty");
                    headers.add("Amount");
                    headers.add("Charge");
                    reportData.add(headers);
                    int amount = 0;
                    for (List<String> a : ls) {
                        List<String> data = new ArrayList<>();
                        for (int i = 1; i < 8; i++) {
                            data.add(a.get(i));
                        }
                        amount += GeneralUtil.stringToDouble(a.get(6));
                        reportData.add(data);
                    }
                    List<String> footer = new ArrayList<>();
                    footer.add("");
                    footer.add("");
                    footer.add("");
                    footer.add("");
                    footer.add("Total");
                    footer.add("" + amount);
                    reportData.add(footer);
                    CSVReadAndWrite.writeDataAtOnce(reportData, file);
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Expenses";
                    String shareSub = "Expenses";
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    String myFilePath = CSVReadAndWrite.getBaseFileWithFolder() + "/" + file;
                    File f = new File(myFilePath);
                    Uri apkURI = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName() + ".provider", f);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, apkURI);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        expensesBinding.btnSharepdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<List<String>> reportData = new ArrayList<>();
                    List<List<String>> ls = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
                    GeneralUtil.sortExpenses(ls);
                    List<String> headers = new ArrayList<>();
                    headers.add("Date");
                    headers.add("Expense Type");
                    headers.add("Expense On");
                    headers.add("Description");
                    headers.add("Qty");
                    headers.add("Amount");
                    headers.add("Charge");
                    reportData.add(headers);
                    int amount = 0;
                    for (List<String> a : ls) {
                        List<String> data = new ArrayList<>();
                        for (int i = 1; i < 8; i++) {
                            data.add(a.get(i));
                        }
                        amount += GeneralUtil.stringToDouble(a.get(6));
                        reportData.add(data);
                    }
                    List<String> footer = new ArrayList<>();
                    footer.add("  ");
                    footer.add("  ");
                    footer.add("  ");
                    footer.add("   ");
                    footer.add("Total");
                    footer.add("" + amount);
                    footer.add("   ");
                    reportData.add(footer);

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Expenses";
                    String shareSub = "Expenses";
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    List<Integer> nc = new ArrayList<>();
                    nc.add(5);
                    nc.add(6);
                    float[] columnWidths = {1.25f, 1.5f, 2f, 2.5f, 0.75f, 1.2f, 1f};
                    int totCash= GeneralUtil.getCashIn(clinetName);
                    int totExp=GeneralUtil.getTotalExpense(clinetName);
                    List<String> reh=new ArrayList<>();
                    reh.add("Total Cash : "+GeneralUtil.doubleFarmate(totCash));
                    reh.add("Total Expenses : "+GeneralUtil.doubleFarmate(totExp));
                    reh.add("Balance Amount : "+GeneralUtil.doubleFarmate(totCash-totExp));
                    String myFilePath = PDFUtil.createPDF(clinetName, reh, reportData, nc, columnWidths);
                    File f = new File(myFilePath);
                    Uri apkURI = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getPackageName() + ".provider", f);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, apkURI);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return expensesBinding.getRoot();

    }


    public void getStock() {
        stocks = new ArrayList<>();
        stocks = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
        GeneralUtil.sortExpenses(stocks);
    }

    public boolean validateField(Object value) {
        if (value != null && value.toString().trim().length() > 0 && !value.toString().trim().equalsIgnoreCase("select"))
            return true;
        else
            return false;
    }

    public List<List<String>> searchData() {
        List<List<String>> result=new ArrayList<>();
        try {
            String searchValue="";
            String searchValue1="";
            try {
                searchValue = expFilterBinding.expType.getSelectedItem().toString();
                searchValue1 = expFilterBinding.expOn.getSelectedItem().toString();
            }catch (Exception e){

            }

            List<List<String>> data = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
            System.out.println("data Size : "+data.size());

            for (List<String> ls : data) {
                boolean check = true;
                if (validateField(searchValue) && !ls.get(2).toString().toLowerCase().startsWith(searchValue.toLowerCase())) {
                    check = false;

                }
                if (validateField(searchValue1) && !ls.get(3).toString().toLowerCase().startsWith(searchValue1.toLowerCase())) {
                    check = false;

                }
                if(validateField(expFilterBinding.fromDate.getText()) && !GeneralUtil.fromDateVal(ls.get(1),expFilterBinding.fromDate.getText().toString()))
                {
                    check = false;

                }
                if(validateField(expFilterBinding.toDate.getText()) && !GeneralUtil.toDateVal(ls.get(1),expFilterBinding.toDate.getText().toString()))
                {
                    check = false;

                }

                if (check) {
                    result.add(ls);
                }
            }
            GeneralUtil.sortExpenses(result);
        } catch (Exception w) {
w.printStackTrace();
        }
        System.out.println("Result Size : "+result.size());
        return result;
    }



    public void tableData(String searchValue, String searchValue1) {
        try {
            int totalExp = 0;
            ArrayList<SubjectData> subData = new ArrayList<>();
            for (List<String> ls : stocks) {
                boolean check = true;
                if (validateField(searchValue) && !ls.get(2).toString().toLowerCase().startsWith(searchValue.toLowerCase())) {
                    check = false;

                }
                if (validateField(searchValue1) && !ls.get(3).toString().toLowerCase().startsWith(searchValue1.toLowerCase())) {
                    check = false;

                }
                if(validateField(expFilterBinding.fromDate.getText()) && !GeneralUtil.fromDateVal(ls.get(1),expFilterBinding.fromDate.getText().toString()))
                {
                    check = false;

                }
                if(validateField(expFilterBinding.toDate.getText()) && !GeneralUtil.toDateVal(ls.get(1),expFilterBinding.toDate.getText().toString()))
                {
                    check = false;

                }

                if (check) {
                    totalExp += GeneralUtil.stringToDouble(ls.get(6));
                    subData.add(getSubjectDataForList(ls));
                }
            }


            expensesBinding.totalExpAmount.setText("Total Rs:" + GeneralUtil.doubleFarmate(totalExp));
            CustomAdapter customAdapter = new CustomAdapter(requireContext(), subData, Expenses.this, "expenses");
            simpleList.setAdapter(customAdapter);
        } catch (Exception w) {

        }
    }


    public SubjectData getSubjectDataForList(List<String> ls) {
        SubjectData s = new SubjectData(ls.get(2) + "/" + ls.get(3), ls.get(1), "Rs : " + GeneralUtil.doubleFarmate(ls.get(6)), ls.get(4) + " / " + ls.get(7));
        s.setImage(ls.get(0));
        return s;
    }


    public String defaultString(Object o) {
        return (o != null && o.toString().length() > 0) ? o.toString() : "";
    }

    public String defaultDropString(Object o) {
        return (o != null && validateField(o.toString())) ? o.toString() : "NA";
    }

    public String defaultNumber(Object o) {
        return (o != null && o.toString().length() > 0) ? o.toString() : "0";
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {
        if (actionId == 1) {
            List<String> si = new ArrayList<>();
            si.add(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            si.add(defaultString(addExpenseBinding.expDate.getText()));
            si.add(defaultDropString(addExpenseBinding.expType.getSelectedItem().toString()));
            si.add(defaultDropString(addExpenseBinding.expOn.getSelectedItem().toString()));
            si.add(defaultString(addExpenseBinding.expDes.getText()));
            si.add(defaultString(addExpenseBinding.expQty.getText()));
            si.add(defaultString(addExpenseBinding.expAmount.getText()));
            si.add(defaultDropString(addExpenseBinding.chargeName.getSelectedItem().toString()));
            si.add("pending");
            stocks.add(si);

            CSVReadAndWrite.writeDataAtOnce(stocks, CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
            getStock();
            tableData(null, null);
        } else if (actionId == 2 && seletedItem != null) {
            int i = 0;
            List<List<String>> exp = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
            for (List<String> s : exp) {
                if (s.get(0).equalsIgnoreCase(seletedItem.get(0))) {
                    exp.remove(i);
                    List<String> si = new ArrayList<>();
                    si.add(seletedItem.get(0));
                    si.add(defaultString(addExpenseBinding.expDate.getText()));
                    si.add(defaultDropString(addExpenseBinding.expType.getSelectedItem().toString()));
                    si.add(defaultDropString(addExpenseBinding.expOn.getSelectedItem().toString()));
                    si.add(defaultString(addExpenseBinding.expDes.getText()));
                    si.add(defaultString(addExpenseBinding.expQty.getText()));
                    si.add(defaultString(addExpenseBinding.expAmount.getText()));
                    si.add(defaultDropString(addExpenseBinding.chargeName.getSelectedItem().toString()));
                    si.add("update");
                    exp.add(i, si);
                    break;
                }
                i++;
            }
            CSVReadAndWrite.writeDataAtOnce(exp, CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));

            getStock();
            tableData(null, null);
        } else if (actionId == 3 && seletedItem != null) {
            int i = 0;
            List<List<String>> exp = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
            for (List<String> s : exp) {
                if (s.get(0).equalsIgnoreCase(seletedItem.get(0))) {
                    exp.remove(i);
                    break;
                }
                i++;
            }
            CSVReadAndWrite.writeDataAtOnce(exp, CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
            getStock();
            tableData(null, null);
        } else if (actionId == 15) {
            tableData(expFilterBinding.expType.getSelectedItem().toString(), expFilterBinding.expOn.getSelectedItem().toString());

        } else if (actionId == 4 && seletedItem != null) {
            addExpenseBinding.expDate.setText(seletedItem.get(1));
            addExpenseBinding.expType.setSelection(getIndec(typesls, seletedItem.get(2)));
            addExpenseBinding.expOn.setSelection(getIndec(exponls, seletedItem.get(3)));
            addExpenseBinding.expDes.setText(seletedItem.get(4));
            addExpenseBinding.expQty.setText(seletedItem.get(5));
            addExpenseBinding.expAmount.setText(seletedItem.get(6));
            addExpenseBinding.chargeName.setSelection(getIndec(chargels, seletedItem.get(7)));
            CustomDailog c = new CustomDailog(addExpenseBinding.getRoot(), Expenses.this, "Add Expense", null, 2);
            c.show(getFragmentManager(), "NoticeDialogFragment");
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String seldate = new SimpleDateFormat("dd-MM-yyyy").format(mCalendar.getTime());
        addExpenseBinding.expDate.setText(seldate);
    }

    @Override
    public void onItemClick(View obj) {

    }


    public int getIndec(List<String> data, String value) {
        int index = 0;
        for (String s : data) {
            if (s.equalsIgnoreCase(value))
                return index;
            index++;
        }
        return 0;
    }

    ;


    @Override
    public void onEdit(String string1, String string2) {
        List<List<String>> exp = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
        for (List<String> ls : exp) {
            if (ls.get(0).equalsIgnoreCase(string1)) {
                seletedItem = ls;
                break;
            }
        }
        CustomDailog c = new CustomDailog(null, Expenses.this, "Alert", "Do you want to Edit data?", 4);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDelete(String string1, String string2) {
        List<List<String>> exp = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
        for (List<String> ls : exp) {
            if (ls.get(0).equalsIgnoreCase(string1)) {
                seletedItem = ls;
                break;
            }
        }
        CustomDailog c = new CustomDailog(null, Expenses.this, "Alert", "Do you want to Delete data?", 3);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }
}
