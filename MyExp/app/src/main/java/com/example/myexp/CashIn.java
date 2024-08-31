package com.example.myexp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myexp.dailog.CustomDailog;
import com.example.myexp.dailog.CustomDailogIntf;
import com.example.myexp.dailog.DatePickerDg;
import com.example.myexp.databinding.AddCashBinding;
import com.example.myexp.databinding.AddCashInBinding;
import com.example.myexp.databinding.AddExpChargesBinding;
import com.example.myexp.databinding.CashInBinding;
import com.example.myexp.databinding.ExpChargesBinding;
import com.example.myexp.databinding.ExpFilterBinding;
import com.example.myexp.listview.CustomListIntf;
import com.example.myexp.listview.SubjectData;
import com.example.myexp.util.CSVReadAndWrite;
import com.example.myexp.util.GeneralUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CashIn extends Fragment implements CustomDailogIntf, CustomListIntf, DatePickerDialog.OnDateSetListener {
    private CashInBinding cashInBinding;
    private SharedPreferences sharedpreferences;
    private AddCashInBinding addCashInBinding;
    private AddCashBinding addCashBinding;
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


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);
        clinetName = sharedpreferences.getString("client", "NA");
        pageName = sharedpreferences.getString("pagename", "NA");
        try {
            ((MainActivity) requireContext()).getSupportActionBar().setTitle(pageName);
        } catch (Exception e) {
        }
        cashInBinding = CashInBinding.inflate(inflater, container, false);
        simpleList = cashInBinding.expseList;
        addCashInBinding = AddCashInBinding.inflate(inflater, container, false);
        expFilterBinding = ExpFilterBinding.inflate(inflater, container, false);
        addCashBinding = AddCashBinding.inflate(inflater, container, false);

        cashInBinding.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String seldate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                addCashInBinding.contactName.setText(null);
                addCashInBinding.contactMobile.setText(null);

                CustomDailog c = new CustomDailog(addCashInBinding.getRoot(), CashIn.this, "Add Cash In", null, 1);
                c.show(getFragmentManager(), "NoticeDialogFragment");
            }
        });


        getStock();
        tableData(null, null);
        return cashInBinding.getRoot();

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
            ArrayList<SubjectData> subData = new ArrayList<>();
            for (List<String> ls : stocks) {
                subData.add(getSubjectDataForList(ls));
            }
            cashInBinding.totalExpAmount.setText("Total : "+GeneralUtil.doubleFarmate(totalExp));
            CustomAdapter customAdapter = new CustomAdapter(requireContext(), subData, CashIn.this, "cashin");
            simpleList.setAdapter(customAdapter);
        } catch (Exception w) {
         w.printStackTrace();
        }
    }


    public SubjectData getSubjectDataForList(List<String> ls) {
        int a= GeneralUtil.getCashIn(clinetName, ls.get(0));
        totalExp+=a;
        SubjectData s = new SubjectData(ls.get(0), "Rs : " + GeneralUtil.doubleFarmate(a), "Ph : " + ls.get(1));
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
            si.add(defaultString(addCashInBinding.contactName.getText()));
            si.add(defaultString(addCashInBinding.contactMobile.getText()));
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
                    si.add(defaultString(addCashInBinding.contactName.getText()));
                    si.add(defaultString(addCashInBinding.contactMobile.getText()));
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
            addCashInBinding.contactMobile.setText(seletedItem.get(1));
            addCashInBinding.contactName.setText(seletedItem.get(0));
            CustomDailog c = new CustomDailog(addCashInBinding.getRoot(), CashIn.this, "Edit Cash In", null, 2);
            c.show(getFragmentManager(), "NoticeDialogFragment");
        }else if  (actionId == 5 && seletedItem != null) {
            List<List<String>> cash = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, "AddCash"));
            List<String> si = new ArrayList<>();
            si.add(defaultString(seletedItem.get(0)));
            si.add(defaultString(addCashBinding.cashDate.getText()));
            si.add(defaultString(addCashBinding.description.getText()));
            si.add(defaultString(addCashBinding.amount.getText()));
            si.add(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            si.add("pending");
            cash.add(si);
            CSVReadAndWrite.writeDataAtOnce(cash, CSVReadAndWrite.tempCSVFileNames(clinetName, "AddCash"));
            getStock();
            tableData(null, null);
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
            editor.putString("contact_name", v.getText().toString());
            editor.commit();
            NavHostFragment.findNavController(CashIn.this)
                    .navigate(R.id.AddCashDetails);

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

    @Override
    public void onView(String string1, String string2) {
        List<List<String>> exp = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
        for (List<String> ls : exp) {
            if (ls.get(0).equalsIgnoreCase(string1)) {
                seletedItem = ls;
                break;
            }
        }
        String seldate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        addCashBinding.cashDate.setText(seldate);
        addCashBinding.cashDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date d = new Date();
                DatePickerDg mDatePickerDialogFragment = new DatePickerDg(CashIn.this, d);
                mDatePickerDialogFragment.show(getFragmentManager(), "DATE PICK");
            }
        });

        addCashBinding.description.setText(null);
        addCashBinding.amount.setText(null);
        CustomDailog c = new CustomDailog(addCashBinding.getRoot(), CashIn.this, "Add Amount", null, 5);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }


    @Override
    public void onEdit(String string1, String string2) {
        List<List<String>> exp = CSVReadAndWrite.readCSVfile(CSVReadAndWrite.tempCSVFileNames(clinetName, pageName));
        for (List<String> ls : exp) {
            if (ls.get(0).equalsIgnoreCase(string1)) {
                seletedItem = ls;
                break;
            }
        }
        CustomDailog c = new CustomDailog(null, CashIn.this, "Alert", "Do you want to Edit data?", 4);
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
        CustomDailog c = new CustomDailog(null, CashIn.this, "Alert", "Do you want to Delete data?", 3);
        c.show(getFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String seldate = new SimpleDateFormat("dd-MM-yyyy").format(mCalendar.getTime());
        addCashBinding.cashDate.setText(seldate);
    }
}
