package com.example.myexp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myexp.dailog.CustomDailog;
import com.example.myexp.dailog.CustomDailogIntf;
import com.example.myexp.dailog.DatePickerDg;
import com.example.myexp.databinding.AddExpChargesBinding;
import com.example.myexp.databinding.AddExpenseBinding;
import com.example.myexp.databinding.ExpChargesBinding;
import com.example.myexp.databinding.ExpFilterBinding;
import com.example.myexp.databinding.ExpensesBinding;
import com.example.myexp.listview.CustomListIntf;
import com.example.myexp.listview.SubjectData;
import com.example.myexp.util.CSVReadAndWrite;
import com.example.myexp.util.GeneralUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpCharges extends Fragment implements CustomDailogIntf, CustomListIntf {
    private ExpChargesBinding expChargesBinding;
    private SharedPreferences sharedpreferences;
    private AddExpChargesBinding addExpChargesBinding;
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
    private int totalExp;
    private int paidExp;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
        pageName = sharedpreferences.getString("pagename", "NA");
        try {
            ((MainActivity) requireContext()).getSupportActionBar().setTitle(pageName);
        } catch (Exception e) {
        }
        expChargesBinding = ExpChargesBinding.inflate(inflater, container, false);
        simpleList = expChargesBinding.expseList;
        addExpChargesBinding = AddExpChargesBinding.inflate(inflater, container, false);
        expFilterBinding = ExpFilterBinding.inflate(inflater, container, false);


        expChargesBinding.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExpChargesBinding.chargeName.setText(null);
                addExpChargesBinding.contactName.setText(null);
                addExpChargesBinding.contactMobile.setText(null);
                addExpChargesBinding.amount.setText(null);

                CustomDailog c = new CustomDailog(addExpChargesBinding.getRoot(), ExpCharges.this, "Add Expense", null, 1);
                c.show(getFragmentManager(), "NoticeDialogFragment");
            }
        });


        getStock();
        tableData(null, null);
        return expChargesBinding.getRoot();

    }


    public void getStock() {
        stocks = new ArrayList<>();
        stocks = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
        ;
    }

    public boolean validateField(String value) {
        if (value != null && value.trim().length() > 0 && !value.trim().equalsIgnoreCase("select"))
            return true;
        else
            return false;

    }

    public void tableData(String searchValue, String searchValue1) {
        try {
            totalExp=0;
            paidExp=0;
            ArrayList<SubjectData> subData = new ArrayList<>();
            for (List<String> ls : stocks) {
               subData.add(getSubjectDataForList(ls));
            }
            expChargesBinding.totalExpAmount.setText(GeneralUtil.doubleFarmate(paidExp)+"/"+GeneralUtil.doubleFarmate(totalExp)+" ("+GeneralUtil.doubleFarmate(totalExp-paidExp)+")");
            CustomAdapter customAdapter = new CustomAdapter(requireContext(), subData, ExpCharges.this, "expenses");
            simpleList.setAdapter(customAdapter);

        } catch (Exception w) {

        }
    }


    public SubjectData getSubjectDataForList(List<String> ls) {
        double a=GeneralUtil.getTotalCharges(clinetName,ls.get(0));
        double a1=GeneralUtil.stringToDouble(ls.get(3));
        paidExp+=a;
        totalExp+=a1;
        SubjectData s = new SubjectData(ls.get(0),"Ph : "+ls.get(2),   GeneralUtil.doubleFarmate(a)+"/"+GeneralUtil.doubleFarmate(a1)+" ("+GeneralUtil.doubleFarmate(a1-a)+")", ls.get(1));
        s.setImage(ls.get(0));

        return s;
    }


    public String defaultString(Object o) {
        return (o != null && o.toString().length() > 0) ? o.toString() : "";
    }

    public String defaultNumber(Object o) {
        return (o != null && o.toString().length() > 0) ? o.toString() : "0";
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int actionId) {
        if (actionId == 1) {
            List<String> si = new ArrayList<>();
            si.add(defaultString(addExpChargesBinding.chargeName.getText()));
            si.add(defaultString(addExpChargesBinding.contactName.getText()));
            si.add(defaultString(addExpChargesBinding.contactMobile.getText()));
            si.add(defaultString(addExpChargesBinding.amount.getText()));
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
                    si.add(defaultString(addExpChargesBinding.chargeName.getText()));
                    si.add(defaultString(addExpChargesBinding.contactName.getText()));
                    si.add(defaultString(addExpChargesBinding.contactMobile.getText()));
                    si.add(defaultString(addExpChargesBinding.amount.getText()));
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
            addExpChargesBinding.chargeName.setText(seletedItem.get(0));
            addExpChargesBinding.contactName.setText(seletedItem.get(1));
            addExpChargesBinding.contactMobile.setText(seletedItem.get(2));
            addExpChargesBinding.amount.setText(seletedItem.get(3));


            CustomDailog c = new CustomDailog(addExpChargesBinding.getRoot(), ExpCharges.this, "Add Expense", null, 2);
            c.show(getFragmentManager(), "NoticeDialogFragment");
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int actionId) {

    }



    @Override
    public void onItemClick(View obj) {
        try {
            TextView v = obj.findViewById(R.id.exp_type);
            System.out.println(v.getText().toString());
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("chargename", v.getText().toString());
            editor.commit();
            NavHostFragment.findNavController(ExpCharges.this)
                    .navigate(R.id.ExpChargesDetails);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        CustomDailog c = new CustomDailog(null, ExpCharges.this, "Alert", "Do you want to Edit data?", 4);
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
        CustomDailog c = new CustomDailog(null, ExpCharges.this, "Alert", "Do you want to Delete data?", 3);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }
}
